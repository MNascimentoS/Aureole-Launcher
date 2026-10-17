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
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dragHandle: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val sheetContent = @Composable {
        SettingsBottomSheetLayout(title = title, content = content)
    }

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
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            modifier = modifier,
            dragHandle = dragHandle,
            shape = RoundedCornerShape(
                topStart = AureoleDS.dimens.radiusLarge,
                topEnd = AureoleDS.dimens.radiusLarge
            ),
            containerColor = AureoleDS.colors.surface,
            contentColor = AureoleDS.colors.onSurfaceMedium
        ) {
            sheetContent()
        }
    }
}

@Composable
private fun SettingsBottomSheetLayout(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.xLarge, vertical = AureoleDS.dimens.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.fillMaxWidth(),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            content()
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxLarge))
    }
}

@AureolePreview
@Composable
private fun SettingsBottomSheetPreview() {
    AureoleLauncherTheme {
        SettingsBottomSheet(
            title = "Preview Title",
            onDismissRequest = {}
        ) {
            AureoleText("Content preview")
        }
    }
}
