@file:Suppress("TooManyFunctions", "LargeClass", "FileLength")
// Suppressed detekt rules because this file serves as the unified Design System icon catalog.

package dev.mnascimentos.aureole.core.designsystem.icons

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

/**
 * A central registry of all icons used in the Aureole Design System.
 */
@Immutable
class AureoleIcons

// Base Shared Icons
@Composable
fun AureoleIcons.Calendar(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_calendar, modifier, tint) }

@Composable
fun AureoleIcons.Icon(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_icon, modifier, tint) }

@Composable
fun AureoleIcons.Placeholder(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_placeholder, modifier, tint) }

@Composable
fun AureoleIcons.Smile(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_smile, modifier, tint) }

@Composable
fun AureoleIcons.Logo(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_logo, modifier, tint) }

@Composable
fun AureoleIcons.Add(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_add, modifier, tint) }

@Composable
fun AureoleIcons.Advanced(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_advanced, modifier, tint) }

@Composable
fun AureoleIcons.AlignRight(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_align_right, modifier, tint) }

// Set A
@Composable
fun AureoleIcons.AppSelect(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_app_select, modifier, tint) }

@Composable
fun AureoleIcons.ArrowDown(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_arrow_up, modifier.rotate(180f), tint) }

@Composable
fun AureoleIcons.ArrowUp(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_arrow_up, modifier, tint) }

@Composable
fun AureoleIcons.Close(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_add, modifier.rotate(45f), tint) }

@Composable
fun AureoleIcons.Check(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_check, modifier, tint) }

@Composable
fun AureoleIcons.Click(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_click, modifier, tint) }

@Composable
fun AureoleIcons.ColorFill(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_color_fill, modifier, tint) }

@Composable
fun AureoleIcons.Corners(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_corners, modifier, tint) }

@Composable
fun AureoleIcons.Delete(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_delete, modifier, tint) }

@Composable
fun AureoleIcons.Dots(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_dots, modifier, tint) }

@Composable
fun AureoleIcons.DragMenu(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_drag_menu, modifier, tint) }

// Set B
@Composable
fun AureoleIcons.Edit(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_edit, modifier, tint) }

@Composable
fun AureoleIcons.EyeOff(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_eye_off, modifier, tint) }

@Composable
fun AureoleIcons.Folder(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_folder, modifier, tint) }

@Composable
fun AureoleIcons.FolderStroke(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_folder_stroke, modifier, tint) }

@Composable
fun AureoleIcons.FolderStrokeColor(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_folder_stroke_color, modifier, tint) }

@Composable
fun AureoleIcons.Glass(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_glass, modifier, tint) }

@Composable
fun AureoleIcons.Header(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_header, modifier, tint) }

@Composable
fun AureoleIcons.Home(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_home, modifier, tint) }

@Composable
fun AureoleIcons.HorizontalContainer(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_horizontal_contaner, modifier, tint) }

@Composable
fun AureoleIcons.Info(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_info, modifier, tint) }

// Set C
@Composable
fun AureoleIcons.Keyboard(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_keyboard, modifier, tint) }

@Composable
fun AureoleIcons.Layout(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_layout, modifier, tint) }

@Composable
fun AureoleIcons.LeftUp(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_left_up, modifier, tint) }

@Composable
fun AureoleIcons.MenuIcon(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_menu_icon, modifier, tint) }

@Composable
fun AureoleIcons.Press(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_press, modifier, tint) }

@Composable
fun AureoleIcons.RefreshCcw(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_refresh_ccw, modifier, tint) }

@Composable
fun AureoleIcons.RightDown(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_right_down, modifier, tint) }

@Composable
fun AureoleIcons.Search(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_search, modifier, tint) }

@Composable
fun AureoleIcons.Settings(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_settings, modifier, tint) }

@Composable
fun AureoleIcons.SmileColor(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_smile_color, modifier, tint) }

// Set D
@Composable
fun AureoleIcons.Star(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_star, modifier, tint) }

@Composable
fun AureoleIcons.Stroke(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_stroke, modifier, tint) }

@Composable
fun AureoleIcons.StrokeColor(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_stroke_color, modifier, tint) }

@Composable
fun AureoleIcons.Text(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_text, modifier, tint) }

@Composable
fun AureoleIcons.TextColor(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_text_color, modifier, tint) }

@Composable
fun AureoleIcons.TinyMenu(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_tiny_menu, modifier, tint) }

@Composable
fun AureoleIcons.Undo(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_undo, modifier, tint) }

@Composable
fun AureoleIcons.VerticalContainer(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_vertical_contaner, modifier, tint) }

@Composable
fun AureoleIcons.CheckSelected(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_check_selected, modifier, tint) }

@Composable
fun AureoleIcons.CheckUnselected(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_check_unselected, modifier, tint) }

@Composable
fun AureoleIcons.MultipleView(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_multiple_view, modifier, tint) }

@Composable
fun AureoleIcons.RoundCheck(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_round_check, modifier, tint) }

@Composable
fun AureoleIcons.Unchecked(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_unchecked, modifier, tint) }

@Composable
internal fun AureoleIcon(
    iconResId: Int,
    modifier: Modifier = Modifier,
    tint: Color? = null
) {
    Icon(
        painter = painterResource(id = iconResId),
        contentDescription = null,
        modifier = modifier,
        tint = tint ?: AureoleDS.colors.onSurfaceMedium
    )
}

val LocalAureoleIcons = staticCompositionLocalOf { AureoleIcons() }
