package com.linkpostr.app.ui

import com.linkpostr.app.domain.PostIdeaRepository

data class LinkPostrUiState(
    val topic: String = "",
    val selectedTone: ToneOption = ToneOption.Professional,
    val generatedPost: String = "",
    val hashtags: List<String> = emptyList(),
    val ideaSuggestions: List<String> = PostIdeaRepository.starterIdeas,
    val isLoading: Boolean = false,
    val loadingLabel: String = "",
    val message: String? = null,
    val isError: Boolean = false,
)

enum class ToneOption(
    val label: String,
    val promptLabel: String,
) {
    Professional("Professional", "professional and credible"),
    Casual("Casual", "casual and friendly"),
    Motivational("Motivational", "motivational and uplifting"),
    Thoughtful("Thoughtful", "reflective and thoughtful"),
}

