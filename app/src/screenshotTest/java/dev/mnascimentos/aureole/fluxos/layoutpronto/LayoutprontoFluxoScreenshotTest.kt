package dev.mnascimentos.aureole.fluxos.layoutpronto

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.LayoutPresetPlan
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.PresetFolder
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.completedTasks
import dev.mnascimentos.aureole.fixtures.OnboardingCase

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
private fun P01EscolhaScreenshotTest() = OnboardingCase(
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
private fun P02ChecklistAplicadoScreenshotTest() = OnboardingCase(
    OnboardingUiState(
        stage = OnboardingStage.CHECKLIST,
        completedTasks = plans[1].completedTasks() + OnboardingTask.SET_DEFAULT_HOME,
        appliedPreset = LayoutPreset.PRODUCTIVE
    )
)
