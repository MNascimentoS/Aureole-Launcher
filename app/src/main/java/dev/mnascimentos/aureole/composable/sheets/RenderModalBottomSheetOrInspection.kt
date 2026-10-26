package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun RenderModalBottomSheetOrInspection(
    sheetContent: @Composable () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = AureoleDS.dimens.radiusLarge,
                    topEnd = AureoleDS.dimens.radiusLarge
                ),
                color = AureoleDS.colors.surface,
                contentColor = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                sheetContent()
            }
        }
    } else {
        @OptIn(ExperimentalMaterial3Api::class)
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = RoundedCornerShape(
                topStart = AureoleDS.dimens.radiusLarge,
                topEnd = AureoleDS.dimens.radiusLarge
            ),
            containerColor = AureoleDS.colors.surface,
            contentColor = AureoleDS.colors.onSurfaceMedium,
            dragHandle = null,
            modifier = modifier
        ) {
            sheetContent()
        }
    }
}

@AureolePreview
@Composable
private fun RenderModalBottomSheetOrInspectionPreview() {
    AureoleLauncherTheme {
        RenderModalBottomSheetOrInspection(
            sheetContent = { AureoleText("Sample Sheet Content") },
            onDismiss = {}
        )
    }
}
