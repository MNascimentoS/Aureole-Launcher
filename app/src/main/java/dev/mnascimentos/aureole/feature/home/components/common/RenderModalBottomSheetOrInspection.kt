package dev.mnascimentos.aureole.feature.home.components.common

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
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

@Composable
internal fun RenderModalBottomSheetOrInspection(
    sheetContent: @Composable () -> Unit,
    onDismiss: () -> Unit
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize(),
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
            dragHandle = null
        ) {
            sheetContent()
        }
    }
}
