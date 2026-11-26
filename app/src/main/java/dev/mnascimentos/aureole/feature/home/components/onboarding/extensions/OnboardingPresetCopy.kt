package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.PresetContentLine
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.PresetGroupKind
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.spec

/** The preset's name, as on its card and in "Use <name>". */
@get:StringRes
val LayoutPreset.nameRes: Int
    get() = when (this) {
        LayoutPreset.MINIMAL -> R.string.home_onboarding_preset_name_minimal
        LayoutPreset.PRODUCTIVE -> R.string.home_onboarding_preset_name_productive
        LayoutPreset.WIDGETS -> R.string.home_onboarding_preset_name_widgets
    }

/** The one line under the name on the preset's card. */
@get:StringRes
val LayoutPreset.lineRes: Int
    get() = when (this) {
        LayoutPreset.MINIMAL -> R.string.home_onboarding_preset_line_minimal
        LayoutPreset.PRODUCTIVE -> R.string.home_onboarding_preset_line_productive
        LayoutPreset.WIDGETS -> R.string.home_onboarding_preset_line_widgets
    }

/** "LAYOUT <NAME> APPLIED", below the title of a step the preset did (RF-F4). */
@get:StringRes
val LayoutPreset.appliedLabelRes: Int
    get() = when (this) {
        LayoutPreset.MINIMAL -> R.string.home_onboarding_preset_applied_label_minimal
        LayoutPreset.PRODUCTIVE -> R.string.home_onboarding_preset_applied_label_productive
        LayoutPreset.WIDGETS -> R.string.home_onboarding_preset_applied_label_widgets
    }

/** What the preset's card lists (RF-F1): clock, favorites, widget and group, in that order. */
val LayoutPreset.contentLines: List<PresetContentLine>
    get() {
        val presetSpec = spec
        val clock = when (presetSpec.clockLayout) {
            ClockLayout.TIME_ONLY -> R.string.home_onboarding_preset_clock_time_only
            ClockLayout.TIME_ON_TOP -> R.string.home_onboarding_preset_clock_time_on_top
            else -> R.string.home_onboarding_preset_clock_date_on_top
        }
        val widget = if (presetSpec.hasWidget) {
            R.string.home_onboarding_preset_widget_next
        } else {
            R.string.home_onboarding_preset_no_widget
        }
        val group = when (presetSpec.groupKind) {
            PresetGroupKind.NONE -> PresetContentLine.Text(R.string.home_onboarding_preset_no_group)
            PresetGroupKind.SIDE -> PresetContentLine.Count(
                R.plurals.home_onboarding_preset_group_folders,
                presetSpec.maxFolders
            )
            PresetGroupKind.DOCK -> PresetContentLine.Count(
                R.plurals.home_onboarding_preset_dock,
                presetSpec.groupAppCount
            )
        }
        return listOf(
            PresetContentLine.Text(clock),
            PresetContentLine.Count(R.plurals.home_onboarding_preset_favorites, presetSpec.favoriteCount),
            PresetContentLine.Text(widget),
            group
        )
    }
