package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.model.LogoDimensions
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion
import dev.mnascimentos.aureole.core.data.repository.ReleaseNotesRepository
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun ReleaseNotesBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    allVersions: List<ReleaseNoteVersion> = remember { ReleaseNotesRepository.getReleaseNotes() }
) {
    var selectedVersion by remember {
        mutableStateOf(allVersions.firstOrNull { it.isCurrent } ?: allVersions.first())
    }

    SettingsBottomSheet(
        title = "",
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HeaderSection(versionName = selectedVersion.versionName)

            Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

            if (allVersions.size > 1) {
                VersionChipsRow(
                    versions = allVersions,
                    selectedVersion = selectedVersion,
                    onVersionSelected = { selectedVersion = it }
                )
                Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                selectedVersion.sections.forEach { section ->
                    ReleaseNoteSectionView(section = section)
                    Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
                }
            }

            Spacer(modifier = Modifier.height(AureoleDS.dimens.large))

            Button(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AureoleDS.colors.onSurfaceHigh,
                    contentColor = AureoleDS.colors.surface
                )
            ) {
                AureoleText(
                    text = stringResource(R.string.core_common_understood),
                    color = AureoleDS.colors.surface,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(versionName: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AureoleDS.icons.Logo(
            modifier = Modifier.size(LogoDimensions.Width, LogoDimensions.Height),
            tint = AureoleDS.colors.onSurfaceHigh
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        AureoleText(
            text = stringResource(R.string.settings_advanced_release_notes_whats_new),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                .background(AureoleDS.colors.outline.copy(alpha = OUTLINE_ALPHA))
                .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xxSmall)
        ) {
            AureoleText(
                text = stringResource(R.string.settings_advanced_release_notes_version, versionName),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun VersionChipsRow(
    versions: List<ReleaseNoteVersion>,
    selectedVersion: ReleaseNoteVersion,
    onVersionSelected: (ReleaseNoteVersion) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = versions,
            key = { it.versionName },
            contentType = { "version_chip" }
        ) { version ->
            val isSelected = (version.versionName == selectedVersion.versionName)
            val bg = if (isSelected) {
                AureoleDS.colors.onSurfaceHigh
            } else {
                AureoleDS.colors.outline.copy(alpha = CHIP_OUTLINE_ALPHA)
            }
            val textColor = if (isSelected) {
                AureoleDS.colors.surface
            } else {
                AureoleDS.colors.onSurfaceMedium
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusMediumLarge))
                    .background(bg)
                    .clickable { onVersionSelected(version) }
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall)
            ) {
                AureoleText(
                    text = if (version.isCurrent) {
                        stringResource(R.string.settings_advanced_release_notes_version_current, version.versionName)
                    } else {
                        stringResource(R.string.settings_advanced_release_notes_version_past, version.versionName)
                    },
                    color = textColor,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun ReleaseNoteSectionView(section: ReleaseNoteSection) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AureoleText(
            text = section.title,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.small),
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
        )

        section.items.forEach { item ->
            ReleaseNoteItemCard(item = item)
            Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        }
    }
}

@Composable
private fun ReleaseNoteItemCard(item: ReleaseNoteItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.outline.copy(alpha = CARD_OUTLINE_ALPHA))
            .padding(AureoleDS.dimens.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TypeBadge(type = item.type)
            Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
            AureoleText(
                text = item.title,
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
        AureoleText(
            text = item.description,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
    }
}

private const val BADGE_ALPHA = 0.2f
private const val OUTLINE_ALPHA = 0.15f
private const val CHIP_OUTLINE_ALPHA = 0.12f
private const val CARD_OUTLINE_ALPHA = 0.08f

@Composable
private fun TypeBadge(type: ReleaseNoteType) {
    val (label, containerColor, textColor) = when (type) {
        ReleaseNoteType.FEATURE -> Triple(
            stringResource(R.string.settings_advanced_release_notes_tag_new),
            AureoleDS.colors.onSurfaceHigh.copy(alpha = BADGE_ALPHA),
            AureoleDS.colors.onSurfaceHigh
        )
        ReleaseNoteType.IMPROVEMENT -> Triple(
            stringResource(R.string.settings_advanced_release_notes_tag_improvement),
            AureoleDS.colors.outline.copy(alpha = BADGE_ALPHA),
            AureoleDS.colors.onSurfaceHigh
        )
        ReleaseNoteType.FIX -> Triple(
            stringResource(R.string.settings_advanced_release_notes_tag_fix),
            AureoleDS.colors.badgeContainer.copy(alpha = BADGE_ALPHA),
            AureoleDS.colors.badgeText
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(containerColor)
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.minimal)
    ) {
        AureoleText(
            text = label,
            color = textColor,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall, fontWeight = FontWeight.Bold)
        )
    }
}

@AureolePreview
@Composable
private fun ReleaseNotesBottomSheetPreview() {
    AureoleLauncherTheme {
        ReleaseNotesBottomSheet(onDismissRequest = {})
    }
}
