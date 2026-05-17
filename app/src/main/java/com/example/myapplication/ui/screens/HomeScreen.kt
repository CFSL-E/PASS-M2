package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.AuthEntry
import com.example.myapplication.data.SampleData
import com.example.myapplication.ui.components.AuthEntryCard

/**
 * 主页 - 模仿 Google Authenticator 的布局
 * 顶部：MD3 SearchBar（使用 Container Transform 动画展开到全屏搜索页）
 * 中间：认证条目列表
 * 右下角：MD3 FAB（使用 Container Transform 动画展开到添加账户页）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entries = remember { SampleData.entries }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val filteredEntries by remember(searchQuery, entries) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                entries
            } else {
                entries.filter {
                    it.issuer.contains(searchQuery, ignoreCase = true) ||
                            it.accountName.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = "Authenticator",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onMenuClick) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "菜单"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )

                // MD3 SearchBar - 点击后通过 Container Transform 动画展开到全屏搜索页
                // 参考：https://m3.material.io/components/search/overview
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { /* 点击键盘搜索时 */ },
                            expanded = false,
                            onExpandedChange = { expanded ->
                                if (expanded) {
                                    onSearchClick() // 导航到搜索页，触发 Container Transform
                                }
                            },
                            placeholder = { Text("搜索账户") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "搜索"
                                )
                            }
                        )
                    },
                    expanded = false,
                    onExpandedChange = { expanded ->
                        if (expanded) {
                            onSearchClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    // 不在主页展开搜索结果，而是导航到搜索页
                }
            }
        },
        floatingActionButton = {
            // MD3 FAB - 使用 Container Transform 动画展开到添加账户页
            // 参考：https://m3.material.io/components/floating-action-button/overview
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加账户"
                )
            }
        }
    ) { innerPadding ->
        if (filteredEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无账户",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = filteredEntries,
                    key = { it.id }
                ) { entry ->
                    AuthEntryCard(entry = entry)
                }
                // 底部留白，避免 FAB 遮挡
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}