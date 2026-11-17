package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintConfig
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintGrid
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintItem
import dev.mnascimentos.aureole.feature.home.grid.model.GridGestureTracker
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

/** The save pill of "Adjust your layout" after its text folds away (ajustes-onboarding RF-16..RF-19). */
@Composable
private fun EditHintCase(config: EditHintConfig, isGesturing: Boolean = false) {
    val tracker = GridGestureTracker().apply { if (isGesturing) begin() }
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeActions provides createDefaultHomeScreenActions().copy(gridGestureTracker = tracker)
        ) {
            OnboardingEditHintComponent(config = config, onSave = {})
        }
    }
}

/** Only the "✓ Save" pill, at the bottom (RF-17). */
@PreviewTest
@AureolePreview
@Composable
private fun OnboardingEditHintPillOnlyScreenshotTest() =
    EditHintCase(EditHintConfig(isVisible = true, startsFolded = true))

/** During a drag or resize the pill fades to ~25% and lets the touch through (RF-18). */
@PreviewTest
@AureolePreview
@Composable
private fun OnboardingEditHintPillGesturingScreenshotTest() =
    EditHintCase(EditHintConfig(isVisible = true, startsFolded = true), isGesturing = true)

/** An item covers the bottom band, so the pill goes to the top (RF-19). */
@PreviewTest
@AureolePreview
@Composable
private fun OnboardingEditHintPillTopScreenshotTest() = EditHintCase(
    EditHintConfig(
        isVisible = true,
        startsFolded = true,
        grid = EditHintGrid(
            items = listOf(EditHintItem(row = BOTTOM_ITEM_ROW, rowSpan = BOTTOM_ITEM_SPAN)),
            maxRows = GRID_ROWS
        )
    )
)

private const val GRID_ROWS = 12
private const val BOTTOM_ITEM_ROW = 8
private const val BOTTOM_ITEM_SPAN = 4
