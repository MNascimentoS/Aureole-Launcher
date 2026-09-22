package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.mnascimentos.aureole.feature.home.grid.model.DragTargetSlot

private const val GRID_OFFSET_PX = 8f
private const val GRID_MARGIN_PX = 16f
private const val GRID_LINE_WIDTH = 2f
private const val GRID_CORNER_RADIUS = 24f
private const val DASH_LENGTH_PX = 10f
private const val TOP_BAR_Z_INDEX = 100f

@Composable
fun GridTopEditBar(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onAddContainer: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .zIndex(TOP_BAR_Z_INDEX),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EditBarCancelButton(onCancel = onCancel)
            EditBarActionGroup(
                onSettings = onSettings,
                onAddContainer = onAddContainer,
                onSave = onSave
            )
        }
    }
}

@Composable
private fun EditBarCancelButton(onCancel: () -> Unit) {
    FilledTonalButton(
        onClick = onCancel,
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cancelar",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Cancelar", maxLines = 1, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun EditBarActionGroup(
    onSettings: () -> Unit,
    onAddContainer: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(onClick = onSettings, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configurações",
                modifier = Modifier.size(20.dp)
            )
        }
        FilledTonalIconButton(onClick = onAddContainer, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar Container",
                modifier = Modifier.size(20.dp)
            )
        }
        Button(
            onClick = onSave,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Salvar", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Salvar",
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GridBackgroundOverlay(
    limits: GridLimits,
    cellWidthPx: Float,
    cellHeightPx: Float,
    activeDragTarget: DragTargetSlot?,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    Canvas(modifier = modifier.fillMaxSize()) {
        val stroke = Stroke(
            width = GRID_LINE_WIDTH,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(DASH_LENGTH_PX, DASH_LENGTH_PX), 0f)
        )
        val gridColor = Color.White.copy(alpha = 0.25f)
        for (col in 0 until limits.maxCols) {
            for (row in 0 until limits.maxRows) {
                drawRoundRect(
                    color = gridColor,
                    topLeft = Offset((col * cellWidthPx) + GRID_OFFSET_PX, (row * cellHeightPx) + GRID_OFFSET_PX),
                    size = Size(cellWidthPx - GRID_MARGIN_PX, cellHeightPx - GRID_MARGIN_PX),
                    cornerRadius = CornerRadius(GRID_CORNER_RADIUS, GRID_CORNER_RADIUS),
                    style = stroke
                )
            }
        }

        activeDragTarget?.let { target ->
            val targetColor = if (target.isValid) primaryColor else errorColor
            val left = (target.col * cellWidthPx) + GRID_OFFSET_PX
            val top = (target.row * cellHeightPx) + GRID_OFFSET_PX
            val width = (target.colSpan * cellWidthPx) - GRID_MARGIN_PX
            val height = (target.rowSpan * cellHeightPx) - GRID_MARGIN_PX

            if (width > 0f && height > 0f) {
                drawRoundRect(
                    color = targetColor.copy(alpha = 0.25f),
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(GRID_CORNER_RADIUS, GRID_CORNER_RADIUS)
                )
                drawRoundRect(
                    color = targetColor.copy(alpha = 0.8f),
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(GRID_CORNER_RADIUS, GRID_CORNER_RADIUS),
                    style = stroke
                )
            }
        }
    }
}
