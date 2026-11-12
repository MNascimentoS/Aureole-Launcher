package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.sheets.model.showsSectionTitles
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val BADGE_ALPHA = 0.2f
private const val CARD_ALPHA = 0.35f

/**
 * Body of the release notes sheet for one [version]: the "Version x" label, then one card per change (badge,
 * title, description). Section titles show as labels only when the version has several sections.
 */
@Composable
internal fun ReleaseNotesBody(version: ReleaseNoteVersion, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        val showTitles = showsSectionTitles(version.sections)
        version.sections.forEach { section ->
            if (showTitles) {
                AureoleMenuSectionLabel(text = section.title)
            }
            section.items.forEach { item -> ReleaseNoteItemCard(item = item) }
        }
    }
}

@Composable
private fun ReleaseNoteItemCard(item: ReleaseNoteItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = CARD_ALPHA))
            .padding(AureoleDS.dimens.medium),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        TypeBadge(type = item.type)
        AureoleText(
            text = item.title,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall, fontWeight = FontWeight.Bold)
        )
        AureoleText(
            text = item.description,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
    }
}

@Composable
private fun TypeBadge(type: ReleaseNoteType, modifier: Modifier = Modifier) {
    val (label, tone) = when (type) {
        ReleaseNoteType.FEATURE -> stringResource(R.string.settings_advanced_release_notes_tag_new) to
            AureoleDS.colors.badgeText
        ReleaseNoteType.IMPROVEMENT -> stringResource(R.string.settings_advanced_release_notes_tag_improvement) to
            AureoleDS.colors.info
        ReleaseNoteType.FIX -> stringResource(R.string.settings_advanced_release_notes_tag_fix) to
            AureoleDS.colors.error
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(tone.copy(alpha = BADGE_ALPHA))
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.minimal)
    ) {
        AureoleText(
            text = label.uppercase(),
            color = tone,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall, fontWeight = FontWeight.Bold)
        )
    }
}

@AureolePreview
@Composable
private fun ReleaseNotesBodyPreview() {
    AureoleLauncherTheme {
        ReleaseNotesBody(
            version = ReleaseNoteVersion(
                versionName = "0.9.13",
                releaseDate = "",
                sections = listOf(
                    ReleaseNoteSection(
                        title = "Section",
                        items = listOf(
                            ReleaseNoteItem("Guided first steps", "A quick tour shows what matters."),
                            ReleaseNoteItem(
                                "Easier settings",
                                "What you use most is closer.",
                                ReleaseNoteType.IMPROVEMENT
                            )
                        )
                    )
                )
            ),
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        )
    }
}
