package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    title: String,
    onDismissRequest: () -> Unit,
    dragHandle: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val sheetContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AureoleDS.dimens.xLarge, vertical = AureoleDS.dimens.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AureoleText(
                text = title,
                style = AureoleTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = AureoleTheme.colors.onSurfaceMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AureoleDS.dimens.large))

            Box(modifier = Modifier.weight(1f, fill = false)) {
                content()
            }

            Spacer(modifier = Modifier.height(AureoleDS.dimens.xxLarge))
        }
    }

    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = AureoleTheme.dimens.cornerRadius,
                    topEnd = AureoleTheme.dimens.cornerRadius
                ),
                color = AureoleTheme.colors.surface,
                contentColor = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                sheetContent()
            }
        }
    } else {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            dragHandle = dragHandle,
            shape = RoundedCornerShape(
                topStart = AureoleTheme.dimens.cornerRadius,
                topEnd = AureoleTheme.dimens.cornerRadius
            ),
            containerColor = AureoleTheme.colors.surface,
            contentColor = AureoleTheme.colors.onSurfaceMedium
        ) {
            sheetContent()
        }
    }
}


@AureolePreview
@Composable
fun SettingsBottomSheetPreview() {
    AureoleLauncherTheme {
        SettingsBottomSheet(
            title = "Preview Title",
            onDismissRequest = {}
        ) {
            AureoleText("Content preview")
        }
    }
}
