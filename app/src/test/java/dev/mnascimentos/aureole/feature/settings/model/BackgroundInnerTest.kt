package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private const val SAVED = 0xFF112233.toInt()
private const val PICKED = 0xFF445566.toInt()

/** The background picker inside the Appearance sheet: what it shows, what leaving it saves and its saved state. */
class BackgroundInnerTest {

    private val solid = BackgroundDraft(BackgroundType.SOLID_COLOR, PICKED)

    @Test
    fun `before any tap it shows what is saved, then the draft`() {
        assertEquals(
            BackgroundDraft(BackgroundType.SOLID_COLOR, SAVED),
            shownBackgroundDraft(null, isCustomImageSet = false, currentColor = SAVED)
        )
        assertEquals(
            BackgroundDraft(BackgroundType.CUSTOM_IMAGE, SAVED),
            shownBackgroundDraft(null, isCustomImageSet = true, currentColor = SAVED)
        )
        assertEquals(solid, shownBackgroundDraft(solid, isCustomImageSet = true, currentColor = SAVED))
    }

    @Test
    fun `back from the background picker or closing the sheet saves a changed color`() {
        assertEquals(PICKED, backgroundColorOnLeaving(SettingsInnerKind.BACKGROUND, solid, false, SAVED))
        assertEquals(PICKED, backgroundColorOnLeaving(null, solid, false, SAVED))
        // A solid color over a photo saves even when it is the same color.
        assertEquals(SAVED, backgroundColorOnLeaving(null, solid.copy(color = SAVED), true, SAVED))
    }

    @Test
    fun `nothing to save without a draft, with the same color, with the image type or from another picker`() {
        assertNull(backgroundColorOnLeaving(SettingsInnerKind.BACKGROUND, null, false, SAVED))
        assertNull(backgroundColorOnLeaving(null, solid.copy(color = SAVED), false, SAVED))
        assertNull(backgroundColorOnLeaving(null, solid.copy(type = BackgroundType.CUSTOM_IMAGE), false, SAVED))
        assertNull(backgroundColorOnLeaving(SettingsInnerKind.PALETTE, solid, false, SAVED))
        assertNull(backgroundColorOnLeaving(SettingsInnerKind.FONT, solid, false, SAVED))
    }

    @Test
    fun `only the background picker and closing the sheet clear the background draft`() {
        assertTrue(leavingSavesBackground(null))
        assertTrue(leavingSavesBackground(SettingsInnerKind.BACKGROUND))
        assertFalse(leavingSavesBackground(SettingsInnerKind.PALETTE))
        assertFalse(leavingSavesBackground(SettingsInnerKind.FONT))
    }

    @Test
    fun `drag down and tap outside bring the list back only from the background picker`() {
        assertTrue(areaDismissReturnsToList(SettingsInnerKind.BACKGROUND))
        assertFalse(areaDismissReturnsToList(SettingsInnerKind.PALETTE))
        assertFalse(areaDismissReturnsToList(SettingsInnerKind.FONT))
        assertFalse(areaDismissReturnsToList(null))
    }

    @Test
    fun `the draft survives being saved as text, and unknown text is no draft`() {
        BackgroundType.entries.forEach { type ->
            val draft = BackgroundDraft(type, PICKED)
            assertEquals(draft, decodeBackgroundDraft(encodeBackgroundDraft(draft)))
        }
        assertNull(encodeBackgroundDraft(null))
        listOf(null, "", "NOPE:1", "SOLID_COLOR", "SOLID_COLOR:x").forEach { assertNull(decodeBackgroundDraft(it)) }
    }

    @Test
    fun `the picker reads the saved background, the photo and the lock screen from the settings`() {
        var editorOpened = false
        var turnedOn = false
        val adjustments = WallpaperAdjustments(dim = 0.3f)
        val params = SettingsUiState(
            manualSeedColor = SAVED,
            isCustomWallpaperSet = true,
            customWallpaperPath = "/photo.jpg",
            wallpaperAdjustments = adjustments,
            isLeftHandedMode = true,
            applyToLockScreen = true,
            canSetWallpaper = false,
            isLockScreenNoticeSeen = true
        ).toBackgroundParams(SettingsScreenActions(onLockScreenTurnOn = { turnedOn = true })) { editorOpened = true }

        assertEquals(SAVED, params.currentColor)
        assertTrue(params.isCustomImageSet)
        assertEquals("/photo.jpg", params.customImagePath)
        assertEquals(adjustments, params.adjustments)
        assertTrue(params.isLeftHanded)
        assertTrue(params.lockScreen.checked)
        assertFalse(params.lockScreen.isAvailable)
        assertTrue(params.lockScreen.isNoticeSeen)
        params.onEditPhotoClick()
        params.lockScreen.onTurnOn()
        assertTrue(editorOpened)
        assertTrue(turnedOn)
    }
}
