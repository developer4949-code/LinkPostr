package com.linkpostr.app.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.Hub
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linkpostr.app.ui.theme.ThemeSuccess

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LinkPostrScreen(
    viewModel: LinkPostrViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(state.message, state.isError) {
        val text = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(text)
        viewModel.clearMessage()
    }

    val finalShareText = remember(state.postDraft, state.hashtags) {
        buildShareText(post = state.postDraft, hashtags = state.hashtags)
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                        ),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 88.dp, y = (-18).dp)
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item {
                    HeaderBlock()
                }

                item {
                    ThemeSelectorCard(
                        selectedTheme = state.selectedAppTheme,
                        onThemeSelected = viewModel::onThemeSelected,
                    )
                }

                item {
                    GlassCard {
                        Text(
                            text = "Ready to post",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Write or paste the post you want to publish. AI can still help, but posting is the main workflow.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = state.postDraft,
                            onValueChange = viewModel::onPostDraftChange,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 7,
                            label = { Text("LinkedIn post draft") },
                            placeholder = { Text("Paste your final post here or write it directly, then tap Post to LinkedIn.") },
                            shape = RoundedCornerShape(18.dp),
                            textStyle = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = "${state.postDraft.length} characters",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Button(
                                onClick = { shareText(context, finalShareText, linkedinOnly = true) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = state.postDraft.isNotBlank() && !state.isLoading,
                            ) {
                                Icon(Icons.Rounded.Hub, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Post to LinkedIn")
                            }
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(finalShareText))
                                    Toast.makeText(context, "Post copied to clipboard.", Toast.LENGTH_SHORT).show()
                                },
                                enabled = state.postDraft.isNotBlank(),
                            ) {
                                Icon(Icons.Rounded.ContentCopy, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy Post")
                            }
                            TextButton(
                                onClick = { shareText(context, finalShareText, linkedinOnly = false) },
                                enabled = state.postDraft.isNotBlank(),
                            ) {
                                Icon(Icons.Rounded.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Anywhere")
                            }
                        }
                    }
                }

                item {
                    GlassCard {
                        Text(
                            text = "AI Assist",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Use AI only when you need help drafting, polishing, hashtags, or emojis before you post.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = state.topic,
                            onValueChange = viewModel::onTopicChange,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            label = { Text("Optional AI prompt") },
                            placeholder = { Text("Example: My internship experience building an Android app") },
                            shape = RoundedCornerShape(18.dp),
                            textStyle = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Tone",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            ToneOption.entries.forEach { tone ->
                                FilterChip(
                                    selected = tone == state.selectedTone,
                                    onClick = { viewModel.onToneSelected(tone) },
                                    label = { Text(tone.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                                    ),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Quick ideas",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            state.ideaSuggestions.forEach { suggestion ->
                                AssistChip(
                                    onClick = { viewModel.onIdeaSelected(suggestion) },
                                    label = {
                                        Text(
                                            text = suggestion,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.AutoAwesome,
                                            contentDescription = null,
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        labelColor = MaterialTheme.colorScheme.onSurface,
                                        leadingIconContentColor = MaterialTheme.colorScheme.primary,
                                    ),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Button(
                                onClick = viewModel::generatePost,
                                enabled = !state.isLoading,
                            ) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Draft")
                            }
                            OutlinedButton(
                                onClick = viewModel::improvePost,
                                enabled = state.postDraft.isNotBlank() && !state.isLoading,
                            ) {
                                Icon(Icons.Rounded.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Polish Draft")
                            }
                            OutlinedButton(
                                onClick = viewModel::generateHashtags,
                                enabled = state.postDraft.isNotBlank(),
                            ) {
                                Icon(Icons.Rounded.Tag, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hashtags")
                            }
                            OutlinedButton(
                                onClick = viewModel::suggestEmojis,
                                enabled = state.postDraft.isNotBlank() || state.topic.isNotBlank(),
                            ) {
                                Icon(Icons.Rounded.EmojiEmotions, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Emoji Touch")
                            }
                        }
                    }
                }

                item {
                    AnimatedVisibility(visible = state.isLoading) {
                        GlassCard {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Column {
                                    Text(
                                        text = state.loadingLabel.ifBlank { "Working on it..." },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = "Preparing your post helpers and LinkedIn-ready draft.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.postDraft.isNotBlank()) {
                    item {
                        LinkedInPreviewCard(
                            post = state.postDraft,
                            hashtagCount = state.hashtags.size,
                        )
                    }

                    if (state.emojiSuggestions.isNotEmpty()) {
                        item {
                            GlassCard {
                                Text(
                                    text = "Emoji Suggestions",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    state.emojiSuggestions.forEach { emoji ->
                                        AssistChip(
                                            onClick = { viewModel.addEmojiToPost(emoji) },
                                            label = { Text("$emoji Add") },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                                                labelColor = MaterialTheme.colorScheme.onSurface,
                                                leadingIconContentColor = MaterialTheme.colorScheme.secondary,
                                            ),
                                            leadingIcon = {
                                                Text(
                                                    text = emoji,
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (state.hashtags.isNotEmpty()) {
                        item {
                            GlassCard {
                                Text(
                                    text = "Hashtags",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    state.hashtags.forEach { tag ->
                                        Surface(
                                            shape = RoundedCornerShape(999.dp),
                                            color = ThemeSuccess.copy(alpha = 0.16f),
                                        ) {
                                            Text(
                                                text = tag,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                style = MaterialTheme.typography.labelLarge,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderBlock() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "LinkPostr",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Post to LinkedIn faster. Draft manually when you want, use AI only when it helps, and publish with fewer steps.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeSelectorCard(
    selectedTheme: AppThemeOption,
    onThemeSelected: (AppThemeOption) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard {
        Text(
            text = "Choose your theme",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Pick from four readable styles. Midnight Blue stays the default and your choice is saved automatically.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(14.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = selectedTheme.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = selectedTheme.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Choose theme",
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                AppThemeOption.entries.forEach { theme ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = theme.label,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (theme == selectedTheme) FontWeight.SemiBold else FontWeight.Medium,
                                )
                                Text(
                                    text = theme.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                        leadingIcon = {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                themePreviewColors(theme).forEach { previewColor ->
                                    ThemePreviewDot(previewColor)
                                }
                            }
                        },
                        onClick = {
                            expanded = false
                            onThemeSelected(theme)
                        },
                    )
                }
            }
        }
    }
}

private fun themePreviewColors(theme: AppThemeOption): List<Color> {
    return when (theme) {
        AppThemeOption.MidnightBlue -> listOf(Color(0xFF5BA6FF), Color(0xFF89C2FF), Color(0xFF09111D))
        AppThemeOption.ElectricNight -> listOf(Color(0xFF3ED2FF), Color(0xFF7A8DFF), Color(0xFF050A16))
        AppThemeOption.OceanLight -> listOf(Color(0xFF1E67D8), Color(0xFF4BA3F2), Color(0xFFEAF4FF))
        AppThemeOption.CloudLight -> listOf(Color(0xFF365ACF), Color(0xFF6C90F1), Color(0xFFF5F8FD))
    }
}

@Composable
private fun ThemePreviewDot(
    color: Color,
) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun LinkedInPreviewCard(
    post: String,
    hashtagCount: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.primary,
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "LP",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column {
                    Text(
                        text = "You",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "LinkPostr draft | Just now",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = post,
                style = MaterialTheme.typography.bodyLarge,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${post.length} characters",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "$hashtagCount hashtags ready",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun GlassCard(
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content,
        )
    }
}

private fun buildShareText(post: String, hashtags: List<String>): String {
    return buildString {
        append(post.trim())
        if (hashtags.isNotEmpty()) {
            append("\n\n")
            append(hashtags.joinToString(" "))
        }
    }.trim()
}

private fun shareText(
    context: Context,
    text: String,
    linkedinOnly: Boolean,
) {
    if (text.isBlank()) {
        Toast.makeText(context, "Nothing to share yet.", Toast.LENGTH_SHORT).show()
        return
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    val safeIntent = if (linkedinOnly) {
        buildLinkedInComposerIntent(context, shareIntent)
            ?: run {
                Toast.makeText(
                    context,
                    "LinkedIn composer was not found. Opening the share sheet instead.",
                    Toast.LENGTH_SHORT,
                ).show()
                Intent.createChooser(shareIntent, "Share your draft")
            }
    } else {
        Intent.createChooser(shareIntent, "Share your draft")
    }

    context.startActivity(safeIntent)
}

private fun buildLinkedInComposerIntent(
    context: Context,
    shareIntent: Intent,
): Intent? {
    val packageManager = context.packageManager
    val linkedInActivity = queryShareActivities(packageManager, shareIntent)
        .firstOrNull { it.activityInfo.packageName == "com.linkedin.android" }
        ?: return null

    return Intent(shareIntent).apply {
        setClassName(linkedInActivity.activityInfo.packageName, linkedInActivity.activityInfo.name)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}

private fun queryShareActivities(
    packageManager: PackageManager,
    shareIntent: Intent,
) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    packageManager.queryIntentActivities(
        shareIntent,
        PackageManager.ResolveInfoFlags.of(0),
    )
} else {
    @Suppress("DEPRECATION")
    packageManager.queryIntentActivities(shareIntent, 0)
}
