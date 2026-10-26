package org.tessarchy.tae.launcher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherPreferencesTest {
    @Test
    fun moveTileReordersOnlyRequestedEntries() {
        val initial = LauncherPreferences()

        val reordered = initial.moveTile(1, 4)

        assertEquals(
            listOf(TileId.Tessarchy, TileId.Phone, TileId.Messages, TileId.Tessa, TileId.Calendar, TileId.Photos, TileId.Music, TileId.Apps),
            reordered.tileOrder,
        )
    }

    @Test
    fun moveTileIgnoresInvalidIndices() {
        val initial = LauncherPreferences()

        assertEquals(initial, initial.moveTile(-1, 2))
        assertEquals(initial, initial.moveTile(2, 99))
        assertEquals(initial, initial.moveTile(2, 2))
    }

    @Test
    fun appsTileAlwaysRemainsVisibleAndWide() {
        val preferences = LauncherPreferences().setTileEnabled(TileId.Apps, false)

        assertTrue(TileId.Apps in preferences.visibleTiles)
        assertTrue(TileId.Apps in preferences.wideTiles)
        assertEquals(preferences, preferences.toggleTileWidth(TileId.Apps))
    }

    @Test
    fun optionalTilesCanBeHiddenAndResized() {
        val hidden = LauncherPreferences().setTileEnabled(TileId.Calendar, false)
        val compact = hidden.toggleTileWidth(TileId.Tessa)

        assertFalse(TileId.Calendar in hidden.visibleTiles)
        assertFalse(TileId.Tessa in compact.wideTiles)
    }

    @Test
    fun sanitizeRestoresMissingTilesAndRemovesDuplicates() {
        val preferences = LauncherPreferences.sanitize(
            order = listOf(TileId.Music, TileId.Music, TileId.Phone),
            hidden = setOf(TileId.Apps, TileId.Calendar),
            wide = setOf(TileId.Phone),
            theme = MetroTheme.Cobalt,
        )

        assertEquals(TileId.entries.size, preferences.tileOrder.size)
        assertEquals(TileId.entries.toSet(), preferences.tileOrder.toSet())
        assertTrue(TileId.Apps in preferences.visibleTiles)
        assertTrue(TileId.Apps in preferences.wideTiles)
        assertEquals(MetroTheme.Cobalt, preferences.theme)
    }
}
