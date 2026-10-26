package org.tessarchy.tae.launcher

import android.content.ComponentName
import androidx.compose.ui.graphics.ImageBitmap

data class LaunchableApp(
    val componentName: ComponentName,
    val label: String,
    val icon: ImageBitmap?,
) {
    val id: String = componentName.flattenToString()
}
