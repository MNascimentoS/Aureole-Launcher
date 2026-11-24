package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.resolvedTasks

/** Checklist label; bold spans are escaped `<b>` tags, read with `AnnotatedString.fromHtml`. */
@StringRes
fun OnboardingTask.titleRes(): Int = when (this) {
    OnboardingTask.SET_DEFAULT_HOME -> R.string.home_onboarding_task_default_home
    OnboardingTask.CHOOSE_PALETTE -> R.string.home_onboarding_task_palette
    OnboardingTask.CHOOSE_FONT -> R.string.home_onboarding_task_font
    OnboardingTask.ADD_CLOCK -> R.string.home_onboarding_task_clock
    OnboardingTask.ADD_FAVORITE_APPS -> R.string.home_onboarding_task_favorites
    OnboardingTask.FILL_CONTAINER -> R.string.home_onboarding_task_container
    OnboardingTask.ADD_WIDGET -> R.string.home_onboarding_task_widget
    OnboardingTask.ADJUST_LAYOUT -> R.string.home_onboarding_task_layout
    OnboardingTask.EXPLORE_SETTINGS -> R.string.home_onboarding_task_settings
}

/** The first unresolved (neither done nor skipped) task in list order: the one with the breathing outline. */
val OnboardingUiState.nextPendingTask: OnboardingTask?
    get() = OnboardingTask.entries.firstOrNull { it !in resolvedTasks }

/** Tasks that only open once every other task is done or skipped: the home can be adjusted after the components. */
private val LOCKED_UNTIL_REST_RESOLVED = setOf(OnboardingTask.ADJUST_LAYOUT, OnboardingTask.EXPLORE_SETTINGS)

/** True while [task] is waiting for the other tasks to be done or skipped (RF-01 of telas-ajustesfundo). */
fun OnboardingUiState.isTaskLocked(task: OnboardingTask): Boolean =
    task in LOCKED_UNTIL_REST_RESOLVED &&
        task !in resolvedTasks &&
        OnboardingTask.entries.any { it !in LOCKED_UNTIL_REST_RESOLVED && it !in resolvedTasks }
