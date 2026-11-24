package dev.mnascimentos.aureole.catalogo.controles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.optiontile.AureoleOptionTile
import dev.mnascimentos.aureole.composable.reorder.ReorderableColumn
import dev.mnascimentos.aureole.composable.reorder.ReorderableRow
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.composable.search.AureoleSearchField
import dev.mnascimentos.aureole.core.designsystem.components.AureoleFloatingSearchBar
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSearchBarState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleCatalogPreview
import dev.mnascimentos.aureole.fixtures.CatalogoColumn
import dev.mnascimentos.aureole.fixtures.CatalogoHand
import dev.mnascimentos.aureole.fixtures.CatalogoLabel

private val sampleItems = listOf("Clock", "Weather", "Calendar")

private fun sampleParams() = ReorderableParams(
    items = sampleItems,
    key = { it },
    actions = ReorderableActions(onMove = { _, _ -> }, onRemove = {}),
)

/** Linhas de switch e de menu com ícone, na mão [isLeftHanded]. */
@Composable
private fun SwitchRowsCase(isLeftHanded: Boolean) = CatalogoHand(isLeftHanded) {
    Column(modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)) {
        AureoleSwitchRow(
            SwitchRowParams(
                title = "Show all apps on Home",
                description = "If disabled, shows only favorited apps",
                checked = true,
                onCheckedChange = {}
            )
        )
        AureoleSwitchRow(
            SwitchRowParams(
                title = "Show position indicator",
                checked = false,
                onCheckedChange = {},
                icon = { m, c -> AureoleDS.icons.Dots(modifier = m, tint = c) }
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = "Appearance",
                subtitle = "Cells, folders and background",
                onClick = {},
                icon = { m, c -> AureoleDS.icons.ColorFill(modifier = m, tint = c) },
                showChevron = true
            )
        )
    }
}

@Composable
private fun OptionTileCase(selected: Boolean) {
    AureoleOptionTile(
        sample = { AureoleText(text = "17:29", color = AureoleDS.colors.onSurfaceHigh) },
        label = "24 h",
        selected = selected,
        onClick = {},
        modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)
    )
}

@Composable
private fun SearchFieldCase(query: String) {
    AureoleSearchField(
        query = query,
        onQueryChange = {},
        placeholder = "Search app…",
        onSearch = {},
        modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)
    )
}

@Composable
private fun FloatingSearchBarCase(query: String, isExpanded: Boolean) {
    AureoleFloatingSearchBar(
        state = AureoleSearchBarState(
            query = query,
            onQueryChange = {},
            isExpanded = isExpanded,
            onExpandedChange = {}
        )
    )
}

@Composable
private fun SampleCard(text: String) {
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.sizeExtraLarge, AureoleDS.dimens.sizeLarge)
            .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusMedium)),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(text = text, color = AureoleDS.colors.onSurfaceHigh)
    }
}

@Composable
private fun SampleRow(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.itemHeightExtraLarge)
            .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .padding(horizontal = AureoleDS.dimens.medium),
        contentAlignment = Alignment.CenterStart
    ) {
        AureoleText(text = text, color = AureoleDS.colors.onSurfaceHigh)
    }
}

@Composable
private fun ReorderRowCase(isLeftHanded: Boolean) {
    LeftHandMirror(isLeftHanded = isLeftHanded) {
        ReorderableRow(
            params = sampleParams().copy(
                spacing = AureoleDS.dimens.xSmall,
                contentPadding = PaddingValues(AureoleDS.dimens.medium)
            ),
            trailingContent = { SampleCard(text = "+") }
        ) { item -> SampleCard(text = item) }
    }
}

@Composable
private fun ReorderColumnCase(isLeftHanded: Boolean) {
    LeftHandMirror(isLeftHanded = isLeftHanded) {
        ReorderableColumn(
            params = sampleParams().copy(
                spacing = AureoleDS.dimens.xSmall,
                contentPadding = PaddingValues(AureoleDS.dimens.medium)
            )
        ) { item -> SampleRow(text = item) }
    }
}

/**
 * Grupo controles: switch e linha de menu, segmentado, option tile, busca e barra flutuante, listas
 * reordenáveis; destro e canhoto onde o layout espelha. A `AureoleRadioSheet` aparece em `telas/ajustesopcao`.
 */
@PreviewTest
@AureoleCatalogPreview
@Composable
private fun ControlesScreenshotTest() = CatalogoColumn {
    val padded = Modifier.padding(horizontal = AureoleDS.dimens.medium)
    CatalogoLabel("AureoleSwitchRow: destro / canhoto")
    SwitchRowsCase(isLeftHanded = false)
    SwitchRowsCase(isLeftHanded = true)
    CatalogoLabel("AureoleSegmentedControl")
    AureoleSegmentedControl(
        options = listOf("None", "Light", "Medium", "Max"),
        selectedIndex = 2,
        onSelect = {},
        modifier = padded
    )
    CatalogoLabel("AureoleOptionTile: selecionado / não")
    Row(modifier = padded) {
        Box(modifier = Modifier.weight(1f)) { OptionTileCase(selected = true) }
        Box(modifier = Modifier.weight(1f)) { OptionTileCase(selected = false) }
    }
    CatalogoLabel("AureoleSearchField: vazio / preenchido")
    SearchFieldCase(query = "")
    SearchFieldCase(query = "cam")
    CatalogoLabel("AureoleFloatingSearchBar: fechada / aberta")
    FloatingSearchBarCase(query = "", isExpanded = false)
    FloatingSearchBarCase(query = "Search text", isExpanded = true)
    CatalogoLabel("ReorderableRow / ReorderableColumn: destro / canhoto")
    ReorderRowCase(isLeftHanded = false)
    ReorderRowCase(isLeftHanded = true)
    ReorderColumnCase(isLeftHanded = false)
    ReorderColumnCase(isLeftHanded = true)
}
