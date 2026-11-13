package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dev.mnascimentos.aureole.composable.dialogs.model.resolveFolderIconKey
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleFolderIcons
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.MenuIcon
import dev.mnascimentos.aureole.core.designsystem.icons.RefreshCcw
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

object FolderIconRegistry {
    /** Old keys without a drawable in the catalog; they keep their design system icon. */
    internal val icons: Map<String, @Composable (Modifier, Color?) -> Unit> = mapOf(
        "edit" to { m, t -> AureoleDS.icons.Edit(m, t) },
        "check" to { m, t -> AureoleDS.icons.Check(m, t) },
        "add" to { m, t -> AureoleDS.icons.Add(m, t) },
        "delete" to { m, t -> AureoleDS.icons.Delete(m, t) },
        "search" to { m, t -> AureoleDS.icons.Search(m, t) },
        "refresh" to { m, t -> AureoleDS.icons.RefreshCcw(m, t) },
        "menu" to { m, t -> AureoleDS.icons.MenuIcon(m, t) },
        "more_vert" to { m, t -> AureoleDS.icons.Dots(m, t) },
    )

    /**
     * Draws the icon saved under [name]. The catalog vectors are white, so they are tinted with [tint]
     * (the high emphasis text color when null).
     */
    @Composable
    fun RenderIcon(
        name: String?,
        modifier: Modifier = Modifier,
        tint: Color? = null
    ) {
        val key = resolveFolderIconKey(name)
        val res = AureoleFolderIcons.resFor(key)
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
