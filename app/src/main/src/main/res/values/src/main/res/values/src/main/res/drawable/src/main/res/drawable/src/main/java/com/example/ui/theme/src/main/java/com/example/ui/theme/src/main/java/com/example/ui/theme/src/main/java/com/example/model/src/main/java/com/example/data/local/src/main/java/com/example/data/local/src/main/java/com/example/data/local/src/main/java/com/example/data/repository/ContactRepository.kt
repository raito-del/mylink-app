package com.example.data.repository

import com.example.data.local.ContactDao
import com.example.data.local.ContactEntity
import com.example.model.Contact
import com.example.model.RelationshipCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ContactRepository(private val contactDao: ContactDao) {

    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getContactById(id: Long): Contact? {
        return contactDao.getContactById(id)?.toDomain()
    }

    suspend fun insertContact(contact: Contact): Long {
        return contactDao.insertContact(ContactEntity.fromDomain(contact))
    }

    suspend fun insertContacts(contacts: List<Contact>) {
        contactDao.insertContacts(contacts.map { ContactEntity.fromDomain(it) })
    }

    suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(ContactEntity.fromDomain(contact))
    }

    suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(ContactEntity.fromDomain(contact))
    }

    suspend fun deleteContactById(id: Long) {
        contactDao.deleteContactById(id)
    }

    suspend fun toggleFavorite(id: Long, currentStatus: Boolean) {
        contactDao.updateFavorite(id, !currentStatus)
    }

    suspend fun populateInitialDataIfEmpty() {
        if (contactDao.getContactCount() == 0) {
            val samples = listOf(
                Contact(
                    fullName = "Sarah Jenkins",
                    relationship = RelationshipCategory.FAMILY,
                    phoneNumber = "+1 415 555 0192",
                    facebook = "sarah.jenkins.family",
                    instagram = "sarah_j_adventures",
                    whatsapp = "+14155550192",
                    notes = "Sister. Birthdays on Oct 14. Lives in San Francisco.",
                    email = "sarah.j@example.com",
                    isFavorite = true
                ),
                Contact(
                    fullName = "David Chen",
                    relationship = RelationshipCategory.FRIEND,
                    phoneNumber = "+1 206 555 0148",
                    facebook = "david.chen.dev",
                    instagram = "dchen_visuals",
                    whatsapp = "+12065550148",
                    notes = "College friend. Photography & hiking enthusiast.",
                    email = "david.chen@example.com",
                    isFavorite = true
                ),
                Contact(
                    fullName = "Uncle Robert Vance",
                    relationship = RelationshipCategory.RELATIVE,
                    phoneNumber = "+1 312 555 0177",
                    facebook = "robert.vance.refrigeration",
                    instagram = "bob_vance_official",
                    whatsapp = "+13125550177",
                    notes = "Chicago family gathering organizer.",
                    email = "robert.vance@example.com",
                    isFavorite = false
                ),
                Contact(
                    fullName = "Elena Rostova",
                    relationship = RelationshipCategory.WORK,
                    phoneNumber = "+44 20 7946 0912",
                    facebook = "elena.rostova.design",
                    instagram = "elena_ui_craft",
                    whatsapp = "+442079460912",
                    notes = "Lead Product Designer at Acme Labs.",
                    email = "elena.rostova@acme.io",
                    isFavorite = false
                )
            )
            insertContacts(samples)
        }
    }
}
