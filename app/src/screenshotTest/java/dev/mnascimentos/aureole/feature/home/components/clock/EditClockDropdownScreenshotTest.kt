package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

class EditClockDropdownScreenshotTest {

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockDropdownDefault() {
        AureoleLauncherTheme {
            EditClockDropdown(
                label = "Preview Label",
                selectedKey = "key1",
                options = listOf(
                    "key1" to "Option 1",
                    "key2" to "Option 2"
                ),
                onOptionSelected = {},
                initialExpanded = false
            )
        }
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockDropdownExpanded() {
        AureoleLauncherTheme {
            EditClockDropdown(
                label = "Preview Label",
                selectedKey = "key1",
                options = listOf(
                    "key1" to "Option 1",
                    "key2" to "Option 2"
                ),
                onOptionSelected = {},
                initialExpanded = true
            )
        }
    }
}
