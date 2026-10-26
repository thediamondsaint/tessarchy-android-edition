package org.tessarchy.tae.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tessarchy.tae.data.LauncherPreferencesRepository
import org.tessarchy.tae.launcher.LaunchableApp
import org.tessarchy.tae.launcher.LauncherPreferences
import org.tessarchy.tae.ui.theme.MetroPalette
import java.time.LocalDateTime

private enum class Destination { Home, Apps, Personalize, Tessa }

@Composable
fun TessarchyLauncher(
    installedApps: StateFlow<List<LaunchableApp>>,
    appsLoading: StateFlow<Boolean>,
    onLaunchApp: (ComponentName) -> Unit,
    onLaunchIntent: (Intent) -> Unit,
) {
    val context = LocalContext.current
    val repository = remember(context) { LauncherPreferencesRepository.from(context) }
    val appList by installedApps.collectAsState()
    val isLoading by appsLoading.collectAsState()
    val scope = rememberCoroutineScope()
    val homeGridState = rememberLazyGridState()
    var preferences by remember { mutableStateOf(LauncherPreferences()) }
    var destination by remember { mutableStateOf(Destination.Home) }
    var isArranging by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(repository) {
        repository.preferences.collect { preferences = it }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            val millisToNextMinute = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(millisToNextMinute)
        }
    }

    val palette = remember(preferences.theme) { MetroPalette.forTheme(preferences.theme) }
    val materialColors = remember(palette) {
        if (palette.lightSystemBars) {
            lightColorScheme(
                primary = palette.mint,
                secondary = palette.calendar,
                background = palette.backgroundBottom,
                surface = palette.surface,
                onBackground = palette.text,
                onSurface = palette.text,
            )
        } else {
            darkColorScheme(
                primary = palette.mint,
                secondary = palette.calendar,
                background = palette.backgroundBottom,
                surface = palette.surface,
                onBackground = palette.text,
                onSurface = palette.text,
            )
        }
    }
    SideEffect {
        val activity = context as? Activity
        val window = activity?.window ?: return@SideEffect
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = palette.lightSystemBars
            isAppearanceLightNavigationBars = palette.lightSystemBars
        }
    }

    BackHandler(enabled = destination != Destination.Home || isArranging) {
        when {
            isArranging -> isArranging = false
            else -> destination = Destination.Home
        }
    }

    fun updatePreferences(transform: (LauncherPreferences) -> LauncherPreferences) {
        val updated = transform(preferences)
        preferences = updated
        scope.launch { repository.save(updated) }
    }

    MaterialTheme(colorScheme = materialColors) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.background)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            when (destination) {
            Destination.Home -> HomeScreen(
                preferences = preferences,
                palette = palette,
                currentTime = currentTime,
                gridState = homeGridState,
                isArranging = isArranging,
                onPersonalize = { destination = Destination.Personalize },
                onApps = { destination = Destination.Apps },
                onTessa = { destination = Destination.Tessa },
                onFinishArranging = { isArranging = false },
                onStartArranging = { isArranging = true },
                onLaunchIntent = onLaunchIntent,
                onMoveTile = { from, to ->
                    updatePreferences { it.moveTile(from, to) }
                },
            )

            Destination.Apps -> AppDrawer(
                apps = appList,
                isLoading = isLoading,
                palette = palette,
                onBack = { destination = Destination.Home },
                onLaunch = onLaunchApp,
            )

            Destination.Personalize -> PersonalizeScreen(
                preferences = preferences,
                palette = palette,
                onBack = { destination = Destination.Home },
                onUpdate = ::updatePreferences,
                onArrange = {
                    isArranging = true
                    destination = Destination.Home
                },
            )

            Destination.Tessa -> TessaPreviewScreen(
                palette = palette,
                onBack = { destination = Destination.Home },
                onPersonalize = { destination = Destination.Personalize },
                onApps = { destination = Destination.Apps },
            )
            }
        }
    }
}
