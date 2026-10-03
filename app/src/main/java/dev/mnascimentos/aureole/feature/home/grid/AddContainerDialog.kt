package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleDialog
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme

@Composable
fun AddContainerDialog(
    onDismissRequest: () -> Unit,
    onSelectType: (LauncherItemType) -> Unit,
    isNested: Boolean = false,
    modifier: Modifier = Modifier
) {
    val titleText = if (isNested) "Adicionar ao Scroll View" else "Adicionar Container"
    AureoleDialog(
        onDismissRequest = onDismissRequest,
        title = titleText,
        modifier = modifier
    ) {
        AddContainerOptionList(
            isNested = isNested,
            onSelectType = { type ->
                onSelectType(type)
                onDismissRequest()
            }
        )
    }
}

@Composable
private fun AddContainerOptionList(
    isNested: Boolean,
    onSelectType: (LauncherItemType) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        AddContainerOptionItem(
            title = "Relógio",
            icon = Icons.Default.Add,
            onClick = { onSelectType(LauncherItemType.CLOCK) }
        )
        AddContainerOptionItem(
            title = "Lista de Aplicativos",
            icon = Icons.AutoMirrored.Filled.List,
            onClick = { onSelectType(LauncherItemType.APPS_LIST) }
        )
        AddContainerOptionItem(
            title = "Barra de Atalhos (Side Panel)",
            icon = Icons.Default.Menu,
            onClick = { onSelectType(LauncherItemType.SHORTCUTS_CONTAINER) }
        )
        AddContainerOptionItem(
            title = "Widget Individual do Android",
            icon = Icons.Default.Settings,
            onClick = { onSelectType(LauncherItemType.SINGLE_APP_WIDGET) }
        )
        AddContainerOptionItem(
            title = "Lista de Widgets do Android",
            icon = Icons.Default.Add,
            onClick = { onSelectType(LauncherItemType.WIDGET_LIST) }
        )
        if (!isNested) {
            AddContainerOptionItem(
                title = "Container Scroll View",
                icon = Icons.AutoMirrored.Filled.List,
                onClick = { onSelectType(LauncherItemType.SCROLL_VIEW) }
            )
        }
    }
}

@Composable
private fun AddContainerOptionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.small))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(vertical = AureoleDS.dimens.small, horizontal = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        AureoleText(
            text = title,
            style = AureoleTheme.typography.bodyLarge,
            color = AureoleTheme.colors.onSurfaceHigh
        )
    }
}
