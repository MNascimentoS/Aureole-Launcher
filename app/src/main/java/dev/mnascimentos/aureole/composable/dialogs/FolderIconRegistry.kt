package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dev.mnascimentos.aureole.composable.dialogs.model.FOLDER_ICON_TRASH_KEY
import dev.mnascimentos.aureole.composable.dialogs.model.folderIconCatalogKey
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Draws a saved folder icon key with the unified [AureoleIconCatalog]. */
object FolderIconRegistry {
    /** Old keys whose drawing is not in the catalog; they keep their design system icon. */
    internal val icons: Map<String, @Composable (Modifier, Color?) -> Unit> = mapOf(
        FOLDER_ICON_TRASH_KEY to { m, t -> AureoleDS.icons.Delete(m, t) },
    )

    /**
     * Draws the icon saved under [name] (any key ever saved, see [folderIconCatalogKey]). The catalog vectors are
     * white, so they are tinted with [tint] (the high emphasis text color when null).
     */
    @Composable
    fun RenderIcon(
        name: String?,
        modifier: Modifier = Modifier,
        tint: Color? = null
    ) {
        val key = folderIconCatalogKey(name)
        val res = AureoleIconCatalog.resFor(key)
        if (res != null) {
            Icon(
                painter = painterResource(res),
                contentDescription = null,
                modifier = modifier,
                tint = tint ?: AureoleDS.colors.onSurfaceHigh
            )
        } else {
            icons[key]?.invoke(modifier, tint)
        }
    }
}

@AureolePreview
@Composable
private fun FolderIconRegistryPreview() {
    AureoleLauncherTheme {
        FolderIconRegistry.RenderIcon(name = "star")
    }
}
