package org.tessarchy.tae.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.tessarchy.tae.R
import org.tessarchy.tae.launcher.LaunchableApp
import org.tessarchy.tae.ui.theme.MetroPalette

@Composable
fun AppDrawer(
    apps: List<LaunchableApp>,
    isLoading: Boolean,
    hasInventoryConsent: Boolean,
    palette: MetroPalette,
    onBack: () -> Unit,
    onLaunch: (android.content.ComponentName) -> Unit,
    onRequestInventoryConsent: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredApps = remember(apps, query) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) apps
        else apps.filter { it.label.contains(normalizedQuery, ignoreCase = true) }
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val backDescription = stringResource(R.string.apps_back)
    val searchDescription = stringResource(R.string.apps_search_action)

    Column(
        modifier = Modifier.fillMaxSize().imePadding().padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 9.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp).semantics { contentDescription = backDescription },
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(palette.mint),
                    modifier = Modifier.size(22.dp),
                )
            }
            androidx.compose.material3.Text(
                text = stringResource(R.string.apps_drawer_title),
                color = palette.text,
                fontSize = 27.sp,
                fontWeight = FontWeight.Light,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(13.dp))
                .background(palette.surface)
                .padding(start = 15.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = palette.text, fontSize = 16.sp),
                cursorBrush = SolidColor(palette.mint),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { scope.launch { listState.scrollToItem(0) } }),
                decorationBox = { field ->
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp)) {
                        if (query.isEmpty()) {
                            androidx.compose.material3.Text(
                                text = stringResource(R.string.apps_search_hint),
                                color = palette.muted,
                                fontSize = 15.sp,
                            )
                        }
                        field()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = searchDescription },
            )
            if (query.isNotEmpty()) {
                val clearDescription = stringResource(R.string.search_clear)
                IconButton(
                    onClick = { query = "" },
                    modifier = Modifier.size(44.dp).semantics { contentDescription = clearDescription },
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_clear),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(palette.muted),
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(11.dp))
        if (!hasInventoryConsent) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                androidx.compose.material3.Text(
                    text = stringResource(R.string.apps_consent_required_title),
                    color = palette.text,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                androidx.compose.material3.Text(
                    text = stringResource(R.string.apps_consent_required_body),
                    color = palette.muted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                )
                androidx.compose.material3.Button(onClick = onRequestInventoryConsent) {
                    androidx.compose.material3.Text(stringResource(R.string.app_inventory_review))
                }
            }
        } else if (filteredApps.isEmpty()) {
            val message = when {
                isLoading -> stringResource(R.string.apps_loading)
                query.isBlank() -> stringResource(R.string.apps_empty)
                else -> stringResource(R.string.apps_no_matches, query.trim())
            }
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Text(
                    text = message,
                    color = palette.muted,
                    fontSize = 15.sp,
                )
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(filteredApps, key = LaunchableApp::id) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(11.dp))
                            .clickable(role = Role.Button) { onLaunch(app.componentName) }
                            .semantics(mergeDescendants = true) { role = Role.Button }
                            .padding(horizontal = 9.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (app.icon != null) {
                            Image(
                                bitmap = app.icon,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(palette.messages),
                                contentAlignment = Alignment.Center,
                            ) {
                                androidx.compose.material3.Text(
                                    app.label.take(1).uppercase(),
                                    color = palette.tileText,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                        Spacer(Modifier.width(13.dp))
                        androidx.compose.material3.Text(
                            text = app.label,
                            color = palette.text,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
