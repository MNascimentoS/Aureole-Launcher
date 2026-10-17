package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Design system header component for list sections (e.g. alphabet headers).
 *
 * @param title The title of the section.
 * @param modifier The modifier to be applied to the layout.
 * @param color The color of the text.
 */
@Composable
fun AureoleSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = AureoleDS.colors.onSurfaceMedium
) {
    AureoleText(
        text = title,
        style = AureoleDS.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}

@AureolePreview
@Composable
private fun AureoleSectionHeaderPreview() {
    AureoleLauncherTheme {
        AureoleSectionHeader(
            title = "A"
        )
    }
}
