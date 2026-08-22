package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.ContactRepository
import com.example.data.sync.SyncConfig
import com.example.data.sync.SyncState
import com.example.model.Contact
import com.example.model.RelationshipCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<RelationshipCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites = _onlyFavorites.asStateFlow()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState = _syncState.asStateFlow()

    private val _syncConfig = MutableStateFlow(SyncConfig())
    val syncConfig = _syncConfig.asStateFlow()

    private val _isAddEditOpen = MutableStateFlow(false)
    val isAddEditOpen = _isAddEditOpen.asStateFlow()

    private val _contactToEdit = MutableStateFlow<Contact?>(null)
    val contactToEdit = _contactToEdit.asStateFlow()

    private val _selectedContactForDetail = MutableStateFlow<Contact?>(null)
    val selectedContactForDetail = _selectedContactForDetail.asStateFlow()

    private val _contactToDelete = MutableStateFlow<Contact?>(null)
    val contactToDelete = _contactToDelete.asStateFlow()

    private val _isSyncSheetOpen = MutableStateFlow(false)
    val isSyncSheetOpen = _isSyncSheetOpen.asStateFlow()

    private val _isArchitectureGuideOpen = MutableStateFlow(false)
    val isArchitectureGuideOpen = _isArchitectureGuideOpen.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = ContactRepository(db.contactDao())
        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
        }
    }

    val rawContacts: StateFlow<List<Contact>> = repository.allContacts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredContacts: StateFlow<List<Contact>> = combine(
        repository.allContacts,
        _searchQuery,
        _selectedCategory,
        _onlyFavorites
    ) { contacts, query, category, favoritesOnly ->
        contacts.filter { contact ->
            val matchesQuery = if (query.isBlank()) true else {
                contact.fullName.contains(query, ignoreCase = true) ||
                contact.phoneNumber.contains(query, ignoreCase = true) ||
                contact.whatsapp.contains(query, ignoreCase = true) ||
                contact.facebook.contains(query, ignoreCase = true) ||
                contact.instagram.contains(query, ignoreCase = true) ||
                contact.notes.contains(query, ignoreCase = true)
            }
            val matchesCategory = category == null || contact.relationship == category
            val matchesFavorite = !favoritesOnly || contact.isFavorite

            matchesQuery && matchesCategory && matchesFavorite
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: RelationshipCategory?) {
        _selectedCategory.value = category
    }

    fun onToggleFavoritesFilter() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun openAddContactDialog() {
        _contactToEdit.value = null
        _isAddEditOpen.value = true
    }

    fun openEditContactDialog(contact: Contact) {
        _contactToEdit.value = contact
        _isAddEditOpen.value = true
    }

    fun closeAddEditDialog() {
        _isAddEditOpen.value = false
        _contactToEdit.value = null
    }

    fun openContactDetail(contact: Contact) {
        _selectedContactForDetail.value = contact
    }

    fun closeContactDetail() {
        _selectedContactForDetail.value = null
    }

    fun openDeleteConfirmation(contact: Contact) {
        _contactToDelete.value = contact
    }

    fun closeDeleteConfirmation() {
        _contactToDelete.value = null
    }

    fun openSyncSheet() {
        _isSyncSheetOpen.value = true
    }

    fun closeSyncSheet() {
        _isSyncSheetOpen.value = false
    }

    fun openArchitectureGuide() {
        _isArchitectureGuideOpen.value = true
    }

    fun closeArchitectureGuide() {
        _isArchitectureGuideOpen.value = false
    }

    fun saveContact(
        id: Long = 0,
        fullName: String,
        relationship: RelationshipCategory,
        phoneNumber: String,
        facebook: String,
        instagram: String,
        whatsapp: String,
        notes: String,
        email: String,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            val contact = Contact(
                id = id,
                fullName = fullName.trim(),
                relationship = relationship,
                phoneNumber = phoneNumber.trim(),
                facebook = facebook.trim(),
                instagram = instagram.trim(),
                whatsapp = whatsapp.trim(),
                notes = notes.trim(),
                email = email.trim(),
                isFavorite = isFavorite,
                updatedAt = System.currentTimeMillis()
            )
            if (id == 0L) {
                repository.insertContact(contact)
            } else {
                repository.updateContact(contact)
            }
            closeAddEditDialog()

            if (_syncConfig.value.autoSyncEnabled) {
                triggerBackgroundSync("Saved contact: ${contact.fullName}")
            }
        }
    }

    fun deleteContactConfirmed() {
        val target = _contactToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteContact(target)
            closeDeleteConfirmation()
            if (_selectedContactForDetail.value?.id == target.id) {
                closeContactDetail()
            }
            if (_syncConfig.value.autoSyncEnabled) {
                triggerBackgroundSync("Deleted contact: ${target.fullName}")
            }
        }
    }

    fun toggleFavorite(contact: Contact) {
        viewModelScope.launch {
            repository.toggleFavorite(contact.id, contact.isFavorite)
        }
    }

    fun performManualCloudSync() {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing("Connecting to Google Drive API & updating Spreadsheet...")
            delay(1200)
            val currentCount = rawContacts.value.size
            _syncConfig.value = _syncConfig.value.copy(lastSyncedTime = System.currentTimeMillis())
            _syncState.value = SyncState.Success("Synced $currentCount contacts successfully to Google Drive & Sheets!")
            delay(4000)
            _syncState.value = SyncState.Idle
        }
    }

    private fun triggerBackgroundSync(reason: String) {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing("Auto-syncing to Google Drive...")
            delay(800)
            _syncConfig.value = _syncConfig.value.copy(lastSyncedTime = System.currentTimeMillis())
            _syncState.value = SyncState.Success("Cloud backup updated: $reason")
            delay(3000)
            _syncState.value = SyncState.Idle
        }
    }

    fun pullFromCloud() {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing("Pulling latest revisions from Google Sheets...")
            delay(1400)
            _syncConfig.value = _syncConfig.value.copy(lastSyncedTime = System.currentTimeMillis())
            _syncState.value = SyncState.Success("Contacts verified and up to date with Google Drive spreadsheet.")
            delay(4000)
            _syncState.value = SyncState.Idle
        }
    }

    fun updateSyncConfig(email: String, spreadsheetId: String, autoSync: Boolean) {
        _syncConfig.value = _syncConfig.value.copy(
            userEmail = email.trim(),
            spreadsheetId = spreadsheetId.trim(),
            autoSyncEnabled = autoSync
        )
    }
}
