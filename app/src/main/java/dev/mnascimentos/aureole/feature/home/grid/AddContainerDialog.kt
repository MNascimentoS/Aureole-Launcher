package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleDialog
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.HorizontalContainer
import dev.mnascimentos.aureole.core.designsystem.icons.MenuIcon
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

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
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AddContainerOptionItem(
            title = "Relógio",
            icon = { m -> AureoleDS.icons.Add(m) },
            onClick = { onSelectType(LauncherItemType.CLOCK) }
        )
        AddContainerOptionItem(
            title = "Lista de Aplicativos",
            icon = { m -> AureoleDS.icons.HorizontalContainer(m) },
            onClick = { onSelectType(LauncherItemType.APPS_LIST) }
        )
        AddContainerOptionItem(
            title = "Container",
            icon = { m -> AureoleDS.icons.MenuIcon(m) },
            onClick = { onSelectType(LauncherItemType.SHORTCUTS_CONTAINER) }
        )
        AddContainerOptionItem(
            title = "Grupo de Widgets do Android",
            icon = { m -> AureoleDS.icons.Add(m) },
            onClick = { onSelectType(LauncherItemType.WIDGET_LIST) }
        )
        if (!isNested) {
            AddContainerOptionItem(
                title = "Container Scroll View",
                icon = { m -> AureoleDS.icons.VerticalContainer(m) },
                onClick = { onSelectType(LauncherItemType.SCROLL_VIEW) }
            )
        }
    }
}

@Composable
private fun AddContainerOptionItem(
    title: String,
    icon: @Composable (Modifier) -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.45f))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AureoleTheme.colors.surface.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            icon(Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        AureoleText(
            text = title,
            style = AureoleTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = AureoleTheme.colors.onSurfaceHigh
        )
    }
}

@AureolePreview
@Composable
fun AddContainerDialogPreview() {
    AureoleLauncherTheme {
        AddContainerDialog(
            onDismissRequest = {},
            onSelectType = {}
        )
    }
}
