@file:Suppress("MagicNumber", "MaxLineLength")

package dev.mnascimentos.aureole.feature.settings.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import dev.mnascimentos.aureole.composable.SettingsActionItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Home
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Undo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.ReleaseNotesBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SettingsBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsAdvancedDialogActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val BOTTOM_SPACER_PERCENT = 0.15f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAdvancedScreen(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    onNavigateToContributors: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateBack)

    val context = LocalContext.current
    var showUninstallDialog by remember { mutableStateOf(false) }
    var showReleaseNotesDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        SettingsAdvancedContent(
            uiState = uiState,
            actions = actions,
            context = context,
            innerPadding = innerPadding,
            dialogActions = SettingsAdvancedDialogActions(
                onNavigateToContributors = onNavigateToContributors,
                onOpenUninstallDialog = { showUninstallDialog = true },
                onOpenReleaseNotesDialog = { showReleaseNotesDialog = true }
            )
        )

        if (showUninstallDialog) {
            UninstallBottomSheet(
                context = context,
                onDismiss = { showUninstallDialog = false }
            )
        }

        if (showReleaseNotesDialog) {
            ReleaseNotesBottomSheet(
                onDismissRequest = { showReleaseNotesDialog = false }
            )
        }
    }
}

@Composable
private fun SettingsAdvancedContent(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    context: Context,
    innerPadding: PaddingValues,
    dialogActions: SettingsAdvancedDialogActions
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        val topSpacerHeight = (maxHeight * (uiState.headerOffsetPercent / 100f))
            .coerceAtLeast(AureoleDS.dimens.medium)
        val bottomSpacerHeight = (maxHeight * BOTTOM_SPACER_PERCENT)
            .coerceAtLeast(AureoleDS.dimens.large)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AureoleDS.dimens.iconXLargeMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topSpacerHeight))

            AureoleText(
                text = "Advanced",
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.fillMaxWidth(),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(AureoleDS.dimens.large))

            AdvancedItemsGroup(
                actions = actions,
                context = context,
                onOpenUninstallDialog = dialogActions.onOpenUninstallDialog,
                onOpenReleaseNotesDialog = dialogActions.onOpenReleaseNotesDialog
            )

            Spacer(modifier = Modifier.height(AureoleDS.dimens.large))

            AdvancedLinksGroup(onNavigateToContributors = dialogActions.onNavigateToContributors)

            Spacer(modifier = Modifier.height(bottomSpacerHeight))
        }
    }
}

@Composable
private fun AdvancedItemsGroup(
    actions: SettingsScreenActions,
    context: Context,
    onOpenUninstallDialog: () -> Unit,
    onOpenReleaseNotesDialog: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingsActionItem(
            title = "Set as default launcher",
            leadingContent = { AureoleDS.icons.Home() },
            onClick = { actions.onSetDefaultLauncherClick() }
        )

        SettingsActionItem(
            title = "App info",
            leadingContent = { AureoleDS.icons.Info() },
            onClick = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
        )

        SettingsActionItem(
            title = "What's new (Release notes)",
            leadingContent = { AureoleDS.icons.Smile() },
            onClick = onOpenReleaseNotesDialog
        )

        SettingsActionItem(
            title = "Reset launcher",
            leadingContent = { AureoleDS.icons.Undo() },
            onClick = { actions.onOpenFactoryResetDialog() }
        )

        SettingsActionItem(
            title = "Uninstall launcher",
            leadingContent = { AureoleDS.icons.Delete() },
            onClick = onOpenUninstallDialog
        )
    }
}

@Composable
private fun AdvancedLinksGroup(onNavigateToContributors: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        AureoleText(
            text = "Contributors",
            color = AureoleDS.colors.onSurfaceLow,
            modifier = Modifier.clickable { onNavigateToContributors() },
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodySmall,
                fontWeight = FontWeight.Normal
            )
        )
        AureoleText(
            text = "Privacy policy",
            color = AureoleDS.colors.onSurfaceLow,
            modifier = Modifier.clickable { },
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodySmall,
                fontWeight = FontWeight.Normal
            )
        )
        AureoleText(
            text = "Terms of service",
            color = AureoleDS.colors.onSurfaceLow,
            modifier = Modifier.clickable { },
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodySmall,
                fontWeight = FontWeight.Normal
            )
        )
        AureoleText(
            text = "Privacy settings",
            color = AureoleDS.colors.onSurfaceLow,
            modifier = Modifier.clickable { },
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodySmall,
                fontWeight = FontWeight.Normal
            )
        )
        AureoleText(
            text = "Open source licenses",
            color = AureoleDS.colors.onSurfaceLow,
            modifier = Modifier.clickable { },
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodySmall,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
private fun UninstallBottomSheet(
    context: Context,
    onDismiss: () -> Unit
) {
    SettingsBottomSheet(
        title = "Uninstall launcher?",
        onDismissRequest = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
            SettingsActionItem(
                title = "Contact us",
                leadingContent = { AureoleDS.icons.Smile() },
                onClick = {
                    onDismiss()
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:aureole@mnascimentos.dev".toUri()
                    }
                    context.startActivity(intent)
                }
            )
            SettingsActionItem(
                title = "Uninstall",
                leadingContent = { AureoleDS.icons.Delete() },
                onClick = {
                    onDismiss()
                    tryUninstall(context)
                }
            )
        }
    }
}

private fun tryUninstall(context: Context) {
    try {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=${context.packageName}".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

@AureolePreview
@Composable
private fun SettingsAdvancedScreenPreview() {
    AureoleLauncherTheme {
        SettingsAdvancedScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onNavigateToContributors = {},
            onNavigateBack = {}
        )
    }
}
