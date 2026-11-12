package dev.mnascimentos.aureole.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.scrubber.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState

private val alphabet = ('A'..'Z').toList()

@Composable
private fun DrawerScrubber(isLeftHandedMode: Boolean) {
    AureoleLauncherTheme {
        ProvideHomeUiState(MainUiState(isAllAppsDrawerOpen = true, isLeftHandedMode = isLeftHandedMode)) {
            Box(modifier = Modifier.fillMaxSize().background(AureoleDS.colors.surface)) {
                CurvedAlphabetScrubber(
                    alphabet = alphabet,
                    callbacks = ScrubberCallbacks(onLetterSelected = {}),
                    options = ScrubberOptions(
                        isAlwaysVisible = true,
                        isGestureExclusionActive = true,
                        isInsetByGestures = true
                    ),
                    modifier = Modifier.align(
                        if (isLeftHandedMode) Alignment.BottomStart else Alignment.BottomEnd
                    )
                )
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AllAppsScrubberRightHandedScreenshotTest() {
    DrawerScrubber(isLeftHandedMode = false)
}

@PreviewTest
@AureolePreview
@Composable
private fun AllAppsScrubberLeftHandedScreenshotTest() {
    DrawerScrubber(isLeftHandedMode = true)
}
