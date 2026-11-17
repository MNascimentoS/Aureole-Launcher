package dev.mnascimentos.aureole.feature.settings.sections

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Doc
import dev.mnascimentos.aureole.core.designsystem.icons.Home
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.RefreshCcw
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Undo
import dev.mnascimentos.aureole.core.designsystem.icons.Users
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.extensions.openLegalPage
import dev.mnascimentos.aureole.feature.settings.extensions.tryUninstall
import dev.mnascimentos.aureole.feature.settings.model.LegalPage
import dev.mnascimentos.aureole.feature.settings.model.SettingsAdvancedDialogActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val DIVIDER_ALPHA = 0.5f

/**
 * The Advanced groups, in this order: "System", "About" and, at the end, the "Care zone" with Reset and Uninstall,
 * both needing a hold. Uninstall hands over to the system, which asks for its own confirmation.
 */
@Composable
fun AdvancedSections(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    dialogActions: SettingsAdvancedDialogActions,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        SystemGroup(uiState = uiState, actions = actions, context = context, dialogActions = dialogActions)
        AboutGroup(context = context, dialogActions = dialogActions)
        HorizontalDivider(
            modifier = Modifier.padding(top = AureoleDS.dimens.xSmall),
            thickness = AureoleDS.dimens.borderMinimal,
            color = AureoleDS.colors.outline.copy(alpha = DIVIDER_ALPHA)
        )
        CareZoneGroup(actions = actions, context = context)
    }
}

@Composable
private fun SystemGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    context: Context,
    dialogActions: SettingsAdvancedDialogActions
) {
    val deliberate = rememberDeliberateTap()
    SettingsSectionGroup(label = stringResource(R.string.settings_section_system)) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_app_info),
                icon = { m, c -> AureoleDS.icons.Info(modifier = m, tint = c) },
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_whats_new),
                icon = { m, c -> AureoleDS.icons.Smile(modifier = m, tint = c) },
                onClick = dialogActions.onOpenReleaseNotesDialog
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_set_default_launcher),
                icon = { m, c -> AureoleDS.icons.Home(modifier = m, tint = c) },
                onClick = { actions.onSetDefaultLauncherClick() }
            )
        )
        AureoleSwitchRow(
            SwitchRowParams(
                title = stringResource(R.string.settings_behavior_check_for_updates),
                checked = uiState.isInAppUpdateEnabled,
                onCheckedChange = { if (deliberate()) actions.onToggleInAppUpdate() },
                icon = { m, c -> AureoleDS.icons.RefreshCcw(modifier = m, tint = c) }
            )
        )
    }
}

@Composable
private fun AboutGroup(context: Context, dialogActions: SettingsAdvancedDialogActions) {
    SettingsSectionGroup(label = stringResource(R.string.settings_section_about)) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_contributors),
                icon = { m, c -> AureoleDS.icons.Users(modifier = m, tint = c) },
                showChevron = true,
                onClick = dialogActions.onNavigateToContributors
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_privacy_policy),
                icon = { m, c -> AureoleDS.icons.Doc(modifier = m, tint = c) },
                showChevron = true,
                onClick = { openLegalPage(context, LegalPage.PRIVACY) }
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_terms_of_service),
                icon = { m, c -> AureoleDS.icons.Doc(modifier = m, tint = c) },
                showChevron = true,
                onClick = { openLegalPage(context, LegalPage.TERMS) }
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_open_source_licenses),
                icon = { m, c -> AureoleDS.icons.Doc(modifier = m, tint = c) },
                showChevron = true,
                onClick = dialogActions.onNavigateToLicenses
            )
        )
    }
}

@Composable
private fun CareZoneGroup(actions: SettingsScreenActions, context: Context) {
    SettingsSectionGroup(label = stringResource(R.string.settings_section_care_zone)) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_reset_launcher),
                subtitle = stringResource(R.string.home_app_menu_hold_to_confirm),
                icon = { m, c -> AureoleDS.icons.Undo(modifier = m, tint = c) },
                isDestructive = true,
                requiresHold = true,
                onClick = actions.onConfirmFactoryReset
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.settings_advanced_uninstall_launcher),
                subtitle = stringResource(R.string.home_app_menu_hold_to_confirm),
                icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                isDestructive = true,
                requiresHold = true,
                onClick = { tryUninstall(context) }
            )
        )
    }
}

@AureolePreview
@Composable
private fun AdvancedSectionsPreview() {
    AureoleLauncherTheme {
        AdvancedSections(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            dialogActions = SettingsAdvancedDialogActions({}, {}, {})
        )
    }
}
