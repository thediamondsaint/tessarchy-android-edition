package org.tessarchy.tae.ui

import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import org.tessarchy.tae.ui.theme.TessarchyTypeScale
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val columnCount = if (maxWidth >= 640.dp) 4 else 2
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            state = gridState,
            contentPadding = PaddingValues(
                horizontal = TessarchySpacing.PageGutter.dp,
                vertical = 12.dp,
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
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.tessarchy_mark),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(9.dp))
            Text(
                text = stringResource(R.string.tessarchy_name_uppercase),
                color = palette.mint,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.weight(1f))
            if (isArranging) {
                Button(onClick = onFinishArranging, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(stringResource(R.string.edit_done))
                }
            } else {
                IconButton(
                    onClick = onPersonalize,
                    modifier = Modifier
                        .size(48.dp)
                        .semantics { contentDescription = personalizeDescription },
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_personalize),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(palette.mint),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        Text(
            text = androidx.compose.ui.text.AnnotatedString(
                currentTime.format(DateTimeFormatter.ofPattern(timePattern, locale)),
            ),
            color = palette.text,
            fontSize = TessarchyTypeScale.Clock.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = (-2).sp,
            lineHeight = 68.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = dateText,
            color = palette.muted,
            fontSize = TessarchyTypeScale.Date.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.7.sp,
        )

        if (isArranging) {
            Text(
                text = stringResource(R.string.arrange_tiles_hint),
                color = palette.muted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 10.dp),
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
        tile == TileId.Apps -> 76.dp
        tile == TileId.Tessa -> if (isWide) 132.dp else 150.dp
        tile == TileId.Calendar -> 174.dp
        tile == TileId.Tessarchy -> 174.dp
        isWide -> 140.dp
        else -> 142.dp
    }
    val actions = buildList {
        if (orderIndex > 0) {
            add(CustomAccessibilityAction(moveBeforeLabel) {
                onMoveTile(orderIndex, orderIndex - 1)
                true
            })
        }
        if (orderIndex < orderCount - 1) {
            add(CustomAccessibilityAction(moveAfterLabel) {
                onMoveTile(orderIndex, orderIndex + 1)
                true
            })
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .alpha(if (isDragging) 0.78f else 1f)
            .clip(RoundedCornerShape(TessarchySpacing.TileRadius.dp))
            .background(tileColor(tile, palette))
            .clickable(
                enabled = !isArranging,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = description
                if (actions.isNotEmpty()) customActions = actions
            },
    ) {
        when (tile) {
            TileId.Tessarchy -> {
                Image(
                    painter = painterResource(R.drawable.tessarchy_mark),
                    contentDescription = null,
                    modifier = Modifier.size(if (isWide) 82.dp else 72.dp).align(Alignment.Center),
                )
                TileText(
                    stringResource(R.string.tessarchy_name_uppercase),
                    palette.tileText,
                    13.sp,
                    FontWeight.Bold,
                    Modifier.align(Alignment.BottomStart).padding(15.dp),
                    letterSpacing = 3.sp,
                )
            }

            TileId.Calendar -> {
                TileText(
                    stringResource(R.string.calendar_today), palette.tileText, 23.sp, FontWeight.Normal,
                    Modifier.align(Alignment.TopStart).padding(15.dp),
                )
                TileText(
                    currentTime.dayOfMonth.toString(), palette.mint, 37.sp, FontWeight.Light,
                    Modifier.align(Alignment.CenterStart).padding(start = 15.dp, top = 13.dp),
                )
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(15.dp)) {
                    val locale = LocalLocale.current.platformLocale
                    TileText(
                        currentTime.format(DateTimeFormatter.ofPattern("EEE", locale)).uppercase(locale),
                        palette.tileText.copy(alpha = 0.72f), 10.sp, FontWeight.SemiBold,
                        letterSpacing = 1.4.sp,
                    )
                    TileText(stringResource(R.string.calendar_title), palette.tileText, 12.sp, FontWeight.Medium)
                }
            }

            TileId.Phone, TileId.Messages, TileId.Photos, TileId.Music -> {
                val (icon, title) = when (tile) {
                    TileId.Phone -> R.drawable.ic_phone to R.string.phone_title
                    TileId.Messages -> R.drawable.ic_message to R.string.messages_title
                    TileId.Photos -> R.drawable.ic_photos to R.string.photos_title
                    else -> R.drawable.ic_music to R.string.music_title
                }
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(palette.tileText),
                    modifier = Modifier.size(31.dp).align(Alignment.Center).padding(bottom = 7.dp),
                )
                TileText(
                    stringResource(title), palette.tileText, 12.sp, FontWeight.Medium,
                    Modifier.align(Alignment.BottomStart).padding(12.dp),
                )
            }

            TileId.Tessa -> {
                TileText(
                    stringResource(R.string.tessa_name), palette.tileText, 29.sp, FontWeight.Light,
                    Modifier.align(Alignment.CenterStart).padding(start = 20.dp),
                    letterSpacing = 8.sp,
                )
                Image(
                    painter = painterResource(R.drawable.tessarchy_mark),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 25.dp).size(58.dp),
                )
                TileText(
                    stringResource(R.string.tessa_tagline), palette.tileText, 9.sp, FontWeight.SemiBold,
                    Modifier.align(Alignment.BottomStart).padding(start = 21.dp, bottom = 13.dp),
                    letterSpacing = 1.2.sp,
                )
            }

            TileId.Apps -> {
                TileText(
                    stringResource(R.string.apps_title), palette.tileText, 20.sp, FontWeight.Medium,
                    Modifier.align(Alignment.CenterStart).padding(start = 17.dp),
                )
                Image(
                    painter = painterResource(R.drawable.ic_apps),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(palette.tileText),
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 17.dp).size(24.dp),
                )
            }
        }

        if (isArranging) {
            IconButton(
                onClick = {},
                modifier = modifier
                    .align(Alignment.TopEnd)
                    .size(48.dp)
                    .clearAndSetSemantics { },
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_drag_handle),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(palette.tileText),
                    modifier = Modifier.size(21.dp),
                )
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

