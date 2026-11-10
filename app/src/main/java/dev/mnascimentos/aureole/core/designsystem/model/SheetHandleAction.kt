package dev.mnascimentos.aureole.core.designsystem.model

/** Accessibility actions offered by the sheet handle. */
internal enum class SheetHandleAction { Expand, Collapse, Settings }

/**
 * Custom accessibility actions of the sheet handle, in reading order.
 *
 * Right-handed: resize first, then settings. Left-handed: settings first, the order of the controls
 * the next phases mirror to the left thumb.
 */
internal fun sheetHandleActions(
    isExpanded: Boolean,
    hasSettings: Boolean,
    isLeftHanded: Boolean
): List<SheetHandleAction> {
    val resize = if (isExpanded) SheetHandleAction.Collapse else SheetHandleAction.Expand
    val actions = if (hasSettings) listOf(resize, SheetHandleAction.Settings) else listOf(resize)
    return if (isLeftHanded) actions.reversed() else actions
}
