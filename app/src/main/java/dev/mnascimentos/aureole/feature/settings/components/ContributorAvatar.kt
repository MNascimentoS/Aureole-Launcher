package dev.mnascimentos.aureole.feature.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun ContributorAvatar(
    @DrawableRes avatar: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(AureoleDS.dimens.avatarSize)
            .background(AureoleDS.colors.surfaceVariant, CircleShape)
    ) {
        Image(
            painter = painterResource(avatar),
            contentDescription = contentDescription,
            modifier = Modifier.size(AureoleDS.dimens.avatarSize),
            colorFilter = ColorFilter.tint(AureoleDS.colors.outline)
        )
    }
}

@AureolePreview
@Composable
private fun ContributorAvatarPreview() {
    AureoleLauncherTheme {
        ContributorAvatar(avatar = R.drawable.contributor_mateus, contentDescription = null)
    }
}
