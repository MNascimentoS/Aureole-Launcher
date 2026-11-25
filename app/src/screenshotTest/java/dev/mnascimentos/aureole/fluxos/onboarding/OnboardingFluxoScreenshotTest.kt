package dev.mnascimentos.aureole.fluxos.onboarding

import android.appwidget.AppWidgetHost
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.MainScaffold
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

/**
 * A home inteira (`MainScaffold`, que desenha a camada do onboarding por cima) com [onboarding]; na prévia de
 * paisagem, a mesma home deitada.
 * [edit] ajusta o resto do estado (modo de edição da grade, por exemplo).
 */
@Composable
private fun OnboardingCase(onboarding: OnboardingUiState, edit: (MainUiState) -> MainUiState = { it }) {
    val current = LocalConfiguration.current
    val isLandscape = current.screenWidthDp > current.screenHeightDp
    val base = createSampleHomeData().uiState
    val items = if (isLandscape) GridDefaults.getDefaultGridItems(isLandscape = true) else base.gridItems
    val uiState = edit(
        base.copy(
            isLandscape = isLandscape,
            gridItems = items,
            landscapeGridItems = if (isLandscape) items else base.landscapeGridItems,
            onboarding = onboarding
        )
    )
    val configuration = Configuration(current).apply {
        if (isLandscape) orientation = Configuration.ORIENTATION_LANDSCAPE
    }
    val context = LocalContext.current
    CompositionLocalProvider(LocalConfiguration provides configuration) {
        HomeCase(uiState) {
            MainScaffold(appWidgetHost = AppWidgetHost(context, HOST_ID), onWidgetSelected = {})
        }
    }
}

private fun coach(vararg done: GestureCoachStep) =
    OnboardingUiState(stage = OnboardingStage.GESTURES, completedSteps = done.toSet())

private fun checklist(expanded: Boolean = true, pendingTask: OnboardingTask? = null) = OnboardingUiState(
    stage = OnboardingStage.CHECKLIST,
    isChecklistExpanded = expanded,
    completedTasks = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_PALETTE),
    pendingTask = pendingTask
)

@PreviewTest
@AureolePreview
@Composable
private fun P01BoasVindasScreenshotTest() = OnboardingCase(OnboardingUiState(stage = OnboardingStage.WELCOME))

@PreviewTest
@AureolePreview
@Composable
private fun P02LauncherPadraoScreenshotTest() =
    OnboardingCase(OnboardingUiState(stage = OnboardingStage.DEFAULT_HOME))

/** O coach no primeiro gesto; em paisagem o passo do alfabeto não existe. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun P03TodosAppsScreenshotTest() = OnboardingCase(coach())

@PreviewTest
@AureolePreview
@Composable
private fun P04AlfabetoScreenshotTest() = OnboardingCase(coach(GestureCoachStep.OPEN_APPS))

@PreviewTest
@AureolePreview
@Composable
private fun P05DeslizarParaBaixoScreenshotTest() =
    OnboardingCase(coach(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB))

@PreviewTest
@AureolePreview
@Composable
private fun P06SegurarScreenshotTest() = OnboardingCase(
    coach(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB)
)

@PreviewTest
@AureolePreview
@Composable
private fun P07ChecklistScreenshotTest() = OnboardingCase(checklist())

/** A lista minimizada vira a pílula sobre a home. */
@PreviewTest
@AureolePreview
@Composable
private fun P09PilulaScreenshotTest() = OnboardingCase(checklist(expanded = false))

/** A tarefa de ajustar o layout espera na grade em edição: a dica de como salvar. */
@PreviewTest
@AureolePreview
@Composable
private fun P10DicaEdicaoScreenshotTest() = OnboardingCase(
    checklist(expanded = false, pendingTask = OnboardingTask.ADJUST_LAYOUT)
) { it.copy(isGridEditMode = true) }

@PreviewTest
@AureolePreview
@Composable
private fun P11ComemoracaoScreenshotTest() =
    OnboardingCase(OnboardingUiState(stage = OnboardingStage.COMPLETED, isCelebrating = true))

private const val HOST_ID = 1024
