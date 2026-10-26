package org.tessarchy.tae

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import androidx.core.graphics.createBitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Night = Color(0xFF101C2B)
private val DeepNavy = Color(0xFF0B1724)
private val TileBlue = Color(0xFF0078D4)
private val Mint = Color(0xFF8CE1C9)
private val White = Color(0xFFF2FBFF)
private val Muted = Color(0xFFA6BFCA)
private val TessGreen = Color(0xFF008272)
private val CalendarViolet = Color(0xFF6B69D6)
private val PhoneRed = Color(0xFFD13438)
private val TessaOrange = Color(0xFFCA5010)
private val PhotosPurple = Color(0xFF5C2D91)
private val MusicMagenta = Color(0xFFC239B3)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Night.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(DeepNavy.toArgb()),
        )
        setContent {
            var installedApps by remember { mutableStateOf(emptyList<LaunchableApp>()) }
            var isLoadingApps by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                try {
                    installedApps = withContext(Dispatchers.IO) { loadLaunchableApps() }
                } finally {
                    isLoadingApps = false
                }
            }
            TessarchyApp(
                installedApps = installedApps,
                isLoadingApps = isLoadingApps,
                onLaunchIntent = ::launchIntent,
                onLaunchPackage = ::launchPackage,
            )
        }
    }

    private fun loadLaunchableApps(): List<LaunchableApp> {
        val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(query, 0)
            .map { result ->
                LaunchableApp(
                    label = result.loadLabel(packageManager).toString(),
                    packageName = result.activityInfo.packageName,
                    icon = runCatching { result.loadIcon(packageManager).toImageBitmap() }.getOrNull(),
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    private fun launchPackage(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
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

private fun android.graphics.drawable.Drawable.toImageBitmap(): ImageBitmap {
    val width = intrinsicWidth.takeIf { it > 0 } ?: 96
    val height = intrinsicHeight.takeIf { it > 0 } ?: 96
    val bitmap = createBitmap(width, height)
    setBounds(0, 0, width, height)
    draw(Canvas(bitmap))
    return bitmap.asImageBitmap()
}

private data class LaunchableApp(
    val label: String,
    val packageName: String,
    val icon: ImageBitmap?,
)

@Composable
private fun TessarchyApp(
    installedApps: List<LaunchableApp>,
    isLoadingApps: Boolean,
    onLaunchIntent: (Intent) -> Unit,
    onLaunchPackage: (String) -> Unit,
) {
    val context = LocalContext.current
    var showApps by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    BackHandler(enabled = showApps) { showApps = false }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            currentTime = LocalDateTime.now()
        }
    }

    val background = Brush.verticalGradient(
        colors = listOf(Night, Color(0xFF11263A), DeepNavy),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        if (showApps) {
            AppDrawer(
                apps = installedApps,
                isLoading = isLoadingApps,
                onBack = { showApps = false },
                onLaunch = onLaunchPackage,
            )
        } else {
            HomeScreen(
                currentTime = currentTime,
                onOpenApps = { showApps = true },
                onLaunchIntent = onLaunchIntent,
                onTessa = {
                    Toast.makeText(context, R.string.toast_tessa, Toast.LENGTH_SHORT).show()
                },
            )
        }
    }
}

@Composable
private fun HomeScreen(
    currentTime: LocalDateTime,
    onOpenApps: () -> Unit,
    onLaunchIntent: (Intent) -> Unit,
    onTessa: () -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val timeText = currentTime.format(DateTimeFormatter.ofPattern("H:mm", locale))
    val dateText = currentTime.format(DateTimeFormatter.ofPattern("EEEE, MMM d", locale))
        .uppercase(locale)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 12.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.tessarchy_mark),
                contentDescription = stringResource(R.string.tessarchy_mark_description),
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(9.dp))
            TextLabel(stringResource(R.string.tessarchy_name_uppercase), 13.sp, Mint, FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.weight(1f))
            TextLabel(stringResource(R.string.home_mobile), 10.sp, Muted, FontWeight.SemiBold, letterSpacing = 1.8.sp)
        }

        Column(modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)) {
            TextLabel(timeText, 62.sp, White, FontWeight.Light, letterSpacing = (-2).sp)
            TextLabel(dateText, 11.sp, Muted, FontWeight.SemiBold, letterSpacing = 1.7.sp)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Tile(
                modifier = Modifier.weight(1f).height(184.dp),
                background = TessGreen,
                contentDescription = stringResource(R.string.tile_tessarchy_description),
                onClick = onTessa,
            ) {
                Image(
                    painter = painterResource(R.drawable.tessarchy_mark),
                    contentDescription = null,
                    modifier = Modifier.size(83.dp).align(Alignment.Center),
                )
                TextLabel(
                    stringResource(R.string.tessarchy_name_uppercase),
                    13.sp,
                    White,
                    FontWeight.Bold,
                    letterSpacing = 3.sp,
                    modifier = Modifier.align(Alignment.BottomStart).padding(15.dp),
                )
            }

            Tile(
                modifier = Modifier.weight(1f).height(184.dp),
                background = CalendarViolet,
                contentDescription = stringResource(R.string.tile_calendar_description),
                onClick = { onLaunchIntent(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR)) },
            ) {
                TextLabel(stringResource(R.string.calendar_today), 25.sp, White, FontWeight.Normal, modifier = Modifier.padding(15.dp))
                TextLabel(
                    currentTime.dayOfMonth.toString(),
                    36.sp,
                    Mint,
                    FontWeight.Light,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 15.dp, top = 17.dp),
                )
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(15.dp)) {
                    TextLabel(dateText.take(3), 10.sp, Muted, FontWeight.SemiBold, letterSpacing = 1.4.sp)
                    TextLabel(stringResource(R.string.calendar_title), 12.sp, White, FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SmallTile(
                title = stringResource(R.string.phone_title),
                symbol = "☎",
                background = PhoneRed,
                tint = White,
                modifier = Modifier.weight(1f).height(122.dp),
                onClick = { onLaunchIntent(Intent(Intent.ACTION_DIAL)) },
            )
            SmallTile(
                title = stringResource(R.string.messages_title),
                symbol = "···",
                background = TileBlue,
                tint = White,
                modifier = Modifier.weight(1f).height(122.dp),
                onClick = { onLaunchIntent(Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", "", null))) },
            )
        }

        Tile(
            modifier = Modifier.fillMaxWidth().height(124.dp),
            background = TessaOrange,
            contentDescription = stringResource(R.string.tile_tessa_description),
            onClick = onTessa,
        ) {
            TextLabel(stringResource(R.string.tessa_name), 29.sp, White, FontWeight.Light, letterSpacing = 8.sp, modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp))
            Image(
                painterResource(R.drawable.tessarchy_mark),
                null,
                Modifier.align(Alignment.CenterEnd).padding(end = 30.dp).size(58.dp),
            )
            TextLabel(stringResource(R.string.tessa_tagline), 9.sp, White, FontWeight.SemiBold, letterSpacing = 1.4.sp, modifier = Modifier.align(Alignment.BottomStart).padding(start = 21.dp, bottom = 14.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SmallTile(
                title = stringResource(R.string.photos_title),
                symbol = "▧",
                background = PhotosPurple,
                tint = White,
                modifier = Modifier.weight(1f).height(120.dp),
                onClick = {
                    onLaunchIntent(Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" })
                },
            )
            SmallTile(
                title = stringResource(R.string.music_title),
                symbol = "▶",
                background = MusicMagenta,
                tint = White,
                modifier = Modifier.weight(1f).height(120.dp),
                onClick = { onLaunchIntent(Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC)) },
            )
        }

        Tile(
            modifier = Modifier.fillMaxWidth().height(76.dp),
            background = Color(0xFF0063B1),
            contentDescription = stringResource(R.string.open_all_apps_description),
            onClick = onOpenApps,
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 17.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextLabel(stringResource(R.string.apps_title), 20.sp, White, FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                TileGlyph("▦", White, 22.sp)
            }
        }
    }
}

@Composable
private fun SmallTile(
    title: String,
    symbol: String,
    background: Color,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Tile(
        modifier = modifier,
        background = background,
        contentDescription = title,
        onClick = onClick,
    ) {
        TileGlyph(symbol, tint, 30.sp, Modifier.align(Alignment.Center).padding(bottom = 9.dp))
        TextLabel(title, 12.sp, White, FontWeight.Medium, modifier = Modifier.align(Alignment.BottomStart).padding(12.dp))
    }
}

@Composable
private fun Tile(
    modifier: Modifier,
    background: Color,
    contentDescription: String,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) {
                this.contentDescription = contentDescription
                role = Role.Button
            },
        content = content,
    )
}

@Composable
private fun AppDrawer(
    apps: List<LaunchableApp>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onLaunch: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(apps, query) {
        apps.filter { it.label.contains(query.trim(), ignoreCase = true) }
    }
    val backDescription = stringResource(R.string.apps_back)

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextLabel(
                "‹",
                36.sp,
                Mint,
                FontWeight.Light,
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .semantics {
                        role = Role.Button
                        contentDescription = backDescription
                    }
                    .padding(end = 12.dp),
            )
            TextLabel(stringResource(R.string.apps_drawer_title), 27.sp, White, FontWeight.Light)
        }

        BasicTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = White, fontSize = 16.sp),
            cursorBrush = SolidColor(Mint),
            decorationBox = { field ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color(0xFF1A3044))
                        .padding(horizontal = 15.dp, vertical = 14.dp),
                ) {
                    if (query.isEmpty()) TextLabel(stringResource(R.string.apps_search_hint), 15.sp, Muted, FontWeight.Normal)
                    field()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(14.dp))
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                val message = when {
                    isLoading -> stringResource(R.string.apps_loading)
                    query.isBlank() -> stringResource(R.string.apps_empty)
                    else -> stringResource(R.string.apps_no_matches, query.trim())
                }
                TextLabel(message, 15.sp, Muted, FontWeight.Normal)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(filtered, key = { it.packageName }) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onLaunch(app.packageName) }
                            .semantics(mergeDescendants = true) { role = Role.Button }
                            .padding(horizontal = 10.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (app.icon != null) {
                            Image(
                                bitmap = app.icon,
                                contentDescription = null,
                                modifier = Modifier.size(38.dp),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(TileBlue),
                                contentAlignment = Alignment.Center,
                            ) {
                                TextLabel(app.label.take(1).uppercase(), 17.sp, White, FontWeight.SemiBold)
                            }
                        }
                        Spacer(Modifier.width(13.dp))
                        TextLabel(app.label, 15.sp, White, FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun TileGlyph(symbol: String, tint: Color, size: androidx.compose.ui.unit.TextUnit, modifier: Modifier = Modifier) {
    TextLabel(symbol, size, tint, FontWeight.Light, modifier = modifier)
}

@Composable
private fun TextLabel(
    text: String,
    size: androidx.compose.ui.unit.TextUnit,
    color: Color,
    weight: FontWeight,
    modifier: Modifier = Modifier,
    letterSpacing: androidx.compose.ui.unit.TextUnit = 0.sp,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    androidx.compose.material3.Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size,
        fontWeight = weight,
        letterSpacing = letterSpacing,
        maxLines = maxLines,
        overflow = overflow,
    )
}
