package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseNotesRepositoryTest {

    @Test
    fun `when fetching release notes, should return non empty list with current version first`() {
        val notes = ReleaseNotesRepository.getReleaseNotes()
        assertTrue(notes.isNotEmpty())

        val current = notes.first()
        assertTrue(current.isCurrent)
        assertEquals("0.7.40", current.versionName)
    }

    @Test
    fun `when fetching latest version notes, should return current version details`() {
        val latest = ReleaseNotesRepository.getLatestVersionNotes()
        assertTrue(latest.isCurrent)
        assertEquals("0.7.40", latest.versionName)
        assertTrue(latest.sections.isNotEmpty())
    }

    @Test
    fun `when querying specific version, should contain mapped sections and items`() {
        val notes = ReleaseNotesRepository.getReleaseNotes()
        val version0313 = notes.find { it.versionName == "0.3.13" }

        assertNotNull(version0313)
        val novidadesSection = version0313!!.sections.find { it.title == "Novidades & Recursos" }
        assertNotNull(novidadesSection)
        assertTrue(novidadesSection!!.items.any { it.title == "Menu de Personalização Rápida" })

        val featureItem = novidadesSection.items.first()
        assertEquals(ReleaseNoteType.FEATURE, featureItem.type)
        assertFalse(featureItem.description.isBlank())
    }
}
