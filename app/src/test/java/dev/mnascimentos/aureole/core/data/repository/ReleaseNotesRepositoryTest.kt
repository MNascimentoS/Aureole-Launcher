package dev.mnascimentos.aureole.core.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseNotesRepositoryTest {

    @Test
    fun `when fetching release notes, should return non empty list with exactly one current version first`() {
        val notes = ReleaseNotesRepository.getReleaseNotes()
        assertTrue("Release notes list should not be empty", notes.isNotEmpty())

        val current = notes.first()
        assertTrue("First release note in list should be marked as current", current.isCurrent)
        assertFalse("Current release note versionName should not be blank", current.versionName.isBlank())

        val currentCount = notes.count { it.isCurrent }
        assertEquals("Only one release note should be marked as current", 1, currentCount)
    }

    @Test
    fun `when fetching latest version notes, should return current version details with sections and items`() {
        val latest = ReleaseNotesRepository.getLatestVersionNotes()
        assertTrue("Latest release note should be marked as current", latest.isCurrent)
        assertFalse("Version name should not be blank", latest.versionName.isBlank())
        assertFalse("Release date should not be blank", latest.releaseDate.isBlank())
        assertTrue("Sections list should not be empty", latest.sections.isNotEmpty())

        val firstSection = latest.sections.first()
        assertFalse("Section title should not be blank", firstSection.title.isBlank())
        assertTrue("Section items should not be empty", firstSection.items.isNotEmpty())

        val firstItem = firstSection.items.first()
        assertFalse("Item title should not be blank", firstItem.title.isBlank())
        assertFalse("Item description should not be blank", firstItem.description.isBlank())
        assertNotNull("Item type should not be null", firstItem.type)
    }

    @Test
    fun `when querying all release notes, every version should have valid structure`() {
        val notes = ReleaseNotesRepository.getReleaseNotes()

        notes.forEach { version ->
            assertFalse(
                "Version name should not be blank for version ${version.versionName}",
                version.versionName.isBlank()
            )
            assertFalse(
                "Release date should not be blank for version ${version.versionName}",
                version.releaseDate.isBlank()
            )
            assertTrue("Sections should not be empty for version ${version.versionName}", version.sections.isNotEmpty())

            version.sections.forEach { section ->
                assertFalse("Section title should not be blank", section.title.isBlank())
                assertTrue("Section items should not be empty", section.items.isNotEmpty())

                section.items.forEach { item ->
                    assertFalse("Item title should not be blank in section ${section.title}", item.title.isBlank())
                    assertFalse(
                        "Item description should not be blank in section ${section.title}",
                        item.description.isBlank()
                    )
                }
            }
        }
    }
}
