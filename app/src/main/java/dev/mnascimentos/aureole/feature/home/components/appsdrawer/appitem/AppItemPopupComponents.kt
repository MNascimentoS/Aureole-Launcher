package dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem

import android.content.ComponentName
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupBox
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupHeader
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupHeaderParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemRowActions

private object AppItemPopupMetrics {
    val PopupWidth = 230.dp
    val ButtonWidth = 54.dp
}

@Composable
fun AppItemPopup(
    app: AppInfo,
    isFavorite: Boolean,
    actions: AppItemRowActions,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        AureolePopupBox(
            modifier = modifier.width(AppItemPopupMetrics.PopupWidth)
        ) {
            val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

            AureolePopupHeader(
                title = app.label,
                params = AureolePopupHeaderParams(
                    iconBitmap = iconBitmap,
                    showAureoleLogo = true
                )
            )

            AppPopupHorizontalActionsRow(
                app = app,
                isFavorite = isFavorite,
                actions = actions,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
internal fun AppPopupHorizontalActionsRow(
    app: AppInfo,
    isFavorite: Boolean,
    actions: AppItemRowActions,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = AureoleDS.dimens.small,
                end = AureoleDS.dimens.small,
                top = AureoleDS.dimens.small,
                bottom = AureoleDS.dimens.small
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (actions.onAppInfoClick != null) {
            AppPopupButton(
                icon = { m, t -> AureoleDS.icons.Info(m, t) },
                label = "App",
                onClick = {
                    actions.onAppInfoClick.invoke(app)
                    onDismiss()
                }
            )
        }

        if (actions.onToggleFavorite != null) {
            AppPopupButton(
                icon = { m, t -> AureoleDS.icons.Delete(m, t) },
                label = if (isFavorite) "Remove" else "Add",
                isDestructive = isFavorite,
                onClick = {
                    actions.onToggleFavorite.invoke(app.packageName)
                    onDismiss()
                }
            )
        }

        if (actions.onEditFavoritesClick != null) {
            AppPopupButton(
                icon = { m, t -> AureoleDS.icons.Settings(m, t) },
                label = "Settings",
                onClick = {
                    actions.onEditFavoritesClick.invoke()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun AppPopupButton(
    icon: @Composable (Modifier, Color) -> Unit,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive) AureoleDS.colors.error else AureoleDS.colors.onSurfaceHigh

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(AppItemPopupMetrics.ButtonWidth)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
            .padding(vertical = AureoleDS.dimens.xxSmall)
    ) {
        icon(Modifier.size(AureoleDS.dimens.iconMedium), contentColor)
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))
        AureoleText(
            text = label,
            color = contentColor,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
    }
}

@AureolePreview
@Composable
private fun AppItemPopupPreview() {
    val sampleApp = AppInfo(
        label = "Browser",
        packageName = "com.example.browser",
        componentName = ComponentName("com.example.browser", "MainActivity"),
        icon = android.graphics.Color.BLUE.toDrawable()
    )
    AureoleLauncherTheme {
        AureolePopupBox(modifier = Modifier.width(AppItemPopupMetrics.PopupWidth)) {
            AppPopupHorizontalActionsRow(
                app = sampleApp,
                isFavorite = false,
                actions = AppItemRowActions(
                    onAppInfoClick = {},
                    onToggleFavorite = {},
                    onEditFavoritesClick = {}
                ),
                onDismiss = {}
            )
        }
    }
}
