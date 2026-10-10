package dev.mnascimentos.aureole.core.data.model

enum class ReleaseNoteType {
    FEATURE,
    IMPROVEMENT,
    FIX
}

data class ReleaseNoteItem(
    val title: String,
    val description: String,
    val type: ReleaseNoteType = ReleaseNoteType.FEATURE
)

data class ReleaseNoteSection(
    val title: String,
    val items: List<ReleaseNoteItem>
)

data class ReleaseNoteVersion(
    val versionName: String,
    val releaseDate: String,
    val isCurrent: Boolean = false,
    val sections: List<ReleaseNoteSection>
)
