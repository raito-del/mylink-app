package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchitectureGuideDialog(
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cloud & OAuth", "Data Structure", "Cross-Platform", "Deploy Guide")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MY LINK Developer & Cloud Guide",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Google Drive API, OAuth 2.0 & Cross-Platform Specs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_guide_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 12.sp, maxLines = 1) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        0 -> CloudAndOAuthTab()
                        1 -> DataStructureTab()
                        2 -> CrossPlatformTab()
                        3 -> DeploymentTab()
                    }
                }
            }
        }
    }
}

@Composable
fun CloudAndOAuthTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GuideCard(
            title = "1. Google Cloud Console Project Setup",
            icon = Icons.Default.Security
        ) {
            Text(
                "1. Go to console.cloud.google.com and create a new project named 'MY LINK Directory'.\n" +
                "2. Navigate to 'APIs & Services' > 'Library' and enable:\n" +
                "   • Google Sheets API\n" +
                "   • Google Drive API\n" +
                "3. Configure OAuth Consent Screen and add scopes:\n" +
                "   • https://www.googleapis.com/auth/spreadsheets\n" +
                "   • https://www.googleapis.com/auth/drive.file"
            )
        }

        GuideCard(
            title = "2. OAuth 2.0 Credentials & Scopes",
            icon = Icons.Default.Code
        ) {
            Text(
                "Create OAuth Client IDs for each target platform:\n" +
                "• Android: Package name com.aistudio.mylink.directory + SHA-1 fingerprint.\n" +
                "• iOS: Bundle Identifier com.aistudio.mylink + Reversed Client ID scheme.\n" +
                "• Desktop (Windows & macOS): Web Client ID with authorized redirect URI."
            )
        }
    }
}

@Composable
fun DataStructureTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GuideCard(
            title = "Google Sheets / Drive Human-Readable Schema",
            icon = Icons.Default.Storage
        ) {
            Text(
                "The spreadsheet is created automatically in user's root Drive with sheet name 'MY_LINK_CONTACTS':"
            )
            Spacer(modifier = Modifier.height(6.dp))
            CodeSnippet(
                "Column A: Contact ID (e.g. 101)\n" +
                "Column B: Full Name (e.g. Sarah Jenkins)\n" +
                "Column C: Category (Family / Friend / Relative / Work)\n" +
                "Column D: Phone Number (e.g. +1 415 555 0192)\n" +
                "Column E: WhatsApp Link (https://wa.me/14155550192)\n" +
                "Column F: Facebook URL (https://facebook.com/sarah)\n" +
                "Column G: Instagram URL (https://instagram.com/sarah)\n" +
                "Column H: Email (sarah@example.com)\n" +
                "Column I: Notes & Reminders\n" +
                "Column J: Last Updated Timestamp (ISO-8601)"
            )
        }

        GuideCard(
            title = "Universal Web Browser Access",
            icon = Icons.Default.Computer
        ) {
            Text(
                "Because data is saved in standard Google Sheets format, any user who loses their phone or laptop can simply navigate to https://sheets.google.com on any browser to view, search, and edit their contacts without reinstalling the app."
            )
        }
    }
}

@Composable
fun CrossPlatformTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GuideCard(
            title = "Cross-Platform Architecture (Flutter / Compose Multiplatform)",
            icon = Icons.Default.Computer
        ) {
            Text(
                "For unified deployment across iOS, Windows, and macOS:\n" +
                "• State Engine: MVVM / Bloc / Riverpod with reactive streams.\n" +
                "• Local Storage: SQLite / Room (Android) & Drift / Isar / Hive for desktop.\n" +
                "• Cloud Engine: googleapis package with google_sign_in authentication.\n" +
                "• Deep-Linking: url_launcher supporting https://wa.me/, tel:, and https:// schemes across all operating systems."
            )
        }
    }
}

@Composable
fun DeploymentTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GuideCard(
            title = "Build & Run Instructions for iOS, macOS, Windows",
            icon = Icons.Default.PhoneIphone
        ) {
            Text("1. iOS (iPhone / iPad):", fontWeight = FontWeight.Bold)
            CodeSnippet("flutter build ipa --release\n# Or open ios/Runner.xcworkspace in Xcode")

            Spacer(modifier = Modifier.height(8.dp))
            Text("2. macOS Desktop:", fontWeight = FontWeight.Bold)
            CodeSnippet("flutter build macos --release\n# Generates signed .dmg / .app bundle")

            Spacer(modifier = Modifier.height(8.dp))
            Text("3. Windows Desktop (MSIX / EXE):", fontWeight = FontWeight.Bold)
            CodeSnippet("flutter build windows --release\n# Produces stand-alone portable Windows EXE")
        }
    }
}

@Composable
fun GuideCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun CodeSnippet(code: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = code,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(10.dp),
            lineHeight = 16.sp
        )
    }
}
