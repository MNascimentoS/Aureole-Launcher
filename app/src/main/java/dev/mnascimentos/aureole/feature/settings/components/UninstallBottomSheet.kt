package dev.mnascimentos.aureole.feature.settings.components

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.extensions.tryUninstall

@Composable
internal fun UninstallBottomSheet(
    context: Context,
    onDismiss: () -> Unit
) {
    AureoleMenuSheet(
        title = stringResource(R.string.settings_advanced_uninstall_dialog_title),
        onDismiss = onDismiss
    ) { dismiss ->
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_contact_us),
                icon = { modifier, tint -> AureoleDS.icons.Smile(modifier = modifier, tint = tint) },
                onClick = {
                    dismiss()
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:aureole@mnascimentos.dev".toUri()
                    }
                    context.startActivity(intent)
                }
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_app_menu_uninstall),
                icon = { modifier, tint -> AureoleDS.icons.Delete(modifier = modifier, tint = tint) },
                isDestructive = true,
                onClick = {
                    dismiss()
                    tryUninstall(context)
                }
            )
        )
    }
}

@AureolePreview
@Composable
private fun UninstallBottomSheetPreview() {
    AureoleLauncherTheme {
        UninstallBottomSheet(context = LocalContext.current, onDismiss = {})
    }
}
