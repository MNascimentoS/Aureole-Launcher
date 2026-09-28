package dev.mnascimentos.aureole.feature.settings.screens

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
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.SettingsHugeTitle
import dev.mnascimentos.aureole.composable.SettingsMenuItem
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContributorsScreen(
    @Suppress("UNUSED_PARAMETER") onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            SettingsHugeTitle(text = "Contributors")

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    SettingsMenuItem(
                        title = "Name",
                        subtitle = "Something something",
                        onClick = {}
                    )
                }

                item {
                    SettingsMenuItem(
                        title = "Name",
                        subtitle = "Something something",
                        onClick = {}
                    )
                }
            }
        }
    }
}
