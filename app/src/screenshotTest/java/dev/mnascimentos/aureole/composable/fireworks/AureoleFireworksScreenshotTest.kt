package dev.mnascimentos.aureole.composable.fireworks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkRocket
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkSpark
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkTone
import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlin.math.cos
import kotlin.math.sin

@PreviewTest
@AureolePreview
@Composable
fun AureoleFireworksScreenshotTest() {
    AureoleLauncherTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / FRAME_ASPECT)
                .background(AureoleDS.colors.surface)
        ) {
            AureoleFireworksFrame(frozenShow())
        }
    }
}

private fun frozenShow() = FireworksState(
    rockets = listOf(FireworkRocket(0.3f, 0.7f, 0.4f, -1f, FireworkTone.PRIMARY)),
    sparks = List(SPARKS) {
        val angle = it * ANGLE_STEP
        FireworkSpark(
            x = 0.65f + cos(angle) * RING,
            y = 0.55f + sin(angle) * RING,
            vx = 0f,
            vy = 0f,
            life = 1f,
            radius = 0.006f,
            tone = FireworkTone.entries[it % FireworkTone.entries.size],
        )
    },
)

private const val FRAME_ASPECT = 2.1f
private const val SPARKS = 40
private const val ANGLE_STEP = 0.157f
private const val RING = 0.2f
