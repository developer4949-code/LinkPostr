package com.linkpostr.app.domain

import com.linkpostr.app.ui.ToneOption
import org.junit.Assert.assertTrue
import org.junit.Test

class HashtagGeneratorTest {

    @Test
    fun `generate returns tone tags and keyword tags`() {
        val tags = HashtagGenerator.generate(
            post = "Completed my Android internship and learned product thinking while building networking features.",
            tone = ToneOption.Professional,
        )

        assertTrue(tags.contains("#CareerGrowth"))
        assertTrue(tags.any { it.startsWith("#Android") || it.startsWith("#Internship") })
    }
}
