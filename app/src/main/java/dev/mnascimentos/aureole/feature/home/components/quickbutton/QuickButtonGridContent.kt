package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.dialogs.model.FolderButtonMetrics
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderLabel
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonGridItemCallbacks
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonGridItemStyle
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.displayTitle
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.systemIntentFor
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.util.IntentUtils
import timber.log.Timber

@Composable
fun GridQuickButtonContent(
    item: LauncherItemState,
    actions: HomeScreenActions,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState = LocalHomeUiState.current
    val defaultModel = QuickButtonModel(id = item.id)
    val model = item.quickButtonModel ?: defaultModel
    val appInfo = remember(model.shortcuts.click, uiState.apps) {
        if (model.shortcuts.click.type == ShortcutType.OPEN_APP) {
            uiState.apps.find { it.packageName == model.shortcuts.click.target }
        } else {
            null
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(FolderButtonMetrics.MediumSize)) {
            QuickButtonGridItem(
                model = model,
                callbacks = QuickButtonGridItemCallbacks(
                    onAction = { runQuickButtonAction(context, actions, it) },
                    onLongPress = { actions.onOpenQuickButtonSettings(item.id) }
                ),
                style = QuickButtonGridItemStyle(appInfo = appInfo)
            )
        }
        if (model.appearance.showLabel) {
            ContainerFolderLabel(name = model.displayTitle(appInfo?.label))
        }
    }
}

/** Runs what a tap on a quick button does: [action] is its click shortcut. */
internal fun runQuickButtonAction(context: Context, actions: HomeScreenActions, action: ShortcutAction) {
    when (action.type) {
        ShortcutType.ALL_APPS -> actions.onAllAppsDrawerOpen()
        ShortcutType.ASSISTANT -> launchVoiceAssistant(context)
        ShortcutType.SETTINGS -> actions.onSettingsClick()
        ShortcutType.NOTIFICATIONS -> IntentUtils.expandNotificationShade(context)
        ShortcutType.OPEN_APP -> launchAppPackage(context, action.target)
        ShortcutType.SYSTEM_INTENT -> launchSystemIntent(context, action.target)
        ShortcutType.NONE -> Unit
    }
}

internal fun launchVoiceAssistant(context: Context) {
    val intent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Timber.tag("QuickButton").w(e, "Could not launch assistant")
    }
}

internal fun launchAppPackage(context: Context, target: String) {
    if (target.isEmpty()) return
    try {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(target)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        }
    } catch (e: ActivityNotFoundException) {
        Timber.tag("QuickButton").w(e, "Could not launch app $target")
    }
}

internal fun launchSystemIntent(context: Context, target: String) {
    val intent = systemIntentFor(target)?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (intent == null) {
        Timber.tag("QuickButton").w("Unknown system shortcut target: %s", target)
        return
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Timber.tag("QuickButton").w(e, "Could not launch system intent $target")
    }
}

@AureolePreview
@Composable
private fun GridQuickButtonContentPreview() {
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
        Box(modifier = Modifier.size(100.dp)) {
            GridQuickButtonContent(
                item = LauncherItemState(id = "test"),
                actions = dummyActions
            )
        }
    }
}
