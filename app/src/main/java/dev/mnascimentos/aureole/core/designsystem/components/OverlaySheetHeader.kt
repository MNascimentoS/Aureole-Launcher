package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.model.HEADER_LOGO_PRESS_MS
import dev.mnascimentos.aureole.core.designsystem.model.HEADER_LOGO_RELEASE_DAMPING
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHeaderTitle
import dev.mnascimentos.aureole.core.designsystem.model.headerLogoPose
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.rememberSystemAnimationsEnabled

/**
 * Title line of the overlay sheet (RF-05, RF-19). With an [action] (the logo), the line is a Row of a 48 dp slot,
 * the title and another 48 dp slot: the logo sits in one slot, centered on the title line, and the other stays empty
 * so the title keeps the center of the sheet and never runs under the logo (a long one is cut). Left-handed mirrors
 * it: the logo goes to the start. A [leadingAction] (a shortcut of the sheet) fills the slot across from the logo,
 * and mirrors with it. Without any action the title takes the whole width.
 * With a [titleIcon] (the app or the folder the sheet is about) icon and title sit at the start of the line, with
 * no empty slot before them, and the logo stays at the end; a long title is cut before the logo.
 */
@Composable
internal fun OverlaySheetHeader(
    title: OverlayHeaderTitle,
    action: (@Composable () -> Unit)? = null,
    isLeftHanded: Boolean = false,
    leadingAction: (@Composable () -> Unit)? = null
) {
    val titleIcon = title.icon
    val hasAction = action != null || leadingAction != null
    val isStartAligned = titleIcon != null
    val startSlot = if (isLeftHanded) action else leadingAction
    val endSlot = if (isLeftHanded) leadingAction else action
    val drawsStartSlot = hasAction && (!isStartAligned || startSlot != null)
    val drawsEndSlot = hasAction && (!isStartAligned || endSlot != null)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(headerPadding(hasAction, drawsStartSlot, drawsEndSlot)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (drawsStartSlot) HeaderSlot(content = startSlot)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = if (isStartAligned) Alignment.CenterStart else Alignment.Center
        ) {
            HeaderTitle(title = title)
        }
        if (drawsEndSlot) HeaderSlot(content = endSlot)
    }
}

/** The 48 dp slots are taller than the text: less vertical padding keeps the header as tall as before. */
@Composable
private fun headerPadding(hasAction: Boolean, drawsStartSlot: Boolean, drawsEndSlot: Boolean): PaddingValues =
    PaddingValues(
        start = if (drawsStartSlot) AureoleDS.dimens.zero else AureoleDS.dimens.small,
        end = if (drawsEndSlot) AureoleDS.dimens.zero else AureoleDS.dimens.small,
        top = if (hasAction) AureoleDS.dimens.xxSmall else AureoleDS.dimens.small,
        bottom = if (hasAction) AureoleDS.dimens.zero else AureoleDS.dimens.sheetHeaderGap
    )

@Composable
private fun HeaderTitle(title: OverlayHeaderTitle) {
    val titleIcon = title.icon
    val subtitle = title.subtitle
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (titleIcon != null) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.iconXLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                    .background(AureoleDS.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                titleIcon(Modifier.size(AureoleDS.dimens.iconMedium))
            }
            Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
        }
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = title.text,
                style = AureoleDS.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AureoleDS.colors.onSurfaceHigh,
                textAlign = if (titleIcon != null) TextAlign.Start else TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = AureoleDS.typography.bodyMedium,
                    color = AureoleDS.colors.onSurfaceMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** A 48 dp slot of the title line; empty, it only balances the one across. */
@Composable
private fun HeaderSlot(content: (@Composable () -> Unit)?) {
    Box(modifier = Modifier.size(AureoleDS.dimens.minTouchTarget), contentAlignment = Alignment.Center) {
        content?.invoke()
    }
}

/**
 * The logo of the sheet header (or [headerIcon] in its place): ~20 dp wide in a 48 dp touch area. While the finger
 * is on it, it shrinks and tilts over a light circle; it springs back on release and [onClick] runs then.
 * Dragging the finger away only brings it back. With the system animations off it does not move (RF-06, D-09).
 */
@Composable
internal fun OverlaySheetHeaderLogo(
    onClick: (() -> Unit)?,
    headerIcon: (@Composable (Modifier) -> Unit)? = null
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val pose = headerLogoPose(isPressed, rememberSystemAnimationsEnabled())
    val scale by animateFloatAsState(pose.scale, logoSpec(isPressed), label = "headerLogoScale")
    val rotation by animateFloatAsState(pose.rotation, logoSpec(isPressed), label = "headerLogoRotation")
    val haloAlpha by animateFloatAsState(pose.haloAlpha, tween(HEADER_LOGO_PRESS_MS), label = "headerLogoHalo")
    val settingsLabel = stringResource(R.string.overlay_sheet_action_settings)
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.minTouchTarget)
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable(interactionSource = interaction, indication = null, role = Role.Button) {
                            onClick()
                        }
                        .semantics { contentDescription = settingsLabel }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconXLarge)
                .graphicsLayer { alpha = haloAlpha }
                .background(AureoleDS.colors.surfaceVariant, CircleShape)
        )
        val iconModifier = Modifier
            .size(AureoleDS.dimens.sheetHeaderLogo)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
        if (headerIcon != null) {
            headerIcon(iconModifier)
        } else {
            AureoleDS.icons.Logo(tint = AureoleDS.colors.onSurfaceHigh, modifier = iconModifier)
        }
    }
}

/** Ease-out into the press; a slightly bouncy spring back out of it. */
private fun logoSpec(isPressed: Boolean): FiniteAnimationSpec<Float> =
    if (isPressed) {
        tween<Float>(HEADER_LOGO_PRESS_MS, easing = EaseOut)
    } else {
        spring(dampingRatio = HEADER_LOGO_RELEASE_DAMPING, stiffness = Spring.StiffnessMedium)
    }

@AureolePreview
@Composable
private fun OverlaySheetHeaderPreview() {
    AureoleLauncherTheme {
        OverlaySheetHeader(
            title = OverlayHeaderTitle("Customize"),
            action = { OverlaySheetHeaderLogo(onClick = {}) }
        )
    }
}

@AureolePreview
@Composable
private fun OverlaySheetHeaderWithIconPreview() {
    AureoleLauncherTheme {
        OverlaySheetHeader(
            title = OverlayHeaderTitle(
                text = "A folder with a long name that is cut before the logo",
                icon = { m -> Box(modifier = m) }
            ),
            action = { OverlaySheetHeaderLogo(onClick = {}) }
        )
    }
}
