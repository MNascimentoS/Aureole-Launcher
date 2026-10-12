package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseNotesRepositoryTest {

    @Test
    fun getReleaseNotesReturnsNonEmptyListWithCurrentVersionFirst() {
        val notes = ReleaseNotesRepository.getReleaseNotes()
        assertTrue(notes.isNotEmpty())

        val current = notes.first()
        assertTrue(current.isCurrent)
        assertEquals("0.5.13", current.versionName)
    }

    @Test
    fun getLatestVersionNotesReturnsCurrentVersion() {
        val latest = ReleaseNotesRepository.getLatestVersionNotes()
        assertTrue(latest.isCurrent)
        assertEquals("0.5.13", latest.versionName)
        assertTrue(latest.sections.isNotEmpty())
    }

    @Test
    fun getReleaseNotesContainsMappedSectionsAndItems() {
        val notes = ReleaseNotesRepository.getReleaseNotes()
        val latest = notes.first { it.versionName == "0.3.13" }

        val novidadesSection = latest.sections.find { it.title == "Novidades & Recursos" }
        assertNotNull(novidadesSection)
        assertTrue(novidadesSection!!.items.any { it.title == "Menu de Personalização Rápida" })

        val featureItem = novidadesSection.items.first()
        assertEquals(ReleaseNoteType.FEATURE, featureItem.type)
        assertFalse(featureItem.description.isBlank())
    }
}
