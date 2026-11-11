package dev.mnascimentos.aureole.composable.reorder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private val sampleItems = listOf("Clock", "Weather", "Calendar")

private fun sampleParams() = ReorderableParams(
    items = sampleItems,
    key = { it },
    actions = ReorderableActions(onMove = { _, _ -> }, onRemove = {}),
)

@Composable
private fun SampleCard(text: String) {
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.sizeExtraLarge, AureoleDS.dimens.sizeLarge)
            .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusMedium)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = AureoleDS.colors.onSurfaceHigh, style = AureoleDS.typography.bodyMedium)
    }
}

@Composable
private fun SampleRow(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.itemHeightExtraLarge)
            .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .padding(horizontal = AureoleDS.dimens.medium),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, color = AureoleDS.colors.onSurfaceHigh, style = AureoleDS.typography.bodyMedium)
    }
}

@Composable
private fun RowCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        LeftHandMirror(isLeftHanded = isLeftHanded) {
            ReorderableRow(
                params = sampleParams().copy(
                    spacing = AureoleDS.dimens.xSmall,
                    contentPadding = PaddingValues(AureoleDS.dimens.medium)
                ),
                trailingContent = { SampleCard(text = "+") }
            ) { item -> SampleCard(text = item) }
        }
    }
}

@Composable
private fun ColumnCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        LeftHandMirror(isLeftHanded = isLeftHanded) {
            ReorderableColumn(
                params = sampleParams().copy(
                    spacing = AureoleDS.dimens.xSmall,
                    contentPadding = PaddingValues(AureoleDS.dimens.medium)
                )
            ) { item -> SampleRow(text = item) }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ReorderableRowRightHandedScreenshotTest() = RowCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ReorderableRowLeftHandedScreenshotTest() = RowCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun ReorderableColumnRightHandedScreenshotTest() = ColumnCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ReorderableColumnLeftHandedScreenshotTest() = ColumnCase(isLeftHanded = true)
