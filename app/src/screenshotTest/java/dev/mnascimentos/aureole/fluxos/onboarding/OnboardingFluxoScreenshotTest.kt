package dev.mnascimentos.aureole.fluxos.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.GestureCoachBackChip
import dev.mnascimentos.aureole.feature.home.components.onboarding.OnboardingVisualSheetComponent
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearanceSelection
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.LayoutPresetPlan
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.PresetFolder
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.completedTasks
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.OnboardingCase
import java.util.TimeZone

private fun coach(vararg done: GestureCoachStep) =
    OnboardingUiState(stage = OnboardingStage.GESTURES, completedSteps = done.toSet())

private fun checklist(
    expanded: Boolean = true,
    pendingTask: OnboardingTask? = null,
    done: Set<OnboardingTask> = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_VISUAL),
    skipped: Set<OnboardingTask> = emptySet()
) = OnboardingUiState(
    stage = OnboardingStage.CHECKLIST,
    isChecklistExpanded = expanded,
    completedTasks = done,
    skippedTasks = skipped,
    pendingTask = pendingTask
)

/** A home sem nada na grade, como a que oferece os layouts prontos. */
private fun emptyHome(state: MainUiState) = state.copy(
    gridItems = emptyList(),
    portraitGridItems = emptyList(),
    landscapeGridItems = emptyList()
)

@PreviewTest
@AureolePreview
@Composable
private fun P01BoasVindasScreenshotTest() =
    OnboardingCase(OnboardingUiState(stage = OnboardingStage.WELCOME), edit = ::emptyHome)

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
private fun P05SegurarScreenshotTest() = OnboardingCase(
    coach(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB)
)

/** "Isso aí!": o cartão do gesto que acabou de dar certo, antes de encolher (2 s). */
@PreviewTest
@AureolePreview
@Composable
private fun P06IssoAiScreenshotTest() = OnboardingCase(
    OnboardingUiState(
        stage = OnboardingStage.GESTURES,
        completedSteps = setOf(GestureCoachStep.OPEN_APPS),
        succeededStep = GestureCoachStep.OPEN_APPS
    )
)

/** O cartão encolhido no chip do canto superior direito, com a home livre. */
@PreviewTest
@AureolePreview
@Composable
private fun P07ChipVolteScreenshotTest() = OnboardingCase(
    OnboardingUiState(stage = OnboardingStage.COMPLETED),
    overlay = { GestureCoachBackChip(glass = OnboardingGlass()) }
)

/** A lista no começo: a primeira etapa da home pendente e os extras ainda sem aparecer liberados. */
@PreviewTest
@AureolePreview
@Composable
private fun P08ChecklistInicioScreenshotTest() =
    OnboardingCase(checklist(done = setOf(OnboardingTask.SET_DEFAULT_HOME)))

/** Falta uma etapa da home: os extras aparecem bloqueados. */
@PreviewTest
@AureolePreview
@Composable
private fun P09ChecklistExtrasBloqueadosScreenshotTest() = OnboardingCase(
    checklist(done = homeTasks - OnboardingTask.FILL_CONTAINER)
)

/** A seção da home resolvida (uma etapa pulada): os extras liberados. */
@PreviewTest
@AureolePreview
@Composable
private fun P10ChecklistExtrasLiberadosScreenshotTest() = OnboardingCase(
    checklist(done = homeTasks - OnboardingTask.ADD_WIDGET, skipped = setOf(OnboardingTask.ADD_WIDGET))
)

/** A lista minimizada vira a pílula sobre a home. */
@PreviewTest
@AureolePreview
@Composable
private fun P11PilulaScreenshotTest() = OnboardingCase(checklist(expanded = false))

/** A sheet Visual na aba Paleta, com a hora e a data de [VISUAL_NOW_MILLIS]. */
@PreviewTest
@AureolePreview
@Composable
private fun P12VisualPaletaScreenshotTest() = VisualCase(AppearancePicker.PALETTE)

/** A mesma sheet na aba Fonte. */
@PreviewTest
@AureolePreview
@Composable
private fun P13VisualFonteScreenshotTest() = VisualCase(AppearancePicker.FONT)

/** O relógio que a etapa acabou de colocar: contorno tracejado e o chip "Novo". */
@PreviewTest
@AureolePreview
@Composable
private fun P14DestaqueRelogioScreenshotTest() = OnboardingCase(peek(GridDefaults.CLOCK_ITEM_ID))

/** Os favoritos que a etapa acabou de colocar. */
@PreviewTest
@AureolePreview
@Composable
private fun P15DestaqueFavoritosScreenshotTest() = OnboardingCase(peek(GridDefaults.APPS_LIST_ITEM_ID))

/** A tarefa de ajustar o layout espera na grade em edição: a dica de como salvar. */
@PreviewTest
@AureolePreview
@Composable
private fun P16DicaEdicaoScreenshotTest() = OnboardingCase(
    checklist(expanded = false, pendingTask = OnboardingTask.ADJUST_LAYOUT),
    edit = { it.copy(isGridEditMode = true) }
)

@PreviewTest
@AureolePreview
@Composable
private fun P17TudoProntoScreenshotTest() =
    OnboardingCase(OnboardingUiState(stage = OnboardingStage.COMPLETED, isCelebrating = true))

private val homeTasks = OnboardingTask.entries.filterNot { it.isExtra }.toSet()

/** A home sozinha depois de uma etapa: a pílula no lugar da lista e o item novo destacado. */
private fun peek(itemId: String) = checklist(
    expanded = false,
    done = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_VISUAL)
).copy(isHomePeeking = true, peekItemId = itemId)

/** A sheet Visual sobre a home; o fuso fica em UTC para a hora fixa sair igual em qualquer máquina. */
@Composable
private fun VisualCase(picker: AppearancePicker) {
    val previous = remember { TimeZone.getDefault() }
    remember { TimeZone.setDefault(TimeZone.getTimeZone("UTC")) }
    DisposableEffect(Unit) { onDispose { TimeZone.setDefault(previous) } }
    OnboardingCase(
        onboarding = checklist(expanded = false).copy(activePicker = picker),
        overlay = {
            OnboardingVisualSheetComponent(
                picker = picker,
                selection = AppearanceSelection(paletteName = "Frostbite", fontName = "Istok Web"),
                actions = OnboardingActions(),
                nowMillis = VISUAL_NOW_MILLIS
            )
        }
    )
}

/** 5 jan 2026, 15:00 UTC. */
private const val VISUAL_NOW_MILLIS = 1_767_625_200_000L

private const val PHONE = "com.example.phone"
private const val MESSAGES = "com.example.messages"
private const val CAMERA = "com.example.camera"
private const val BROWSER = "com.example.browser"
private const val SETTINGS = "com.example.settings"

/** Os três layouts como o aparelho de exemplo os montaria. */
private val plans = listOf(
    LayoutPresetPlan(LayoutPreset.MINIMAL, favorites = listOf(PHONE, MESSAGES, CAMERA, BROWSER, SETTINGS)),
    LayoutPresetPlan(
        LayoutPreset.PRODUCTIVE,
        favorites = listOf(PHONE, MESSAGES, CAMERA, BROWSER),
        folders = listOf(
            PresetFolder(category = 4, name = "Social", packageNames = listOf(MESSAGES, PHONE)),
            PresetFolder(category = 5, name = "Productivity", packageNames = listOf(BROWSER, SETTINGS))
        ),
        groupApps = listOf(CAMERA)
    ),
    LayoutPresetPlan(
        LayoutPreset.WIDGETS,
        favorites = listOf(PHONE, MESSAGES, CAMERA),
        groupApps = listOf(BROWSER, SETTINGS, PHONE, CAMERA)
    )
)

/** "Escolha um ponto de partida": os três cartões, o Produtivo selecionado. */
@PreviewTest
@AureolePreview
@Composable
private fun P18EscolhaLayoutScreenshotTest() = OnboardingCase(
    OnboardingUiState(
        stage = OnboardingStage.WELCOME,
        isChoosingPreset = true,
        presetPlans = plans,
        selectedPreset = LayoutPreset.PRODUCTIVE
    )
)

/** A lista logo depois de aplicar o Produtivo: as etapas do layout feitas e a do widget ainda pendente. */
@PreviewTest
@AureolePreview
@Composable
private fun P19ChecklistLayoutAplicadoScreenshotTest() = OnboardingCase(
    OnboardingUiState(
        stage = OnboardingStage.CHECKLIST,
        completedTasks = plans[1].completedTasks() + OnboardingTask.SET_DEFAULT_HOME,
        appliedPreset = LayoutPreset.PRODUCTIVE
    )
)
