package dev.mnascimentos.aureole.telas.relogioseletor

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.sheets.ComponentColorSheet
import dev.mnascimentos.aureole.composable.sheets.ComponentFontSheet
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorOptions
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.ComponentFontOptions
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.fixtures.OrientedCase

/** Seletor de fonte do relógio por cima da sheet; negrito ligado ou desligado é o mesmo layout (D-18). */
@Composable
private fun FontCase(isExpanded: Boolean) = OrientedCase {
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
                isBold = false,
                onBoldChange = {},
                startsExpanded = isExpanded
            )
        )
    }
}

@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = FontCase(isExpanded = false)

/** RF-F2: o seletor de fonte expandido chega ao teto de 0,9 da tela. */
@PreviewTest
@AureolePreview
@Composable
private fun ExpandidoScreenshotTest() = FontCase(isExpanded = true)

/** Seletor de cor do cartão, com o cartão "Nenhuma" escolhido; o de cor do texto tem o mesmo layout (D-18). */
@PreviewTest
@AureolePreview
@Composable
private fun CorScreenshotTest() = AureoleLauncherTheme {
    ComponentColorSheet(
        options = ComponentColorOptions(
            role = ComponentColorRole.CARD,
            title = "Card color",
            subtitle = "Only for this clock",
            selected = null,
            themeColor = AureoleDS.colors.onSurfaceHigh,
            onSelect = {}
        ),
        onDismissRequest = {}
    )
}
