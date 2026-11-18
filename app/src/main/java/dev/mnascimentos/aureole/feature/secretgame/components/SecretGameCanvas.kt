package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.feature.secretgame.model.SecretFaceKind
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameColors
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing as D
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning
import dev.mnascimentos.aureole.feature.secretgame.model.SecretParticleKind
import dev.mnascimentos.aureole.feature.secretgame.model.SecretSprite

/** Painters of the sprites, remembered once per canvas. */
private class SecretPainters(val logo: Painter, val ship: Painter, val creator: Painter, val dreamer: Painter)

@Composable
private fun rememberSecretPainters(): SecretPainters = SecretPainters(
    logo = rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_aureole_logo)),
    ship = rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_aureole_game_ship)),
    creator = BitmapPainter(ImageBitmap.imageResource(R.drawable.secret_game_creator)),
    dreamer = BitmapPainter(ImageBitmap.imageResource(R.drawable.secret_game_dreamer)),
)

/** Draws one [SecretGameFrame]: stateless, every distance is a fraction of the screen width. */
@Composable
fun SecretGameCanvas(
    frame: SecretGameFrame,
    colors: SecretGameColors,
    aspect: Float,
    modifier: Modifier = Modifier
) {
    val painters = rememberSecretPainters()
    Canvas(modifier = modifier.fillMaxSize()) {
        drawWorld(frame, colors, painters)
    }
    SecretGameTexts(frame = frame, colors = colors, aspect = aspect, modifier = Modifier.fillMaxSize())
}

private fun DrawScope.drawWorld(frame: SecretGameFrame, colors: SecretGameColors, painters: SecretPainters) {
    val u = size.width
    frame.streaks.forEach {
        drawLine(
            color = colors.ship.copy(alpha = D.STREAK_ALPHA),
            start = Offset(it.x * u, it.y * u),
            end = Offset(it.x * u, (it.y + it.length) * u),
            strokeWidth = D.STREAK_WIDTH * u
        )
    }
    frame.faces.forEach {
        drawFaceGlow(it.sprite, colors.face)
        val (painter, aspect) = if (it.kind == SecretFaceKind.CREATOR) {
            painters.creator to D.FACE_ASPECT_CREATOR
        } else {
            painters.dreamer to D.FACE_ASPECT_DREAMER
        }
        drawSprite(painter, aspect, it.sprite, colors.face)
    }
    if (frame.flame > 0f) drawFlame(frame, colors.ship)
    frame.logo?.let { drawSprite(painters.logo, SecretGameTuning.LOGO_ASPECT, it, colors.ship) }
    frame.ship?.let { drawSprite(painters.ship, SecretGameTuning.SHIP_ASPECT, it, colors.ship) }
    frame.glow?.let { drawGlow(it.x, it.y, it.radius, it.alpha, colors.ship) }
    frame.particles.forEach {
        val color = if (it.kind == SecretParticleKind.SHIP) colors.ship else colors.face
        drawCircle(color.copy(alpha = it.life.coerceIn(0f, 1f)), it.radius * u, Offset(it.x * u, it.y * u))
    }
    frame.buttons?.let { drawButtons(it.draw, it.collapse, colors.ship) }
}

private fun DrawScope.drawSprite(painter: Painter, aspect: Float, sprite: SecretSprite, tint: Color) {
    val width = sprite.width * size.width
    val height = width * aspect
    val center = Offset(sprite.x * size.width, sprite.y * size.width)
    withTransform({
        translate(center.x - width * D.HALF, center.y - height * D.HALF)
        rotate(sprite.rotation * D.RAD_TO_DEG, Offset(width * D.HALF, height * D.HALF))
    }) {
        with(painter) { draw(Size(width, height), sprite.alpha, ColorFilter.tint(tint)) }
    }
}

private fun DrawScope.drawFaceGlow(sprite: SecretSprite, color: Color) {
    val u = size.width
    val w = sprite.width * u * D.FACE_GLOW_WIDTH
    val h = sprite.width * u * D.FACE_GLOW_HEIGHT
    val top = sprite.y * u - h * D.FACE_GLOW_RISE
    drawOval(
        brush = Brush.verticalGradient(
            listOf(Color.Transparent, color.copy(alpha = D.FACE_GLOW_ALPHA * sprite.alpha)),
            startY = top,
            endY = top + h
        ),
        topLeft = Offset(sprite.x * u - w * D.HALF, top),
        size = Size(w, h)
    )
}

private fun DrawScope.drawFlame(frame: SecretGameFrame, color: Color) {
    val ship = frame.ship ?: return
    val u = size.width
    val w = D.FLAME_WIDTH * u * frame.flame
    val h = D.FLAME_HEIGHT * u * frame.flame
    val top = (ship.y + ship.width * D.FLAME_OFFSET) * u
    drawOval(
        brush = Brush.verticalGradient(
            listOf(color.copy(alpha = D.FLAME_ALPHA * ship.alpha), Color.Transparent),
            startY = top,
            endY = top + h
        ),
        topLeft = Offset(ship.x * u - w * D.HALF, top),
        size = Size(w, h)
    )
}

private fun DrawScope.drawGlow(x: Float, y: Float, radius: Float, alpha: Float, color: Color) {
    val u = size.width
    val center = Offset(x * u, y * u)
    drawCircle(
        brush = Brush.radialGradient(
            listOf(color.copy(alpha = alpha.coerceIn(0f, 1f)), Color.Transparent),
            center = center,
            radius = radius * u
        ),
        radius = radius * u,
        center = center
    )
}
