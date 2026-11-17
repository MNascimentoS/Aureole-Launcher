package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.palette.readableOn
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.NoQuickButtonCallbacks
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonGridItemCallbacks
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonGridItemStyle
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.defaultName
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.resolvedName

@Composable
fun QuickButtonGridItem(
    model: QuickButtonModel,
    callbacks: QuickButtonGridItemCallbacks,
    modifier: Modifier = Modifier,
    style: QuickButtonGridItemStyle = QuickButtonGridItemStyle()
) {
    val appInfo = style.appInfo
    val outlineIcon = style.outlineIcon
    val iconSize = style.iconSize ?: AureoleDS.dimens.iconXLarge
    val shape = style.shape ?: RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    val currentModel by rememberUpdatedState(model)
    val currentOnAction by rememberUpdatedState(callbacks.onAction)
    val currentOnLongPress by rememberUpdatedState(callbacks.onLongPress)

    val backgroundColor = model.appearance.backgroundColor.resolve(AureoleDS.colors)

    val baseModifier = modifier
        .fillMaxSize()
        .clip(shape)
        .background(backgroundColor)

    val strokeModifier = if (model.appearance.strokeEnabled) {
        baseModifier.border(AureoleDS.dimens.minimal, model.appearance.strokeColor.resolve(AureoleDS.colors), shape)
    } else {
        baseModifier
    }

    // The name (or the click action) is what TalkBack reads for the button (botao-rapido RF-01).
    val name = model.appearance.resolvedName(model.defaultName(appInfo?.label))

    Box(
        modifier = strokeModifier
            .semantics { contentDescription = name }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { currentOnAction(currentModel.shortcuts.click) },
                    onLongPress = { currentOnLongPress() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val clickShortcut = model.shortcuts.click
        if (clickShortcut.type == ShortcutType.OPEN_APP && appInfo != null) {
            AureoleAppIcon(app = appInfo, size = iconSize)
        } else {
            if (outlineIcon) {
                // Low contrast (RF-06): a slightly larger copy in a readable color sits behind the icon as its outline.
                Icon(
                    painter = painterResource(id = model.appearance.iconRes),
                    contentDescription = null,
                    tint = readableOn(backgroundColor, AureoleDS.colors),
                    modifier = Modifier.size(iconSize + AureoleDS.dimens.xxSmall)
                )
            }
            Icon(
                painter = painterResource(id = model.appearance.iconRes),
                contentDescription = null,
                tint = model.appearance.iconColor.resolve(AureoleDS.colors),
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@AureolePreview
@Composable
private fun QuickButtonGridItemPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.size(AureoleDS.dimens.radiusFull)) {
            QuickButtonGridItem(model = QuickButtonModel(), callbacks = NoQuickButtonCallbacks)
        }
    }
}
