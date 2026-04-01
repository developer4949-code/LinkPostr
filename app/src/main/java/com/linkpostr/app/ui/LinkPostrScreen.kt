package com.linkpostr.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Hub
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
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

    val finalShareText = remember(state.generatedPost, state.hashtags) {
        buildShareText(post = state.generatedPost, hashtags = state.hashtags)
    }

    Scaffold(
        containerColor = Color.Transparent,
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
                            text = "Post ideas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
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
                    }
                }

                item {
                    GlassCard {
                        Text(
                            text = "Create your draft",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = state.topic,
                            onValueChange = viewModel::onTopicChange,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            label = { Text("What should your LinkedIn post be about?") },
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = viewModel::generatePost,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Post")
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
                                        text = "Hugging Face is handling the writing-heavy part.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.generatedPost.isNotBlank()) {
                    item {
                        LinkedInPreviewCard(
                            post = state.generatedPost,
                            hashtagCount = state.hashtags.size,
                        )
                    }

                    item {
                        GlassCard {
                            Text(
                                text = "Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Button(
                                    onClick = viewModel::improvePost,
                                    enabled = !state.isLoading,
                                ) {
                                    Icon(Icons.Rounded.Edit, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Polish Tone")
                                }
                                OutlinedButton(
                                    onClick = viewModel::generateHashtags,
                                ) {
                                    Icon(Icons.Rounded.Tag, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generate Hashtags")
                                }
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(finalShareText))
                                        Toast.makeText(context, "Post copied to clipboard.", Toast.LENGTH_SHORT).show()
                                    },
                                ) {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Copy")
                                }
                                OutlinedButton(
                                    onClick = { shareText(context, finalShareText, linkedinOnly = true) },
                                ) {
                                    Icon(Icons.Rounded.Hub, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Share to LinkedIn")
                                }
                                TextButton(
                                    onClick = { shareText(context, finalShareText, linkedinOnly = false) },
                                ) {
                                    Icon(Icons.Rounded.Share, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Share Anywhere")
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
            text = "Switch between four looks, stay in light or dark mode, and keep writing in a cleaner, faster workspace.",
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
    GlassCard {
        Text(
            text = "Choose your theme",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Pick from four styles. Your selection is saved automatically for the next launch.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(14.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AppThemeOption.entries.forEach { theme ->
                ThemeOptionTile(
                    theme = theme,
                    selected = theme == selectedTheme,
                    onClick = { onThemeSelected(theme) },
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionTile(
    theme: AppThemeOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
    }
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = if (theme.isDark) 0.16f else 0.1f)
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
    }

    Surface(
        modifier = Modifier
            .width(162.dp)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val previewColors = themePreviewColors(theme)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                previewColors.forEach { previewColor ->
                    ThemePreviewDot(previewColor)
                }
            }
            Text(
                text = theme.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = theme.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
        if (linkedinOnly) {
            `package` = "com.linkedin.android"
        }
    }

    val safeIntent = if (linkedinOnly && shareIntent.resolveActivity(context.packageManager) == null) {
        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/feed/"))
    } else if (linkedinOnly) {
        shareIntent
    } else {
        Intent.createChooser(shareIntent, "Share your draft")
    }

    context.startActivity(safeIntent)
}
