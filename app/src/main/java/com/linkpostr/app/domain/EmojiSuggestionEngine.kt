package com.linkpostr.app.domain

import com.linkpostr.app.ui.ToneOption

object EmojiSuggestionEngine {

    fun suggest(sourceText: String, tone: ToneOption): List<String> {
        val normalized = sourceText.lowercase()
        val contextEmojis = buildList {
            if ("internship" in normalized || "career" in normalized || "job" in normalized) add("💼")
            if ("project" in normalized || "build" in normalized || "app" in normalized) add("📱")
            if ("learn" in normalized || "skill" in normalized || "growth" in normalized) add("📚")
            if ("exam" in normalized || "study" in normalized || "college" in normalized) add("🎓")
            if ("team" in normalized || "community" in normalized || "network" in normalized) add("🤝")
            if ("launch" in normalized || "result" in normalized || "milestone" in normalized) add("🚀")
        }

        val toneEmojis = when (tone) {
            ToneOption.Professional -> listOf("💼", "📈", "🎯", "✅")
            ToneOption.Casual -> listOf("✨", "🙌", "😊", "🤝")
            ToneOption.Motivational -> listOf("🔥", "🚀", "💪", "🌟")
            ToneOption.Thoughtful -> listOf("💡", "🧠", "🌱", "📚")
        }

        return (contextEmojis + toneEmojis)
            .distinct()
            .take(5)
    }

    fun append(post: String, emoji: String): String {
        if (post.isBlank()) {
            return post
        }

        if (post.contains(emoji)) {
            return post
        }

        return "$post $emoji"
    }
}
