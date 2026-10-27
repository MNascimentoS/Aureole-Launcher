package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsExtensionsTest {

    @Test
    fun `getVersionName should return valid version string or fallback`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val versionName = getVersionName(context)
        assertNotNull(versionName)
    }
}
