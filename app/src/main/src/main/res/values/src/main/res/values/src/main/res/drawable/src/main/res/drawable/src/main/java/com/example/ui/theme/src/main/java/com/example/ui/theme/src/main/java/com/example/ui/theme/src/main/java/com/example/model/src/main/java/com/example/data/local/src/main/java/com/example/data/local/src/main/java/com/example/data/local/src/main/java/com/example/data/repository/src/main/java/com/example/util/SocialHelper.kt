package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.model.Contact

object SocialHelper {

    fun sanitizePhoneNumber(phone: String): String {
        val clean = phone.replace(Regex("[^0-9+]"), "")
        return if (clean.startsWith("+")) clean.substring(1) else clean
    }

    fun getWhatsAppUrl(whatsappOrPhone: String): String {
        val clean = sanitizePhoneNumber(whatsappOrPhone)
        return if (clean.isNotBlank()) "https://wa.me/$clean" else ""
    }

    fun getFacebookUrl(facebookInput: String): String {
        val trimmed = facebookInput.trim()
        return when {
            trimmed.isBlank() -> ""
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            else -> "https://facebook.com/${trimmed.removePrefix("@")}"
        }
    }

    fun getInstagramUrl(instagramInput: String): String {
        val trimmed = instagramInput.trim()
        return when {
            trimmed.isBlank() -> ""
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            else -> "https://instagram.com/${trimmed.removePrefix("@")}"
        }
    }

    fun openWhatsApp(context: Context, whatsappOrPhone: String) {
        val url = getWhatsAppUrl(whatsappOrPhone)
        if (url.isBlank()) {
            Toast.makeText(context, "No WhatsApp number provided", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openFacebook(context: Context, facebookInput: String) {
        val url = getFacebookUrl(facebookInput)
        if (url.isBlank()) {
            Toast.makeText(context, "No Facebook profile provided", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open Facebook: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openInstagram(context: Context, instagramInput: String) {
        val url = getInstagramUrl(instagramInput)
        if (url.isBlank()) {
            Toast.makeText(context, "No Instagram profile provided", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open Instagram: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun dialPhone(context: Context, phoneNumber: String) {
        if (phoneNumber.isBlank()) {
            Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phoneNumber.trim()}")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied $label to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareContact(context: Context, contact: Contact) {
        val summary = buildString {
            appendLine("📇 ${contact.fullName} (${contact.relationship.label})")
            if (contact.phoneNumber.isNotBlank()) appendLine("📞 Phone: ${contact.phoneNumber}")
            if (contact.whatsapp.isNotBlank()) appendLine("💬 WhatsApp: https://wa.me/${sanitizePhoneNumber(contact.whatsapp)}")
            if (contact.facebook.isNotBlank()) appendLine("🌐 Facebook: ${getFacebookUrl(contact.facebook)}")
            if (contact.instagram.isNotBlank()) appendLine("📸 Instagram: ${getInstagramUrl(contact.instagram)}")
            if (contact.notes.isNotBlank()) appendLine("📝 Notes: ${contact.notes}")
            appendLine("— Shared via MY LINK Directory")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Contact: ${contact.fullName}")
            putExtra(Intent.EXTRA_TEXT, summary)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(intent, "Share Contact via"))
    }
}
