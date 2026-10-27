package dev.mnascimentos.aureole.util

import android.app.Application
import android.app.role.RoleManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class DefaultHomeRoleUtilsTest {

    private val context: Application = RuntimeEnvironment.getApplication()

    @Test
    @Config(sdk = [28])
    fun `when on Android 9, should not offer the role dialog`() {
        assertNull(DefaultHomeRoleUtils.createRequestRoleIntent(context))
    }

    @Test
    @Config(sdk = [33])
    fun `when the home role is available and not held, should build the role request`() {
        shadowOf(context.getSystemService(RoleManager::class.java)).addAvailableRole(RoleManager.ROLE_HOME)

        val intent = DefaultHomeRoleUtils.createRequestRoleIntent(context)

        assertNotNull(intent)
        assertEquals(RoleManager.ROLE_HOME, intent?.getStringExtra("android.intent.extra.ROLE_NAME"))
    }

    @Test
    @Config(sdk = [33])
    fun `when the home role is already held, should not ask again`() {
        val shadow = shadowOf(context.getSystemService(RoleManager::class.java))
        shadow.addAvailableRole(RoleManager.ROLE_HOME)
        shadow.addHeldRole(RoleManager.ROLE_HOME)

        assertNull(DefaultHomeRoleUtils.createRequestRoleIntent(context))
    }

    @Test
    @Config(sdk = [33])
    fun `when the home role is unavailable, should not offer the dialog`() {
        assertNull(DefaultHomeRoleUtils.createRequestRoleIntent(context))
    }
}
