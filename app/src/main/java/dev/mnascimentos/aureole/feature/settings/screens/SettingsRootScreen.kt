package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.R
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
import dev.mnascimentos.aureole.feature.secretgame.components.SecretBestScorePopup
import dev.mnascimentos.aureole.feature.secretgame.components.rememberSecretGameTrigger
import dev.mnascimentos.aureole.feature.settings.components.SettingsAreaCard
import dev.mnascimentos.aureole.feature.settings.extensions.getVersionName
import dev.mnascimentos.aureole.feature.settings.model.SettingsLayout
import dev.mnascimentos.aureole.feature.settings.model.SettingsNavigationActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.settingsLayoutFor
import dev.mnascimentos.aureole.util.percentToFraction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRootScreen(
    uiState: SettingsUiState,
    onNavigateToBehavior: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToAdvanced: () -> Unit,
    modifier: Modifier = Modifier,
    onSecretGameOver: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val versionName = remember(context) { getVersionName(context) }
    var showReleaseNotes by remember { mutableStateOf(false) }
    var isGameOpen by remember { mutableStateOf(false) }
    var showBestScore by remember { mutableStateOf(false) }
    val secretTrigger = rememberSecretGameTrigger(
        onOpenGame = { isGameOpen = true },
        onShowBest = { showBestScore = true }
    )

    Scaffold(
        modifier = modifier,
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val onHeaderClick = { showReleaseNotes = true }
            val thankYouModifier = secretTrigger
            val navigation = SettingsNavigationActions(
                onNavigateToBehavior = onNavigateToBehavior,
                onNavigateToAppearance = onNavigateToAppearance,
                onNavigateToAdvanced = onNavigateToAdvanced
            )
            when (settingsLayoutFor(maxWidth.value, maxHeight.value)) {
                SettingsLayout.SINGLE_COLUMN -> SettingsSingleColumn(
                    versionName = versionName,
                    topSpacerHeight = (maxHeight * uiState.headerOffsetPercent.percentToFraction())
                        .coerceAtLeast(AureoleDS.dimens.medium),
                    areaHeight = maxHeight,
                    navigation = navigation,
                    onHeaderClick = onHeaderClick,
                    thankYouModifier = thankYouModifier
                )
                SettingsLayout.TWO_PANES -> SettingsTwoPanes(
                    versionName = versionName,
                    areaHeight = maxHeight,
                    navigation = navigation,
                    onHeaderClick = onHeaderClick,
                    thankYouModifier = thankYouModifier
                )
            }
        }

        if (showBestScore) {
            SecretBestScorePopup(
                bestScore = uiState.secretBestScore,
                triggerText = stringResource(R.string.settings_root_thank_you),
                onDismissRequest = { showBestScore = false }
            )
        }

        if (showReleaseNotes) {
            ReleaseNotesBottomSheet(
                onDismissRequest = { showReleaseNotes = false }
            )
        }
    }
}

/** Portrait: header, options and the thank-you line pinned to the bottom (or right after the options when tall). */
@Composable
private fun SettingsSingleColumn(
    versionName: String,
    topSpacerHeight: Dp,
    areaHeight: Dp,
    navigation: SettingsNavigationActions,
    onHeaderClick: () -> Unit,
    thankYouModifier: Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .heightIn(min = areaHeight)
            .padding(horizontal = AureoleDS.dimens.xLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(topSpacerHeight))
        SettingsHeader(versionName = versionName, onClick = onHeaderClick)
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxLarge))
        SettingsNavigationList(navigation)
        Spacer(modifier = Modifier.weight(1f))
        SettingsThankYou(
            modifier = thankYouModifier.padding(top = AureoleDS.dimens.large, bottom = AureoleDS.dimens.xLarge)
        )
    }
}

/** Landscape: header on the left, options and the thank-you line on the right, both centered vertically. */
@Composable
private fun SettingsTwoPanes(
    versionName: String,
    areaHeight: Dp,
    navigation: SettingsNavigationActions,
    onHeaderClick: () -> Unit,
    thankYouModifier: Modifier
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = AureoleDS.dimens.xLarge),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.large)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .heightIn(min = areaHeight),
            contentAlignment = Alignment.Center
        ) {
            SettingsHeader(versionName = versionName, onClick = onHeaderClick)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .heightIn(min = areaHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                SettingsNavigationList(navigation)
            }
            SettingsThankYou(
                modifier = thankYouModifier.padding(top = AureoleDS.dimens.large, bottom = AureoleDS.dimens.xLarge)
            )
        }
    }
}

@Composable
private fun SettingsThankYou(modifier: Modifier = Modifier) {
    AureoleText(
        text = stringResource(R.string.settings_root_thank_you),
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = modifier,
        format = AureoleTextFormatParams(
            style = AureoleDS.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    )
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
                modifier = Modifier.size(AureoleDS.dimens.sizeExtraLarge, AureoleDS.dimens.sizeLarge),
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
private fun SettingsNavigationList(navigation: SettingsNavigationActions) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingsAreaCard(
            icon = { m, c -> AureoleDS.icons.Edit(modifier = m, tint = c) },
            title = stringResource(R.string.settings_root_appearance_title),
            subtitle = stringResource(R.string.settings_root_appearance_subtitle),
            onClick = navigation.onNavigateToAppearance
        )
        SettingsAreaCard(
            icon = { m, c -> AureoleDS.icons.Star(modifier = m, tint = c) },
            title = stringResource(R.string.settings_root_behavior_title),
            subtitle = stringResource(R.string.settings_root_behavior_subtitle),
            onClick = navigation.onNavigateToBehavior
        )
        SettingsAreaCard(
            icon = { m, c -> AureoleDS.icons.Advanced(modifier = m, tint = c) },
            title = stringResource(R.string.settings_root_advanced_title),
            subtitle = stringResource(R.string.settings_root_advanced_subtitle),
            onClick = navigation.onNavigateToAdvanced
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
