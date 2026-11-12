package dev.mnascimentos.aureole.feature.home.components.scrubber

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.android.tools.screenshot.PreviewTest
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.HomeOverlaysContent
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.letterCenterY
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberBoundsFor
import dev.mnascimentos.aureole.feature.home.createMockHomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.HomeOverlaysConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private val APP_LABELS = listOf(
    "Alarm", "Books", "Camera", "Drive", "Email", "Files", "Gallery", "Health", "Inbox", "Journal", "Keep",
    "Lens", "Maps", "News", "Office", "Phone", "Quiz", "Radio", "Settings", "Translate", "Updates", "Video",
    "Wallet", "Xylophone", "Yoga", "Zen"
)
private val ICON_COLORS = listOf(0xFF1B65C0, 0xFF43A047, 0xFFE53935, 0xFFFB8C00, 0xFF8E24AA)

/** The drawer as the home edge scrub leaves it with the finger resting on [letter] (RF-20, RF-21). */
@Composable
private fun DrawerHeldOnLetter(letter: Char, isLeftHandedMode: Boolean) {
    val apps = APP_LABELS.mapIndexed { index, label ->
        AppInfo(
            label = label,
            packageName = "com.example.${label.lowercase()}",
            componentName = ComponentName("com.example.${label.lowercase()}", "MainActivity"),
            icon = ColorDrawable(ICON_COLORS[index % ICON_COLORS.size].toInt())
        )
    }
    val alphabet = APP_LABELS.map { it.first() }
    val letterIndex = alphabet.indexOf(letter)
    val uiState = MainUiState(
        apps = apps,
        filteredApps = apps,
        alphabet = alphabet,
        letterIndexMap = alphabet.withIndex().associate { (index, char) -> char to index },
        isAllAppsDrawerOpen = true,
        isLeftHandedMode = isLeftHandedMode,
        isLoading = false,
        isHazeEnabled = false
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides uiState,
            LocalLeftHanded provides isLeftHandedMode,
            LocalHomeActions provides createMockHomeScreenActions()
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val heightPx = constraints.maxHeight.toFloat()
                val bounds = scrubberBoundsFor(heightPx, LocalDensity.current)
                HomeOverlaysContent(
                    config = HomeOverlaysConfig(
                        uiState = uiState,
                        actions = createMockHomeScreenActions(),
                        listState = rememberLazyListState(initialFirstVisibleItemIndex = letterIndex),
                        hazeState = remember { HazeState() },
                        externalTouchY = letterCenterY(letterIndex, bounds.heightPx, alphabet.size),
                        coroutineScope = rememberCoroutineScope(),
                        screenHeightPx = heightPx,
                        onExternalTouchYReset = {}
                    )
                )
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ScrubberHoldOnPRightHandedScreenshotTest() {
    DrawerHeldOnLetter(letter = 'P', isLeftHandedMode = false)
}

@PreviewTest
@AureolePreview
@Composable
private fun ScrubberHoldOnPLeftHandedScreenshotTest() {
    DrawerHeldOnLetter(letter = 'P', isLeftHandedMode = true)
}
