package dev.mnascimentos.aureole.feature.home.components.containers.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** The three tips of the first group (RF-F2), in order; [number] is the step shown on the card. */
enum class GroupTip(val number: Int) {
    /** Items tab of the empty group: "This is your app & folder group", button "Add app". */
    FIRST_APP(1),

    /** Items tab, back from "Add apps" with an item: "Done, the group is on the home", button "Choose position". */
    CHOOSE_POSITION(2),

    /** Layout tab: "Standing or lying down?"; picking a position ends the tips. */
    PICK_POSITION(3);

    /** The tab whose content shows this tip at its top. */
    val tab: GroupTab
        get() = if (this == PICK_POSITION) GroupTab.LAYOUT else GroupTab.ITEMS

    companion object {
        const val COUNT = 3
    }
}

/** The tip a group sheet opens with: the first one for a group just added, while the tips were never seen (D-07). */
fun firstGroupTip(isNew: Boolean, areTipsSeen: Boolean): GroupTip? =
    if (isNew && !areTipsSeen) GroupTip.FIRST_APP else null

/**
 * The tips of the first group as the user goes (RF-F1, RF-F2): [tip] is the one shown, null once they are over.
 * Skipping or the last tip ends them and calls [onFinished] once, which saves `container_tips_seen`.
 */
class GroupTipFlow(initial: GroupTip?, private val onFinished: () -> Unit) {
    var tip: GroupTip? by mutableStateOf(initial)
        private set

    /** Back from "Add apps": with an item in the group, tip 1 becomes tip 2. */
    fun onAppsPageClosed(itemCount: Int) {
        if (tip == GroupTip.FIRST_APP && itemCount > 0) move(GroupTip.CHOOSE_POSITION)
    }

    /** The Layout tab opened (its button or the tab bar): tip 2 becomes tip 3. */
    fun onTabSelected(tab: GroupTab) {
        if (tip == GroupTip.CHOOSE_POSITION && tab == GroupTab.LAYOUT) move(GroupTip.PICK_POSITION)
    }

    /** A position was picked: tip 3 was the last one. */
    fun onPositionPicked() {
        if (tip == GroupTip.PICK_POSITION) move(null)
    }

    /** "Skip tips". */
    fun skip() {
        if (tip != null) move(null)
    }

    private fun move(next: GroupTip?) {
        tip = next
        if (next == null) onFinished()
    }
}
