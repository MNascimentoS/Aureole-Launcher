package dev.mnascimentos.aureole.feature.settings

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.settings.model.PreferenceItemConfig
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val TAG = "SettingsGroupComponents2"
private const val ALPHA_DISABLED = 0.38f
private const val ALPHA_SEMI_TRANSPARENT = 0.5f

@Composable
fun AppearanceSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "General")
        PreferenceCard {
            PreferenceRowItem(
                config = PreferenceItemConfig(
                    title = "Set as Default Launcher",
                    subtitle = if (uiState.isDefaultLauncher) {
                        "Aureole Launcher is currently set as your default home app"
                    } else {
                        "Tap to select Aureole Launcher as your default home app"
                    },
                    leadingIcon = Icons.Default.Home
                ),
                trailingContent = {
                    if (uiState.isDefaultLauncher) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                onClick = actions.onSetDefaultLauncherClick
            )
        }
    }
}

@Composable
fun SystemSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Updates & System")
        PreferenceCard {
            PreferenceSwitchRow(
                config = PreferenceItemConfig(
                    title = "Buscar atualizações no Google Play",
                    subtitle = "Exibe um aviso no início caso exista uma nova versão do app disponível"
                ),
                checked = uiState.isInAppUpdateEnabled,
                onCheckedChange = { actions.onToggleInAppUpdate() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
            )

            PreferenceRowItem(
                config = PreferenceItemConfig(
                    title = "Apagar todos os dados",
                    subtitle = "Restaurar o aplicativo ao seu estado de instalação inicial",
                    leadingIcon = Icons.Default.Delete
                ),
                onClick = actions.onOpenFactoryResetDialog
            )
        }
    }
}

@Composable
fun AboutSettingsGroup() {
    val context = LocalContext.current
    val versionName = remember(context) {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName ?: "1.0"
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Package info not found", e)
            "1.0"
        }
    }

    Column {
        PreferenceCategoryHeader(title = "About")
        PreferenceCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Aureole Launcher",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version $versionName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
