package dev.mnascimentos.aureole.feature.home.components.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class OnboardingStringsTest {

    private fun keys(folder: String): Set<String> {
        val file = listOf("src/main/res/$folder/strings.xml", "app/src/main/res/$folder/strings.xml")
            .map(::File)
            .first { it.exists() }
        return Regex("""<string name="(home_onboarding_[a-z0-9_]+|settings_advanced_replay_onboarding)"""")
            .findAll(file.readText())
            .map { it.groupValues[1] }
            .toSet()
    }

    @Test
    fun `when an onboarding string exists in english, should exist in portuguese`() {
        val english = keys("values")

        assertTrue(english.size > 40)
        assertEquals(english, keys("values-pt-rBR"))
    }
}
