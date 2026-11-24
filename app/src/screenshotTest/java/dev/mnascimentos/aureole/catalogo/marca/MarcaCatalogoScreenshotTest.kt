package dev.mnascimentos.aureole.catalogo.marca

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.composable.clock.ClockHeader
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ResolvedClockConfig
import dev.mnascimentos.aureole.composable.fireworks.AureoleFireworksFrame
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkRocket
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkSpark
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkTone
import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import dev.mnascimentos.aureole.composable.minihome.AureoleMiniHome
import dev.mnascimentos.aureole.composable.minihome.model.MiniHomeFavoritesLayout
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.composable.preview.aureoleDashedBorder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.data.model.ClockMessage
import dev.mnascimentos.aureole.core.designsystem.components.AureoleLogoRing
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDimens
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleCatalogPreview
import dev.mnascimentos.aureole.fixtures.CatalogoColumn
import dev.mnascimentos.aureole.fixtures.CatalogoLabel
import kotlin.math.cos
import kotlin.math.sin

private val ICON_SIZE = 64.dp
private val LOGO_LARGE = 72.dp
private val FIREWORKS_WIDTH = 180.dp
private const val FIREWORKS_ASPECT = 2.1f
private const val SPARKS = 40
private const val ANGLE_STEP = 0.157f
private const val RING = 0.2f

/** Uma célula da grade de uma home de 411 dp com quatro colunas; a baixa testa a data longe da hora (RF-B4). */
private val CELL_WIDTH = 92.dp
private val CELL_HEIGHT = 88.dp
private val LOW_CELL_HEIGHT = 64.dp
private const val CARD_COLOR = 0xFF2B2F3A.toInt()

private fun adaptiveIcon(): Drawable {
    val glyph = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(Color.WHITE)
    }
    return AdaptiveIconDrawable(ColorDrawable(0xFF1B65C0.toInt()), InsetDrawable(glyph, 0.25f))
}

private fun appWith(icon: Drawable) = AppInfo(
    label = "App",
    packageName = "com.example.app",
    componentName = ComponentName("com.example.app", "MainActivity"),
    icon = icon
)

/** Adaptativo (sangra), bitmap legado (nunca recortado) e adaptativo temático (tile), no raio [radiusDp]. */
@Composable
private fun IconsAtRadius(radiusDp: Int) {
    val config = AureoleThemeConfig(
        darkTheme = isSystemInDarkTheme(),
        aureoleDimens = AureoleDimens.forCornerRadius(radiusDp)
    )
    AureoleLauncherTheme(config) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)
        ) {
            AureoleAppIcon(app = appWith(adaptiveIcon()), size = ICON_SIZE)
            AureoleAppIcon(app = appWith(0xFF2E7D32.toInt().toDrawable()), size = ICON_SIZE)
            AureoleAppIcon(app = appWith(adaptiveIcon()), size = ICON_SIZE, params = AureoleAppIconParams(isThemed = true))
            AureoleText(text = "$radiusDp dp", color = AureoleDS.colors.onSurfaceLow)
        }
    }
}

/** O relógio da home nos tamanhos da grade (RF-B1 a RF-B3): a mesma organização de 4×2 até um 2×1 baixo. */
@Composable
private fun ClockCell(columns: Int, height: Dp) {
    Box(modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.xSmall)) {
        ClockHeader(
            resolvedConfig = ResolvedClockConfig(
                style = ClockLayout.MSG_TIME_DATE.key,
                // Mensagem própria, para a saudação não mudar com a hora da execução.
                customGreeting = "Boa tarde",
                message = ClockMessage.CUSTOM.key,
                alignment = "CENTER",
                fontFamily = "DEFAULT",
                timeFormat = "24H",
                dateFormat = "DEFAULT",
                textColor = 0,
                backgroundColor = CARD_COLOR
            ),
            modifier = Modifier.size(CELL_WIDTH * columns, height),
            hazeConfig = ClockHazeConfig(isBackgroundEnabled = true)
        )
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

/** Logo, fogos parados, borda tracejada e moldura de prévia. */
@Composable
private fun BrandRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
        modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)
    ) {
        Box(
            modifier = Modifier
                .width(FIREWORKS_WIDTH)
                .aspectRatio(1f / FIREWORKS_ASPECT)
                .background(AureoleDS.colors.surface)
        ) {
            AureoleFireworksFrame(frozenShow())
        }
        Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)) {
            Box(modifier = Modifier.aureoleDashedBorder()) {
                AureoleText(
                    text = "Dashed",
                    color = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.padding(AureoleDS.dimens.medium)
                )
            }
            AureolePreviewFrame {
                AureoleText(text = "Preview content", color = AureoleDS.colors.onSurfaceHigh)
            }
        }
    }
}

/**
 * Grupo marca: `AureoleLogoRing`, `AureoleFireworks`, `AureoleDashedBorder`, `AureolePreviewFrame`,
 * `AureoleMiniHome` (destro, canhoto, ícones temáticos), raio do ícone (0, 20, 24 dp) e o relógio na grade.
 */
@PreviewTest
@AureoleCatalogPreview
@Composable
private fun MarcaScreenshotTest() = CatalogoColumn {
    CatalogoLabel("AureoleLogoRing: 0 / 0,5 / 1 grande / indeterminado")
    Row(
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
        modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)
    ) {
        AureoleLogoRing(progress = 0f)
        AureoleLogoRing(progress = 0.5f)
        AureoleLogoRing(progress = 1f, modifier = Modifier.size(LOGO_LARGE))
        // O preview desenha o anel parado: o arco indeterminado no ângulo de repouso.
        AureoleLogoRing(progress = null)
    }
    CatalogoLabel("AureoleFireworks / AureoleDashedBorder / AureolePreviewFrame")
    BrandRow()
    CatalogoLabel("AureoleMiniHome: destro / canhoto / ícones temáticos")
    listOf(false to false, true to false, false to true).forEach { (isLeftHanded, themedIcons) ->
        Box(modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)) {
            AureoleMiniHome(
                content = sampleMiniHomeContent(),
                layout = MiniHomeFavoritesLayout(isLeftHanded = isLeftHanded, themedIcons = themedIcons)
            )
        }
    }
    CatalogoLabel("AppIconRadius: 0 / 20 / 24 dp")
    IconsAtRadius(0)
    IconsAtRadius(20)
    IconsAtRadius(24)
    CatalogoLabel("ClockHeader na grade: 4×2 / 4×1 / 2×1 / 2×1 baixo")
    ClockCell(columns = 4, height = CELL_HEIGHT * 2)
    ClockCell(columns = 4, height = CELL_HEIGHT)
    Row {
        ClockCell(columns = 2, height = CELL_HEIGHT)
        ClockCell(columns = 2, height = LOW_CELL_HEIGHT)
    }
}
