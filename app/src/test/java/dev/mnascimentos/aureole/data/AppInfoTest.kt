package dev.mnascimentos.aureole.data

import dev.mnascimentos.aureole.data.db.AppInfoEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class AppInfoTest {

    @Test
    fun appInfoEntityCreation_isCorrect() {
        val entity = AppInfoEntity(
            packageName = "com.example.app",
            label = "Example App",
            activityName = "com.example.app.MainActivity",
        )

        assertEquals("com.example.app", entity.packageName)
        assertEquals("Example App", entity.label)
        assertEquals("com.example.app.MainActivity", entity.activityName)
    }
}
