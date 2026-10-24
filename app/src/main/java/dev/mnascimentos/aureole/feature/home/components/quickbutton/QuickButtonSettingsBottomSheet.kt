package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonAppearanceMetrics
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay

private const val DISMISS_DELAY_MS = 300L

@Composable
fun QuickButtonSettingsBottomSheet(
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

    val activeModel = uiState.activeQuickButtonModel ?: QuickButtonModel()
    val activeAppInfo = remember(activeModel.shortcuts.click, uiState.apps) {
        if (activeModel.shortcuts.click.type == ShortcutType.OPEN_APP) {
            uiState.apps.find { it.packageName == activeModel.shortcuts.click.target }
        } else {
            null
        }
    }
    val effectiveHazeState = if (uiState.isHazeEnabled) hazeState else null

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = stringResource(R.string.qb_settings_title),
                onSettingsClick = { actions.onSettingsClick() },
                hazeState = effectiveHazeState,
                isVisible = isVisible,
                animated = true,
                maxSheetHeightFraction = 0.3f
            ),
            previewContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(QuickButtonAppearanceMetrics.PreviewBoxHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(QuickButtonAppearanceMetrics.PreviewItemSize)) {
                        QuickButtonGridItem(
                            model = activeModel,
                            appInfo = activeAppInfo,
                            onAction = {},
                            onLongPress = {}
                        )
                    }
                }
            },
            bottomBarContent = null,
            sheetContent = {
                QuickButtonSettingsSheetContent(
                    activeModel = activeModel,
                    uiState = uiState,
                    actions = actions,
                    onDismiss = { isVisible = false }
                )
            }
        )
    )
}

@Composable
private fun QuickButtonSettingsSheetContent(
    activeModel: QuickButtonModel,
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsRow(
            title = stringResource(R.string.qb_appearance),
            icon = { m -> AureoleDS.icons.Edit(m) },
            onClick = { actions.onOpenQuickButtonAppearance() }
        )

        val clickValue = when (activeModel.shortcuts.click.type) {
            ShortcutType.OPEN_APP -> {
                val pkg = activeModel.shortcuts.click.target
                val appLabel = uiState.apps.find { it.packageName == pkg }?.label
                if (appLabel != null) {
                    stringResource(R.string.qb_open_app_name, appLabel)
                } else {
                    stringResource(
                        R.string.qb_open_app
                    )
                }
            }
            else -> activeModel.shortcuts.click.displayName
        }

        SettingsRow(
            title = stringResource(R.string.qb_click_shortcut),
            value = clickValue,
            icon = { m -> AureoleDS.icons.Click(m) },
            onClick = { actions.onOpenShortcutSelection("click") }
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

        SettingsRow(
            title = stringResource(dev.mnascimentos.aureole.R.string.qb_remove),
            icon = { m -> AureoleDS.icons.Delete(m, tint = AureoleDS.colors.error) },
            textColor = AureoleDS.colors.error,
            onClick = {
                uiState.activeQuickButtonId?.let { actions.onDeleteGridItem(it) }
                onDismiss()
            }
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    }
}

@AureolePreview
@Composable
private fun QuickButtonSettingsBottomSheetPreview() {
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
        QuickButtonSettingsBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel()
            ),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}
