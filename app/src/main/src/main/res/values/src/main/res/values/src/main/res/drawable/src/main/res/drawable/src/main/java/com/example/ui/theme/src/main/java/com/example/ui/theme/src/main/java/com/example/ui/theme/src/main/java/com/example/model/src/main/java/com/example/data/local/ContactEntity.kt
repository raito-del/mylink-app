package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Contact
import com.example.model.RelationshipCategory

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val relationship: String,
    val phoneNumber: String,
    val facebook: String = "",
    val instagram: String = "",
    val whatsapp: String = "",
    val notes: String = "",
    val email: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val googleSheetRowId: String? = null
) {
    fun toDomain(): Contact {
        return Contact(
            id = id,
            fullName = fullName,
            relationship = RelationshipCategory.fromString(relationship),
            phoneNumber = phoneNumber,
            facebook = facebook,
            instagram = instagram,
            whatsapp = whatsapp,
            notes = notes,
            email = email,
            isFavorite = isFavorite,
            createdAt = createdAt,
            updatedAt = updatedAt,
            googleSheetRowId = googleSheetRowId
        )
    }

    companion object {
        fun fromDomain(contact: Contact): ContactEntity {
            return ContactEntity(
                id = contact.id,
                fullName = contact.fullName,
                relationship = contact.relationship.name,
                phoneNumber = contact.phoneNumber,
                facebook = contact.facebook,
                instagram = contact.instagram,
                whatsapp = contact.whatsapp,
                notes = contact.notes,
                email = contact.email,
                isFavorite = contact.isFavorite,
                createdAt = contact.createdAt,
                updatedAt = contact.updatedAt,
                googleSheetRowId = contact.googleSheetRowId
            )
        }
    }
}
