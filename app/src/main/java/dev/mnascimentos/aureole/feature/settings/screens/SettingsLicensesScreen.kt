package dev.mnascimentos.aureole.feature.settings.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.components.LicenseDetailBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.OpenSourceEntries
import dev.mnascimentos.aureole.feature.settings.model.OssEntry
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.groupBySection

/** Everything shipped in the APK, by section, with author and license; tapping a row opens its license text. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLicensesScreen(
    uiState: SettingsUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateBack)
    var selected by remember { mutableStateOf<OssEntry?>(null) }
    val sections = remember { groupBySection(OpenSourceEntries) }

    Scaffold(
        modifier = modifier,
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = AureoleDS.dimens.iconXLargeMedium),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            item {
                Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
                AureoleText(
                    text = stringResource(R.string.settings_licenses_title),
                    color = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.fillMaxWidth(),
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                )
            }
            sections.forEach { (section, entries) ->
                item(key = section.name) {
                    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
                    AureoleMenuSectionLabel(text = stringResource(section.title))
                }
                items(items = entries, key = { it.name }) { entry ->
                    AureoleMenuRow(
                        params = MenuRowParams(
                            title = entry.name,
                            subtitle = stringResource(
                                R.string.settings_licenses_author_license,
                                entry.author,
                                stringResource(entry.license.label)
                            ),
                            onClick = { selected = entry },
                            showChevron = true
                        )
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(AureoleDS.dimens.large)) }
        }

        selected?.let { entry ->
            ProvideHomeUiState(MainUiState(isLeftHandedMode = uiState.isLeftHandedMode)) {
                LicenseDetailBottomSheet(entry = entry, onDismiss = { selected = null })
            }
        }
    }
}

@AureolePreview
@Composable
private fun SettingsLicensesScreenPreview() {
    AureoleLauncherTheme {
        SettingsLicensesScreen(uiState = SettingsUiState(), onNavigateBack = {})
    }
}
