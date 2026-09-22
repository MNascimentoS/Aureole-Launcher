package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

object FolderIconRegistry {
    internal val icons: Map<String, ImageVector> = mapOf(
        "star" to Icons.Default.Star,
        "home" to Icons.Default.Home,
        "favorite" to Icons.Default.Favorite,
        "person" to Icons.Default.Person,
        "settings" to Icons.Default.Settings,
        "info" to Icons.Default.Info,
        "lock" to Icons.Default.Lock,
        "place" to Icons.Default.Place,
        "thumb_up" to Icons.Default.ThumbUp,
        "warning" to Icons.Default.Warning,
        "notifications" to Icons.Default.Notifications,
        "edit" to Icons.Default.Edit,
        "check" to Icons.Default.Check,
        "add" to Icons.Default.Add,
        "delete" to Icons.Default.Delete,
        "search" to Icons.Default.Search,
        "share" to Icons.Default.Share,
        "refresh" to Icons.Default.Refresh,
        "menu" to Icons.Default.Menu,
        "more_vert" to Icons.Default.MoreVert,
        "location_on" to Icons.Default.LocationOn,
        "create" to Icons.Default.Create,
        "date_range" to Icons.Default.DateRange,
        "play" to Icons.Default.PlayArrow,
        "cart" to Icons.Default.ShoppingCart,
        "build" to Icons.Default.Build,
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

    fun getIcon(name: String?): ImageVector? {
        if (name == null) return null
        val lower = name.lowercase().trim()
        val key = aliases[lower] ?: aliases[lower.replace(" ", "_")] ?: lower
        return icons[key] ?: icons[key.replace(" ", "_")]
    }
}
