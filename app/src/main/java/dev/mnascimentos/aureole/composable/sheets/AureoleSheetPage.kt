package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.icons.ChevronLeft
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * The header of a page shown inside an edit sheet in place of its tabs (the clock's color page, the group's
 * "Add apps"): back arrow, [title] and [subtitle], and no tab bar. Back ([onBack], the arrow or the system back)
 * returns to the tab; dragging down or tapping outside still closes the sheet ([onClose]). Without [hasBackArrow]
 * (the group's color page) the arrow is left out and back stays on the system back.
 */
fun AureoleEditOverlayConfig.asSheetPage(
    title: String,
    subtitle: String?,
    onBack: () -> Unit,
    onClose: () -> Unit,
    hasBackArrow: Boolean = true
): AureoleEditOverlayConfig = copy(
    title = title,
    subtitle = subtitle,
    onBackPress = onBack,
    onDismiss = onClose,
    headerLeadingAction = if (hasBackArrow) ({ SheetPageBackButton(onBack = onBack) }) else null,
    hasTabBottomBar = false
)

/** Back arrow of a page inside a sheet: back to the tab it came from. */
@Composable
fun SheetPageBackButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.overlay_sheet_page_back)
    IconButton(onClick = onBack, modifier = modifier.semantics { contentDescription = description }) {
        AureoleDS.icons.ChevronLeft(
            modifier = Modifier.size(AureoleDS.dimens.iconMedium),
            tint = AureoleDS.colors.onSurfaceHigh
        )
    }
}

@AureolePreview
@Composable
private fun SheetPageBackButtonPreview() {
    AureoleLauncherTheme {
        SheetPageBackButton(onBack = {})
    }
}
