@file:Suppress("MagicNumber", "MaxLineLength")

package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.SettingsMenuItem
import dev.mnascimentos.aureole.composable.SettingsMenuParams
import dev.mnascimentos.aureole.composable.sheets.ReleaseNotesBottomSheet
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Advanced
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.extensions.getVersionName
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val BOTTOM_SPACER_PERCENT = 0.1f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRootScreen(
    uiState: SettingsUiState,
    onNavigateToBehavior: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToAdvanced: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val versionName = remember(context) { getVersionName(context) }
    var showReleaseNotes by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val topSpacerHeight = (maxHeight * (uiState.headerOffsetPercent / 100f)).coerceAtLeast(
                AureoleDS.dimens.medium
            )
            val bottomSpacerHeight = (maxHeight * BOTTOM_SPACER_PERCENT).coerceAtLeast(AureoleDS.dimens.large)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AureoleDS.dimens.xLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(topSpacerHeight))

                SettingsHeader(
                    versionName = versionName,
                    onClick = { showReleaseNotes = true }
                )

                Spacer(modifier = Modifier.height(AureoleDS.dimens.xxLarge))

                SettingsNavigationList(
                    onNavigateToBehavior = onNavigateToBehavior,
                    onNavigateToAppearance = onNavigateToAppearance,
                    onNavigateToAdvanced = onNavigateToAdvanced
                )

                Spacer(modifier = Modifier.height(bottomSpacerHeight))

                AureoleText(
                    text = stringResource(R.string.settings_root_thank_you),
                    color = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.padding(bottom = AureoleDS.dimens.xxLarge),
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        if (showReleaseNotes) {
            ReleaseNotesBottomSheet(
                onDismissRequest = { showReleaseNotes = false }
            )
        }
    }
}

@Composable
private fun SettingsHeader(
    versionName: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AureoleDS.icons.Logo(
                modifier = Modifier.size(120.dp, 68.dp),
                tint = AureoleDS.colors.onSurfaceHigh
            )
            Spacer(modifier = Modifier.height(AureoleDS.dimens.xLarge))
            AureoleText(
                text = stringResource(R.string.settings_root_title),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))
            AureoleText(
                text = stringResource(R.string.settings_root_version, versionName),
                color = AureoleDS.colors.outline,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
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
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
        modifier = Modifier.fillMaxWidth().padding(horizontal = AureoleDS.dimens.xSmall)
    ) {
        SettingsMenuItem(
            params = SettingsMenuParams(
                title = stringResource(R.string.settings_root_appearance_title),
                subtitle = stringResource(R.string.settings_root_appearance_subtitle),
                leadingContent = { AureoleDS.icons.Edit() },
                onClick = onNavigateToAppearance
            ),
            modifier = Modifier.background(Color.Transparent)
        )

        SettingsMenuItem(
            params = SettingsMenuParams(
                title = stringResource(R.string.settings_root_behavior_title),
                subtitle = stringResource(R.string.settings_root_behavior_subtitle),
                leadingContent = { AureoleDS.icons.Star() },
                onClick = onNavigateToBehavior
            ),
            modifier = Modifier.background(Color.Transparent)
        )

        SettingsMenuItem(
            params = SettingsMenuParams(
                title = stringResource(R.string.settings_root_advanced_title),
                subtitle = stringResource(R.string.settings_root_advanced_subtitle),
                leadingContent = { AureoleDS.icons.Advanced() },
                onClick = onNavigateToAdvanced
            ),
            modifier = Modifier.background(Color.Transparent)
        )
    }
}

@AureolePreview
@Composable
private fun SettingsRootScreenPreview() {
    AureoleLauncherTheme {
        SettingsRootScreen(
            uiState = SettingsUiState(),
            onNavigateToBehavior = {},
            onNavigateToAppearance = {},
            onNavigateToAdvanced = {}
        )
    }
}
