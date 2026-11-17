package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.animation.Crossfade
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
import dev.mnascimentos.aureole.core.designsystem.components.AureoleLogoRing
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Advanced
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.model.LOGO_RING_LOGO_FRACTION_LARGE
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.SettingsAreaCard
import dev.mnascimentos.aureole.feature.settings.extensions.getVersionName
import dev.mnascimentos.aureole.feature.settings.model.SettingsLayout
import dev.mnascimentos.aureole.feature.settings.model.SettingsNavigationActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.SettingsUpdateRing
import dev.mnascimentos.aureole.feature.settings.model.ringProgress
import dev.mnascimentos.aureole.feature.settings.model.settingsLayoutFor
import dev.mnascimentos.aureole.util.percentToFraction

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
            val header: @Composable () -> Unit = {
                SettingsHeader(
                    versionName = versionName,
                    updateRing = uiState.updateRing,
                    onClick = { showReleaseNotes = true }
                )
            }
            val navigation = SettingsNavigationActions(
                onNavigateToBehavior = onNavigateToBehavior,
                onNavigateToAppearance = onNavigateToAppearance,
                onNavigateToAdvanced = onNavigateToAdvanced
            )
            when (settingsLayoutFor(maxWidth.value, maxHeight.value)) {
                SettingsLayout.SINGLE_COLUMN -> SettingsSingleColumn(
                    header = header,
                    topSpacerHeight = (maxHeight * uiState.headerOffsetPercent.percentToFraction())
                        .coerceAtLeast(AureoleDS.dimens.medium),
                    areaHeight = maxHeight,
                    navigation = navigation
                )
                SettingsLayout.TWO_PANES -> SettingsTwoPanes(
                    header = header,
                    areaHeight = maxHeight,
                    navigation = navigation
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

/** Portrait: header, options and the thank-you line pinned to the bottom (or right after the options when tall). */
@Composable
private fun SettingsSingleColumn(
    header: @Composable () -> Unit,
    topSpacerHeight: Dp,
    areaHeight: Dp,
    navigation: SettingsNavigationActions
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
        header()
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxLarge))
        SettingsNavigationList(navigation)
        Spacer(modifier = Modifier.weight(1f))
        SettingsThankYou(modifier = Modifier.padding(top = AureoleDS.dimens.large, bottom = AureoleDS.dimens.xLarge))
    }
}

/** Landscape: header on the left, options and the thank-you line on the right, both centered vertically. */
@Composable
private fun SettingsTwoPanes(
    header: @Composable () -> Unit,
    areaHeight: Dp,
    navigation: SettingsNavigationActions
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
            header()
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
                modifier = Modifier.padding(top = AureoleDS.dimens.large, bottom = AureoleDS.dimens.xLarge)
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
    updateRing: SettingsUpdateRing,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SettingsHeaderLogo(updateRing = updateRing)
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

/**
 * The big logo of the Settings root. While an update is checked or downloaded it turns into the logo ring (RF-13),
 * in the same box, so nothing around it moves.
 */
@Composable
private fun SettingsHeaderLogo(updateRing: SettingsUpdateRing) {
    Crossfade(
        targetState = updateRing == SettingsUpdateRing.Hidden,
        label = "settingsHeaderLogo",
        modifier = Modifier.size(AureoleDS.dimens.sizeExtraLarge, AureoleDS.dimens.sizeLarge)
    ) { isHidden ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isHidden) {
                AureoleDS.icons.Logo(modifier = Modifier.fillMaxSize(), tint = AureoleDS.colors.onSurfaceHigh)
            } else {
                AureoleLogoRing(
                    progress = updateRing.ringProgress,
                    size = AureoleDS.dimens.sizeLarge,
                    logoFraction = LOGO_RING_LOGO_FRACTION_LARGE,
                    contentDescription = stringResource(
                        if (updateRing is SettingsUpdateRing.Downloading) {
                            R.string.settings_root_update_downloading
                        } else {
                            R.string.settings_root_update_checking
                        }
                    )
                )
            }
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
private fun SettingsRootScreenUpdatingPreview() {
    AureoleLauncherTheme {
        SettingsRootScreen(
            uiState = SettingsUiState(updateRing = SettingsUpdateRing.Downloading(fraction = 0.4f)),
            onNavigateToBehavior = {},
            onNavigateToAppearance = {},
            onNavigateToAdvanced = {}
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
