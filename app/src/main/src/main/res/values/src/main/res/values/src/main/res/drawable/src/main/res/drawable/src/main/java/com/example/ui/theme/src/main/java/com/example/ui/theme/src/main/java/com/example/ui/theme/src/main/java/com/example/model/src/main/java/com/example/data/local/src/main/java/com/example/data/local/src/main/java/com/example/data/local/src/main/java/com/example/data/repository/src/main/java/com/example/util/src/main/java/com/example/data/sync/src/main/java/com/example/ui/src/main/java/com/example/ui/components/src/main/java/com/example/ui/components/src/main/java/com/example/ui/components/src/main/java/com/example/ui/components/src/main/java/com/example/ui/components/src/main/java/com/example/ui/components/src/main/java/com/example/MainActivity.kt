package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.sync.SyncState
import com.example.model.Contact
import com.example.ui.ContactViewModel
import com.example.ui.components.AddEditContactDialog
import com.example.ui.components.ArchitectureGuideDialog
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.ContactCard
import com.example.ui.components.ContactDetailDialog
import com.example.ui.components.GoogleCloudSyncSheet
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TertiaryEmerald

class MainActivity : ComponentActivity() {

    private val viewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MyLinkApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLinkApp(viewModel: ContactViewModel) {
    val contacts by viewModel.filteredContacts.collectAsStateWithLifecycle()
    val rawContacts by viewModel.rawContacts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val onlyFavorites by viewModel.onlyFavorites.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val syncConfig by viewModel.syncConfig.collectAsStateWithLifecycle()

    val isAddEditOpen by viewModel.isAddEditOpen.collectAsStateWithLifecycle()
    val contactToEdit by viewModel.contactToEdit.collectAsStateWithLifecycle()
    val selectedDetailContact by viewModel.selectedContactForDetail.collectAsStateWithLifecycle()
    val contactToDelete by viewModel.contactToDelete.collectAsStateWithLifecycle()
    val isSyncSheetOpen by viewModel.isSyncSheetOpen.collectAsStateWithLifecycle()
    val isArchitectureGuideOpen by viewModel.isArchitectureGuideOpen.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MyLinkHeader(
                syncState = syncState,
                onSyncClick = { viewModel.openSyncSheet() },
                onGuideClick = { viewModel.openArchitectureGuide() }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddContactDialog() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Contact", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("fab_add_contact")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            CategoryChipRow(
                selectedCategory = selectedCategory,
                onlyFavorites = onlyFavorites,
                onCategorySelected = { viewModel.onCategorySelected(it) },
                onToggleFavorites = { viewModel.onToggleFavoritesFilter() }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (contacts.size == rawContacts.size) {
                        "Showing all ${contacts.size} contacts"
                    } else {
                        "Showing ${contacts.size} of ${rawContacts.size} contacts"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.openSyncSheet() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TertiaryEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Google Drive Synced",
                        fontSize = 11.sp,
                        color = TertiaryEmerald,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (contacts.isEmpty()) {
                EmptyStateView(
                    searchQuery = searchQuery,
                    isFavoriteOnly = onlyFavorites,
                    onAddContact = { viewModel.openAddContactDialog() },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("contact_list"),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = contacts,
                        key = { it.id }
                    ) { contact ->
                        ContactCard(
                            contact = contact,
                            onCardClick = { viewModel.openContactDetail(contact) },
                            onEditClick = { viewModel.openEditContactDialog(contact) },
                            onDeleteClick = { viewModel.openDeleteConfirmation(contact) },
                            onFavoriteToggle = { viewModel.toggleFavorite(contact) }
                        )
                    }
                }
            }
        }
    }

    if (isAddEditOpen) {
        AddEditContactDialog(
            contactToEdit = contactToEdit,
            onDismiss = { viewModel.closeAddEditDialog() },
            onSave = { id, fullName, relationship, phone, fb, ig, wa, notes, email, isFav ->
                viewModel.saveContact(id, fullName, relationship, phone, fb, ig, wa, notes, email, isFav)
            }
        )
    }

    selectedDetailContact?.let { contact ->
        ContactDetailDialog(
            contact = contact,
            onDismiss = { viewModel.closeContactDetail() },
            onEdit = {
                viewModel.openEditContactDialog(contact)
            },
            onFavoriteToggle = { viewModel.toggleFavorite(contact) }
        )
    }

    if (isSyncSheetOpen) {
        GoogleCloudSyncSheet(
            syncConfig = syncConfig,
            syncState = syncState,
            contacts = rawContacts,
            onDismiss = { viewModel.closeSyncSheet() },
            onManualSync = { viewModel.performManualCloudSync() },
            onPullFromCloud = { viewModel.pullFromCloud() },
            onUpdateConfig = { email, sheetId, autoSync ->
                viewModel.updateSyncConfig(email, sheetId, autoSync)
            },
            onOpenGuide = {
                viewModel.closeSyncSheet()
                viewModel.openArchitectureGuide()
            }
        )
    }

    if (isArchitectureGuideOpen) {
        ArchitectureGuideDialog(
            onDismiss = { viewModel.closeArchitectureGuide() }
        )
    }

    contactToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { viewModel.closeDeleteConfirmation() },
            title = { Text("Delete Contact?") },
            text = { Text("Are you sure you want to delete ${target.fullName}? This will also sync and remove the record from your Google Drive spreadsheet.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteContactConfirmed() },
                    modifier = Modifier.testTag("confirm_delete_btn")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeDeleteConfirmation() }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun MyLinkHeader(
    syncState: SyncState,
    onSyncClick: () -> Unit,
    onGuideClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "MY LINK",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "MY LINK",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Cloud Directory & Social Hub",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onSyncClick)
                        .testTag("header_sync_badge"),
                    shape = RoundedCornerShape(20.dp),
                    color = when (syncState) {
                        is SyncState.Syncing -> MaterialTheme.colorScheme.primaryContainer
                        is SyncState.Success -> TertiaryEmerald.copy(alpha = 0.15f)
                        is SyncState.Error -> MaterialTheme.colorScheme.errorContainer
                        SyncState.Idle -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        when (syncState) {
                            is SyncState.Syncing -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Syncing", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Cloud Synced",
                                    tint = TertiaryEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text("Drive Synced", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TertiaryEmerald)
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onGuideClick,
                    modifier = Modifier.size(36.dp).testTag("header_guide_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Architecture & Setup Guide",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search by name, phone, handle, or notes...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("search_input")
    )
}

@Composable
fun EmptyStateView(
    searchQuery: String,
    isFavoriteOnly: Boolean,
    onAddContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (searchQuery.isNotEmpty()) Icons.Default.PersonSearch else Icons.Default.Contacts,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when {
                    searchQuery.isNotEmpty() -> "No contacts found"
                    isFavoriteOnly -> "No favorite contacts yet"
                    else -> "Your directory is empty"
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Material
