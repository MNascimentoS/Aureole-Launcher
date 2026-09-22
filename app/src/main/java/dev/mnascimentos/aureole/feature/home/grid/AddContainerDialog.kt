package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

@Composable
fun AddContainerDialog(
    onDismissRequest: () -> Unit,
    onSelectType: (LauncherItemType) -> Unit,
    isNested: Boolean = false,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = if (isNested) {
                    "Adicionar ao Scroll View"
                } else {
                    "Adicionar Container"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                AddContainerOptionItem(
                    title = "Relógio",
                    icon = Icons.Default.Add,
                    onClick = {
                        onSelectType(LauncherItemType.CLOCK)
                        onDismissRequest()
                    }
                )
                AddContainerOptionItem(
                    title = "Lista de Aplicativos",
                    icon = Icons.Default.List,
                    onClick = {
                        onSelectType(LauncherItemType.APPS_LIST)
                        onDismissRequest()
                    }
                )
                AddContainerOptionItem(
                    title = "Barra de Atalhos (Side Panel)",
                    icon = Icons.Default.Menu,
                    onClick = {
                        onSelectType(LauncherItemType.SHORTCUTS_SIDE_PANEL)
                        onDismissRequest()
                    }
                )
                AddContainerOptionItem(
                    title = "Widget Individual do Android",
                    icon = Icons.Default.Settings,
                    onClick = {
                        onSelectType(LauncherItemType.SINGLE_APP_WIDGET)
                        onDismissRequest()
                    }
                )
                AddContainerOptionItem(
                    title = "Lista de Widgets do Android",
                    icon = Icons.Default.Add,
                    onClick = {
                        onSelectType(LauncherItemType.WIDGET_LIST)
                        onDismissRequest()
                    }
                )
                if (!isNested) {
                    AddContainerOptionItem(
                        title = "Container Scroll View",
                        icon = Icons.Default.List,
                        onClick = {
                            onSelectType(LauncherItemType.SCROLL_VIEW)
                            onDismissRequest()
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancelar")
            }
        },
        modifier = modifier
    )
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
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
