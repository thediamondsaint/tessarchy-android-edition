package org.tessarchy.tae.launcher

import androidx.annotation.StringRes
import org.tessarchy.tae.R

enum class TileId(
    val key: String,
    @StringRes val title: Int,
    val defaultWide: Boolean = false,
    val required: Boolean = false,
) {
    Tessarchy("tessarchy", R.string.tessarchy_name_uppercase),
    Calendar("calendar", R.string.calendar_title),
    Phone("phone", R.string.phone_title),
    Messages("messages", R.string.messages_title),
    Tessa("tessa", R.string.tessa_name, defaultWide = true),
    Photos("photos", R.string.photos_title),
    Music("music", R.string.music_title),
    Apps("apps", R.string.apps_title, defaultWide = true, required = true),
    ;

    companion object {
        val defaultOrder = entries

        fun parse(key: String): TileId? = entries.firstOrNull { it.key == key }
    }
}

enum class MetroTheme(val key: String, @StringRes val label: Int) {
    Midnight("midnight", R.string.theme_midnight),
    Daylight("daylight", R.string.theme_daylight),
    Cobalt("cobalt", R.string.theme_cobalt),
    ;

    companion object {
        fun parse(key: String): MetroTheme = entries.firstOrNull { it.key == key } ?: Midnight
    }
}

data class LauncherPreferences(
    val tileOrder: List<TileId> = TileId.defaultOrder,
    val hiddenTiles: Set<TileId> = emptySet(),
    val wideTiles: Set<TileId> = TileId.entries.filterTo(mutableSetOf()) { it.defaultWide },
    val theme: MetroTheme = MetroTheme.Midnight,
) {
    val visibleTiles: List<TileId>
        get() = tileOrder.filter { it !in hiddenTiles || it.required }

    fun moveTile(fromIndex: Int, toIndex: Int): LauncherPreferences {
        if (fromIndex !in tileOrder.indices || toIndex !in tileOrder.indices || fromIndex == toIndex) {
            return this
        }
        return copy(
            tileOrder = tileOrder.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            },
        )
    }

    fun setTileEnabled(tile: TileId, enabled: Boolean): LauncherPreferences = when {
        tile.required -> this
        enabled -> copy(hiddenTiles = hiddenTiles - tile)
        else -> copy(hiddenTiles = hiddenTiles + tile)
    }

    fun toggleTileWidth(tile: TileId): LauncherPreferences =
        if (tile.required) this
        else if (tile in wideTiles) copy(wideTiles = wideTiles - tile)
        else copy(wideTiles = wideTiles + tile)

    companion object {
        fun sanitize(
            order: List<TileId>,
            hidden: Set<TileId>,
            wide: Set<TileId>,
            theme: MetroTheme,
        ): LauncherPreferences {
            val cleanOrder = (order + TileId.defaultOrder).distinct()
            return LauncherPreferences(
                tileOrder = cleanOrder,
                hiddenTiles = hidden - TileId.entries.filter { it.required }.toSet(),
                wideTiles = wide + TileId.entries.filter { it.required },
                theme = theme,
            )
        }
    }
}
