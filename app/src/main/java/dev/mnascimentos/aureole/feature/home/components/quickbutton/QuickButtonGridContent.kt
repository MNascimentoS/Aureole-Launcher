package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
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

    Box(modifier = modifier.fillMaxSize()) {
        QuickButtonGridItem(
            model = model,
            appInfo = appInfo,
            onAction = { shortcutAction ->
                when (shortcutAction.type) {
                    ShortcutType.ALL_APPS -> actions.onAllAppsDrawerOpen()
                    ShortcutType.ASSISTANT -> launchVoiceAssistant(context)
                    ShortcutType.SETTINGS -> actions.onSettingsClick()
                    ShortcutType.NOTIFICATIONS -> IntentUtils.expandNotificationShade(context)
                    ShortcutType.OPEN_APP -> launchAppPackage(context, shortcutAction.target)
                    ShortcutType.SYSTEM_INTENT -> launchSystemIntent(context, shortcutAction.target)
                    ShortcutType.NONE -> { /* Do nothing */ }
                }
            },
            onLongPress = {
                actions.onOpenQuickButtonSettings(item.id)
            }
        )
    }
}

private fun launchVoiceAssistant(context: Context) {
    val intent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Timber.tag("QuickButton").w(e, "Could not launch assistant")
    }
}

private fun launchAppPackage(context: Context, target: String) {
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

private fun launchSystemIntent(context: Context, target: String) {
    val intent = when (target) {
        "Browser" -> Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
        "Camera" -> Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        "Dialer" -> Intent(Intent.ACTION_DIAL)
        else -> Intent(Settings.ACTION_SETTINGS)
    }.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
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
