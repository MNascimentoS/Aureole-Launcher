package dev.mnascimentos.aureole.composable.clock

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ResolvedClockConfig
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.LocalGlassLevel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Regression for the crash when the glass changes in "Cards and glass": toggling haze, background,
 * greeting style and glass level must recompose [ClockHeader] without a slot table mismatch.
 */
@RunWith(RobolectricTestRunner::class)
class ClockHeaderRecompositionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultConfig = ResolvedClockConfig(
        style = SettingsRepository.DEFAULT_CLOCK_STYLE,
        customGreeting = "",
        alignment = SettingsRepository.DEFAULT_CLOCK_ALIGNMENT,
        fontFamily = SettingsRepository.DEFAULT_CLOCK_FONT_FAMILY,
        timeFormat = SettingsRepository.DEFAULT_CLOCK_TIME_FORMAT,
        dateFormat = SettingsRepository.DEFAULT_CLOCK_DATE_FORMAT,
        textColor = 0,
        backgroundColor = 0
    )

    private val greetingConfig = defaultConfig.copy(style = "CUSTOM_GREETING", customGreeting = "Hello there")

    @Test
    fun `toggling haze, background, style and glass level recomposes without crashing`() {
        val hazeState = HazeState()
        var hazeConfig by mutableStateOf(ClockHazeConfig(hazeState = hazeState, isHazeEnabled = true))
        var resolvedConfig by mutableStateOf(defaultConfig)
        var glassLevel by mutableStateOf(GlassLevel.MEDIUM)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalGlassLevel provides glassLevel) {
                    ClockHeader(resolvedConfig = resolvedConfig, hazeConfig = hazeConfig)
                }
            }
        }
        composeTestRule.waitForIdle()

        val steps: List<() -> Unit> = listOf(
            { hazeConfig = hazeConfig.copy(isHazeEnabled = false) },
            { glassLevel = GlassLevel.NONE },
            { hazeConfig = hazeConfig.copy(isHazeEnabled = true) },
            { glassLevel = GlassLevel.MEDIUM },
            { hazeConfig = hazeConfig.copy(isBackgroundEnabled = false) },
            { hazeConfig = hazeConfig.copy(isHazeEnabled = false) },
            { hazeConfig = hazeConfig.copy(isBackgroundEnabled = true) },
            { resolvedConfig = greetingConfig },
            { hazeConfig = hazeConfig.copy(isHazeEnabled = true, hazeOpacity = 0.2f) },
            { resolvedConfig = defaultConfig },
            { hazeConfig = hazeConfig.copy(isHazeEnabled = false, isBackgroundEnabled = false) },
            { resolvedConfig = greetingConfig },
            { hazeConfig = hazeConfig.copy(isHazeEnabled = true, isBackgroundEnabled = true) },
            { resolvedConfig = defaultConfig.copy(backgroundColor = 0xFF112233.toInt()) }
        )
        steps.forEach { step ->
            step()
            composeTestRule.waitForIdle()
        }

        resolvedConfig = greetingConfig
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Hello there").assertIsDisplayed()
    }
}
