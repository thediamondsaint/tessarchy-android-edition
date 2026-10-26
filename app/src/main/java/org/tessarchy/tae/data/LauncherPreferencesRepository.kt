package org.tessarchy.tae.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.tessarchy.tae.launcher.LauncherPreferences
import org.tessarchy.tae.launcher.MetroTheme
import org.tessarchy.tae.launcher.TileId
import java.io.IOException

private val Context.launcherDataStore by preferencesDataStore(name = "launcher_preferences")
private val orderKey = stringPreferencesKey("tile_order")
private val hiddenKey = stringPreferencesKey("hidden_tiles")
private val wideKey = stringPreferencesKey("wide_tiles")
private val themeKey = stringPreferencesKey("metro_theme")

private fun parseTileIds(value: String?): List<TileId> = value.orEmpty()
    .split(',')
    .mapNotNull(TileId::parse)

private fun Preferences.toLauncherPreferences(): LauncherPreferences = LauncherPreferences.sanitize(
    order = this[orderKey]?.let(::parseTileIds) ?: TileId.defaultOrder,
    hidden = parseTileIds(this[hiddenKey]).toSet(),
    wide = this[wideKey]?.let(::parseTileIds)?.toSet()
        ?: TileId.entries.filterTo(mutableSetOf()) { it.defaultWide || it.required },
    theme = MetroTheme.parse(this[themeKey].orEmpty()),
)

class LauncherPreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val preferences: Flow<LauncherPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences -> preferences.toLauncherPreferences() }

    suspend fun save(updated: LauncherPreferences) {
        dataStore.edit { stored ->
            stored[orderKey] = updated.tileOrder.joinToString(separator = ",") { it.key }
            stored[hiddenKey] = updated.hiddenTiles.joinToString(separator = ",") { it.key }
            stored[wideKey] = updated.wideTiles.joinToString(separator = ",") { it.key }
            stored[themeKey] = updated.theme.key
        }
    }

    companion object {
        fun from(context: Context): LauncherPreferencesRepository =
            LauncherPreferencesRepository(context.applicationContext.launcherDataStore)
    }
}
