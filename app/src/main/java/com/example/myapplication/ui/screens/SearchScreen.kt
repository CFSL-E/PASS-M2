package com.example.myapplication.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.SampleData
import com.example.myapplication.ui.components.AuthEntryCard

/**
 * 搜索页 - 通过 Container Transform 动画从搜索栏展开到全屏
 * 参考：https://m3.material.io/components/search/overview
 * 动画参考：https://m3.material.io/styles/motion/transitions/transition-patterns#425522a0-60f9-4ae0-ba7b-4617f0e40e64
 * 
 * Container Transform 模式：搜索栏作为起始元素，整个搜索页面作为结束元素
 * 这是一个原生的导航页面（通过 Navigation Compose 路由跳转），而非通过控制组件显隐实现
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entries = remember { SampleData.entries }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val filteredEntries by remember(searchQuery, entries) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                emptyList()
            } else {
                entries.filter {
                    it.issuer.contains(searchQuery, ignoreCase = true) ||
                            it.accountName.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    // 自动聚焦搜索框
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            // 全屏 SearchBar - MD3 搜索组件的全屏展开状态
            // Container Transform: 搜索栏 → 全屏搜索视图
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { /* 执行搜索 */ },
                        expanded = true,
                        onExpandedChange = { expanded ->
                            if (!expanded) {
                                onBackClick()
                            }
                        },
                        placeholder = { Text("搜索账户") },
                        leadingIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "返回"
                                )
                            }
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "清除"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                },
                expanded = true,
                onExpandedChange = { expanded ->
                    if (!expanded) {
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                // 搜索结果列表
                if (filteredEntries.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = filteredEntries,
                            key = { it.id }
                        ) { entry ->
                            AuthEntryCard(entry = entry)
                        }
                    }
                } else if (searchQuery.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "未找到匹配结果",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "输入关键词搜索账户",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // 搜索页的主体区域（搜索栏下方）
        // 当搜索栏展开时，这里显示半透明背景
        Spacer(modifier = Modifier.padding(innerPadding))
    }
}