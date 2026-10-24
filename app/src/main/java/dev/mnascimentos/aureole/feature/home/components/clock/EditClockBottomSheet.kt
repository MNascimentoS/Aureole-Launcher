package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay

internal val PRESET_TEXT_COLORS = listOf(
    Pair("Padrão", 0),
    Pair("Azul Gelado", 0xFF90CAF9.toInt()),
    Pair("Lavanda", 0xFFE1BEE7.toInt()),
    Pair("Branco", 0xFFFFFFFF.toInt()),
    Pair("Ouro", 0xFFFFE082.toInt()),
    Pair("Menta", 0xFFA5D6A7.toInt()),
    Pair("Coral", 0xFFFFAB91.toInt())
)

internal val PRESET_BG_COLORS = listOf(
    Pair("Padrão", 0),
    Pair("Escuro Slate", 0xFF1E2124.toInt()),
    Pair("Roxo Profundo", 0xFF281C74.toInt()),
    Pair("Azul Noturno", 0xFF1A237E.toInt()),
    Pair("Vinho", 0xFF4A148C.toInt()),
    Pair("Verde Escuro", 0xFF1B5E20.toInt())
)

private const val DISMISS_DELAY_MS = 300L

@Suppress("LongMethod") // Bottom sheet composable initializing clock editor state
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClockBottomSheet(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    var isVisible by remember { mutableStateOf(true) }

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(DISMISS_DELAY_MS)
            onDismissRequest()
        }
    }

    var tempCustomGreeting by remember(uiState.clockCustomGreeting) {
        mutableStateOf(uiState.clockCustomGreeting)
    }

    val effectiveHazeState = if (uiState.isHazeEnabled) hazeState else null

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = stringResource(R.string.clock_customize_title),
                onSettingsClick = { actions.onSettingsClick() },
                hazeState = effectiveHazeState,
                isVisible = isVisible,
                animated = true,
                maxSheetHeightFraction = 0.65f
            ),
            previewContent = {
                EditClockPreviewCard(
                    uiState = uiState,
                    tempCustomGreeting = tempCustomGreeting,
                    hazeState = effectiveHazeState
                )
            },
            bottomBarContent = {
                EditClockActionButtons(
                    actions = actions,
                    onDismissRequest = { isVisible = false },
                    onResetGreeting = { tempCustomGreeting = "" }
                )
            },
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    EditClockStyleSection(
                        uiState = uiState,
                        actions = actions,
                        tempCustomGreeting = tempCustomGreeting,
                        onGreetingChange = {
                            tempCustomGreeting = it
                            actions.onUpdateClockCustomGreeting(it)
                        }
                    )

                    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

                    EditClockAlignmentSection(uiState = uiState, actions = actions)

                    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

                    EditClockFormatSection(uiState = uiState, actions = actions)

                    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

                    EditClockFontSection(uiState = uiState, actions = actions)

                    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

                    EditClockTextColorSection(uiState = uiState, actions = actions)

                    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

                    EditClockBackgroundSection(uiState = uiState, actions = actions)

                    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
                }
            }
        )
    )
}

@Composable
private fun EditClockPreviewCard(
    uiState: MainUiState,
    tempCustomGreeting: String,
    hazeState: HazeState? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMediumLarge))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.85f))
            .padding(AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        ClockHeader(
            hazeConfig = ClockHazeConfig(
                isHazeEnabled = uiState.isHazeEnabled,
                hazeState = hazeState,
                isBackgroundEnabled = uiState.isClockBackgroundEnabled
            ),
            config = ClockHeaderConfig(
                style = uiState.clockStyle,
                customGreeting = tempCustomGreeting,
                alignment = uiState.clockAlignment,
                fontFamily = uiState.clockFontFamily,
                timeFormat = uiState.clockTimeFormat,
                dateFormat = uiState.clockDateFormat,
                textColor = uiState.clockTextColor,
                backgroundColor = uiState.clockBackgroundColor
            )
        )
    }
}

@AureolePreview
@Composable
private fun EditClockBottomSheetContentPreview() {
    val dummyActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )
    AureoleLauncherTheme {
        EditClockBottomSheet(
            uiState = MainUiState(),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}
