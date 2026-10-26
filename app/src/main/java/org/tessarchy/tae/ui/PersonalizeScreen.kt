package org.tessarchy.tae.ui

import android.app.role.RoleManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tessarchy.tae.R
import org.tessarchy.tae.launcher.LauncherPreferences
import org.tessarchy.tae.launcher.MetroTheme
import org.tessarchy.tae.launcher.TileId
import org.tessarchy.tae.ui.theme.MetroPalette

@Composable
fun PersonalizeScreen(
    preferences: LauncherPreferences,
    palette: MetroPalette,
    onBack: () -> Unit,
    onUpdate: ((LauncherPreferences) -> LauncherPreferences) -> Unit,
    onPrivacy: () -> Unit,
    onArrange: () -> Unit,
) {
    val context = LocalContext.current
    val roleManager = remember(context) { context.getSystemService(RoleManager::class.java) }
    var isHomeApp by remember { mutableStateOf(roleManager?.isRoleHeld(RoleManager.ROLE_HOME) == true) }
    val homeRoleRequest = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        isHomeApp = roleManager?.isRoleHeld(RoleManager.ROLE_HOME) == true
    }
    var showResetDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val backDescription = stringResource(R.string.back_to_home)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = backDescription },
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(palette.mint),
                    modifier = Modifier.size(22.dp),
                )
            }
            Text(
                text = stringResource(R.string.personalize_title),
                color = palette.text,
                fontSize = 27.sp,
                fontWeight = FontWeight.Light,
            )
        }
        Text(
            text = stringResource(R.string.personalize_subtitle),
            color = palette.muted,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 22.dp),
        )

        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(palette.surface).padding(16.dp),
        ) {
            Text(
                text = stringResource(if (isHomeApp) R.string.home_app_active_title else R.string.home_app_setup_title),
                color = palette.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(if (isHomeApp) R.string.home_app_active_body else R.string.home_app_setup_body),
                color = palette.muted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 5.dp, bottom = if (isHomeApp) 0.dp else 12.dp),
            )
            if (!isHomeApp && roleManager?.isRoleAvailable(RoleManager.ROLE_HOME) == true) {
                Button(onClick = {
                    homeRoleRequest.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
                }) {
                    Text(stringResource(R.string.set_home_app))
                }
            }
        }

        SectionTitle(stringResource(R.string.appearance_title), palette)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
            MetroTheme.entries.forEach { theme ->
                ThemeOption(
                    theme = theme,
                    selected = preferences.theme == theme,
                    palette = palette,
                    onClick = { onUpdate { it.copy(theme = theme) } },
                )
            }
        }

        SectionTitle(stringResource(R.string.tile_layout_title), palette, Modifier.padding(top = 26.dp))
        Text(
            text = stringResource(R.string.arrange_tiles_hint),
            color = palette.muted,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(start = 3.dp, top = 5.dp, bottom = 10.dp),
        )

        preferences.tileOrder.forEachIndexed { index, tile ->
            TilePreferenceRow(
                tile = tile,
                isVisible = tile !in preferences.hiddenTiles || tile.required,
                isWide = tile in preferences.wideTiles,
                orderIndex = index,
                orderCount = preferences.tileOrder.size,
                palette = palette,
                onEnabled = { enabled -> onUpdate { it.setTileEnabled(tile, enabled) } },
                onWidth = { onUpdate { it.toggleTileWidth(tile) } },
                onMove = { target -> onUpdate { it.moveTile(index, target) } },
            )
        }

        Button(
            onClick = onArrange,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
        ) {
            Text(stringResource(R.string.arrange_tiles), fontWeight = FontWeight.SemiBold)
        }

        TextButton(onClick = { showResetDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(stringResource(R.string.settings_reset_layout), color = palette.muted)
        }
        TextButton(onClick = onPrivacy, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(stringResource(R.string.privacy_and_data), color = palette.mint)
        }
        Text(
            text = stringResource(R.string.launcher_version),
            color = palette.muted.copy(alpha = 0.7f),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp, bottom = 18.dp),
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.settings_reset_layout)) },
            text = { Text(stringResource(R.string.settings_reset_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    onUpdate { LauncherPreferences() }
                    showResetDialog = false
                }) { Text(stringResource(R.string.settings_reset)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.settings_cancel))
                }
            },
        )
    }
}

@Composable
private fun ThemeOption(
    theme: MetroTheme,
    selected: Boolean,
    palette: MetroPalette,
    onClick: () -> Unit,
) {
    val optionPalette = remember(theme) { MetroPalette.forTheme(theme) }
    val label = stringResource(theme.label)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surface)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(7.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = palette.text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.theme_choice, label),
                color = palette.muted,
                fontSize = 10.sp,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(start = 8.dp)) {
            listOf(optionPalette.apps, optionPalette.tessa, optionPalette.music, optionPalette.tessarchy).forEach { color ->
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.size(14.dp).clip(RoundedCornerShape(2.dp)).background(color),
                )
            }
        }
    }
}

@Composable
private fun TilePreferenceRow(
    tile: TileId,
    isVisible: Boolean,
    isWide: Boolean,
    orderIndex: Int,
    orderCount: Int,
    palette: MetroPalette,
    onEnabled: (Boolean) -> Unit,
    onWidth: () -> Unit,
    onMove: (Int) -> Unit,
) {
    val label = stringResource(tile.title)
    val enabledDescription = stringResource(R.string.tile_enabled, label)
    val widthDescription = stringResource(R.string.tile_width, label)
    val earlierDescription = stringResource(R.string.move_tile_before, label)
    val laterDescription = stringResource(R.string.move_tile_after, label)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surface)
            .padding(horizontal = 12.dp, vertical = 9.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = palette.text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(
                    if (tile.required) stringResource(R.string.tile_required)
                    else enabledDescription,
                    color = palette.muted,
                    fontSize = 10.sp,
                )
            }
            if (!tile.required) {
                Switch(
                    checked = isVisible,
                    onCheckedChange = onEnabled,
                    modifier = Modifier.semantics { contentDescription = enabledDescription },
                )
            }
            androidx.compose.material3.IconButton(
                onClick = { onMove(orderIndex - 1) },
                enabled = orderIndex > 0,
                modifier = Modifier
                    .size(44.dp)
                    .semantics { contentDescription = earlierDescription },
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_arrow_up),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(if (orderIndex > 0) palette.text else palette.muted),
                    modifier = Modifier.size(20.dp),
                )
            }
            androidx.compose.material3.IconButton(
                onClick = { onMove(orderIndex + 1) },
                enabled = orderIndex < orderCount - 1,
                modifier = Modifier
                    .size(44.dp)
                    .semantics { contentDescription = laterDescription },
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        if (orderIndex < orderCount - 1) palette.text else palette.muted,
                    ),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.tile_size_label),
                color = palette.muted,
                fontSize = 10.sp,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = isWide,
                onCheckedChange = { onWidth() },
                enabled = tile != TileId.Apps,
                modifier = Modifier.semantics { contentDescription = widthDescription },
            )
            Text(
                stringResource(if (isWide) R.string.wide_tile else R.string.compact_tile),
                color = palette.text,
                fontSize = 11.sp,
                modifier = Modifier.width(62.dp).padding(start = 7.dp),
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, palette: MetroPalette, modifier: Modifier = Modifier) {
    Text(
        text = title,
        color = palette.mint,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        modifier = modifier.padding(start = 3.dp),
    )
}
