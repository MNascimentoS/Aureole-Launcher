package dev.mnascimentos.aureole.core.data.repository

import com.google.gson.Gson
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import java.util.UUID

internal class ContainerItemMapper(private val gson: Gson = Gson()) {
    fun mapItemQuickButton(item: ContainerItemEntity): ContainerItemEntity {
        if (item.itemType != ContainerItemType.QUICK_BUTTON) return item
        val parsedModel = item.quickButtonModel
            ?: item.quickButtonJson?.let {
                try {
                    gson.fromJson(it, QuickButtonModel::class.java)
                } catch (_: Exception) {
                    null
                }
            }
            ?: item.quickButtonId?.let { qbId -> QuickButtonModel(id = qbId) }
        return item.copy(
            quickButtonId = parsedModel?.id ?: item.quickButtonId
        ).also { it.quickButtonModel = parsedModel }
    }

    fun buildItemEntities(panel: ContainerModel): List<ContainerItemEntity> {
        val itemEntities = mutableListOf<ContainerItemEntity>()
        if (panel.items.isNotEmpty()) {
            panel.items.forEachIndexed { index, item ->
                val updatedItem = if (item.itemType == ContainerItemType.QUICK_BUTTON) {
                    val qbModel = item.quickButtonModel
                        ?: panel.quickButtons.find { it.id == item.quickButtonId || it.id == item.id }
                    val qbJson = qbModel?.let { gson.toJson(it) }
                    item.copy(
                        orderIndex = index,
                        quickButtonId = qbModel?.id ?: item.quickButtonId,
                        quickButtonJson = qbJson
                    ).also { it.quickButtonModel = qbModel }
                } else {
                    item.copy(orderIndex = index)
                }
                itemEntities.add(updatedItem)
            }
        } else {
            var orderIndex = 0
            panel.folders.forEach { folder ->
                itemEntities.add(
                    ContainerItemEntity(
                        id = UUID.randomUUID().toString(),
                        panelId = panel.id,
                        itemType = ContainerItemType.FOLDER,
                        folderId = folder.id,
                        orderIndex = orderIndex++
                    )
                )
            }
            panel.appPackageNames.forEach { pkg ->
                itemEntities.add(
                    ContainerItemEntity(
                        id = UUID.randomUUID().toString(),
                        panelId = panel.id,
                        itemType = ContainerItemType.APP,
                        packageName = pkg,
                        orderIndex = orderIndex++
                    )
                )
            }
            panel.quickButtons.forEach { qb ->
                itemEntities.add(
                    ContainerItemEntity(
                        id = UUID.randomUUID().toString(),
                        panelId = panel.id,
                        itemType = ContainerItemType.QUICK_BUTTON,
                        quickButtonId = qb.id,
                        quickButtonJson = gson.toJson(qb),
                        orderIndex = orderIndex++
                    ).also { it.quickButtonModel = qb }
                )
            }
        }
        return itemEntities
    }
}
