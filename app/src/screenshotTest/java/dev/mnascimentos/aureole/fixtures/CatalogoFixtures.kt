package dev.mnascimentos.aureole.fixtures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** Coluna de um grupo do catálogo (`@AureoleCatalogPreview`): as variantes uma embaixo da outra. */
@Composable
internal fun CatalogoColumn(content: @Composable ColumnScope.() -> Unit) {
    AureoleLauncherTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

/** Rótulo pequeno acima de cada variante, para achar a variante no PNG. */
@Composable
internal fun CatalogoLabel(text: String) {
    AureoleText(
        text = text,
        color = AureoleDS.colors.onSurfaceLow,
        format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall),
        modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xxSmall)
    )
}

/** Moldura de altura fixa para peças que ocupam a tela inteira (sheet, overlay, dialog, véu). */
@Composable
internal fun CatalogoFrame(height: Dp, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(height).clipToBounds()) { content() }
}

/** Variante com a mão do usuário: a peça lê o lado de `LocalHomeUiState`. */
@Composable
internal fun CatalogoHand(isLeftHanded: Boolean, content: @Composable () -> Unit) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) { content() }
}
