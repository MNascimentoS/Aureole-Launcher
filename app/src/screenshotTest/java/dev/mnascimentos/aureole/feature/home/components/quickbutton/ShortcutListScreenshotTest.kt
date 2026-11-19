package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutCategory
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSearchCandidate
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionSheetParams
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** The system shortcuts with their own DS icon where no app answers, and one saved but unavailable (RF-B3, RF-B5). */
@PreviewTest
@AureolePreview
@Composable
private fun ShortcutListIconsScreenshotTest() {
    val targets = listOf("Browser", "Calendar", "Camera", "Clock", "Contacts", "Files", "Wi-Fi Settings")
    val candidates = targets.map { ShortcutSearchCandidate(ShortcutAction.systemIntent(it), it) } +
        ShortcutSearchCandidate(ShortcutAction.systemIntent("Music Player"), "Music Player (unavailable)", isAvailable = false)
    AureoleLauncherTheme {
        ShortcutSelectionSheetContent(
            params = ShortcutSelectionSheetParams(
                category = ShortcutCategory.SYSTEM,
                currentAction = ShortcutAction.systemIntent("Calendar"),
                defaultCandidates = candidates,
                uiState = MainUiState()
            ),
            onApplyShortcut = {},
            isScrollable = false
        )
    }
}
