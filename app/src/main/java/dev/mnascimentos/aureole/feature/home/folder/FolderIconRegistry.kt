package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.Advanced
import dev.mnascimentos.aureole.core.designsystem.icons.Calendar
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.DragMenu
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.EyeOff
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.icons.Home
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.MenuIcon
import dev.mnascimentos.aureole.core.designsystem.icons.RefreshCcw
import dev.mnascimentos.aureole.core.designsystem.icons.RightDown
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

object FolderIconRegistry {
    // We map keys directly to the AureoleDS.icons function using a Composable lambda
    internal val icons: Map<String, @Composable (Modifier, Color?) -> Unit> = mapOf(
        "star" to { m, t -> AureoleDS.icons.Star(m, t) },
        "home" to { m, t -> AureoleDS.icons.Home(m, t) },
        "favorite" to { m, t -> AureoleDS.icons.Star(m, t) }, // Fallback from Favorite to Star
        "person" to { m, t -> AureoleDS.icons.Smile(m, t) }, // Fallback from Person to Smile
        "settings" to { m, t -> AureoleDS.icons.Settings(m, t) },
        "info" to { m, t -> AureoleDS.icons.Info(m, t) },
        "lock" to { m, t -> AureoleDS.icons.EyeOff(m, t) }, // Fallback from Lock to EyeOff
        "place" to { m, t -> AureoleDS.icons.Home(m, t) }, // Fallback from Place to Home
        "thumb_up" to { m, t -> AureoleDS.icons.Check(m, t) }, // Fallback from ThumbUp to Check
        "warning" to { m, t -> AureoleDS.icons.Info(m, t) }, // Fallback from Warning to Info
        "notifications" to { m, t -> AureoleDS.icons.Star(m, t) }, // Fallback
        "edit" to { m, t -> AureoleDS.icons.Edit(m, t) },
        "check" to { m, t -> AureoleDS.icons.Check(m, t) },
        "add" to { m, t -> AureoleDS.icons.Add(m, t) },
        "delete" to { m, t -> AureoleDS.icons.Delete(m, t) },
        "search" to { m, t -> AureoleDS.icons.Search(m, t) },
        "share" to { m, t -> AureoleDS.icons.DragMenu(m, t) }, // Fallback
        "refresh" to { m, t -> AureoleDS.icons.RefreshCcw(m, t) },
        "menu" to { m, t -> AureoleDS.icons.MenuIcon(m, t) },
        "more_vert" to { m, t -> AureoleDS.icons.Dots(m, t) }, // Fallback from MoreVert to Dots
        "location_on" to { m, t -> AureoleDS.icons.Home(m, t) }, // Fallback
        "create" to { m, t -> AureoleDS.icons.Edit(m, t) }, // Fallback from Create to Edit
        "date_range" to { m, t -> AureoleDS.icons.Calendar(m, t) },
        "play" to { m, t -> AureoleDS.icons.RightDown(m, t) }, // Fallback
        "cart" to { m, t -> AureoleDS.icons.Folder(m, t) }, // Fallback
        "build" to { m, t -> AureoleDS.icons.Advanced(m, t) }, // Fallback from Build to Advanced
    )

    private val aliases: Map<String, String> = mapOf(
        "games" to "play",
        "jogos" to "play",
        "media" to "play",
        "fun" to "thumb_up",
        "diversao" to "thumb_up",
        "ai" to "star",
        "ias" to "star",
        "banking" to "cart",
        "banco" to "cart",
        "shopping" to "cart",
        "tools" to "build",
        "ferramentas" to "build",
        "security" to "lock",
        "seguranca" to "lock",
        "work" to "person",
        "trabalho" to "person",
        "social" to "favorite"
    )

    @Composable
    fun RenderIcon(
        name: String?,
        modifier: Modifier = Modifier,
        tint: Color? = null
    ) {
        val lower = name?.lowercase()?.trim() ?: ""
        val key = aliases[lower] ?: aliases[lower.replace(" ", "_")] ?: lower
        val iconComposable = icons[key] ?: icons[key.replace(" ", "_")] ?: icons["folder"]
        if (iconComposable != null) {
            iconComposable(modifier, tint)
        } else {
            AureoleDS.icons.Folder(modifier, tint)
        }
    }
}
