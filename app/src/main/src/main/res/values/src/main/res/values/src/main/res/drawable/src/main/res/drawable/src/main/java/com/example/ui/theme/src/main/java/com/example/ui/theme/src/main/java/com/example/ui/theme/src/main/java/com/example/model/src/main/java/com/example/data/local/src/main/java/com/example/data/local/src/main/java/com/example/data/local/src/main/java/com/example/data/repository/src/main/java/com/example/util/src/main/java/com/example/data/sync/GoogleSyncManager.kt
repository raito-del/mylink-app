package com.example.data.sync

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.model.Contact
import com.example.model.RelationshipCategory
import com.example.util.SocialHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class SyncState {
    data object Idle : SyncState()
    data class Syncing(val message: String = "Syncing with Google Drive...") : SyncState()
    data class Success(val message: String, val timestamp: Long = System.currentTimeMillis()) : SyncState()
    data class Error(val errorMessage: String) : SyncState()
}

data class SyncConfig(
    val userEmail: String = "wazed7820@gmail.com",
    val isConnected: Boolean = true,
    val spreadsheetName: String = "MY LINK - Contacts Directory",
    val spreadsheetId: String = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
    val autoSyncEnabled: Boolean = true,
    val lastSyncedTime: Long = System.currentTimeMillis() - (1000 * 60 * 12)
)

object GoogleSyncManager {

    fun generateHumanReadableCsv(contacts: List<Contact>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return buildString {
            appendLine("ID,Full Name,Category,Phone Number,WhatsApp Direct Link,Facebook Profile,Instagram Profile,Email,Notes,Last Updated")
            for (c in contacts) {
                val waLink = SocialHelper.getWhatsAppUrl(if (c.whatsapp.isNotBlank()) c.whatsapp else c.phoneNumber)
                val fbLink = SocialHelper.getFacebookUrl(c.facebook)
                val igLink = SocialHelper.getInstagramUrl(c.instagram)
                val dateStr = dateFormat.format(Date(c.updatedAt))

                val row = listOf(
                    c.id.toString(),
                    escapeCsv(c.fullName),
                    escapeCsv(c.relationship.label),
                    escapeCsv(c.phoneNumber),
                    escapeCsv(waLink),
                    escapeCsv(fbLink),
                    escapeCsv(igLink),
                    escapeCsv(c.email),
                    escapeCsv(c.notes),
                    escapeCsv(dateStr)
                ).joinToString(",")
                appendLine(row)
            }
        }
    }

    private fun escapeCsv(value: String): String {
        var v = value.replace("\r", " ").replace("\n", " ")
        return if (v.contains(",") || v.contains("\"")) {
            "\"" + v.replace("\"", "\"\"") + "\""
        } else {
            v
        }
    }

    fun getSpreadsheetWebUrl(spreadsheetId: String): String {
        return "https://docs.google.com/spreadsheets/d/$spreadsheetId/edit"
    }

    fun openInGoogleSheetsWeb(context: Context, spreadsheetId: String) {
        val url = getSpreadsheetWebUrl(spreadsheetId)
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open browser: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportAndShareCsv(context: Context, contacts: List<Contact>) {
        val csv = generateHumanReadableCsv(contacts)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "MY_LINK_Contacts_Backup.csv")
            putExtra(Intent.EXTRA_TEXT, csv)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(intent, "Export / Share Contacts CSV"))
    }
}
