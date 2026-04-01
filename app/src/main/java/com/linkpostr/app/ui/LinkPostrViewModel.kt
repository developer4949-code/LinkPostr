package com.linkpostr.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkpostr.app.data.LinkPostrRepository
import com.linkpostr.app.data.ThemePreferences
import com.linkpostr.app.domain.HashtagGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LinkPostrViewModel(
    private val repository: LinkPostrRepository,
    private val themePreferences: ThemePreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LinkPostrUiState(
            selectedAppTheme = themePreferences.getSelectedTheme(),
        ),
    )
    val uiState: StateFlow<LinkPostrUiState> = _uiState.asStateFlow()

    fun onTopicChange(value: String) {
        _uiState.update { it.copy(topic = value) }
    }

    fun onToneSelected(tone: ToneOption) {
        _uiState.update { it.copy(selectedTone = tone) }
    }

    fun onThemeSelected(theme: AppThemeOption) {
        themePreferences.saveSelectedTheme(theme)
        _uiState.update { it.copy(selectedAppTheme = theme) }
    }

    fun onIdeaSelected(idea: String) {
        _uiState.update { it.copy(topic = idea.removeSuffix(".")) }
    }

    fun generatePost() {
        val topic = uiState.value.topic.trim()
        if (topic.isBlank()) {
            showMessage("Add a topic first so I know what to write about.", isError = true)
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingLabel = "Generating your LinkedIn post...",
                    message = null,
                    isError = false,
                )
            }

            repository.generatePost(topic, uiState.value.selectedTone)
                .onSuccess { post ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadingLabel = "",
                            generatedPost = post,
                            hashtags = emptyList(),
                            message = "Fresh draft ready.",
                            isError = false,
                        )
                    }
                }
                .onFailure { error ->
                    showMessage(error.message ?: "Unable to generate the post right now.", isError = true)
                }
        }
    }

    fun improvePost() {
        val post = uiState.value.generatedPost.trim()
        if (post.isBlank()) {
            showMessage("Generate a post first, then I can polish it.", isError = true)
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingLabel = "Polishing the tone...",
                    message = null,
                    isError = false,
                )
            }

            repository.improvePost(post, uiState.value.selectedTone)
                .onSuccess { rewritten ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadingLabel = "",
                            generatedPost = rewritten,
                            message = "Tone polished for LinkedIn.",
                            isError = false,
                        )
                    }
                }
                .onFailure { error ->
                    showMessage(error.message ?: "Unable to improve the post right now.", isError = true)
                }
        }
    }

    fun generateHashtags() {
        val post = uiState.value.generatedPost.trim()
        if (post.isBlank()) {
            showMessage("Generate a post first, then I can build hashtags.", isError = true)
            return
        }

        val hashtags = HashtagGenerator.generate(post, uiState.value.selectedTone)
        _uiState.update {
            it.copy(
                hashtags = hashtags,
                message = "Hashtags refreshed.",
                isError = false,
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, isError = false) }
    }

    private fun showMessage(text: String, isError: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = false,
                loadingLabel = "",
                message = text,
                isError = isError,
            )
        }
    }
}
