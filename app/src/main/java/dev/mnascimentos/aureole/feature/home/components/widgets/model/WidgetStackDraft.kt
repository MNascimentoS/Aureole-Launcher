package dev.mnascimentos.aureole.feature.home.components.widgets.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.mnascimentos.aureole.composable.reorder.model.move

/**
 * Working copy of the widget stack in the editor. [widgets] follows the stack while a card is dragged and goes back
 * to the saved order whenever [sync] receives a new one (an undo included).
 */
class WidgetStackDraft(saved: List<Int>) {
    var widgets by mutableStateOf(saved)
        private set
    private var selectedId by mutableStateOf<Int?>(null)

    /** Bumped when the preview has to scroll the selected card into view. */
    var revealToken by mutableIntStateOf(0)
        private set

    /** The widget the "Move" row acts on; the first one until a card is tapped or dragged. */
    val selected: Int? get() = selectedId?.takeIf { it in widgets } ?: widgets.firstOrNull()

    /** Replaces the draft with the [saved] order. */
    fun sync(saved: List<Int>) {
        widgets = saved
    }

    fun select(id: Int?) {
        selectedId = id
    }

    fun remove(id: Int) {
        widgets = widgets - id
    }

    /** A card dragged from position [from] to [to]; it becomes the selected one. */
    fun drag(from: Int, to: Int) {
        selectedId = widgets.getOrNull(from)
        widgets = move(widgets, from, to)
    }

    /** The move row sent [id] to position [target]; the preview scrolls to it. */
    fun moveTo(id: Int, target: Int) {
        selectedId = id
        revealToken++
        widgets = move(widgets, widgets.indexOf(id), target)
    }
}
