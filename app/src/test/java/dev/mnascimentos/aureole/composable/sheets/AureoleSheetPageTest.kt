package dev.mnascimentos.aureole.composable.sheets

import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AureoleSheetPageTest {

    private fun page(hasBackArrow: Boolean, onBack: () -> Unit = {}) = AureoleEditOverlayConfig().asSheetPage(
        title = "Group background",
        subtitle = "Only for this group",
        onBack = onBack,
        onClose = {},
        hasBackArrow = hasBackArrow
    )

    @Test
    fun `page has the back arrow by default`() {
        val config = AureoleEditOverlayConfig().asSheetPage(
            title = "Add apps",
            subtitle = null,
            onBack = {},
            onClose = {}
        )

        assertNotNull(config.headerLeadingAction)
    }

    @Test
    fun `page without the back arrow has no leading action but keeps the system back`() {
        var backs = 0
        val config = page(hasBackArrow = false, onBack = { backs++ })

        assertNull(config.headerLeadingAction)
        config.onBackPress?.invoke()
        assertEquals(1, backs)
    }

    @Test
    fun `page hides the tab bar and keeps its title`() {
        val config = page(hasBackArrow = false)

        assertEquals("Group background", config.title)
        assertEquals(false, config.hasTabBottomBar)
    }
}
