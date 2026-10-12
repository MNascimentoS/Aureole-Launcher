package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickFontBottomSheet(
    selectedFontName: String,
    onFontSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetContent = @Composable {
        PickFontSheetContent(selectedFontName = selectedFontName, onFontSelected = onFontSelected)
    }

    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = AureoleDS.colors.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                sheetContent()
            }
        }
    } else {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            dragHandle = null,
            containerColor = AureoleDS.colors.surface
        ) {
            sheetContent()
        }
    }
}

@Composable
private fun PickFontSheetContent(
    selectedFontName: String,
    onFontSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AureoleDS.dimens.large, bottom = AureoleDS.dimens.xLarge)
    ) {
        AureoleText(
            text = "Pick Font",
            style = AureoleTheme.typography.titleMedium,
            color = AureoleTheme.colors.onSurfaceHigh,
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.xLarge)
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

        Box(modifier = Modifier.weight(1f, fill = false)) {
            LazyColumn {
                items(AvailableFonts.keys.toList()) { fontName ->
                    FontOptionItem(
                        fontName = fontName,
                        isSelected = fontName == selectedFontName,
                        onFontSelected = onFontSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun FontOptionItem(
    fontName: String,
    isSelected: Boolean,
    onFontSelected: (String) -> Unit
) {
    val font = AvailableFonts[fontName]
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFontSelected(fontName) }
            .padding(horizontal = AureoleDS.dimens.xLarge, vertical = AureoleDS.dimens.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = fontName,
            style = AureoleTheme.typography.bodyLarge,
            fontFamily = font,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            AureoleDS.icons.Check(
                tint = AureoleTheme.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge)
            )
        }
    }
}

@AureolePreview
@Composable
fun PickFontBottomSheetPreview() {
    AureoleLauncherTheme {
        PickFontBottomSheet(
            selectedFontName = "Istok Web",
            onFontSelected = {},
            onDismissRequest = {}
        )
    }
}
