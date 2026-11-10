package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

/** What one hold-and-slide gesture does to every app it reaches. */
enum class DragSelectionMode { Mark, Unmark }

/**
 * Reducer of the hold-and-slide gesture of the folder picker. The app under the finger when the long press
 * fires decides the mode: an unmarked app starts marking, a marked one starts unmarking. Every app the
 * finger reaches afterwards gets that mode once per gesture, so sliding back over it changes nothing.
 *
 * [selectedPackages] is the picker's local selection; it is changed in place.
 */
class DragSelection(private val selectedPackages: MutableList<String>) {

    var mode: DragSelectionMode? = null
        private set

    private val visited = mutableSetOf<String>()
    private var changedCount = 0

    val isActive: Boolean get() = mode != null

    /** Starts a gesture on [pkg]; returns true, the start always ticks. */
    fun start(pkg: String, isSelected: Boolean): Boolean {
        mode = if (isSelected) DragSelectionMode.Unmark else DragSelectionMode.Mark
        visited.clear()
        changedCount = 0
        return enter(pkg)
    }

    /** The finger reached [pkg]. Returns true when it is the first time in this gesture: one tic per app. */
    fun enter(pkg: String): Boolean {
        val current = mode ?: return false
        if (!visited.add(pkg)) return false
        val isSelected = pkg in selectedPackages
        when {
            current == DragSelectionMode.Mark && !isSelected -> selectedPackages.add(pkg)
            current == DragSelectionMode.Unmark && isSelected -> selectedPackages.remove(pkg)
            else -> return true
        }
        changedCount++
        return true
    }

    /** Ends the gesture and returns how many apps changed. A gesture that never started returns 0. */
    fun end(): Int {
        val changed = changedCount
        mode = null
        visited.clear()
        changedCount = 0
        return changed
    }
}
