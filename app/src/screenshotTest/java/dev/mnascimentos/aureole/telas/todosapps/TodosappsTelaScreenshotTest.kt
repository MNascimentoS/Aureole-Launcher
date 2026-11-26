package dev.mnascimentos.aureole.telas.todosapps

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.android.tools.screenshot.PreviewTest
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.AppsListDrawer
import dev.mnascimentos.aureole.feature.home.components.overlays.HomeOverlaysContent
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.letterCenterY
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberBoundsFor
import dev.mnascimentos.aureole.feature.home.model.DrawerDragState
import dev.mnascimentos.aureole.feature.home.model.HomeOverlaysConfig
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.HomeScreenCase
import dev.mnascimentos.aureole.fixtures.sampleApp

private val APP_LABELS = listOf(
    "Alarm", "Books", "Camera", "Drive", "Email", "Files", "Gallery", "Health", "Inbox", "Journal", "Keep",
    "Lens", "Maps", "News", "Office", "Phone", "Quiz", "Radio", "Settings", "Translate", "Updates", "Video",
    "Wallet", "Xylophone", "Yoga", "Zen"
)
private val ICON_COLORS = listOf(0xFF1B65C0, 0xFF43A047, 0xFFE53935, 0xFFFB8C00, 0xFF8E24AA)

private val DRAWER_APPS = listOf(
    sampleApp("Camera", "com.example.camera", 0xFF1B65C0),
    sampleApp("Gallery", "com.example.gallery", 0xFF43A047),
    sampleApp("Settings", "com.example.settings", 0xFFE53935)
)

@Composable
private fun DrawerCase(uiState: MainUiState) = HomeCase(uiState) {
    AppsListDrawer(listState = rememberLazyListState())
}

/** A gaveta como o scrub da borda da home a deixa, com o dedo parado em [letter]. */
@Composable
private fun DrawerHeldOnLetter(letter: Char) {
    val apps = APP_LABELS.mapIndexed { index, label ->
        sampleApp(label, "com.example.${label.lowercase()}", ICON_COLORS[index % ICON_COLORS.size])
    }
    val alphabet = APP_LABELS.map { it.first() }
    val letterIndex = alphabet.indexOf(letter)
    val uiState = MainUiState(
        apps = apps,
        filteredApps = apps,
        alphabet = alphabet,
        letterIndexMap = alphabet.withIndex().associate { (index, char) -> char to index },
        isAllAppsDrawerOpen = true,
        isLoading = false,
        isHazeEnabled = false
    )
    HomeCase(uiState) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val heightPx = constraints.maxHeight.toFloat()
            val bounds = scrubberBoundsFor(heightPx, LocalDensity.current)
            HomeOverlaysContent(
                config = HomeOverlaysConfig(
                    uiState = uiState,
                    actions = createDefaultHomeScreenActions(),
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

@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = DrawerCase(
    MainUiState(
        apps = DRAWER_APPS,
        filteredApps = DRAWER_APPS,
        alphabet = listOf('C', 'G', 'S'),
        isLoading = false,
        isHazeEnabled = false
    )
)

@PreviewTest
@AureolePreview
@Composable
private fun BuscaScreenshotTest() = DrawerCase(
    MainUiState(
        apps = DRAWER_APPS.take(1),
        filteredApps = DRAWER_APPS.take(1),
        searchQuery = "Cam",
        appliedSearchQuery = "Cam",
        alphabet = listOf('C'),
        isLoading = false,
        isHazeEnabled = false
    )
)

@PreviewTest
@AureolePreview
@Composable
private fun AlfabetoScreenshotTest() = DrawerHeldOnLetter(letter = 'P')

/** A home com a gaveta a meio caminho de um swipe up: a gaveta sobe, a home escurece e encolhe (gestos-swipe RF-04). */
@PreviewTest
@AureolePreview
@Composable
private fun GavetaPuxadaScreenshotTest() = HomeScreenCase(
    uiState = MainUiState(
        apps = DRAWER_APPS,
        filteredApps = DRAWER_APPS,
        alphabet = listOf('C', 'G', 'S'),
        isLoading = false,
        isHazeEnabled = false,
        isSwipeUpAllAppsEnabled = true
    ),
    drawerDrag = DrawerDragState(isAnimated = false, initialProgress = PULLED_PROGRESS)
)

private const val PULLED_PROGRESS = 0.5f
