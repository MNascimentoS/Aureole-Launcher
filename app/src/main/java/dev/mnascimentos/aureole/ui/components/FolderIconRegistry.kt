package dev.mnascimentos.aureole.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

object FolderIconRegistry {
    val icons: Map<String, ImageVector> = mapOf(
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
    )

    fun getIcon(name: String?): ImageVector? = name?.let { icons[it] }
}
