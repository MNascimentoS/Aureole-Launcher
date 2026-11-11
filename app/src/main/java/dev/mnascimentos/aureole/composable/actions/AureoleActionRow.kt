package dev.mnascimentos.aureole.composable.actions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.composable.actions.model.ACTION_PRIMARY_WEIGHT
import dev.mnascimentos.aureole.composable.actions.model.ACTION_SECONDARY_WEIGHT
import dev.mnascimentos.aureole.composable.actions.model.ActionRowParams
import dev.mnascimentos.aureole.composable.actions.model.ActionSlot
import dev.mnascimentos.aureole.composable.actions.model.actionRowOrder
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Footer of the edit sheets: an outlined secondary button (weight 1) and a filled primary button
 * (weight 1.4), 48 dp tall and full width. In the left-handed mode the order is mirrored.
 */
@Composable
fun AureoleActionRow(
    params: ActionRowParams,
    isLeftHanded: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.smallCompact),
        verticalAlignment = Alignment.CenterVertically
    ) {
        actionRowOrder(isLeftHanded).forEach { slot ->
            when (slot) {
                ActionSlot.SECONDARY -> SecondaryButton(params)
                ActionSlot.PRIMARY -> PrimaryButton(params)
            }
        }
    }
}

@Composable
private fun RowScope.PrimaryButton(params: ActionRowParams) {
    AureolePrimaryActionButton(
        label = params.primaryLabel,
        onClick = params.onPrimary,
        modifier = Modifier.weight(ACTION_PRIMARY_WEIGHT)
    )
}

/** The filled, pill-shaped primary button of the edit sheets: "Save", "Apply", "Done". Alone it fills the width. */
@Composable
fun AureolePrimaryActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = AureoleDS.dimens.minTouchTarget),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = AureoleDS.colors.onSurfaceHigh,
            contentColor = AureoleDS.colors.surface
        )
    ) {
        ActionLabel(label, AureoleDS.colors.surface)
    }
}

@Composable
private fun RowScope.SecondaryButton(params: ActionRowParams) {
    val color = if (params.isSecondaryDestructive) AureoleDS.colors.error else AureoleDS.colors.onSurfaceMedium
    OutlinedButton(
        onClick = params.onSecondary,
        modifier = Modifier
            .weight(ACTION_SECONDARY_WEIGHT)
            .heightIn(min = AureoleDS.dimens.minTouchTarget),
        shape = CircleShape,
        border = BorderStroke(AureoleDS.dimens.borderMinimal, AureoleDS.colors.surfaceVariant)
    ) {
        ActionLabel(params.secondaryLabel, color)
    }
}

@Composable
private fun ActionLabel(text: String, color: Color) {
    AureoleText(
        text = text,
        color = color,
        format = AureoleTextFormatParams(fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    )
}

@AureolePreview
@Composable
private fun AureoleActionRowPreview() {
    AureoleLauncherTheme {
        AureoleActionRow(
            params = ActionRowParams("Apply", {}, "Cancel", {}),
            isLeftHanded = false
        )
    }
}
