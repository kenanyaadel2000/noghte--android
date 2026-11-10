package com.example.noghte

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceListScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateToSettings: () -> Unit,
    onResourceClick: (String) -> Unit // ✅ پارامتر جدید برای کلیک روی منبع
) {
    // گرفتن رنگ‌ها از تم فعلی (روشن یا تیره)
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.resources_title), 
                        color = primaryColor, 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = primaryColor)
                    }
                    IconButton(onClick = { /* TODO: Profile */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = primaryColor)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = surfaceColor) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_resources)) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = primaryColor,
                        indicatorColor = primaryColor,
                        unselectedIconColor = textColor.copy(alpha = 0.6f),
                        unselectedTextColor = textColor.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_logs)) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = primaryColor,
                        indicatorColor = primaryColor,
                        unselectedIconColor = textColor.copy(alpha = 0.6f),
                        unselectedTextColor = textColor.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_profile)) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = primaryColor,
                        indicatorColor = primaryColor,
                        unselectedIconColor = textColor.copy(alpha = 0.6f),
                        unselectedTextColor = textColor.copy(alpha = 0.6f)
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (selectedTab) {
                0 -> {
                    // تب منابع
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(FakeData.resources) { resource ->
                            ResourceCard(
                                resource = resource, 
                                primaryColor = primaryColor, 
                                surfaceColor = surfaceColor,
                                textColor = textColor,
                                onClick = { onResourceClick(resource.id) } // ✅ پاس دادن ID به تابع
                            )
                        }
                    }
                }
                1 -> {
                    // تب لاگ‌ها (فعلا خالی)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.coming_soon), color = textColor.copy(alpha = 0.5f))
                    }
                }
                2 -> {
                    // تب پروفایل (فعلا خالی)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.coming_soon), color = textColor.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceCard(
    resource: Resource, 
    primaryColor: Color, 
    surfaceColor: Color,
    textColor: Color,
    onClick: () -> Unit // ✅ پارامتر جدید برای کلیک
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // ✅ کارت کلیک‌پذیر شد
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // آیکون بر اساس نوع منبع
            val icon = when (resource.type) {
                "API" -> Icons.Default.Link
                "WEB_APP" -> Icons.Default.Language
                "DATABASE" -> Icons.Default.Storage
                else -> Icons.Default.Star
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(primaryColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = primaryColor)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resource.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = resource.description,
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.6f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (resource.accessLevel == "FULL") stringResource(id = R.string.access_full) else stringResource(id = R.string.access_read),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }
    }
}
