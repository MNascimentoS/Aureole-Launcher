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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
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
import dev.mnascimentos.aureole.feature.secretgame.components.SecretGameLayer
import dev.mnascimentos.aureole.feature.secretgame.components.secretGameTrigger
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameHostState
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameModifiers
import dev.mnascimentos.aureole.feature.settings.components.SettingsAreaCard
import dev.mnascimentos.aureole.feature.settings.extensions.getVersionName
import dev.mnascimentos.aureole.feature.settings.model.SettingsHeaderParams
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
    modifier: Modifier = Modifier
) {
    SettingsRootScreen(
        uiState = uiState,
        navigation = SettingsNavigationActions(onNavigateToBehavior, onNavigateToAppearance, onNavigateToAdvanced),
        modifier = modifier,
        onSecretGameOver = {}
    )
}

/** The root of Settings; [onSecretGameOver] receives the score of the hidden game (see [SecretGameLayer]). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRootScreen(
    uiState: SettingsUiState,
    navigation: SettingsNavigationActions,
    modifier: Modifier = Modifier,
    onSecretGameOver: (Int) -> Unit
) {
    val context = LocalContext.current
    val versionName = remember(context) { getVersionName(context) }
    var showReleaseNotes by remember { mutableStateOf(false) }
    val game = rememberSaveable(saver = SecretGameHostState.Saver) { SecretGameHostState() }
    val secret = SecretGameModifiers(
        thankYou = Modifier.secretGameTrigger(game, LocalHapticFeedback.current),
        logo = game.logoModifier
    )
    val header = SettingsHeaderParams(versionName, onClick = { showReleaseNotes = true }, secret = secret)

    Box(modifier = modifier.fillMaxSize().then(game.rootModifier)) {
        Scaffold(
            modifier = Modifier,
            containerColor = AureoleDS.colors.surface
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .graphicsLayer { alpha = game.settingsAlpha }
            ) {
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
        SecretGameLayer(host = game, bestScore = uiState.secretBestScore, onGameOver = onSecretGameOver)
    }
}

/** Portrait: header, options and the thank-you line pinned to the bottom (or right after the options when tall). */
@Composable
private fun SettingsSingleColumn(
    header: SettingsHeaderParams,
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
        SettingsHeader(header)
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxLarge))
        SettingsNavigationList(navigation)
        Spacer(modifier = Modifier.weight(1f))
        SettingsThankYou(
            modifier = header.secret.thankYou.padding(top = AureoleDS.dimens.large, bottom = AureoleDS.dimens.xLarge)
        )
    }
}

/** Landscape: header on the left, options and the thank-you line on the right, both centered vertically. */
@Composable
private fun SettingsTwoPanes(
    header: SettingsHeaderParams,
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
            SettingsHeader(header)
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
                modifier = header.secret.thankYou.padding(
                    top = AureoleDS.dimens.large,
                    bottom = AureoleDS.dimens.xLarge
                )
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
private fun SettingsHeader(params: SettingsHeaderParams) {
    Box(
        modifier = Modifier
            .clickable { params.onClick() }
            .padding(AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AureoleDS.icons.Logo(
                modifier = params.secret.logo.size(AureoleDS.dimens.sizeExtraLarge, AureoleDS.dimens.sizeLarge),
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
                text = stringResource(R.string.settings_root_version, params.versionName),
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
