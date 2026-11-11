package dev.mnascimentos.aureole.composable.sheets.model

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseNotesModelsTest {

    private fun version(name: String, isCurrent: Boolean = false) =
        ReleaseNoteVersion(versionName = name, releaseDate = "", isCurrent = isCurrent, sections = emptyList())

    private val versions = listOf(version("0.9.18"), version("0.9.13", isCurrent = true), version("0.9.12"))

    @Test
    fun `opens on the current version`() {
        assertEquals("0.9.13", initialReleaseVersionName(versions))
    }

    @Test
    fun `opens on the newest version when none is current`() {
        assertEquals("0.9.18", initialReleaseVersionName(versions.map { it.copy(isCurrent = false) }))
    }

    @Test
    fun `has no initial version without versions`() {
        assertNull(initialReleaseVersionName(emptyList()))
        assertNull(resolveReleaseVersion(emptyList(), null))
    }

    @Test
    fun `resolves the chosen version by name`() {
        assertEquals("0.9.12", resolveReleaseVersion(versions, "0.9.12")?.versionName)
    }

    @Test
    fun `falls back to the initial version when the chosen name is gone or null`() {
        assertEquals("0.9.13", resolveReleaseVersion(versions, "9.9.9")?.versionName)
        assertEquals("0.9.13", resolveReleaseVersion(versions, null)?.versionName)
    }

    @Test
    fun `section titles show only with several sections`() {
        val section = ReleaseNoteSection(title = "A", items = emptyList())
        assertFalse(showsSectionTitles(emptyList()))
        assertFalse(showsSectionTitles(listOf(section)))
        assertTrue(showsSectionTitles(listOf(section, section)))
    }
}
