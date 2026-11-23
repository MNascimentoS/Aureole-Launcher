package dev.mnascimentos.aureole.core.designsystem.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.R

@Composable
fun AureoleIcons.AlignRight(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_align_right, modifier, tint) }

/** Points to the end of the row and mirrors by itself in right-to-left layouts (left-handed mode). */
@Composable
fun AureoleIcons.ChevronRight(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_chevron_right, modifier, tint) }

/** Points to the start of the row; like [ChevronRight] it mirrors by itself in right-to-left layouts. */
@Composable
fun AureoleIcons.ChevronLeft(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_chevron_right, modifier.rotate(CHEVRON_LEFT_ROTATION_DEGREES), tint) }

private const val CHEVRON_LEFT_ROTATION_DEGREES = 180f
private const val ARROW_DOWN_ROTATION_DEGREES = 180f
private const val CLOSE_SQUARE_ROTATION_DEGREES = 45f

// Set A
@Composable
fun AureoleIcons.AppSelect(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_app_select, modifier, tint) }

@Composable
fun AureoleIcons.ArrowDown(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_arrow_up, modifier.rotate(ARROW_DOWN_ROTATION_DEGREES), tint) }

@Composable
fun AureoleIcons.ArrowUp(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_arrow_up, modifier, tint) }

@Composable
fun AureoleIcons.CloseSquare(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_add, modifier.rotate(CLOSE_SQUARE_ROTATION_DEGREES), tint) }

@Composable
fun AureoleIcons.Close(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_close, modifier, tint) }

@Composable
fun AureoleIcons.Check(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_check, modifier, tint) }
