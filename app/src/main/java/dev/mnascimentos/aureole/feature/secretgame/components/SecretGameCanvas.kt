package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.fireworks.drawFireworks
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkColors
import dev.mnascimentos.aureole.feature.secretgame.model.SecretButtonsFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretFaceKind
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameColors
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameLayout
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning
import dev.mnascimentos.aureole.feature.secretgame.model.SecretParticleKind
import dev.mnascimentos.aureole.feature.secretgame.model.SecretSprite
import dev.mnascimentos.aureole.feature.secretgame.model.SecretStarSprite
import dev.mnascimentos.aureole.feature.secretgame.model.SecretStarsTuning
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing as D

/** Painters of the sprites, remembered once per canvas. */
private class SecretPainters(val logo: Painter, val ship: Painter, val creator: Painter, val dreamer: Painter)

/** Sprites read once per canvas, not on every frame (re-reading them each frame breaks the animated preview). */
private class SecretSprites(
    val logo: ImageVector,
    val ship: ImageBitmap,
    val creator: ImageBitmap,
    val dreamer: ImageBitmap
)

@Composable
private fun rememberSecretPainters(): SecretPainters {
    val res = LocalResources.current
    // No key: the sprites never change while the game runs, and the preview tool reports a "cycle" after it
    // parses the same XML 50 times in one (animated) render.
    val sprites = remember {
        SecretSprites(
            logo = ImageVector.vectorResource(res = res, resId = R.drawable.ic_aureole_logo),
            ship = ImageBitmap.imageResource(res, R.drawable.secret_game_ship),
            creator = ImageBitmap.imageResource(res, R.drawable.secret_game_creator),
            dreamer = ImageBitmap.imageResource(res, R.drawable.secret_game_dreamer),
        )
    }
    val logo = rememberVectorPainter(sprites.logo)
    return remember(sprites, logo) {
        SecretPainters(
            logo = logo,
            ship = BitmapPainter(sprites.ship),
            creator = BitmapPainter(sprites.creator),
            dreamer = BitmapPainter(sprites.dreamer),
        )
    }
}

/**
 * Draws one [SecretGameFrame] in the arena it fills: stateless, every distance is a fraction of the arena width.
 * Nothing is clipped, so the background (and the side buttons) also reach the [SecretGameLayout.sideSpan].
 */
@Composable
fun SecretGameCanvas(
    frame: SecretGameFrame,
    colors: SecretGameColors,
    layout: SecretGameLayout,
    modifier: Modifier = Modifier
) {
    val painters = rememberSecretPainters()
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawWorld(frame, colors, painters, layout)
        }
        SecretGameTexts(frame = frame, colors = colors, layout = layout, modifier = Modifier.fillMaxSize())
    }
}

private fun DrawScope.drawWorld(
    frame: SecretGameFrame,
    colors: SecretGameColors,
    painters: SecretPainters,
    layout: SecretGameLayout
) {
    val u = size.width
    drawStars(frame.stars, colors.face)
    if (layout.sideSpan > 0f) frame.buttons?.let { drawArenaEdges(it, colors.ship) }
    frame.streaks.forEach {
        drawLine(
            color = colors.ship.copy(alpha = D.STREAK_ALPHA * frame.streaksAlpha),
            start = Offset(it.x * u, it.y * u),
            end = Offset(it.x * u, (it.y + it.length) * u),
            strokeWidth = D.STREAK_WIDTH * u
        )
    }
    frame.faces.forEach {
        val (painter, aspect) = if (it.kind == SecretFaceKind.CREATOR) {
            painters.creator to D.FACE_ASPECT_CREATOR
        } else {
            painters.dreamer to D.FACE_ASPECT_DREAMER
        }
        if (it.trail > 0f) drawTrail(it.sprite, it.trail, aspect, colors.ship)
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
    val fireworkColors = FireworkColors(colors.ship, colors.face, colors.ship)
    drawFireworks(frame.rockets, frame.sparks, fireworkColors, frame.fireworksAlpha)
    frame.buttons?.let { drawButtons(it, layout, colors.ship) }
}

/** Two faint lines on the sides of the arena, drawn and collapsed together with the button frames (D-02). */
private fun DrawScope.drawArenaEdges(buttons: SecretButtonsFrame, color: Color) {
    val alpha = D.ARENA_EDGE_ALPHA * buttons.draw.coerceAtMost(1f) * (1f - buttons.collapse)
    if (alpha <= 0f) return
    val tint = color.copy(alpha = alpha)
    val stroke = D.ARENA_EDGE_STROKE_DP.dp.toPx()
    listOf(0f, size.width).forEach { drawLine(tint, Offset(it, 0f), Offset(it, size.height), stroke) }
}

private fun DrawScope.drawStars(stars: List<SecretStarSprite>, color: Color) {
    val u = size.width
    stars.forEach {
        val center = Offset(it.x * u, it.y * u)
        if (it.tail > 0f) {
            drawWarpStar(it, color)
            return@forEach
        }
        if (it.glowAlpha > 0f) {
            drawCircle(color.copy(alpha = it.glowAlpha), it.radius * SecretStarsTuning.GLOW_SCALE * u, center)
        }
        drawCircle(color.copy(alpha = it.alpha), it.radius * u, center)
    }
}

/** Warp: risco de (x, y − tail) até (x, y), transparente no topo e na cor dos rostos na ponta; sem halo. */
private fun DrawScope.drawWarpStar(star: SecretStarSprite, color: Color) {
    val u = size.width
    val top = Offset(star.x * u, (star.y - star.tail) * u)
    val tip = Offset(star.x * u, star.y * u)
    val width = maxOf(1.dp.toPx(), star.radius * SecretStarsTuning.WARP_STROKE * u)
    drawLine(
        brush = Brush.verticalGradient(
            listOf(Color.Transparent, color.copy(alpha = star.alpha)),
            startY = top.y,
            endY = tip.y
        ),
        start = top,
        end = tip,
        strokeWidth = width,
        cap = StrokeCap.Round
    )
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
