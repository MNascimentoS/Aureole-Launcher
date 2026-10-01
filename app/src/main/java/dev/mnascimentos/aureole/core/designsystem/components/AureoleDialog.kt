package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme

private const val DIALOG_WIDTH_FRACTION = 0.92f

@Composable
fun AureoleDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(DIALOG_WIDTH_FRACTION)
                .clip(RoundedCornerShape(22.dp))
                .border(
                    width = 0.5.dp,
                    color = AureoleTheme.colors.outline,
                    shape = RoundedCornerShape(22.dp)
                ),
            shape = RoundedCornerShape(22.dp),
            color = AureoleTheme.colors.surface,
            contentColor = AureoleTheme.colors.onSurfaceHigh
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (title != null) {
                    AureoleDialogHeader(
                        title = title,
                        modifier = Modifier,
                        icon = icon,
                        onDismiss = onDismissRequest
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AureoleDS.dimens.large)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun AureoleDialogHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onDismiss: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(AureoleTheme.colors.surfaceVariant)
            .border(
                width = 0.5.dp,
                color = AureoleTheme.colors.outline
            )
            .padding(horizontal = AureoleDS.dimens.medium),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AureoleTheme.colors.onSurfaceHigh,
                        modifier = Modifier.size(20.dp)
                    )
                }
                AureoleText(
                    text = title,
                    style = AureoleTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AureoleTheme.colors.onSurfaceHigh
                )
            }
            if (onDismiss != null) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AureoleTheme.colors.onSurfaceMedium,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
