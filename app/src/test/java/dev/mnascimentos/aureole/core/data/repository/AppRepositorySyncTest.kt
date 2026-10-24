package dev.mnascimentos.aureole.core.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Looper
import dev.mnascimentos.aureole.core.data.receiver.PackageReceiver
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class AppRepositorySyncTest {

    private lateinit var context: Context
    private lateinit var repository: AppRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = AppRepository(context)
    }

    @After
    fun tearDown() {
        // The Room database is shared by every test class in the JVM.
        removeLauncherApps()
        runBlocking { repository.syncApps() }
    }

    @Test
    fun `when syncing, should store launchable apps except the launcher itself`() = runBlocking {
        installLauncherApp(MAIL, "Mail")
        installLauncherApp(context.packageName, "Aureole")

        repository.syncApps()

        val apps = repository.getInstalledApps()
        assertEquals(listOf(MAIL), apps.map { it.packageName })
        assertEquals("Mail", apps.single().label)
    }

    @Test
    fun `when no launchable app is left, should clear the stored list`() = runBlocking {
        installLauncherApp(MAIL, "Mail")
        repository.syncApps()

        removeLauncherApps()
        repository.syncApps()

        assertTrue(repository.getInstalledApps().isEmpty())
    }

    @Test
    fun `when the stored list is empty, should sync before answering`() = runBlocking {
        installLauncherApp(NOTES, "Notes")

        val apps = repository.getInstalledApps()

        assertEquals(listOf(NOTES), apps.map { it.packageName })
    }

    @Test
    fun `when a single package is synced, should add it, or remove it once uninstalled`() = runBlocking {
        installLauncherApp(MAIL, "Mail")
        repository.syncApps()
        installLauncherApp(NOTES, "Notes", alsoForPackageQuery = true)

        repository.syncPackage(NOTES)
        assertEquals(setOf(MAIL, NOTES), repository.getInstalledApps().map { it.packageName }.toSet())

        repository.syncPackage("com.gone")
        repository.syncPackage(context.packageName)
        repository.removePackage(MAIL)
        assertEquals(listOf(NOTES), repository.getInstalledApps().map { it.packageName })
    }

    @Test
    fun `when launching an app, should start its activity in a new task`() {
        repository.launchApp(MAIL, "$MAIL.Main")

        val started = shadowOf(RuntimeEnvironment.getApplication()).nextStartedActivity
        assertEquals(ComponentName(MAIL, "$MAIL.Main"), started.component)
        assertTrue(started.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun `when the package receiver hears an uninstall, should remove the app`() = runBlocking {
        installLauncherApp(MAIL, "Mail")
        installLauncherApp(NOTES, "Notes")
        repository.syncApps()
        shadowOf(context.packageManager).removeResolveInfosForIntent(launcherIntent(), MAIL)

        broadcast(packageIntent(Intent.ACTION_PACKAGE_REMOVED, MAIL))

        awaitApps { it == listOf(NOTES) }
    }

    @Test
    fun `when the package receiver hears an install, should add the app`() = runBlocking {
        installLauncherApp(NOTES, "Notes", alsoForPackageQuery = true)

        broadcast(packageIntent(Intent.ACTION_PACKAGE_ADDED, NOTES))

        awaitApps { NOTES in it }
    }

    @Test
    fun `when the package receiver hears a replacing removal or bad intents, should keep the apps`() = runBlocking {
        installLauncherApp(MAIL, "Mail")
        repository.syncApps()

        broadcast(packageIntent(Intent.ACTION_PACKAGE_REMOVED, MAIL).putExtra(Intent.EXTRA_REPLACING, true))
        broadcast(packageIntent(Intent.ACTION_PACKAGE_REMOVED, context.packageName))
        // Intents without a package or without an action return before doing any work.
        PackageReceiver().onReceive(context, Intent(Intent.ACTION_PACKAGE_REMOVED))
        PackageReceiver().onReceive(context, Intent().setData(Uri.parse("package:$MAIL")))
        Thread.sleep(SETTLE_MS)

        assertEquals(listOf(MAIL), repository.getInstalledApps().map { it.packageName })
    }

    /**
     * Delivers the intent the way the system does, so goAsync() hands the receiver a PendingResult.
     * Calling onReceive() directly would leave it null and crash on a background thread.
     */
    private fun broadcast(intent: Intent) {
        val receiver = PackageReceiver()
        val filter = IntentFilter(intent.action).apply { addDataScheme("package") }
        context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        context.sendBroadcast(intent.setPackage(context.packageName))
        shadowOf(Looper.getMainLooper()).idle()
        Thread.sleep(SETTLE_MS)
        context.unregisterReceiver(receiver)
    }

    private fun packageIntent(action: String, packageName: String) =
        Intent(action).setData(Uri.parse("package:$packageName"))

    private suspend fun awaitApps(condition: (List<String>) -> Boolean) {
        val deadline = System.currentTimeMillis() + TIMEOUT_MS
        while (!condition(repository.getInstalledApps().map { it.packageName })) {
            shadowOf(Looper.getMainLooper()).idle()
            check(System.currentTimeMillis() < deadline) { "Apps did not change" }
            Thread.sleep(POLL_MS)
        }
    }

    private fun resolveInfo(packageName: String, label: String) = ResolveInfo().apply {
        nonLocalizedLabel = label
        activityInfo = ActivityInfo().apply {
            this.packageName = packageName
            name = "$packageName.Main"
            applicationInfo = ApplicationInfo().apply { this.packageName = packageName }
        }
    }

    private fun launcherIntent() = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)

    private fun installLauncherApp(packageName: String, label: String, alsoForPackageQuery: Boolean = false) {
        val shadowPm = shadowOf(context.packageManager)
        shadowPm.addResolveInfoForIntent(launcherIntent(), resolveInfo(packageName, label))
        if (alsoForPackageQuery) {
            shadowPm.addResolveInfoForIntent(launcherIntent().setPackage(packageName), resolveInfo(packageName, label))
        }
    }

    private fun removeLauncherApps() {
        val shadowPm = shadowOf(context.packageManager)
        listOf(MAIL, NOTES, context.packageName).forEach { pkg ->
            shadowPm.removeResolveInfosForIntent(launcherIntent(), pkg)
        }
    }

    private companion object {
        const val MAIL = "com.example.mail"
        const val NOTES = "com.example.notes"
        const val TIMEOUT_MS = 5_000L
        const val POLL_MS = 20L
        const val SETTLE_MS = 300L
    }
}
