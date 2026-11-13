package dev.mnascimentos.aureole.composable.sheets.model

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion

/** Name of the version the release notes sheet opens on: the current one, else the newest; null without versions. */
fun initialReleaseVersionName(versions: List<ReleaseNoteVersion>): String? =
    (versions.firstOrNull { it.isCurrent } ?: versions.firstOrNull())?.versionName

/** The version called [selectedName], or the initial one when that name is not in [versions] anymore. */
fun resolveReleaseVersion(versions: List<ReleaseNoteVersion>, selectedName: String?): ReleaseNoteVersion? =
    versions.firstOrNull { it.versionName == selectedName }
        ?: versions.firstOrNull { it.versionName == initialReleaseVersionName(versions) }

/** Section titles only help when a version has more than one section; a lone section just lists its cards. */
fun showsSectionTitles(sections: List<ReleaseNoteSection>): Boolean = sections.size > 1
