package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHostSpec
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.Contributor
import dev.mnascimentos.aureole.feature.settings.model.Contributors
import dev.mnascimentos.aureole.util.IntentUtils

/**
 * "Contributors" as a sheet: the people behind Aureole, as a centered block with the rows aligned at the start;
 * tapping one opens their GitHub profile.
 */
@Composable
fun ContributorsBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    host: OverlayHostSpec = OverlayHostSpec()
) {
    val context = LocalContext.current
    AureoleMenuSheet(
        title = stringResource(R.string.settings_contributors_title),
        onDismiss = onDismissRequest,
        modifier = modifier,
        params = MenuSheetParams(host = host, showHeaderAction = false)
    ) {
        // The block is centered; the rows start at the same edge inside it, so the avatars line up.
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
                horizontalAlignment = Alignment.Start
            ) {
                Contributors.forEach { contributor ->
                    ContributorRow(
                        contributor = contributor,
                        onClick = { IntentUtils.openUrl(context, contributor.githubUrl) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContributorRow(contributor: Contributor, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .heightIn(min = AureoleDS.dimens.minTouchTarget)
            .clickable(
                onClickLabel = stringResource(R.string.settings_contributors_open_github),
                role = Role.Button,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContributorAvatar(avatar = contributor.avatar, contentDescription = null)

        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))

        Column {
            AureoleText(
                text = stringResource(contributor.name),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
            )
            AureoleText(
                text = stringResource(contributor.role),
                color = AureoleDS.colors.onSurfaceLow,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
            )
        }
    }
}

@AureolePreview
@Composable
private fun ContributorsBottomSheetPreview() {
    AureoleLauncherTheme {
        ContributorsBottomSheet(onDismissRequest = {})
    }
}
