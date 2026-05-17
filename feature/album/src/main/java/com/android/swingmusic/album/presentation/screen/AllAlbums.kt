package com.android.swingmusic.album.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.android.swingmusic.album.presentation.event.AlbumWithInfoUiEvent
import com.android.swingmusic.album.presentation.event.AlbumsUiEvent
import com.android.swingmusic.album.presentation.state.AllAlbumsUiState
import com.android.swingmusic.album.presentation.util.pagingAlbums
import com.android.swingmusic.album.presentation.viewmodel.AlbumWithInfoViewModel
import com.android.swingmusic.album.presentation.viewmodel.AllAlbumsViewModel
import com.android.swingmusic.common.presentation.navigator.CommonNavigator
import com.android.swingmusic.core.data.util.Resource
import com.android.swingmusic.core.domain.model.Album
import com.android.swingmusic.core.domain.util.SortBy
import com.android.swingmusic.uicomponent.presentation.component.AlbumItem
import com.android.swingmusic.uicomponent.presentation.component.SortByChip
import com.android.swingmusic.uicomponent.presentation.theme.SwingMusicTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.android.swingmusic.uicomponent.R as UiComponents

@Composable
private fun AllAlbums(
    pagingAlbums: LazyPagingItems<Album>,
    allAlbumsUiState: AllAlbumsUiState,
    sortByPairs: List<Pair<SortBy, String>>,
    showOnRefreshIndicator: Boolean,
    onUpdateGridCount: (Int) -> Unit,
    onSortBy: (Pair<SortBy, String>) -> Unit,
    onClickAlbumItem: (albumHash: String) -> Unit,
    onRetry: () -> Unit,
    baseUrl: String
) {
    val gridState = rememberLazyGridState()
    val albumCount = when (val result = allAlbumsUiState.totalAlbums) {
        is Resource.Loading -> -1
        is Resource.Error -> 0
        is Resource.Success -> result.data!!
    }

    val loadingState = when {
        pagingAlbums.loadState.append is LoadState.Loading -> pagingAlbums.loadState.append as LoadState.Loading
        pagingAlbums.loadState.prepend is LoadState.Loading -> pagingAlbums.loadState.prepend as LoadState.Loading
        pagingAlbums.loadState.refresh is LoadState.Loading -> pagingAlbums.loadState.refresh as LoadState.Loading
        else -> null
    }

    val errorState = when {
        pagingAlbums.loadState.append is LoadState.Error -> pagingAlbums.loadState.append as LoadState.Error
        pagingAlbums.loadState.prepend is LoadState.Error -> pagingAlbums.loadState.prepend as LoadState.Error
        pagingAlbums.loadState.refresh is LoadState.Error -> pagingAlbums.loadState.refresh as LoadState.Error
        else -> null
    }

    var isGridCountMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            columns = GridCells.Fixed(allAlbumsUiState.gridCount),
            state = gridState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
        ) {
            item(span = { GridItemSpan(allAlbumsUiState.gridCount) }) {
                AlbumsHeader(
                    albumCount = albumCount,
                    gridCount = allAlbumsUiState.gridCount,
                    menuExpanded = isGridCountMenuExpanded,
                    setMenuExpanded = { isGridCountMenuExpanded = it },
                    onGridChange = onUpdateGridCount,
                )
            }

            item(span = { GridItemSpan(allAlbumsUiState.gridCount) }) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.onSurface)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = stringResource(UiComponents.string.sort_by),
                                color = MaterialTheme.colorScheme.surface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                            )
                        }
                    }
                    items(sortByPairs) { pair ->
                        SortByChip(
                            labelPair = pair,
                            sortOrder = allAlbumsUiState.sortOrder,
                            isSelected = allAlbumsUiState.sortBy == pair
                        ) { clickedPair ->
                            onSortBy(clickedPair)
                        }
                    }
                }
            }

            if (pagingAlbums.loadState.refresh is LoadState.NotLoading) {
                pagingAlbums(pagingAlbums) { album ->
                    if (album == null) return@pagingAlbums
                    AlbumItem(
                        modifier = Modifier.fillMaxSize(),
                        album = album,
                        baseUrl = baseUrl,
                        onClick = { onClickAlbumItem(it) }
                    )
                }

                item(span = { GridItemSpan(allAlbumsUiState.gridCount) }) {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }

            loadingState?.let {
                if (!showOnRefreshIndicator) {
                    item(span = { GridItemSpan(allAlbumsUiState.gridCount) }) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = stringResource(UiComponents.string.albums_loading))
                                Spacer(modifier = Modifier.height(8.dp))
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            errorState?.let {
                item(span = { GridItemSpan(allAlbumsUiState.gridCount) }) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = it.error.message ?: "Error loading albums",
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                pagingAlbums.retry()
                                onRetry()
                            }) {
                                Text(text = stringResource(UiComponents.string.retry))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumsHeader(
    albumCount: Int,
    gridCount: Int,
    menuExpanded: Boolean,
    setMenuExpanded: (Boolean) -> Unit,
    onGridChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(UiComponents.string.nav_albums),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = getAlbumCountText(albumCount),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f),
            )
        }
        Box {
            IconButton(onClick = { setMenuExpanded(!menuExpanded) }) {
                Icon(
                    painter = painterResource(id = UiComponents.drawable.grid),
                    contentDescription = "Grid count",
                )
            }
            DropdownMenu(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                expanded = menuExpanded,
                onDismissRequest = { setMenuExpanded(false) }
            ) {
                Text(
                    text = stringResource(UiComponents.string.grid_count),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                (2..4).forEach { count ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12))
                            .background(
                                if (gridCount == count)
                                    MaterialTheme.colorScheme.inverseSurface.copy(alpha = .1F)
                                else Color.Unspecified
                            ),
                        interactionSource = MutableInteractionSource(),
                        onClick = {
                            if (gridCount != count) {
                                setMenuExpanded(false)
                                onGridChange(count)
                            }
                        },
                        text = {
                            Text(
                                maxLines = 1,
                                text = count.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = .84F)
                            )
                        },
                        trailingIcon = {
                            if (gridCount == count)
                                Icon(
                                    modifier = Modifier.padding(start = 12.dp),
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Checked"
                                )
                        }
                    )
                    if (count < 4) Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AllAlbumScreen(
    allAlbumsViewModel: AllAlbumsViewModel = hiltViewModel(),
    albumWithInfoViewModel: AlbumWithInfoViewModel = hiltViewModel(),
    albumNavigator: CommonNavigator
) {
    val pagingAlbums =
        allAlbumsViewModel.allAlbumsUiState.value.pagingAlbums.collectAsLazyPagingItems()
    val albumsUiState by remember { allAlbumsViewModel.allAlbumsUiState }
    val sortAlbumsByPairs by remember { derivedStateOf { allAlbumsViewModel.sortAlbumsByEntries.toList() } }

    val baseUrl by allAlbumsViewModel.baseUrl.collectAsStateWithLifecycle()

    var showOnRefreshIndicator by remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()

    val errorState = when {
        pagingAlbums.loadState.append is LoadState.Error -> pagingAlbums.loadState.append as LoadState.Error
        pagingAlbums.loadState.prepend is LoadState.Error -> pagingAlbums.loadState.prepend as LoadState.Error
        pagingAlbums.loadState.refresh is LoadState.Error -> pagingAlbums.loadState.refresh as LoadState.Error
        else -> null
    }

    if (pagingAlbums.loadState.refresh is LoadState.NotLoading) {
        showOnRefreshIndicator = false
    }

    errorState?.let {
        showOnRefreshIndicator = false
    }

    SwingMusicTheme(navBarColor = MaterialTheme.colorScheme.inverseOnSurface) {
        PullToRefreshBox(
            isRefreshing = showOnRefreshIndicator,
            state = refreshState,
            onRefresh = {
                showOnRefreshIndicator = true

                allAlbumsViewModel.onAlbumsUiEvent(AlbumsUiEvent.OnPullToRefresh)
            },
            indicator = {
                PullToRefreshDefaults.Indicator(
                    modifier = Modifier
                        .padding(top = 76.dp)
                        .align(Alignment.TopCenter),
                    isRefreshing = showOnRefreshIndicator,
                    state = refreshState
                )
            }
        ) {
            AllAlbums(
                pagingAlbums = pagingAlbums,
                allAlbumsUiState = albumsUiState,
                sortByPairs = sortAlbumsByPairs,
                baseUrl = baseUrl ?: "https://default",
                showOnRefreshIndicator = showOnRefreshIndicator,
                onUpdateGridCount = { gridCount ->
                    allAlbumsViewModel.onAlbumsUiEvent(AlbumsUiEvent.OnUpdateGridCount(gridCount))
                },
                onSortBy = { pair ->
                    allAlbumsViewModel.onAlbumsUiEvent(AlbumsUiEvent.OnSortBy(pair))
                },
                onClickAlbumItem = {
                    albumWithInfoViewModel.onAlbumWithInfoUiEvent(AlbumWithInfoUiEvent.ResetState)
                    albumNavigator.gotoAlbumWithInfo(albumHash = it)
                },
                onRetry = {
                    allAlbumsViewModel.onAlbumsUiEvent(AlbumsUiEvent.OnRetry)
                }
            )
        }
    }
}

@Composable
private fun getAlbumCountText(count: Int): String {
    return when (count) {
        -1 -> stringResource(UiComponents.string.albums_loading)
        0 -> stringResource(UiComponents.string.albums_count_zero)
        1 -> stringResource(UiComponents.string.albums_count_one)
        else -> {
            val formattedCount = count.toString().reversed().chunked(3).joinToString(" ").reversed()
            stringResource(UiComponents.string.albums_count_template, formattedCount)
        }
    }
}
