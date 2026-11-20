package dev.mnascimentos.aureole.core.designsystem.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.R

@Composable
fun AureoleIcons.CheckSelected(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_app_select, modifier, tint) }

@Composable
fun AureoleIcons.CheckUnselected(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_check_unselected, modifier, tint) }

@Composable
fun AureoleIcons.MultipleView(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_multiple_view, modifier, tint) }

@Composable
fun AureoleIcons.RoundCheck(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_round_check, modifier, tint) }

@Composable
fun AureoleIcons.Unchecked(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_check_unselected, modifier, tint) }

@Composable
fun AureoleIcons.Crosshair(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_center, modifier, tint) }

@Composable
fun AureoleIcons.RotateClockwise(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_rotate_cw, modifier, tint) }

@Composable
fun AureoleIcons.ImageSwap(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_image_swap, modifier, tint) }

@Composable
fun AureoleIcons.Image(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_image, modifier, tint) }
