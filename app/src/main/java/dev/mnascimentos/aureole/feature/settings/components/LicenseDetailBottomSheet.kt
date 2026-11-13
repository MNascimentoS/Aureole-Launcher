package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.actions.AureolePrimaryActionButton
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.LicenseType
import dev.mnascimentos.aureole.feature.settings.model.OpenSourceEntries
import dev.mnascimentos.aureole.feature.settings.model.OssEntry
import dev.mnascimentos.aureole.util.IntentUtils

/**
 * Detail of one listed item: the copyright line and the full license text in a box, and a button that opens the
 * project. Items whose license is only a link ([LicenseType.PLAY_CORE_TERMS]) show just a "View terms" button.
 */
@Composable
fun LicenseDetailBottomSheet(
    entry: OssEntry,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val textRes = entry.license.text
    val licenseText = remember(textRes) {
        textRes?.let { resources.openRawResource(it).bufferedReader().use { reader -> reader.readText() } }
    }
    val isLinkOnly = licenseText == null
    val buttonLabel = if (isLinkOnly) R.string.settings_licenses_view_terms else R.string.settings_licenses_view_project
    val buttonUrl = if (isLinkOnly) entry.license.url else entry.projectUrl

    AureoleMenuSheet(
        title = entry.name,
        onDismiss = onDismiss,
        modifier = modifier,
        params = MenuSheetParams(
            bottomBar = {
                AureolePrimaryActionButton(
                    label = stringResource(buttonLabel),
                    onClick = { IntentUtils.openUrl(context, buttonUrl) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.xxSmall),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            AureoleText(
                text = stringResource(
                    R.string.settings_licenses_author_license,
                    stringResource(R.string.settings_licenses_copyright, entry.author),
                    stringResource(entry.license.label)
                ),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
            )
            if (licenseText != null) LicenseTextBox(licenseText)
        }
    }
}

@Composable
private fun LicenseTextBox(text: String) {
    AureoleText(
        text = text,
        color = AureoleDS.colors.onSurfaceLow,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant)
            .padding(AureoleDS.dimens.medium),
        format = AureoleTextFormatParams(
            style = AureoleDS.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
        )
    )
}

@AureolePreview
@Composable
private fun LicenseDetailBottomSheetPreview() {
    AureoleLauncherTheme {
        LicenseDetailBottomSheet(entry = OpenSourceEntries.first(), onDismiss = {})
    }
}
