package dev.mnascimentos.aureole.feature.settings

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.settings.model.PreferenceItemConfig
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val TAG = "SettingsGroupComponents2"
private const val ALPHA_DISABLED = 0.38f
private const val ALPHA_SEMI_TRANSPARENT = 0.5f
private const val ALPHA_FULL = 1.0f

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
fun WallpaperSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Personalização & Papel de Parede")
        PreferenceCard {
            WallpaperSettingsContent(uiState = uiState, actions = actions)
        }
    }
}

@Composable
private fun WallpaperSettingsContent(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Usar cores do papel de parede",
                subtitle = "Extrair paleta de cores dinâmicas do papel de parede do sistema",
                leadingIcon = Icons.Default.Edit
            ),
            checked = uiState.isDynamicWallpaperEnabled,
            onCheckedChange = { actions.onToggleDynamicWallpaper() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        ThemeColorPreferenceRow(
            isDynamicWallpaperEnabled = uiState.isDynamicWallpaperEnabled,
            manualSeedColor = uiState.manualSeedColor,
            onOpenColorPickerDialog = actions.onOpenColorPickerDialog
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        WallpaperImagePreferenceRows(
            isCustomWallpaperSet = uiState.isCustomWallpaperSet,
            onChangeWallpaperClick = actions.onChangeWallpaperClick,
            onRestoreDefaultWallpaperClick = actions.onRestoreDefaultWallpaperClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Ícones Temáticos",
                subtitle = "Usar cor primária do tema nos ícones de aplicativos",
                leadingIcon = Icons.Default.Star
            ),
            checked = uiState.isThemedAppIconsEnabled,
            onCheckedChange = { actions.onToggleThemedAppIcons() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        val clockSubtitle = "Exibe o container com efeito de desfoque (blur) " +
            "para o relógio quando o desfoque estiver ativo"
        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Fundo do Relógio",
                subtitle = clockSubtitle,
                leadingIcon = Icons.Default.Edit
            ),
            checked = uiState.isClockBackgroundEnabled,
            onCheckedChange = { actions.onToggleClockBackground() }
        )
    }
}

@Composable
private fun ThemeColorPreferenceRow(
    isDynamicWallpaperEnabled: Boolean,
    manualSeedColor: Int,
    onOpenColorPickerDialog: () -> Unit
) {
    PreferenceRowItem(
        config = PreferenceItemConfig(
            title = "Cor Primária do Tema",
            subtitle = if (isDynamicWallpaperEnabled) {
                "Desabilitado quando as cores do papel de parede estão ativas"
            } else {
                "Toque para escolher uma cor primária personalizada"
            },
            enabled = !isDynamicWallpaperEnabled
        ),
        trailingContent = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (!isDynamicWallpaperEnabled) {
                            Color(manualSeedColor)
                        } else {
                            Color(manualSeedColor).copy(alpha = ALPHA_DISABLED)
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(
                            alpha = if (!isDynamicWallpaperEnabled) ALPHA_FULL else ALPHA_DISABLED
                        ),
                        shape = CircleShape
                    )
            )
        },
        onClick = onOpenColorPickerDialog
    )
}

@Composable
private fun WallpaperImagePreferenceRows(
    isCustomWallpaperSet: Boolean,
    onChangeWallpaperClick: () -> Unit,
    onRestoreDefaultWallpaperClick: () -> Unit
) {
    Column {
        PreferenceRowItem(
            config = PreferenceItemConfig(
                title = "Alterar Papel de Parede",
                subtitle = "Escolher uma imagem do seu dispositivo",
                leadingIcon = Icons.Default.Edit
            ),
            onClick = onChangeWallpaperClick
        )

        if (isCustomWallpaperSet) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
            )

            PreferenceRowItem(
                config = PreferenceItemConfig(
                    title = "Restaurar Padrão",
                    subtitle = "Remover imagem customizada e usar o papel de parede padrão",
                    leadingIcon = Icons.Default.Delete
                ),
                onClick = onRestoreDefaultWallpaperClick
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
