@file:Suppress("MagicNumber", "MaxLineLength")

package dev.mnascimentos.aureole.feature.settings.screens

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.SettingsMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Advanced
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val BOTTOM_SPACER_PERCENT = 0.1f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRootScreen(
    uiState: SettingsUiState,
    @Suppress("UNUSED_PARAMETER") actions: SettingsScreenActions,
    onNavigateToBehavior: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToAdvanced: () -> Unit,
) {
    val context = LocalContext.current
    val versionName = remember(context) { getVersionName(context) }

    Scaffold(
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val topSpacerHeight = (maxHeight * (uiState.headerOffsetPercent / 100f)).coerceAtLeast(16.dp)
            val bottomSpacerHeight = (maxHeight * BOTTOM_SPACER_PERCENT).coerceAtLeast(24.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AureoleDS.spacings.xLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(topSpacerHeight))

                SettingsHeader(versionName = versionName)

                Spacer(modifier = Modifier.height(36.dp))

                SettingsNavigationList(
                    onNavigateToBehavior = onNavigateToBehavior,
                    onNavigateToAppearance = onNavigateToAppearance,
                    onNavigateToAdvanced = onNavigateToAdvanced
                )

                Spacer(modifier = Modifier.height(bottomSpacerHeight))

                AureoleText(
                    text = "Thank you!",
                    style = AureoleTheme.typography.bodyMedium,
                    color = AureoleTheme.colors.onSurfaceMedium,
                    modifier = Modifier.padding(bottom = AureoleDS.spacings.xxLarge),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SettingsHeader(versionName: String) {
    Box(
        modifier = Modifier.padding(AureoleDS.spacings.medium),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AureoleDS.icons.Logo(
                modifier = Modifier.size(120.dp, 68.dp),
                tint = AureoleTheme.colors.onSurfaceHigh
            )
            Spacer(modifier = Modifier.height(AureoleDS.spacings.xLarge))
            AureoleText(
                text = "Aureole",
                style = AureoleTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
            Spacer(modifier = Modifier.height(AureoleDS.spacings.xxSmall))
            AureoleText(
                text = "Version $versionName",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.outline
            )
        }
    }
}

@Composable
private fun SettingsNavigationList(
    onNavigateToBehavior: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToAdvanced: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AureoleDS.spacings.medium),
        modifier = Modifier.fillMaxWidth().padding(horizontal = AureoleDS.spacings.xSmall)
    ) {
        SettingsMenuItem(
            title = "Behavior",
            subtitle = "Gestures, buttons, and controls",
            leadingContent = { AureoleDS.icons.Star() },
            onClick = onNavigateToBehavior,
            modifier = Modifier.background(Color.Transparent)
        )

        SettingsMenuItem(
            title = "Appearance",
            subtitle = "Colors, themes, and layout",
            leadingContent = { AureoleDS.icons.Edit() },
            onClick = onNavigateToAppearance,
            modifier = Modifier.background(Color.Transparent)
        )

        SettingsMenuItem(
            title = "Advanced",
            subtitle = "System tools and legal info",
            leadingContent = { AureoleDS.icons.Advanced() },
            onClick = onNavigateToAdvanced,
            modifier = Modifier.background(Color.Transparent)
        )
    }
}

private fun getVersionName(context: Context): String {
    return try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        packageInfo.versionName ?: "1.0.0"
    } catch (_: Throwable) {
        "1.0.0"
    }
}
