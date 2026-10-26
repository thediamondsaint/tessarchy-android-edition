package org.tessarchy.tae.ui

import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tessarchy.tae.R
import org.tessarchy.tae.launcher.LauncherPreferences
import org.tessarchy.tae.launcher.TileId
import org.tessarchy.tae.ui.theme.MetroPalette
import org.tessarchy.tae.ui.theme.TessarchySpacing
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    preferences: LauncherPreferences,
    palette: MetroPalette,
    currentTime: LocalDateTime,
    gridState: LazyGridState,
    isArranging: Boolean,
    onPersonalize: () -> Unit,
    onApps: () -> Unit,
    onTessa: () -> Unit,
    onFinishArranging: () -> Unit,
    onStartArranging: () -> Unit,
    onLaunchIntent: (Intent) -> Unit,
    onMoveTile: (fromIndex: Int, toIndex: Int) -> Unit,
) {
    val visibleTiles = preferences.visibleTiles
    val reorderableState = rememberReorderableLazyGridState(
        lazyGridState = gridState,
        scrollThresholdPadding = androidx.compose.foundation.layout.WindowInsets.systemBars.asPaddingValues(),
    ) { from, to ->
        val fromTile = TileId.parse(from.key.toString()) ?: return@rememberReorderableLazyGridState
        val toTile = TileId.parse(to.key.toString()) ?: return@rememberReorderableLazyGridState
        val fromIndex = preferences.tileOrder.indexOf(fromTile)
        val toIndex = preferences.tileOrder.indexOf(toTile)
        if (fromIndex >= 0 && toIndex >= 0) onMoveTile(fromIndex, toIndex)
    }

    Column(modifier = Modifier.fillMaxSize()) {
      BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
        val columnCount = if (maxWidth >= 640.dp) 4 else 2
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            state = gridState,
            contentPadding = PaddingValues(
                start = TessarchySpacing.PageGutter.dp,
                top = 12.dp,
                end = TessarchySpacing.PageGutter.dp,
                bottom = 14.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(TessarchySpacing.TileGap.dp),
            horizontalArrangement = Arrangement.spacedBy(TessarchySpacing.TileGap.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(key = "home_header", span = { GridItemSpan(maxLineSpan) }) {
                HomeHeader(
                    palette = palette,
                    currentTime = currentTime,
                    isArranging = isArranging,
                    onPersonalize = onPersonalize,
                    onFinishArranging = onFinishArranging,
                )
            }

            item(key = "home_section", span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "YOUR DAY",
                        color = palette.muted.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                    Spacer(Modifier.weight(1f))
                    Box(Modifier.size(5.dp).clip(RoundedCornerShape(50)).background(palette.mint))
                    Spacer(Modifier.width(6.dp))
                    Text("PERSONAL SPACE", color = palette.muted.copy(alpha = 0.72f), fontSize = 8.sp, letterSpacing = 1.sp)
                }
            }

            items(
                items = visibleTiles,
                key = { it.key },
                span = { tile ->
                    val tileSpan = when {
                        tile == TileId.Apps -> maxLineSpan
                        tile in preferences.wideTiles -> minOf(2, maxLineSpan)
                        else -> 1
                    }
                    GridItemSpan(tileSpan)
                },
            ) { tile ->
                val orderIndex = preferences.tileOrder.indexOf(tile)
                ReorderableItem(reorderableState, key = tile.key) { isDragging ->
                    HomeTile(
                        tile = tile,
                        palette = palette,
                        currentTime = currentTime,
                        isWide = tile in preferences.wideTiles,
                        isArranging = isArranging,
                        isDragging = isDragging,
                        orderIndex = orderIndex,
                        orderCount = preferences.tileOrder.size,
                        onMoveTile = onMoveTile,
                        onClick = {
                            if (!isArranging) {
                                when (tile) {
                                    TileId.Tessarchy -> onPersonalize()
                                    TileId.Calendar -> onLaunchIntent(
                                        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR),
                                    )
                                    TileId.Phone -> onLaunchIntent(Intent(Intent.ACTION_DIAL))
                                    TileId.Messages -> onLaunchIntent(
                                        Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", "", null)),
                                    )
                                    TileId.Tessa -> onTessa()
                                    TileId.Photos -> onLaunchIntent(
                                        Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" },
                                    )
                                    TileId.Music -> onLaunchIntent(
                                        Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC),
                                    )
                                    TileId.Apps -> onApps()
                                }
                            }
                        },
                        modifier = Modifier.draggableHandle(enabled = isArranging),
                    )
                }
            }
        }

      }
      WindowTape(
            palette = palette,
            onApps = onApps,
            onTessa = onTessa,
            onPersonalize = onPersonalize,
        )
    }
}

@Composable
private fun HomeHeader(
    palette: MetroPalette,
    currentTime: LocalDateTime,
    isArranging: Boolean,
    onPersonalize: () -> Unit,
    onFinishArranging: () -> Unit,
) {
    val context = LocalContext.current
    val locale = LocalLocale.current.platformLocale
    val timePattern = if (DateFormat.is24HourFormat(context)) "H:mm" else "h:mm"
    val personalizeDescription = stringResource(R.string.personalize)
    val dateText = remember(currentTime.toLocalDate(), locale) {
        currentTime.format(DateTimeFormatter.ofPattern("EEEE, MMM d", locale)).uppercase(locale)
    }
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.mint.copy(alpha = 0.13f))
                    .padding(5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.tessarchy_mark),
                    contentDescription = null,
                    modifier = Modifier.size(21.dp),
                )
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(R.string.tessarchy_name_uppercase),
                    color = palette.text,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                )
                Text("A SPACE THAT’S YOURS", color = palette.muted, fontSize = 7.sp, letterSpacing = 1.1.sp)
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currentTime.format(DateTimeFormatter.ofPattern(timePattern, locale)),
                    color = palette.text,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-1.5).sp,
                    lineHeight = 38.sp,
                )
                Text(dateText, color = palette.muted, fontSize = 7.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.75.sp)
            }
            if (isArranging) {
                Button(
                    onClick = onFinishArranging,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text(stringResource(R.string.edit_done), fontSize = 10.sp)
                }
            } else {
                IconButton(
                    onClick = onPersonalize,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 5.dp)
                        .semantics { contentDescription = personalizeDescription },
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_personalize),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(palette.mint),
                        modifier = Modifier.size(19.dp),
                    )
                }
            }
        }

        if (isArranging) {
            Text(
                text = stringResource(R.string.arrange_tiles_hint),
                color = palette.muted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun HomeTile(
    tile: TileId,
    palette: MetroPalette,
    currentTime: LocalDateTime,
    isWide: Boolean,
    isArranging: Boolean,
    isDragging: Boolean,
    orderIndex: Int,
    orderCount: Int,
    onMoveTile: (fromIndex: Int, toIndex: Int) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val description = tileDescription(tile)
    val title = stringResource(tile.title)
    val moveBeforeLabel = stringResource(R.string.move_tile_before, title)
    val moveAfterLabel = stringResource(R.string.move_tile_after, title)
    val height = when {
        tile == TileId.Apps -> 88.dp
        tile == TileId.Tessa -> if (isWide) 58.dp else 90.dp
        tile == TileId.Calendar -> 156.dp
        tile == TileId.Tessarchy -> 156.dp
        isWide -> 142.dp
        else -> 150.dp
    }
    val shape = RoundedCornerShape(19.dp)
    val actions = buildList {
        if (orderIndex > 0) add(CustomAccessibilityAction(moveBeforeLabel) {
            onMoveTile(orderIndex, orderIndex - 1)
            true
        })
        if (orderIndex < orderCount - 1) add(CustomAccessibilityAction(moveAfterLabel) {
            onMoveTile(orderIndex, orderIndex + 1)
            true
        })
    }
    val backgroundImage = when (tile) {
        TileId.Tessarchy -> R.drawable.tile_waterfall
        TileId.Calendar -> R.drawable.tile_mountains
        TileId.Phone -> R.drawable.tile_city
        TileId.Messages -> R.drawable.tile_clouds
        TileId.Tessa -> R.drawable.tile_clouds
        TileId.Photos -> R.drawable.tile_waterfall
        TileId.Music -> R.drawable.tile_clouds
        TileId.Apps -> null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .alpha(if (isDragging) 0.78f else 1f)
            .clip(shape)
            .background(tileColor(tile, palette))
            .border(1.dp, Color.White.copy(alpha = 0.25f), shape)
            .clickable(enabled = !isArranging, role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) {
                contentDescription = description
                if (actions.isNotEmpty()) customActions = actions
            },
    ) {
        if (backgroundImage != null) {
            Image(
                painter = painterResource(backgroundImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF09111D).copy(alpha = 0.20f),
                            Color(0xFF09111D).copy(alpha = 0.32f),
                            Color(0xFF09111D).copy(alpha = 0.78f),
                        ),
                    ),
                ),
            )
        } else {
            Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(palette.apps, palette.backgroundTop))))
        }

        Box(
            Modifier.align(Alignment.TopEnd).padding(12.dp).size(46.dp)
                .clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.07f)),
        )
        when (tile) {
            TileId.Tessarchy -> {
                Image(
                    painter = painterResource(R.drawable.tessarchy_mark),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).size(if (isWide) 58.dp else 48.dp),
                )
                Column(Modifier.align(Alignment.BottomStart).padding(start = 15.dp, bottom = 14.dp)) {
                    TileText(stringResource(R.string.tessarchy_name_uppercase), Color.White, 11.sp, FontWeight.Bold, letterSpacing = 2.1.sp)
                    TileText("YOUR PHONE, YOUR SHAPE", Color.White.copy(alpha = 0.76f), 7.sp, FontWeight.Medium, letterSpacing = 1.1.sp)
                }
            }
            TileId.Calendar -> {
                TileText("TODAY", Color.White.copy(alpha = 0.83f), 9.sp, FontWeight.Bold,
                    Modifier.align(Alignment.TopStart).padding(start = 14.dp, top = 14.dp), letterSpacing = 1.5.sp)
                TileText(currentTime.dayOfMonth.toString(), Color.White, 39.sp, FontWeight.Light,
                    Modifier.align(Alignment.CenterStart).padding(start = 14.dp, top = 7.dp))
                Column(Modifier.align(Alignment.BottomStart).padding(start = 14.dp, bottom = 13.dp)) {
                    val locale = LocalLocale.current.platformLocale
                    TileText(currentTime.format(DateTimeFormatter.ofPattern("EEE · MMM", locale)).uppercase(locale), Color.White.copy(alpha = 0.77f), 8.sp, FontWeight.SemiBold, letterSpacing = 1.sp)
                    TileText(stringResource(R.string.calendar_title), Color.White, 12.sp, FontWeight.SemiBold)
                }
            }
            TileId.Phone -> {
                TileText("QUICK DIAL", Color.White.copy(alpha = 0.82f), 8.sp, FontWeight.Bold,
                    Modifier.align(Alignment.TopStart).padding(start = 13.dp, top = 13.dp), letterSpacing = 1.2.sp)
                Image(painterResource(R.drawable.ic_phone), null,
                    colorFilter = ColorFilter.tint(Color.White), modifier = Modifier.align(Alignment.Center).size(28.dp))
                TileText("Phone", Color.White, 12.sp, FontWeight.SemiBold,
                    Modifier.align(Alignment.BottomStart).padding(start = 13.dp, bottom = 12.dp))
            }
            TileId.Messages -> {
                TileText("MESSAGES", Color.White.copy(alpha = 0.82f), 8.sp, FontWeight.Bold,
                    Modifier.align(Alignment.TopStart).padding(start = 13.dp, top = 13.dp), letterSpacing = 1.2.sp)
                Image(painterResource(R.drawable.ic_message), null,
                    colorFilter = ColorFilter.tint(Color.White), modifier = Modifier.align(Alignment.Center).size(28.dp))
                TileText("Open inbox", Color.White, 12.sp, FontWeight.SemiBold,
                    Modifier.align(Alignment.BottomStart).padding(start = 13.dp, bottom = 12.dp))
            }
            TileId.Tessa -> {
                Box(
                    Modifier.align(Alignment.CenterEnd).padding(end = 16.dp).size(44.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Brush.radialGradient(listOf(Color(0xFF99F5E1), Color(0xFF43B8C0), Color(0x55453897)))),
                )
                Image(painterResource(R.drawable.tessarchy_mark), null,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 27.dp).size(22.dp))
                Column(Modifier.align(Alignment.CenterStart).padding(start = 17.dp)) {
                    TileText("TESSA", Color.White, 20.sp, FontWeight.Light, letterSpacing = 3.sp)
                    TileText("ASSISTANT PREVIEW", Color.White.copy(alpha = 0.82f), 7.sp, FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
            TileId.Photos -> {
                TileText("YOUR MOMENTS", Color.White.copy(alpha = 0.82f), 8.sp, FontWeight.Bold,
                    Modifier.align(Alignment.TopStart).padding(start = 13.dp, top = 13.dp), letterSpacing = 1.2.sp)
                Image(painterResource(R.drawable.ic_photos), null,
                    colorFilter = ColorFilter.tint(Color.White), modifier = Modifier.align(Alignment.Center).size(27.dp))
                TileText("Photos", Color.White, 12.sp, FontWeight.SemiBold,
                    Modifier.align(Alignment.BottomStart).padding(start = 13.dp, bottom = 12.dp))
            }
            TileId.Music -> {
                TileText("SOUNDTRACK", Color.White.copy(alpha = 0.82f), 8.sp, FontWeight.Bold,
                    Modifier.align(Alignment.TopStart).padding(start = 13.dp, top = 13.dp), letterSpacing = 1.2.sp)
                Image(painterResource(R.drawable.ic_music), null,
                    colorFilter = ColorFilter.tint(Color.White), modifier = Modifier.align(Alignment.Center).size(27.dp))
                TileText("Music", Color.White, 12.sp, FontWeight.SemiBold,
                    Modifier.align(Alignment.BottomStart).padding(start = 13.dp, bottom = 12.dp))
            }
            TileId.Apps -> {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        TileText("APP LIBRARY", Color.White.copy(alpha = 0.74f), 8.sp, FontWeight.Bold, letterSpacing = 1.2.sp)
                        TileText("All apps", Color.White, 18.sp, FontWeight.SemiBold)
                    }
                    Image(painterResource(R.drawable.ic_apps), null,
                        colorFilter = ColorFilter.tint(Color.White), modifier = Modifier.size(23.dp))
                }
            }
        }

        if (isArranging) {
            IconButton(
                onClick = {},
                modifier = modifier.align(Alignment.TopEnd).size(48.dp).clearAndSetSemantics { },
            ) {
                Image(painterResource(R.drawable.ic_drag_handle), null,
                    colorFilter = ColorFilter.tint(Color.White), modifier = Modifier.size(21.dp))
            }
        }
    }
}

private enum class TapeStop(val label: String) {
    Home("Home"), Apps("Apps"), Tessa("Tessa"), Studio("Studio"),
}

@Composable
private fun WindowTape(
    palette: MetroPalette,
    onApps: () -> Unit,
    onTessa: () -> Unit,
    onPersonalize: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stops = remember { TapeStop.entries }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var dragDistance by remember { mutableFloatStateOf(0f) }
    val listState = rememberLazyListState()
    val markerFraction by animateFloatAsState(
        targetValue = selectedIndex.toFloat() / (stops.lastIndex.coerceAtLeast(1)),
        label = "window tape position",
    )
    LaunchedEffect(selectedIndex) { listState.animateScrollToItem(selectedIndex) }

    fun openStop(index: Int) {
        when (stops[index]) {
            TapeStop.Home -> Unit
            TapeStop.Apps -> onApps()
            TapeStop.Tessa -> onTessa()
            TapeStop.Studio -> onPersonalize()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, palette.backgroundBottom.copy(alpha = 0.96f), palette.backgroundBottom)))
            .padding(top = 36.dp, bottom = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 19.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("WINDOW TAPE", color = palette.muted.copy(alpha = 0.9f), fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(Modifier.weight(1f))
            Text("SWIPE TO MOVE", color = palette.muted.copy(alpha = 0.62f), fontSize = 7.sp, letterSpacing = 1.sp)
        }
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(stops) { index, stop ->
                val isSelected = selectedIndex == index
                Column(
                    modifier = Modifier.width(76.dp).clickable {
                        selectedIndex = index
                        openStop(index)
                    }.semantics(mergeDescendants = true) { role = Role.Tab; selected = isSelected },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TapePreview(stop, palette, isSelected)
                    Text(
                        stop.label.uppercase(), color = if (isSelected) palette.text else palette.muted,
                        fontSize = 7.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.8.sp, modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
        Canvas(
            modifier = Modifier.fillMaxWidth().height(36.dp).padding(horizontal = 20.dp)
                .pointerInput(stops.size) {
                    detectHorizontalDragGestures(
                        onDragEnd = { dragDistance = 0f; openStop(selectedIndex) },
                        onHorizontalDrag = { _, amount ->
                            dragDistance += amount
                            if (dragDistance < -22.dp.toPx()) {
                                selectedIndex = (selectedIndex + 1).coerceAtMost(stops.lastIndex)
                                dragDistance = 0f
                            } else if (dragDistance > 22.dp.toPx()) {
                                selectedIndex = (selectedIndex - 1).coerceAtLeast(0)
                                dragDistance = 0f
                            }
                        },
                    )
                },
        ) {
            val y = size.height / 2
            val start = 0f
            val end = size.width
            drawLine(Color.White.copy(alpha = 0.22f), Offset(start, y), Offset(end, y), strokeWidth = 2.dp.toPx())
            drawLine(palette.mint.copy(alpha = 0.9f), Offset(start, y), Offset(start + (end - start) * markerFraction, y), strokeWidth = 2.dp.toPx())
            drawCircle(palette.mint.copy(alpha = 0.22f), radius = 7.dp.toPx(), center = Offset(start + (end - start) * markerFraction, y))
            drawCircle(palette.mint, radius = 3.dp.toPx(), center = Offset(start + (end - start) * markerFraction, y))
        }
    }
}

@Composable
private fun TapePreview(stop: TapeStop, palette: MetroPalette, selected: Boolean) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        Modifier.width(76.dp).height(44.dp).clip(shape)
            .background(palette.surface.copy(alpha = if (selected) 0.98f else 0.78f))
            .border(1.dp, if (selected) palette.mint.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.13f), shape)
            .padding(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (stop) {
            TapeStop.Home -> Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                listOf(palette.tessarchy, palette.calendar, palette.phone, palette.messages).forEach { color ->
                    Box(Modifier.weight(1f).height(27.dp).clip(RoundedCornerShape(3.dp)).background(color.copy(alpha = 0.85f)))
                }
            }
            TapeStop.Apps -> Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(3) { index ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).clip(RoundedCornerShape(3.dp)).background(listOf(palette.mint, palette.calendar, palette.tessa)[index]))
                        Spacer(Modifier.width(4.dp))
                        Box(Modifier.fillMaxWidth(0.72f).height(3.dp).clip(RoundedCornerShape(4.dp)).background(Color.White.copy(alpha = 0.55f)))
                    }
                }
            }
            TapeStop.Tessa -> Box(Modifier.size(26.dp).clip(RoundedCornerShape(50)).background(Brush.radialGradient(listOf(palette.mint, palette.calendar))))
            TapeStop.Studio -> Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(palette.tessarchy, palette.calendar, palette.tessa).forEach { color ->
                    Box(Modifier.weight(1f).height(22.dp).clip(RoundedCornerShape(4.dp)).background(color))
                }
            }
        }
    }
}

@Composable
private fun TileText(
    text: String,
    color: Color,
    size: androidx.compose.ui.unit.TextUnit,
    weight: FontWeight,
    modifier: Modifier = Modifier,
    letterSpacing: androidx.compose.ui.unit.TextUnit = 0.sp,
) {
    Text(
        text = text,
        color = color,
        fontSize = size,
        fontWeight = weight,
        letterSpacing = letterSpacing,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
private fun tileColor(tile: TileId, palette: MetroPalette): Color = when (tile) {
    TileId.Tessarchy -> palette.tessarchy
    TileId.Calendar -> palette.calendar
    TileId.Phone -> palette.phone
    TileId.Messages -> palette.messages
    TileId.Tessa -> palette.tessa
    TileId.Photos -> palette.photos
    TileId.Music -> palette.music
    TileId.Apps -> palette.apps
}

@Composable
private fun tileDescription(tile: TileId): String = when (tile) {
    TileId.Tessarchy -> stringResource(R.string.tile_tessarchy_description)
    TileId.Calendar -> stringResource(R.string.tile_calendar_description)
    TileId.Phone -> stringResource(R.string.tile_phone_description)
    TileId.Messages -> stringResource(R.string.tile_messages_description)
    TileId.Tessa -> stringResource(R.string.tile_tessa_description)
    TileId.Photos -> stringResource(R.string.tile_photos_description)
    TileId.Music -> stringResource(R.string.tile_music_description)
    TileId.Apps -> stringResource(R.string.tile_apps_description)
}

