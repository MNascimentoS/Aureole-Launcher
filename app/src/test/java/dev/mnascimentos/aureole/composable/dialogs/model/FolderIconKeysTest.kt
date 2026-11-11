package dev.mnascimentos.aureole.composable.dialogs.model

import dev.mnascimentos.aureole.composable.dialogs.FolderIconRegistry
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleFolderIcons
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FolderIconKeysTest {

    private val oldKeys = listOf(
        "star", "home", "favorite", "person", "settings", "info", "lock", "place", "thumb_up", "warning",
        "notifications", "edit", "check", "add", "delete", "search", "share", "refresh", "menu", "more_vert",
        "location_on", "create", "date_range", "play", "cart", "build"
    )

    @Test
    fun `every old key resolves to something that can be drawn`() {
        oldKeys.forEach { old ->
            val key = resolveFolderIconKey(old)

            assertTrue(
                "$old -> $key",
                AureoleFolderIcons.resFor(key) != null || FolderIconRegistry.icons.containsKey(key)
            )
            assertTrue("$old fell back to the folder", key != FOLDER_ICON_FALLBACK)
        }
    }

    @Test
    fun `old keys that used to show a wrong icon now map to the right one`() {
        assertEquals("lock", resolveFolderIconKey("lock"))
        assertEquals("shopping_cart", resolveFolderIconKey("cart"))
        assertEquals("play_circle", resolveFolderIconKey("play"))
        assertEquals("share", resolveFolderIconKey("share"))
        assertEquals("warning", resolveFolderIconKey("warning"))
        assertEquals("location_on", resolveFolderIconKey("place"))
        assertEquals("calendar_month", resolveFolderIconKey("date_range"))
    }

    @Test
    fun `aliases, case and spaces are accepted`() {
        assertEquals("sports_esports", resolveFolderIconKey("Games"))
        assertEquals("account_balance", resolveFolderIconKey(" banco "))
        assertEquals("music_note", resolveFolderIconKey("Music Note"))
    }

    @Test
    fun `an unknown or null key falls back to the folder`() {
        assertEquals(FOLDER_ICON_FALLBACK, resolveFolderIconKey(null))
        assertEquals(FOLDER_ICON_FALLBACK, resolveFolderIconKey("nope"))
        assertNotNull(AureoleFolderIcons.resFor(FOLDER_ICON_FALLBACK))
    }

    @Test
    fun `resFor is null for a null or unknown key`() {
        assertNull(AureoleFolderIcons.resFor(null))
        assertNull(AureoleFolderIcons.resFor("nope"))
    }

    @Test
    fun `the registry draws exactly the keys that have no catalog drawable`() {
        assertEquals(LEGACY_FOLDER_ICON_KEYS, FolderIconRegistry.icons.keys)
        assertTrue(LEGACY_FOLDER_ICON_KEYS.all { AureoleFolderIcons.resFor(it) == null })
    }
}
