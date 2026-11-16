package dev.mnascimentos.aureole.core.designsystem.model

/** Accessibility actions offered by the sheet gesture zone (the invisible strip at the top of the sheet). */
internal enum class SheetHandleAction { Expand, Collapse, Settings }

/**
 * Custom accessibility actions of the sheet gesture zone, in reading order.
 *
 * Right-handed: resize first, then settings. Left-handed: settings first, the order of the controls
 * the next phases mirror to the left thumb. A sheet at rest that cannot expand ([canExpand] false, its content
 * already fits) offers no resize action.
 */
internal fun sheetHandleActions(
    isExpanded: Boolean,
    hasSettings: Boolean,
    isLeftHanded: Boolean,
    canExpand: Boolean = true
): List<SheetHandleAction> {
    val resize = when {
        isExpanded -> SheetHandleAction.Collapse
        canExpand -> SheetHandleAction.Expand
        else -> null
    }
    val actions = listOfNotNull(resize, SheetHandleAction.Settings.takeIf { hasSettings })
    return if (isLeftHanded) actions.reversed() else actions
}
