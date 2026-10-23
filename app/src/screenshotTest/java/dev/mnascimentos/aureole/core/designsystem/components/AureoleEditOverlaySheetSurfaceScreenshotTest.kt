package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetStateData
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

class AureoleEditOverlaySheetSurfaceScreenshotTest {

    @PreviewTest
    @AureolePreview
    @Composable
    private fun SheetSurfaceDefault() {
        AureoleLauncherTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(visible = true) {
                    SheetSurface(
                        stateData = OverlaySheetStateData(
                            dragExpansion = 0f,
                            onDragExpansionChanged = {},
                            triggerDismiss = {},
                            effectiveAnimated = false,
                            animScope = this
                        ),
                        params = AureoleEditOverlayParams(
                            config = AureoleEditOverlayConfig(
                                title = "Preview Title",
                                onSettingsClick = {}
                            ),
                            previewContent = { AureoleText("Preview Content") },
                            bottomBarContent = null,
                            sheetContent = { AureoleText("Sheet Content") }
                        ),
                        bounds = SheetBounds(maxHeight = 300.dp, maxExpansionAllowed = 100.dp)
                    )
                }
            }
        }
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun SheetSurfaceExpanded() {
        AureoleLauncherTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(visible = true) {
                    SheetSurface(
                        stateData = OverlaySheetStateData(
                            dragExpansion = 100f,
                            onDragExpansionChanged = {},
                            triggerDismiss = {},
                            effectiveAnimated = false,
                            animScope = this
                        ),
                        params = AureoleEditOverlayParams(
                            config = AureoleEditOverlayConfig(
                                title = "Preview Title",
                                onSettingsClick = {}
                            ),
                            previewContent = { AureoleText("Preview Content") },
                            bottomBarContent = null,
                            sheetContent = { AureoleText("Sheet Content") }
                        ),
                        bounds = SheetBounds(maxHeight = 300.dp, maxExpansionAllowed = 100.dp)
                    )
                }
            }
        }
    }
}
