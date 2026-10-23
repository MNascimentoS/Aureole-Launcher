package dev.mnascimentos.aureole.feature.settings.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.SettingsHugeTitle
import dev.mnascimentos.aureole.composable.SettingsMenuItem
import dev.mnascimentos.aureole.composable.SettingsMenuParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContributorsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onNavigateBack)

    Scaffold(
        modifier = modifier,
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(innerPadding)
                .padding(horizontal = AureoleDS.dimens.large, vertical = AureoleDS.dimens.medium)
        ) {
            SettingsHugeTitle(text = "Contributors")

            Spacer(modifier = Modifier.height(AureoleDS.dimens.large))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
                contentPadding = PaddingValues(bottom = AureoleDS.dimens.xxLarge * 2)
            ) {
                item {
                    SettingsMenuItem(
                        SettingsMenuParams(
                            title = "Name",
                            subtitle = "Something something",
                            onClick = {}
                        )
                    )
                }

                item {
                    SettingsMenuItem(
                        SettingsMenuParams(
                            title = "Name",
                            subtitle = "Something something",
                            onClick = {}
                        )
                    )
                }
            }
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
