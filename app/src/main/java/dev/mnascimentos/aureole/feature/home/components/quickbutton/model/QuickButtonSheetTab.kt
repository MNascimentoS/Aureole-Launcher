package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType

/** The two tabs of the quick button sheet (botao-rapido RF-03): the icon catalog and the colors. */
internal enum class QuickButtonSheetTab { ICON, COLORS }

/** The color the "Icon color | Background color" control edits. The stroke color has its own picker. */
internal val QUICK_BUTTON_COLOR_TARGETS: List<QuickButtonColorTarget> =
    listOf(QuickButtonColorTarget.ICON, QuickButtonColorTarget.BACKGROUND)

/** Contrast ratio under which the preview warns that the icon is hard to see (botao-rapido RF-06). */
internal const val LOW_CONTRAST_RATIO = 3.0

private const val LUMINANCE_OFFSET = 0.05f

internal fun quickButtonSheetTabIndex(tab: QuickButtonSheetTab): Int = QuickButtonSheetTab.entries.indexOf(tab)

internal fun quickButtonSheetTabAt(index: Int): QuickButtonSheetTab =
    QuickButtonSheetTab.entries.getOrElse(index) { QuickButtonSheetTab.ICON }

internal fun quickButtonColorTargetIndex(target: QuickButtonColorTarget): Int =
    QUICK_BUTTON_COLOR_TARGETS.indexOf(target).coerceAtLeast(0)

internal fun quickButtonColorTargetAt(index: Int): QuickButtonColorTarget =
    QUICK_BUTTON_COLOR_TARGETS.getOrElse(index) { QuickButtonColorTarget.ICON }

/** True when [icon] over [background] has a contrast below [LOW_CONTRAST_RATIO] (translucent reads as opaque). */
internal fun isLowContrast(icon: Color, background: Color): Boolean {
    val a = icon.copy(alpha = 1f).luminance()
    val b = background.copy(alpha = 1f).luminance()
    return (maxOf(a, b) + LUMINANCE_OFFSET) / (minOf(a, b) + LUMINANCE_OFFSET) < LOW_CONTRAST_RATIO
}

/**
 * Name of the button (botao-rapido RF-01): what was typed, trimmed, or else [fallback], the name of the click action
 * (the app name when the click opens an app). It is the TalkBack description and, with "Show name", the home label.
 */
internal fun QuickButtonAppearance.resolvedName(fallback: String): String =
    label?.trim()?.takeIf { it.isNotEmpty() } ?: fallback

/** The name the click action gives the button when none was typed. */
internal fun QuickButtonModel.defaultName(appLabel: String?, clickName: String): String = appLabel ?: clickName

/**
 * The one name of a quick button everywhere it shows (grid label, container label, manage list, appearance
 * sheet, TalkBack): the typed name, else the click shortcut's localized name, and only with no shortcut the
 * generic "Quick Button".
 */
@Composable
internal fun QuickButtonModel.displayTitle(appLabel: String?): String {
    val click = shortcuts.click
    val clickName = if (click.type == ShortcutType.NONE) {
        stringResource(R.string.home_grid_item_quick_button)
    } else {
        click.localizedName(LocalContext.current.resources)
    }
    return appearance.resolvedName(defaultName(appLabel, clickName))
}

/** This appearance with the typed name trimmed and a blank one stored as null (follows the click action). */
internal fun QuickButtonAppearance.normalized(): QuickButtonAppearance =
    copy(label = label?.trim()?.takeIf { it.isNotEmpty() })
