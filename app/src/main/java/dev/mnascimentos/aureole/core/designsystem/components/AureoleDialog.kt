package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions

private const val DIALOG_WIDTH_FRACTION = 0.92f

@Composable
fun AureoleDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: @Composable ((Modifier) -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val dialogContent = @Composable {
        Surface(
            modifier = modifier
                .fillMaxWidth(DIALOG_WIDTH_FRACTION)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge)),
            shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge),
            color = AureoleTheme.colors.surface,
            contentColor = AureoleTheme.colors.onSurfaceHigh
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (title != null) {
                    AureoleDialogHeader(
                        title = title,
                        modifier = Modifier,
                        icon = icon
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

    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            dialogContent()
        }
    } else {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            dialogContent()
        }
    }
}

@Composable
fun AureoleDialogHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: @Composable ((Modifier) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(AureoleTheme.colors.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon(Modifier.size(AureoleDS.dimens.iconMedium))
                Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            }
            AureoleText(
                text = title,
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        val homeActions = LocalHomeActions.current
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = AureoleDS.dimens.xSmall)
                .size(AureoleDS.dimens.xLarge)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .clickable { homeActions.onSettingsClick() },
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleTheme.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@AureolePreview
@Composable
fun AureoleDialogPreview() {
    AureoleLauncherTheme {
        AureoleDialog(
            onDismissRequest = {},
            title = "Sample Dialog Title"
        ) {
            AureoleText(
                text = "This is sample content inside AureoleDialog.",
                style = AureoleTheme.typography.bodyMedium
            )
        }
    }
}
