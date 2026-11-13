package dev.mnascimentos.aureole.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class AureoleDimensCornerRadiusTest {

    private val base = AureoleDimens()

    private fun AureoleDimens.scaledRadii(): List<Dp> = listOf(
        radiusMinimal,
        radiusXSmall,
        radiusSmallCompact,
        radiusSmall,
        radiusMedium,
        radiusMediumLarge
    )

    private fun assertDp(expected: Dp, actual: Dp) =
        assertEquals(expected.value, actual.value, TOLERANCE)

    @Test
    fun `radius 0 makes every radius and the pill straight`() {
        val dimens = AureoleDimens.forCornerRadius(0)

        dimens.scaledRadii().forEach { assertDp(0.dp, it) }
        assertDp(0.dp, dimens.radiusLarge)
        assertDp(0.dp, dimens.radiusFull)
    }

    @Test
    fun `radius 20 keeps today's visual and only sets radiusLarge to 20`() {
        val dimens = AureoleDimens.forCornerRadius(20)

        assertEquals(base.copy(radiusLarge = 20.dp), dimens)
        assertDp(100.dp, dimens.radiusFull)
    }

    @Test
    fun `radius 24 scales the smaller radii proportionally`() {
        val dimens = AureoleDimens.forCornerRadius(24)

        assertDp(24.dp, dimens.radiusLarge)
        assertDp(base.radiusMinimal * 1.2f, dimens.radiusMinimal)
        assertDp(base.radiusXSmall * 1.2f, dimens.radiusXSmall)
        assertDp(base.radiusSmallCompact * 1.2f, dimens.radiusSmallCompact)
        assertDp(base.radiusSmall * 1.2f, dimens.radiusSmall)
        assertDp(base.radiusMedium * 1.2f, dimens.radiusMedium)
        assertDp(base.radiusMediumLarge * 1.2f, dimens.radiusMediumLarge)
        assertDp(100.dp, dimens.radiusFull)
    }

    @Test
    fun `radius changes never touch spacings, icons or borders`() {
        listOf(0, 20, 24).forEach { userDp ->
            val dimens = AureoleDimens.forCornerRadius(userDp)

            assertEquals(
                base.copy(
                    radiusMinimal = dimens.radiusMinimal,
                    radiusXSmall = dimens.radiusXSmall,
                    radiusSmallCompact = dimens.radiusSmallCompact,
                    radiusSmall = dimens.radiusSmall,
                    radiusMedium = dimens.radiusMedium,
                    radiusMediumLarge = dimens.radiusMediumLarge,
                    radiusLarge = dimens.radiusLarge,
                    radiusFull = dimens.radiusFull
                ),
                dimens
            )
        }
    }

    private companion object {
        const val TOLERANCE = 0.001f
    }
}
