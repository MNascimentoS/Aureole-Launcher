package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef

/** The color of the quick button that a row of swatches in the appearance editor changes. */
internal enum class QuickButtonColorTarget {
    BACKGROUND, STROKE, ICON
}

/** The color of [target] in this appearance. */
internal fun QuickButtonAppearance.colorOf(target: QuickButtonColorTarget): ThemeColorRef = when (target) {
    QuickButtonColorTarget.BACKGROUND -> backgroundColor
    QuickButtonColorTarget.STROKE -> strokeColor
    QuickButtonColorTarget.ICON -> iconColor
}

/** This appearance with the color of [target] replaced by [color]; the other colors are kept. */
internal fun QuickButtonAppearance.withColor(target: QuickButtonColorTarget, color: ThemeColorRef): QuickButtonAppearance =
    when (target) {
        QuickButtonColorTarget.BACKGROUND -> copy(backgroundColor = color)
        QuickButtonColorTarget.STROKE -> copy(strokeColor = color)
        QuickButtonColorTarget.ICON -> copy(iconColor = color)
    }
