package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorOptions
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.ComponentFontOptions
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val OLD_COLOR = 0xFF90CAF9.toInt()

@Composable
private fun FontCase(isBold: Boolean, isExpanded: Boolean = false) {
    AureoleLauncherTheme {
        ComponentFontSheet(
            selectedFontName = FONT_KEY_LAUNCHER,
            onFontSelected = {},
            onDismissRequest = {},
            options = ComponentFontOptions(
                title = "Clock font",
                subtitle = "Only for this clock",
                sectionLabel = "Clock font",
                sample = "16:16",
                isBold = isBold,
                onBoldChange = {},
                startsExpanded = isExpanded
            )
        )
    }
}

@Composable
private fun ColorCase(role: ComponentColorRole, selected: ThemeColorRef?) {
    AureoleLauncherTheme {
        ComponentColorSheet(
            options = ComponentColorOptions(
                role = role,
                title = if (role == ComponentColorRole.TEXT) "Text color" else "Card color",
                subtitle = "Only for this clock",
                selected = selected,
                themeColor = AureoleDS.colors.onSurfaceHigh,
                onSelect = {}
            ),
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ClockFontPickerBoldOffScreenshotTest() = FontCase(isBold = false)

@PreviewTest
@AureolePreview
@Composable
private fun ClockFontPickerBoldOnScreenshotTest() = FontCase(isBold = true)

@PreviewTest
@AureolePreview
@Composable
private fun ClockTextColorPickerScreenshotTest() = ColorCase(ComponentColorRole.TEXT, ThemeColorRef.Fixed(OLD_COLOR))

@PreviewTest
@AureolePreview
@Composable
private fun ClockCardColorPickerScreenshotTest() = ColorCase(ComponentColorRole.CARD, null)

/** RF-F2: the expanded font picker reaches the 0.9 ceiling of the screen. */
@PreviewTest
@AureolePreview
@Composable
private fun ClockFontPickerExpandedScreenshotTest() = FontCase(isBold = false, isExpanded = true)
