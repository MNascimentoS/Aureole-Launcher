package dev.mnascimentos.aureole.core.data.db

import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RoomConvertersTest {

    private val converters = RoomConverters()

    @Test
    fun testFromContainerItemType() {
        assertNull(converters.fromContainerItemType(null))
        assertEquals("APP", converters.fromContainerItemType(ContainerItemType.APP))
        assertEquals("FOLDER", converters.fromContainerItemType(ContainerItemType.FOLDER))
    }

    @Test
    fun testToContainerItemType() {
        assertNull(converters.toContainerItemType(null))
        assertEquals(ContainerItemType.APP, converters.toContainerItemType("APP"))
        assertEquals(ContainerItemType.FOLDER, converters.toContainerItemType("FOLDER"))
        assertEquals(ContainerItemType.APP, converters.toContainerItemType("INVALID_TYPE"))
    }
}
