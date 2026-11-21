package dev.mnascimentos.aureole.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import java.lang.reflect.InvocationTargetException

/** Opening the notification shade (ajustes-review-aislan-2 RF-A1, RF-A5). */
@RunWith(RobolectricTestRunner::class)
class IntentUtilsShadeTest {

    private val tag = "IntentUtils"

    @Before
    fun setUp() {
        ShadowLog.clear()
    }

    @Test
    fun `the manifest should request EXPAND_STATUS_BAR, without which the shade never opens`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)

        assertTrue(info.requestedPermissions.orEmpty().contains(Manifest.permission.EXPAND_STATUS_BAR))
    }

    @Test
    fun `a successful expansion should report true and log nothing`() {
        assertTrue(IntentUtils.runShadeExpansion {})
        assertTrue(ShadowLog.getLogsForTag(tag).isEmpty())
    }

    @Test
    fun `a security exception wrapped by reflection should be logged with its real cause`() {
        val denied = SecurityException("needs EXPAND_STATUS_BAR")

        val opened = IntentUtils.runShadeExpansion { throw InvocationTargetException(denied) }

        assertFalse(opened)
        val log = ShadowLog.getLogsForTag(tag).single()
        assertEquals(Log.WARN, log.type)
        assertEquals(denied, log.throwable)
    }

    @Test
    fun `a missing status bar service should be logged, not crash`() {
        val opened = IntentUtils.runShadeExpansion { error("No status bar service") }

        assertFalse(opened)
        assertEquals(Log.WARN, ShadowLog.getLogsForTag(tag).single().type)
    }

    @Test
    fun `a missing hidden method should be logged, not crash`() {
        val opened = IntentUtils.runShadeExpansion { throw NoSuchMethodException("expandNotificationsPanel") }

        assertFalse(opened)
        assertEquals(Log.WARN, ShadowLog.getLogsForTag(tag).single().type)
    }
}
