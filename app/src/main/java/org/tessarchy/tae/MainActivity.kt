package org.tessarchy.tae

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.createBitmap
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tessarchy.tae.launcher.LaunchableApp
import org.tessarchy.tae.ui.TessarchyLauncher
import org.tessarchy.tae.ui.theme.MetroPalette
import java.text.Collator
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val installedApps = MutableStateFlow<List<LaunchableApp>>(emptyList())
    private val appsLoading = MutableStateFlow(true)
    private val installedAppsFlow = installedApps.asStateFlow()
    private val appsLoadingFlow = appsLoading.asStateFlow()
    private var refreshJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val defaultPalette = MetroPalette.forTheme(org.tessarchy.tae.launcher.MetroTheme.Midnight)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(defaultPalette.backgroundTop.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(defaultPalette.backgroundBottom.toArgb()),
        )
        setContent {
            TessarchyLauncher(
                installedApps = installedAppsFlow,
                appsLoading = appsLoadingFlow,
                onLaunchApp = ::launchApp,
                onLaunchIntent = ::launchIntent,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            appsLoading.value = true
            try {
                installedApps.value = withContext(Dispatchers.IO) { loadLaunchableApps() }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                installedApps.value = emptyList()
                Toast.makeText(this@MainActivity, R.string.apps_load_failed, Toast.LENGTH_SHORT).show()
            } finally {
                appsLoading.value = false
            }
        }
    }

    private fun loadLaunchableApps(): List<LaunchableApp> {
        val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val results = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                query,
                PackageManager.ResolveInfoFlags.of(0L),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(query, 0)
        }

        val locale = resources.configuration.locales[0] ?: Locale.getDefault()
        val collator = Collator.getInstance(locale)
        return results
            .mapNotNull { result ->
                val activity = result.activityInfo ?: return@mapNotNull null
                val componentName = ComponentName(activity.packageName, activity.name)
                LaunchableApp(
                    componentName = componentName,
                    label = result.loadLabel(packageManager).toString(),
                    icon = runCatching { result.loadIcon(packageManager).toImageBitmap() }.getOrNull(),
                )
            }
            .distinctBy(LaunchableApp::id)
            .sortedWith(compareBy(collator) { it.label })
    }

    private fun launchApp(componentName: ComponentName) {
        val intent = packageManager.getLaunchIntentForPackage(componentName.packageName)
            ?.apply { component = componentName }
        if (intent == null) {
            Toast.makeText(this, R.string.toast_open_failed, Toast.LENGTH_SHORT).show()
            return
        }
        runCatching { startActivity(intent) }
            .onFailure { Toast.makeText(this, R.string.toast_open_failed, Toast.LENGTH_SHORT).show() }
    }

    private fun launchIntent(intent: Intent) {
        runCatching { startActivity(intent) }
            .onFailure { Toast.makeText(this, R.string.toast_no_app, Toast.LENGTH_SHORT).show() }
    }
}

private fun Drawable.toImageBitmap(): ImageBitmap {
    val width = intrinsicWidth.takeIf { it > 0 }?.coerceAtMost(MAX_APP_ICON_SIZE) ?: MAX_APP_ICON_SIZE
    val height = intrinsicHeight.takeIf { it > 0 }?.coerceAtMost(MAX_APP_ICON_SIZE) ?: MAX_APP_ICON_SIZE
    val bitmap = createBitmap(width, height)
    setBounds(0, 0, width, height)
    draw(Canvas(bitmap))
    return bitmap.asImageBitmap()
}

private const val MAX_APP_ICON_SIZE = 96
