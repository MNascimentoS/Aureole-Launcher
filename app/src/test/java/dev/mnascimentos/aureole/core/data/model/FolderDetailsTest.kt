package dev.mnascimentos.aureole.core.data.model

import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderDetailsTest {

    private val folder = AppFolder(
        id = "f1",
        panelId = "p1",
        name = "Bank",
        icon = "lock",
        appPackageNames = listOf("a", "b"),
        displayAsGrid = true
    )

    @Test
    fun `details carry the name and the look of the folder`() {
        assertEquals(FolderDetails(name = "Bank", icon = "lock"), folder.details())
    }

    @Test
    fun `withDetails changes the look and keeps id, container and apps`() {
        val details = FolderDetails(
            name = "Money",
            icon = null,
            iconType = FolderIconType.EMOJI,
            emoji = "e",
            color = ThemeColorRef.Role(ThemeColorRole.ACCENT)
        )

        val updated = folder.withDetails(details)

        assertEquals(details, updated.details())
        val expected = folder.copy(
            name = "Money",
            icon = null,
            iconType = FolderIconType.EMOJI,
            emoji = "e",
            color = ThemeColorRef.Role(ThemeColorRole.ACCENT)
        )
        assertEquals(expected, updated)
    }

    @Test
    fun `the partial update writes the color as its JSON`() {
        val color = ThemeColorRef.Fixed(0xFF102030.toInt())
        val update = FolderDetailsUpdate.of("f1", FolderDetails(name = "Bank", color = color))

        assertEquals("f1", update.id)
        assertEquals(color.toJson(), update.color)
        assertEquals(FolderIconType.ICON, update.iconType)
    }
}
