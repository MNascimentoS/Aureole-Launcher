package dev.mnascimentos.aureole.core.data.model

import androidx.annotation.Keep
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import java.util.UUID

@Keep
data class AppFolder(
    val id: String = UUID.randomUUID().toString(),
    val panelId: String? = null,
    val name: String,
    val iconPackage: String? = null,
    val icon: String? = null,
    val appPackageNames: List<String> = emptyList(),
    val displayAsGrid: Boolean = false,
    /** What the folder shows: [icon] (an icon key), the first letter of [name] or [emoji]. */
    val iconType: FolderIconType = FolderIconType.ICON,
    val emoji: String? = null,
    /** Own color of the folder (a theme role or a fixed color); null uses the color of the container. */
    val color: ThemeColorRef? = null,
)
