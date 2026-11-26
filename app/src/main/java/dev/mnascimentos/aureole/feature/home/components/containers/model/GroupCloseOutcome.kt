package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.mnascimentos.aureole.core.data.model.ContainerModel

/** What closing the group sheet does on the home (RF-B7, RF-B8). */
sealed interface GroupCloseOutcome {
    /** Nothing changed: no snackbar. */
    data object Unchanged : GroupCloseOutcome

    /** The group changed: "Group updated", and Undo puts [previous] back. */
    data class Updated(val previous: ContainerModel) : GroupCloseOutcome

    /** A group just added got items: "Group added to the home", and Undo removes the group. */
    data object Added : GroupCloseOutcome

    /** A group just added was closed empty: it leaves the grid, with no snackbar (D-11). */
    data object DiscardedEmpty : GroupCloseOutcome
}

/** The outcome of closing the sheet on [saved], opened on [previous]; [isNew] for a group just added (RF-A4). */
fun groupCloseOutcome(previous: ContainerModel?, saved: ContainerModel, isNew: Boolean): GroupCloseOutcome = when {
    isNew && saved.items.isEmpty() -> GroupCloseOutcome.DiscardedEmpty
    isNew -> GroupCloseOutcome.Added
    previous != null && previous != saved -> GroupCloseOutcome.Updated(previous)
    else -> GroupCloseOutcome.Unchanged
}
