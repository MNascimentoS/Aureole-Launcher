package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState

/** Checklist label; bold spans are escaped `<b>` tags, read with `AnnotatedString.fromHtml`. */
@StringRes
fun OnboardingTask.titleRes(): Int = when (this) {
    OnboardingTask.SET_DEFAULT_HOME -> R.string.home_onboarding_task_default_home
    OnboardingTask.CHOOSE_PALETTE -> R.string.home_onboarding_task_palette
    OnboardingTask.CHOOSE_FONT -> R.string.home_onboarding_task_font
    OnboardingTask.ADD_FAVORITE_APPS -> R.string.home_onboarding_task_apps
    OnboardingTask.FILL_CONTAINER -> R.string.home_onboarding_task_container
    OnboardingTask.ADD_WIDGET -> R.string.home_onboarding_task_widget
    OnboardingTask.EXPLORE_SETTINGS -> R.string.home_onboarding_task_settings
}

/** The first pending task in list order: the one with the breathing outline. */
val OnboardingUiState.nextPendingTask: OnboardingTask?
    get() = OnboardingTask.entries.firstOrNull { it !in completedTasks }
