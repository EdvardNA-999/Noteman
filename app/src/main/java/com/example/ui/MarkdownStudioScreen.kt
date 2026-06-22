package com.example.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.data.Note
import com.example.util.AppLanguage
import com.example.util.Locales
import com.example.util.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownStudioScreen(
    navController: NavHostController,
    viewModel: NoteViewModel,
    language: AppLanguage
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val notes by viewModel.allNotes.collectAsStateWithLifecycle()

    var textContent by remember {
        mutableStateOf(
            """# Markdown Playground
## Welcome to Noteman Markdown Studio!

You can write standard markdown format here, and it will be rendered live!

### Supported Syntaxes:
- Use **double asterisks** for **bold text**
- Use *single asterisk* for *italic text*
- Use `#` for title header 1
- Use `##` for sub-header 2
- Use `###` for sub-header 3
- Use `---` for a horizontal splitter divider

Enjoy compiling your ideas offline!
"""
        )
    }

    var selectedTab by remember { mutableStateOf(0) } // 0 = Write/Editor, 1 = Preview
    var showLoadNoteDialog by remember { mutableStateOf(false) }

    // Storage Access Framework Launcher to create and export .md file
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/markdown")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    stream.write(textContent.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(
                    context,
                    Locales.getString("save_success", language).ifEmpty { "Saved successfully!" },
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "${Locales.getString("save_error", language).ifEmpty { "Error saving file" }}: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Locales.getString("markdown_studio", language),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Load Note Button
                    IconButton(onClick = { showLoadNoteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = Locales.getString("load_note", language)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NotemanBottomNavigation(navController = navController, language = language)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Tab choice
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = Locales.getString("editor_tab", language),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = Locales.getString("preview_tab", language),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
            }

            // Main Editor workspace
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (selectedTab == 0) {
                    OutlinedTextField(
                        value = textContent,
                        onValueChange = { textContent = it },
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        placeholder = {
                            Text(text = Locales.getString("content", language))
                        }
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            item {
                                MarkdownText(
                                    text = textContent.ifEmpty { "*No content to preview*" },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Workspace Utilities Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Copy button
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(textContent))
                        Toast.makeText(
                            context,
                            Locales.getString("copied_toast", language),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Locales.getString("copy", language),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // Share button
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, textContent)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, Locales.getString("share", language)))
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Locales.getString("share", language),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Export to .md file button
            Button(
                onClick = {
                    saveFileLauncher.launch("noteman_export.md")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(vertical = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = "Export"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Locales.getString("export_md", language),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Load existing note selection Dialog
    if (showLoadNoteDialog) {
        Dialog(onDismissRequest = { showLoadNoteDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = Locales.getString("select_note_to_load", language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (notes.isEmpty()) {
                        Text(
                            text = Locales.getString("no_notes_to_load", language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .maxHeight(260.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(notes.size) { index ->
                                val note = notes[index]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            textContent = "# ${note.title}\n\n${note.content}"
                                            showLoadNoteDialog = false
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = "Note",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Column {
                                        Text(
                                            text = note.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (note.tags.isNotEmpty()) {
                                            Text(
                                                text = note.tags,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { showLoadNoteDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = Locales.getString("cancel", language))
                    }
                }
            }
        }
    }
}

// Extension to safely cap LazyColumn maxHeight inside dialogs
private fun Modifier.maxHeight(max: androidx.compose.ui.unit.Dp): Modifier = this.heightIn(max = max)
