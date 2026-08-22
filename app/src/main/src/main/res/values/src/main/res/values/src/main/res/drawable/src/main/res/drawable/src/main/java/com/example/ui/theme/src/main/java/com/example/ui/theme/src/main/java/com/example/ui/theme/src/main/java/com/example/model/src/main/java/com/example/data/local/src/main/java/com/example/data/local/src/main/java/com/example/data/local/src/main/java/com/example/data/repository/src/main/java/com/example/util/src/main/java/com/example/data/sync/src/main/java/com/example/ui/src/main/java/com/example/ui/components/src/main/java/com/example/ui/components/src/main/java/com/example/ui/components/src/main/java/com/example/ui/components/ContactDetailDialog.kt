package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Contact
import com.example.util.SocialHelper

@Composable
fun ContactDetailDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val context = LocalContext.current
    val categoryColor = getCategoryColor(contact.relationship)
    val initials = contact.fullName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifBlank { "?" }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.testTag("detail_fav_btn")
                    ) {
                        Icon(
                            imageVector = if (contact.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Favorite",
                            tint = if (contact.isFavorite) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outline
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { SocialHelper.shareContact(context, contact) },
                            modifier = Modifier.testTag("detail_share_btn")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("detail_close_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(categoryColor, categoryColor.copy(alpha = 0.6f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = contact.fullName,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = categoryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${contact.relationship.emoji} ${contact.relationship.label}",
                        color = categoryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (contact.phoneNumber.isNotBlank()) {
                        DetailQuickAction(
                            icon = "📞",
                            label = "Call",
                            onClick = { SocialHelper.dialPhone(context, contact.phoneNumber) }
                        )
                    }

                    val wa = contact.whatsapp.ifBlank { contact.phoneNumber }
                    if (wa.isNotBlank()) {
                        DetailQuickAction(
                            icon = "💬",
                            label = "WhatsApp",
                            onClick = { SocialHelper.openWhatsApp(context, wa) }
                        )
                    }

                    if (contact.facebook.isNotBlank()) {
                        DetailQuickAction(
                            icon = "🌐",
                            label = "Facebook",
                            onClick = { SocialHelper.openFacebook(context, contact.facebook) }
                        )
                    }

                    if (contact.instagram.isNotBlank()) {
                        DetailQuickAction(
                            icon = "📸",
                            label = "Instagram",
                            onClick = { SocialHelper.openInstagram(context, contact.instagram) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (contact.phoneNumber.isNotBlank()) {
                        DetailItemRow(
                            label = "Phone Number",
                            value = contact.phoneNumber,
                            icon = Icons.Default.Phone,
                            onCopy = { SocialHelper.copyToClipboard(context, "Phone Number", contact.phoneNumber) }
                        )
                    }

                    if (contact.whatsapp.isNotBlank()) {
                        val waUrl = SocialHelper.getWhatsAppUrl(contact.whatsapp)
                        DetailItemRow(
                            label = "WhatsApp Deep Link",
                            value = waUrl,
                            icon = Icons.Default.Phone,
                            onCopy = { SocialHelper.copyToClipboard(context, "WhatsApp Link", waUrl) }
                        )
                    }

                    if (contact.facebook.isNotBlank()) {
                        val fbUrl = SocialHelper.getFacebookUrl(contact.facebook)
                        DetailItemRow(
                            label = "Facebook Profile",
                            value = fbUrl,
                            icon = null,
                            iconEmoji = "🌐",
                            onCopy = { SocialHelper.copyToClipboard(context, "Facebook Link", fbUrl) }
                        )
                    }

                    if (contact.instagram.isNotBlank()) {
                        val igUrl = SocialHelper.getInstagramUrl(contact.instagram)
                        DetailItemRow(
                            label = "Instagram Profile",
                            value = igUrl,
                            icon = null,
                            iconEmoji = "📸",
                            onCopy = { SocialHelper.copyToClipboard(context, "Instagram Link", igUrl) }
                        )
                    }

                    if (contact.email.isNotBlank()) {
                        DetailItemRow(
                            label = "Email Address",
                            value = contact.email,
                            icon = Icons.Default.Email,
                            onCopy = { SocialHelper.copyToClipboard(context, "Email", contact.email) }
                        )
                    }

                    if (contact.notes.isNotBlank()) {
                        DetailItemRow(
                            label = "Notes & Relationship Info",
                            value = contact.notes,
                            icon = null,
                            iconEmoji = "📝",
                            onCopy = { SocialHelper.copyToClipboard(context, "Notes", contact.notes) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onEdit()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("detail_edit_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Contact Details")
                }
            }
        }
    }
}

@Composable
fun DetailQuickAction(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = icon, fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DetailItemRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconEmoji: String? = null,
    onCopy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (iconEmoji != null) {
                    Text(text = iconEmoji, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy $label",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
