/**
 * Texto.
 *
 * @en Text
 */
package dev.mnascimentos.aureole.catalogo.texto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSectionHeader
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleCatalogPreview
import dev.mnascimentos.aureole.fixtures.CatalogoColumn
import dev.mnascimentos.aureole.fixtures.CatalogoLabel

@Composable
private fun StyleSample(text: String, style: TextStyle, color: Color) {
    AureoleText(text = text, color = color, format = AureoleTextFormatParams(style = style))
}

/** Tipografia própria do DS. */
@Composable
private fun CustomTypographyCase() {
    val high = AureoleDS.colors.onSurfaceHigh
    val medium = AureoleDS.colors.onSurfaceMedium
    val type = AureoleDS.typography
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        StyleSample("Huge Title (Custom DS)", type.hugeTitle, high)
        StyleSample("Mid Title (Custom DS)", type.midTitle, high)
        StyleSample("Small Title (Custom DS)", type.smallTitle, high)
        StyleSample("Text (Custom DS)", type.text, medium)
        StyleSample("Small Text (Custom DS)", type.smallText, medium)
    }
}

/** Escala do Material 3 pelo `AureoleDS.typography`. */
@Composable
private fun MaterialTypographyCase() {
    val high = AureoleDS.colors.onSurfaceHigh
    val medium = AureoleDS.colors.onSurfaceMedium
    val type = AureoleDS.typography
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        StyleSample("Display Large", type.displayLarge, high)
        StyleSample("Display Medium", type.displayMedium, high)
        StyleSample("Display Small", type.displaySmall, high)
        StyleSample("Title Large", type.titleLarge, high)
        StyleSample("Title Medium", type.titleMedium, high)
        StyleSample("Title Small", type.titleSmall, high)
        StyleSample("Body Large", type.bodyLarge, medium)
        StyleSample("Body Medium", type.bodyMedium, medium)
        StyleSample("Body Small", type.bodySmall, medium)
        StyleSample("Label Large", type.labelLarge, medium)
        StyleSample("Label Medium", type.labelMedium, medium)
        StyleSample("Label Small", type.labelSmall, medium)
    }
}

/** Cabeçalhos de seção nas quatro cores em uso. */
@Composable
private fun SectionHeadersCase() {
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)) {
        AureoleSectionHeader(title = "A", color = AureoleDS.colors.onSurfaceMedium)
        AureoleSectionHeader(title = "Favorites", color = AureoleDS.colors.onSurfaceHigh)
        AureoleSectionHeader(title = "Recent Apps", color = AureoleDS.colors.onSurfaceLow)
        AureoleSectionHeader(title = "Alerts", color = AureoleDS.colors.error)
    }
}

/**
 * Texto.
 *
 * Grupo texto: `AureoleText` nas duas escalas e `AureoleSectionHeader`.
 *
 * @en Text
 */
@PreviewTest
@AureoleCatalogPreview
@Composable
private fun TextoScreenshotTest() = CatalogoColumn {
    val padded = Modifier.padding(horizontal = AureoleDS.dimens.medium)
    CatalogoLabel("AureoleText: DS")
    Column(modifier = padded) { CustomTypographyCase() }
    CatalogoLabel("AureoleText: Material")
    Column(modifier = padded) { MaterialTypographyCase() }
    CatalogoLabel("AureoleSectionHeader")
    Column(modifier = padded) { SectionHeadersCase() }
}
