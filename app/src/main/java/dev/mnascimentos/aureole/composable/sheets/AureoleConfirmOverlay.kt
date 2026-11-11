package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.actions.AureoleActionRow
import dev.mnascimentos.aureole.composable.actions.AureolePrimaryActionButton
import dev.mnascimentos.aureole.composable.actions.model.ActionRowParams
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Edit overlay with an optional preview anchored above the sheet and a Cancel / confirm action row below it, the
 * filled button on the thumb side (or the filled button alone, when `hasSecondary` is false). Used by the
 * editors that keep a draft until the user confirms (background, color picker, folder form, quick button
 * appearance). The confirm button runs `onPrimary` and closes; Cancel, swiping down and tapping outside only
 * close. [bottomExtra] sits above the action row, glued to it (a text field that has to stay next to the keyboard).
 */
@Composable
fun AureoleConfirmOverlay(
    params: ConfirmOverlayParams,
    modifier: Modifier = Modifier,
    preview: (@Composable BoxScope.() -> Unit)? = null,
    bottomExtra: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val overlayState = rememberAureoleEditOverlayState()
    val cancelLabel = params.secondaryLabel ?: stringResource(R.string.core_common_cancel)
    AureoleEditOverlay(
        modifier = modifier,
        state = overlayState,
        onDismissRequest = params.onDismissRequest,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = params.title,
                hazeState = params.hazeState,
                isLeftHanded = params.isLeftHanded,
                onSettingsClick = params.onSettingsClick
            ),
            previewContent = preview,
            bottomBarContent = {
                bottomExtra?.invoke(this)
                if (params.hasSecondary) {
                    AureoleActionRow(
                        params = ActionRowParams(
                            primaryLabel = params.primaryLabel,
                            onPrimary = {
                                params.onPrimary()
                                overlayState.internalVisible = false
                            },
                            secondaryLabel = cancelLabel,
                            onSecondary = { overlayState.internalVisible = false },
                            primaryHoldMs = params.primaryHoldMs
                        ),
                        isLeftHanded = params.isLeftHanded != params.isPrimaryAwayFromThumb
                    )
                } else {
                    AureolePrimaryActionButton(
                        label = params.primaryLabel,
                        onClick = {
                            params.onPrimary()
                            overlayState.internalVisible = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            sheetContent = content
        )
    )
}

@AureolePreview
@Composable
private fun AureoleConfirmOverlayPreview() {
    AureoleLauncherTheme {
        AureoleConfirmOverlay(
            params = ConfirmOverlayParams(
                title = "Title",
                primaryLabel = "Apply",
                onPrimary = {},
                onDismissRequest = {}
            )
        ) {
            AureoleText(text = "Content", color = AureoleDS.colors.onSurfaceHigh)
        }
    }
}
