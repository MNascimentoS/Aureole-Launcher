package dev.mnascimentos.aureole.feature.home.components.containers.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.CONTAINER_BACKGROUND_THEME_REF
import dev.mnascimentos.aureole.core.data.model.CONTAINER_ITEM_THEME_REF
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef

/** Tabs of "Customize group", in the order of the bar at the foot of the sheet (RF-B1, D-03). */
enum class GroupTab(@StringRes val labelRes: Int) {
    ITEMS(R.string.home_container_group_tab_items),
    LAYOUT(R.string.home_container_group_tab_layout),
    APPEARANCE(R.string.home_container_group_tab_appearance)
}

/** The two colors of a group, each a row of the Appearance tab and a color page (RF-E1, RF-E2). */
enum class GroupColorTarget(@StringRes val titleRes: Int, val themeRef: ThemeColorRef) {
    BACKGROUND(R.string.home_container_group_color_background, CONTAINER_BACKGROUND_THEME_REF),
    ITEM(R.string.home_container_group_color_item, CONTAINER_ITEM_THEME_REF)
}

/**
 * What the group sheet shows: its tabs, or a page in their place (RF-B10, D-04), with a back arrow to the tab it
 * came from and no tab bar.
 */
sealed interface GroupSheetPage {
    data object Tabs : GroupSheetPage
    data object AddApps : GroupSheetPage
    data object NewFolder : GroupSheetPage
    data object QuickButton : GroupSheetPage
    data class Color(val target: GroupColorTarget) : GroupSheetPage
    data class FolderEditor(val folderId: String) : GroupSheetPage
    data class QuickButtonEditor(val quickButtonId: String) : GroupSheetPage
}

/** The alignment options of the Layout tab (RF-D2), each with the position it saves. */
enum class GroupAlignment(val position: String) {
    START("Top"),
    CENTER("Center"),
    END("Bottom")
}

/** True for a group lying down, as a dock ("Horizontal"); false for one standing up on the side. */
fun isLyingDown(orientation: String): Boolean = orientation.equals(ORIENTATION_HORIZONTAL, ignoreCase = true)

/** The saved orientation of a group lying down ([isLyingDown]) or standing up. */
fun orientationOf(isLyingDown: Boolean): String = if (isLyingDown) ORIENTATION_HORIZONTAL else ORIENTATION_VERTICAL

/**
 * The label of [alignment] for a group standing up (Top / Center / Bottom) or lying down (Left / Center / Right):
 * the same saved value, named after where the items go (RF-D2).
 */
@StringRes
fun alignmentLabelRes(alignment: GroupAlignment, isLyingDown: Boolean): Int = when (alignment) {
    GroupAlignment.START ->
        if (isLyingDown) R.string.home_container_group_align_left else R.string.home_container_group_align_top
    GroupAlignment.CENTER -> R.string.home_container_group_align_center
    GroupAlignment.END ->
        if (isLyingDown) R.string.home_container_group_align_right else R.string.home_container_group_align_bottom
}

/** The alignment option a saved [position] stands for; -1 for the old spread ones, which stay as they are (D-14). */
fun alignmentIndexOf(position: String): Int = GroupAlignment.entries.indexOfFirst { it.position == position }

/** Fixed values of the group sheet that are not design system tokens. */
object GroupSheetMetrics {
    /** The "+" slots after the first one in the preview of an empty group (RF-B4). */
    const val FADED_SLOT_ALPHA = 0.4f

    /** Fewest apps a new folder takes (D-09). */
    const val MIN_FOLDER_APPS = 2

    /** "+" slots in the preview of an empty group, and after the items of one that has some (RF-B4). */
    const val EMPTY_GROUP_SLOTS = 3

    /** Opacity of a disabled button (Material's 38%). */
    const val DISABLED_ALPHA = 0.38f
}

/** How many "+" slots the preview shows after [itemCount] items (RF-B4). */
fun addSlotCountFor(itemCount: Int): Int = if (itemCount == 0) GroupSheetMetrics.EMPTY_GROUP_SLOTS else 1

private const val ORIENTATION_HORIZONTAL = "Horizontal"
private const val ORIENTATION_VERTICAL = "Vertical"

/**
 * What the group sheet's frame depends on: whether it [isVisible] (it slides out while closing), the thumb side
 * ([isLeftHanded]) and the [contentKey] that tells the sheet its content swapped for one of another height.
 */
data class GroupSheetFrame(val isVisible: Boolean, val isLeftHanded: Boolean, val contentKey: Any)
