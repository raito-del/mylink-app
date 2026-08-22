package com.example.model

enum class RelationshipCategory(val label: String, val emoji: String) {
    FAMILY("Family", "👨‍👩‍👧"),
    FRIEND("Friend", "🤝"),
    RELATIVE("Relative", "👥"),
    WORK("Work", "💼"),
    OTHER("Other", "📌");

    companion object {
        fun fromString(value: String): RelationshipCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

data class Contact(
    val id: Long = 0,
    val fullName: String,
    val relationship: RelationshipCategory,
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
)
