package com.linkpostr.app.ui

import com.linkpostr.app.domain.PostIdeaRepository

data class LinkPostrUiState(
    val topic: String = "",
    val selectedTone: ToneOption = ToneOption.Professional,
    val selectedAppTheme: AppThemeOption = AppThemeOption.MidnightBlue,
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

enum class AppThemeOption(
    val label: String,
    val description: String,
    val isDark: Boolean,
) {
    MidnightBlue("Midnight Blue", "Dark and slick navy", true),
    ElectricNight("Electric Night", "Bold dark blue glow", true),
    OceanLight("Ocean Light", "Fresh light blue canvas", false),
    CloudLight("Cloud Light", "Soft clean light theme", false),
}
