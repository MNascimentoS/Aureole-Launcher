package dev.mnascimentos.aureole.feature.settings.sections

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.settings.components.SheetSectionLabel
import dev.mnascimentos.aureole.feature.settings.model.isDeliberateTap

private const val GROUP_FILL_ALPHA = 0.45f

/**
 * A labeled group of rows on a card, the building block of the Appearance, Behavior and Advanced sections. Every
 * label but the first ([isFirst]) has `large` space above it, so the groups read apart.
 */
@Composable
internal fun SettingsSectionGroup(
    label: String,
    modifier: Modifier = Modifier,
    isFirst: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SheetSectionLabel(
            text = label,
            modifier = Modifier
                .then(if (isFirst) Modifier else Modifier.padding(top = AureoleDS.dimens.large))
                .padding(start = AureoleDS.dimens.xxSmall, bottom = AureoleDS.dimens.xSmall)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = GROUP_FILL_ALPHA))
                .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.minimal)
        ) {
            content()
        }
    }
}

/** Taps that land right after the section opened are ignored (the finger that opened it); see [isDeliberateTap]. */
@Composable
internal fun rememberDeliberateTap(): () -> Boolean {
    val openedAtMs = remember { SystemClock.uptimeMillis() }
    return { isDeliberateTap(openedAtMs, SystemClock.uptimeMillis()) }
}
