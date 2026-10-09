package dev.mnascimentos.aureole.feature.home.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion
import dev.mnascimentos.aureole.core.data.repository.ReleaseNotesRepository
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.SettingsBottomSheet

@Composable
fun ReleaseNotesBottomSheet(
    onDismissRequest: () -> Unit,
    allVersions: List<ReleaseNoteVersion> = remember { ReleaseNotesRepository.getReleaseNotes() }
) {
    var selectedVersion by remember {
        mutableStateOf(allVersions.firstOrNull { it.isCurrent } ?: allVersions.first())
    }

    SettingsBottomSheet(
        title = "",
        onDismissRequest = onDismissRequest
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
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                AureoleText(
                    text = "Entendido",
                    style = AureoleTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
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
            modifier = Modifier.size(64.dp, 36.dp),
            tint = AureoleTheme.colors.onSurfaceHigh
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        AureoleText(
            text = "O que há de novo",
            style = AureoleTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AureoleTheme.colors.onSurfaceHigh
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AureoleTheme.colors.outline.copy(alpha = OUTLINE_ALPHA))
                .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xxSmall)
        ) {
            AureoleText(
                text = "Versão $versionName",
                style = AureoleTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = AureoleTheme.colors.onSurfaceMedium
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
        items(versions) { version ->
            val isSelected = (version.versionName == selectedVersion.versionName)
            val bg = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                AureoleTheme.colors.outline.copy(alpha = CHIP_OUTLINE_ALPHA)
            }
            val textColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                AureoleTheme.colors.onSurfaceMedium
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bg)
                    .clickable { onVersionSelected(version) }
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall)
            ) {
                AureoleText(
                    text = if (version.isCurrent) "v${version.versionName} (Atual)" else "v${version.versionName}",
                    style = AureoleTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
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
            style = AureoleTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleTheme.colors.onSurfaceHigh,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.small)
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
            .clip(RoundedCornerShape(16.dp))
            .background(AureoleTheme.colors.outline.copy(alpha = CARD_OUTLINE_ALPHA))
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
                style = AureoleTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = AureoleTheme.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
        AureoleText(
            text = item.description,
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceMedium
        )
    }
}

private const val FIX_BADGE_CONTAINER_COLOR = 0xFF4CAF50
private const val FIX_BADGE_TEXT_COLOR = 0xFF81C784
private const val BADGE_ALPHA = 0.2f
private const val OUTLINE_ALPHA = 0.15f
private const val CHIP_OUTLINE_ALPHA = 0.12f
private const val CARD_OUTLINE_ALPHA = 0.08f

@Composable
private fun TypeBadge(type: ReleaseNoteType) {
    val (label, containerColor, textColor) = when (type) {
        ReleaseNoteType.FEATURE -> Triple(
            "Novo",
            MaterialTheme.colorScheme.primary.copy(alpha = BADGE_ALPHA),
            MaterialTheme.colorScheme.primary
        )
        ReleaseNoteType.IMPROVEMENT -> Triple(
            "Melhoria",
            AureoleTheme.colors.outline.copy(alpha = BADGE_ALPHA),
            AureoleTheme.colors.onSurfaceHigh
        )
        ReleaseNoteType.FIX -> Triple(
            "Correção",
            Color(FIX_BADGE_CONTAINER_COLOR).copy(alpha = BADGE_ALPHA),
            Color(FIX_BADGE_TEXT_COLOR)
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        AureoleText(
            text = label,
            style = AureoleTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@AureolePreview
@Composable
fun ReleaseNotesBottomSheetPreview() {
    AureoleLauncherTheme {
        ReleaseNotesBottomSheet(onDismissRequest = {})
    }
}
