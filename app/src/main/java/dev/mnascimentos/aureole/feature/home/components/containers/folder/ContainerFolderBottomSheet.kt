package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.RenderModalBottomSheetOrInspection
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.settings.SettingsActivity

@Composable
internal fun ContainerFolderBottomSheet(
    params: ContainerFolderBottomSheetParams,
    onDismiss: () -> Unit
) {
    val sheetContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.xLarge)
        ) {
            ContainerFolderBottomSheetHeader()

            Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = 6.dp)
            ) {
                ContainerFolderMenuItems(params)
            }
        }
    }

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss)
}

@Composable
private fun ContainerFolderMenuItems(params: ContainerFolderBottomSheetParams) {
    AureolePopupMenuItem(
        title = "Editar atalhos",
        customIcon = {
            AureoleDS.icons.Star(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        },
        onClick = params.onEditShortcuts
    )

    AureolePopupMenuItem(
        title = "Renomear",
        customIcon = {
            AureoleDS.icons.Edit(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        },
        onClick = params.onRename
    )

    AureolePopupMenuItem(
        title = "Ícone",
        customIcon = {
            AureoleDS.icons.Smile(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        },
        onClick = params.onIcon
    )

    AureolePopupMenuItem(
        title = "Remover pasta",
        customIcon = {
            AureoleDS.icons.Delete(
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        },
        isDestructive = true,
        onClick = params.onRemoveFolder
    )

    AureolePopupMenuItem(
        title = "Configurações do Container",
        customIcon = {
            AureoleDS.icons.Settings(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        },
        onClick = params.onOpenContainerSettings
    )
}

@Composable
private fun ContainerFolderBottomSheetHeader() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium)
    ) {
        AureoleText(
            text = "Folder settings",
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleDS.colors.onSurfaceHigh,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = AureoleDS.dimens.medium, bottom = AureoleDS.dimens.small)
        )

        IconButton(
            onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 6.dp)
                .size(26.dp)
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
