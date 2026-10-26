package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.ui.test.junit4.createComposeRule
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FolderIconRegistryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRenderIconWithNullAndUnknownAndAliases() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderIconRegistry.RenderIcon(name = null)
                FolderIconRegistry.RenderIcon(name = "star")
                FolderIconRegistry.RenderIcon(name = "games")
                FolderIconRegistry.RenderIcon(name = "unknown icon")
                FolderIconRegistry.RenderIcon(name = "tools")
            }
        }
    }
}
