package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.SoundOff
import dev.mnascimentos.aureole.core.designsystem.icons.SoundOn
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Pill of the pause screen that flips the game's sound; its touch target is at least the DS minimum. */
@Composable
fun SecretSoundButton(isOn: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(if (isOn) R.string.secret_game_sound_on else R.string.secret_game_sound_off)
    val color = AureoleDS.colors.onSurfaceHigh
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusFull)
    Row(
        modifier = modifier
            .heightIn(min = AureoleDS.dimens.minTouchTarget)
            .clip(shape)
            .border(BorderStroke(AureoleDS.dimens.borderMinimal, color), shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Switch
            ) {
                onToggle()
            }
            .semantics { contentDescription = label }
            .padding(horizontal = AureoleDS.dimens.medium),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = Modifier.size(AureoleDS.dimens.iconMedium)
        if (isOn) AureoleDS.icons.SoundOn(modifier = icon, tint = color) else AureoleDS.icons.SoundOff(icon, color)
        AureoleText(
            text = label,
            color = color,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
    }
}

@AureolePreview
@Composable
private fun SecretSoundButtonOnPreview() {
    AureoleLauncherTheme { SecretSoundButton(isOn = true, onToggle = {}) }
}

@AureolePreview
@Composable
private fun SecretSoundButtonOffPreview() {
    AureoleLauncherTheme { SecretSoundButton(isOn = false, onToggle = {}) }
}
