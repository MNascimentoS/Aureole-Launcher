package dev.mnascimentos.aureole.composable.mirror

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import dev.mnascimentos.aureole.composable.mirror.model.mirroredLayoutDirection
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded

/**
 * Mirrors the row drawn in [content] in the left-handed mode: children run right to left and start-aligned
 * text sits on the right. Does nothing for right-handed users. [isLeftHanded] defaults to the launcher setting.
 */
@Composable
fun LeftHandMirror(
    isLeftHanded: Boolean = LocalLeftHanded.current,
    content: @Composable () -> Unit
) {
    val direction = mirroredLayoutDirection(isLeftHanded, LocalLayoutDirection.current)
    CompositionLocalProvider(LocalLayoutDirection provides direction, content = content)
}
