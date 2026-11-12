package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

private const val TAG = "ClockConfigRepository"

/**
 * The look of each clock on the home grid, saved as JSON per grid item id in its own preferences file.
 *
 * The first use after updating from a launcher with a single clock (0.9.20 and older) copies the clock look from the
 * settings to every clock the portrait and landscape layouts have, once ([ensureMigrated]). Every read and write
 * waits for it, so nobody sees a clock without its old look. The settings keep the look, now only as the
 * [defaultConfig] of a new clock.
 */
class ClockConfigRepository(
    private val context: Context,
    private val gridRepository: GridRepository,
    private val settingsRepository: SettingsRepository,
) {
    private val gson = Gson()
    private val migrationLock = Mutex()
    private val writeLock = Mutex()
    private val prefs: SharedPreferences
        get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Volatile
    private var isLoaded = false

    private val _configs = MutableStateFlow<Map<String, ClockConfig>>(emptyMap())

    /** Every saved clock config by grid item id; filled by the first read or write. */
    val configs: StateFlow<Map<String, ClockConfig>> = _configs.asStateFlow()

    /** The look a new clock starts with: the clock look in the settings. */
    fun defaultConfig(): ClockConfig = settingsRepository.globalClockConfig()

    /**
     * Copies the settings clock look to every clock of the current layouts, once per install. Later calls (and a new
     * install, whose flag is saved on the first run) change nothing. A clock that already has a config keeps it.
     * Runs off the main thread; when it fails the flag is not saved, so the next start tries again.
     */
    suspend fun ensureMigrated() {
        if (isLoaded) return
        migrationLock.withLock {
            if (isLoaded) return
            withContext(Dispatchers.IO) {
                if (!prefs.getBoolean(KEY_MIGRATED_FROM_GLOBALS, false)) {
                    runCatching { migrateFromGlobals() }
                        .onFailure { Timber.tag(TAG).w(it, "Error migrating the clock settings") }
                }
                _configs.value = readAll()
            }
            isLoaded = true
        }
    }

    private fun migrateFromGlobals() {
        val clockIds = gridRepository.currentClockItemIds(settingsRepository.isLeftHandedMode)
        val globalJson = gson.toJson(defaultConfig())
        prefs.edit(commit = true) {
            clockIds.filterNot { prefs.contains(keyOf(it)) }.forEach { putString(keyOf(it), globalJson) }
            putBoolean(KEY_MIGRATED_FROM_GLOBALS, true)
        }
    }

    /** Every saved clock config by grid item id. */
    suspend fun getAll(): Map<String, ClockConfig> {
        ensureMigrated()
        return configs.value
    }

    /** The config of the clock [itemId], or null when it has none saved. */
    suspend fun get(itemId: String): ClockConfig? = getAll()[itemId]

    /** The config of the clock [itemId], or the [defaultConfig] when it has none saved. */
    suspend fun getOrDefault(itemId: String): ClockConfig = get(itemId) ?: defaultConfig()

    /**
     * Saves the config of the clock [itemId]. The [configs] change at once, in call order; the file then gets the
     * newest config of the clock, so a slower earlier write never overwrites a later one.
     */
    suspend fun save(itemId: String, config: ClockConfig) {
        ensureMigrated()
        _configs.update { it + (itemId to config) }
        persist(itemId)
    }

    /** Deletes the config of the clock [itemId] only. */
    suspend fun remove(itemId: String) {
        ensureMigrated()
        _configs.update { it - itemId }
        persist(itemId)
    }

    /** Writes what [configs] holds now for the clock [itemId]: its config, or nothing when it has none. */
    private suspend fun persist(itemId: String) = writeLock.withLock {
        withContext(Dispatchers.IO) {
            val key = keyOf(itemId)
            val config = _configs.value[itemId]
            prefs.edit(commit = true) {
                if (config == null) remove(key) else putString(key, gson.toJson(config))
            }
        }
    }

    private fun readAll(): Map<String, ClockConfig> = prefs.all.mapNotNull { (key, value) ->
        val itemId = key.removePrefix(KEY_CONFIG_PREFIX)
        val json = value as? String
        if (key == itemId || json == null) return@mapNotNull null
        gson.parseClockConfig(json)?.let { itemId to it }
    }.toMap()

    companion object {
        const val PREFS_NAME = "aureole_clock_prefs"
        private const val KEY_MIGRATED_FROM_GLOBALS = "migrated_from_globals"
    }
}

private const val KEY_CONFIG_PREFIX = "clock_config_"

private fun keyOf(itemId: String) = KEY_CONFIG_PREFIX + itemId

private fun Gson.parseClockConfig(json: String): ClockConfig? = runCatching { fromJson(json, ClockConfig::class.java) }
    .onFailure { Timber.tag(TAG).w(it, "Ignoring an unreadable clock config") }
    .getOrNull()
