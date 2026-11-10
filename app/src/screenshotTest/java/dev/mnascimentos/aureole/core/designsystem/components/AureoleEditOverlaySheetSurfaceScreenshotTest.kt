package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetMotion
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetStateData
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val REST_MAX_PX = 900f
private const val EXPANDED_PX = 1400f

class AureoleEditOverlaySheetSurfaceScreenshotTest {

    @PreviewTest
    @AureolePreview
    @Composable
    private fun SheetSurfaceDefault() {
        AureoleLauncherTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(visible = true, modifier = Modifier.align(Alignment.BottomCenter)) {
                    SheetSurface(
                        stateData = OverlaySheetStateData(
                            isExpanded = false,
                            onExpandedChanged = {},
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
                        bounds = SheetBounds(restMaxPx = REST_MAX_PX, expandedPx = EXPANDED_PX),
                        motion = remember { OverlaySheetMotion() }
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
                AnimatedVisibility(visible = true, modifier = Modifier.align(Alignment.BottomCenter)) {
                    SheetSurface(
                        stateData = OverlaySheetStateData(
                            isExpanded = true,
                            onExpandedChanged = {},
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
                        bounds = SheetBounds(restMaxPx = REST_MAX_PX, expandedPx = EXPANDED_PX),
                        motion = remember { OverlaySheetMotion() }
                    )
                }
            }
        }
    }
}
