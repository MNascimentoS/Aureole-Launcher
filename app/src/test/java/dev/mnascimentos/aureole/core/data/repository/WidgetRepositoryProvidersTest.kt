package dev.mnascimentos.aureole.core.data.repository

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class WidgetRepositoryProvidersTest {

    private lateinit var context: Context
    private lateinit var repository: WidgetRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = WidgetRepository(context)
    }

    private fun installApp(packageName: String, label: String) {
        shadowOf(context.packageManager).installPackage(
            PackageInfo().apply {
                this.packageName = packageName
                applicationInfo = ApplicationInfo().apply {
                    this.packageName = packageName
                    nonLocalizedLabel = label
                }
            }
        )
    }

    private fun addProvider(packageName: String, className: String, minWidth: Int, minHeight: Int) {
        val info = AppWidgetProviderInfo().apply {
            provider = ComponentName(packageName, className)
            this.minWidth = minWidth
            this.minHeight = minHeight
        }
        // The label comes from the hidden providerInfo field, which the system fills in on a device.
        AppWidgetProviderInfo::class.java.getField("providerInfo").set(
            info,
            ActivityInfo().apply {
                this.packageName = packageName
                name = className
                nonLocalizedLabel = className.substringAfterLast('.')
                applicationInfo = ApplicationInfo().apply { this.packageName = packageName }
            }
        )
        shadowOf(AppWidgetManager.getInstance(context)).addInstalledProvider(info)
    }

    @Test
    fun `when no widget is installed, should list nothing`() = runBlocking {
        assertTrue(repository.getAvailableWidgets().isEmpty())
    }

    @Test
    fun `when apps provide widgets, should group them by app sorted by name with a span estimate`() = runBlocking {
        installApp("com.zeta.weather", "Zeta Weather")
        installApp("com.alpha.notes", "Alpha Notes")
        addProvider("com.zeta.weather", "com.zeta.weather.Small", minWidth = 40, minHeight = 40)
        addProvider("com.zeta.weather", "com.zeta.weather.Large", minWidth = 250, minHeight = 110)
        addProvider("com.alpha.notes", "com.alpha.notes.List", minWidth = 110, minHeight = 180)

        val groups = repository.getAvailableWidgets()

        assertEquals(listOf("Alpha Notes", "Zeta Weather"), groups.map { it.appName })
        val weather = groups.last()
        assertEquals(2, weather.availableWidgets.size)
        val small = weather.availableWidgets.first { it.widgetId.endsWith("Small") }
        val large = weather.availableWidgets.first { it.widgetId.endsWith("Large") }
        assertEquals(1, small.minSpanX)
        assertTrue(large.minSpanX > small.minSpanX)
        assertTrue(large.minSpanY >= 1)
    }

    @Test
    fun `when a widget's app cannot be found, should skip it`() = runBlocking {
        installApp("com.alpha.notes", "Alpha Notes")
        addProvider("com.alpha.notes", "com.alpha.notes.List", minWidth = 110, minHeight = 110)
        addProvider("com.ghost.app", "com.ghost.app.Widget", minWidth = 110, minHeight = 110)

        assertEquals(listOf("com.alpha.notes"), repository.getAvailableWidgets().map { it.appId })
    }
}
