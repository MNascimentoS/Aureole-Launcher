package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class OpenSourceLicensesTest {

    @Test
    fun `every entry has an https project url`() {
        OpenSourceEntries.forEach { assertTrue(it.name, it.projectUrl.startsWith("https://")) }
    }

    @Test
    fun `every license url is https`() {
        LicenseType.entries.forEach { assertTrue(it.name, it.url.startsWith("https://")) }
    }

    @Test
    fun `every license with text points to an existing raw resource`() {
        val rawIds = R.raw::class.java.fields.map { it.getInt(null) }.toSet()
        LicenseType.entries.mapNotNull { it.text }.forEach { assertTrue(rawIds.contains(it)) }
    }

    @Test
    fun `license text files are not empty`() {
        listOf("license_apache_2_0.txt", "license_ofl_1_1.txt").forEach { name ->
            val file = File("src/main/res/raw/$name")
            assertTrue(name, file.isFile && file.readText().isNotBlank())
        }
    }

    @Test
    fun `play core terms has no text, only a link`() {
        assertNull(LicenseType.PLAY_CORE_TERMS.text)
        assertNotNull(LicenseType.APACHE_2_0.text)
        assertNotNull(LicenseType.OFL_1_1.text)
    }

    @Test
    fun `every font resource is covered by an entry in the fonts section`() {
        val prefixes = OpenSourceEntries
            .filter { it.section == OssSection.FONTS }
            .mapNotNull { it.fontResource }
        val fontNames = R.font::class.java.fields.map { it.name }
        assertTrue(fontNames.isNotEmpty())
        fontNames.forEach { font ->
            assertTrue("no license entry for font $font", prefixes.any { font.startsWith(it) })
        }
    }

    @Test
    fun `every font entry covers at least one font resource`() {
        val fontNames = R.font::class.java.fields.map { it.name }
        OpenSourceEntries.filter { it.section == OssSection.FONTS }.forEach { entry ->
            assertTrue(entry.name, fontNames.any { it.startsWith(entry.fontResource.orEmpty()) })
        }
    }

    @Test
    fun `entry names are unique`() {
        assertEquals(OpenSourceEntries.size, OpenSourceEntries.map { it.name }.toSet().size)
    }

    @Test
    fun `groupBySection keeps section order and drops empty sections`() {
        val grouped = groupBySection(OpenSourceEntries)
        assertEquals(listOf(OssSection.LIBRARIES, OssSection.ICONS, OssSection.FONTS), grouped.map { it.first })
        val onlyFonts = groupBySection(OpenSourceEntries.filter { it.section == OssSection.FONTS })
        assertEquals(listOf(OssSection.FONTS), onlyFonts.map { it.first })
        assertTrue(groupBySection(emptyList()).isEmpty())
    }
}
