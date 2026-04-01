package com.linkpostr.app.domain

import com.linkpostr.app.ui.ToneOption

object HashtagGenerator {

    private val stopWords = setOf(
        "about", "after", "again", "also", "been", "being", "from", "have", "just",
        "into", "like", "more", "that", "this", "with", "your", "their", "there",
        "while", "where", "which", "will", "would", "could", "should", "really",
        "very", "what", "when", "than", "them", "they", "were", "then", "over",
    )

    fun generate(post: String, tone: ToneOption): List<String> {
        val keywords = Regex("[A-Za-z][A-Za-z0-9]+")
            .findAll(post.lowercase())
            .map { it.value }
            .filter { token -> token.length > 4 && token !in stopWords }
            .distinct()
            .take(4)
            .map(::toHashtag)
            .toMutableList()

        val toneTags = when (tone) {
            ToneOption.Professional -> listOf("#CareerGrowth", "#ProfessionalDevelopment")
            ToneOption.Casual -> listOf("#BuildInPublic", "#LearningJourney")
            ToneOption.Motivational -> listOf("#KeepGoing", "#GrowthMindset")
            ToneOption.Thoughtful -> listOf("#Leadership", "#CareerReflections")
        }

        val defaults = listOf("#LinkedInTips", "#Opportunity", "#PersonalBrand")

        return (keywords + toneTags + defaults)
            .distinct()
            .take(6)
    }

    private fun toHashtag(word: String): String {
        return "#" + word.split(Regex("[^a-z0-9]+"))
            .filter { it.isNotBlank() }
            .joinToString("") { token -> token.replaceFirstChar(Char::titlecase) }
    }
}

