package dev.mnascimentos.aureole.feature.settings.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDialogFlags
import dev.mnascimentos.aureole.feature.settings.model.AppearanceMenuDialogActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.sections.AppearanceSections
import dev.mnascimentos.aureole.util.percentToFraction

private const val BOTTOM_SPACER_RATIO = 0.15f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppearanceScreen(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateBack)

    var flags by remember { mutableStateOf(AppearanceDialogFlags()) }

    Scaffold(
        modifier = modifier,
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        SettingsAppearanceContent(
            uiState = uiState,
            actions = actions,
            flags = flags,
            onFlagsChanged = { flags = it },
            innerPadding = innerPadding,
        )

        AppearanceDialogs(
            flags = flags,
            uiState = uiState,
            actions = actions,
            onDismissDialog = {
                flags = AppearanceDialogFlags()
            }
        )
    }
}

@Composable
private fun SettingsAppearanceContent(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    flags: AppearanceDialogFlags,
    onFlagsChanged: (AppearanceDialogFlags) -> Unit,
    innerPadding: PaddingValues,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.TopCenter
    ) {
        val topSpacerHeight = (maxHeight * uiState.headerOffsetPercent.percentToFraction()).coerceAtLeast(
            AureoleDS.dimens.medium
        )
        val bottomSpacerHeight = (maxHeight * BOTTOM_SPACER_RATIO).coerceAtLeast(AureoleDS.dimens.large)

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = AureoleDS.dimens.settingsContentMaxWidth)
                .fillMaxWidth()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AureoleDS.dimens.iconXLargeMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topSpacerHeight))

            AureoleText(
                text = stringResource(R.string.settings_appearance_title),
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.fillMaxWidth(),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(AureoleDS.dimens.large))

            AppearanceSections(
                uiState = uiState,
                actions = actions,
                dialogActions = AppearanceMenuDialogActions(
                    onShowSetBackgroundDialog = { onFlagsChanged(flags.copy(showSetBg = true)) },
                    onShowCardsGlassDialog = { onFlagsChanged(flags.copy(showCardsGlass = true)) },
                    onShowPaletteDialog = { onFlagsChanged(flags.copy(showPalette = true)) },
                    onShowFontDialog = { onFlagsChanged(flags.copy(showFont = true)) }
                )
            )

            Spacer(modifier = Modifier.height(bottomSpacerHeight))
        }
    }
}

@AureolePreview
@Composable
private fun SettingsAppearanceScreenPreview() {
    AureoleLauncherTheme {
        SettingsAppearanceScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onNavigateBack = {}
        )
    }
}
