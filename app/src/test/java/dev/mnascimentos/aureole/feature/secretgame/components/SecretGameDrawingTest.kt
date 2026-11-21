package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import dev.mnascimentos.aureole.feature.secretgame.model.SecretSprite
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Draws the parts of the game that need no bitmap (the sprites are not decodable on the Robolectric canvas). */
@RunWith(RobolectricTestRunner::class)
class SecretGameDrawingTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `when drawing the trail of a falling face, should render without failing`() {
        rule.setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawTrail(
                        SecretSprite(0.5f, 0.6f, 0.2f, 0f, 1f),
                        trail = 0.3f,
                        faceAspect = 1f,
                        color = Color.White
                    )
                }
            }
        }

        rule.onRoot().captureToImage()
    }

    @Test
    fun `when the frames of the buttons are drawn, should render hidden, half and complete`() {
        var draw by mutableFloatStateOf(0f)
        rule.setContent {
            Canvas(modifier = Modifier.fillMaxSize()) { drawButtons(draw, collapse = 0f, color = Color.White) }
        }

        listOf(0f, 0.0005f, 0.5f, 1f).forEach {
            draw = it
            rule.waitForIdle()
            rule.onRoot().captureToImage()
        }
    }
}
