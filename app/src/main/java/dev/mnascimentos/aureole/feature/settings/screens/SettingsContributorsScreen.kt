package dev.mnascimentos.aureole.feature.settings.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.ContributorAvatar
import dev.mnascimentos.aureole.feature.settings.model.Contributor
import dev.mnascimentos.aureole.feature.settings.model.Contributors
import dev.mnascimentos.aureole.util.IntentUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContributorsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onNavigateBack)
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(innerPadding)
                .padding(horizontal = AureoleDS.dimens.large),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AureoleText(
                text = stringResource(R.string.settings_contributors_title),
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.fillMaxWidth(),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(AureoleDS.dimens.large))

            Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)) {
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
private fun SettingsContributorsScreenPreview() {
    AureoleLauncherTheme {
        SettingsContributorsScreen(
            onNavigateBack = {}
        )
    }
}
