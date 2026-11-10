package com.example.noghte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceListScreen() {
    val primaryGold = Color(0xFFD4AF37)
    val darkBackground = Color(0xFF121212)
    val cardBackground = Color(0xFF1E1E1E)
    
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.resources_title), color = primaryGold, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = primaryGold)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = primaryGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = cardBackground) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_resources)) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = primaryGold,
                        indicatorColor = primaryGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_logs)) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = primaryGold,
                        indicatorColor = primaryGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_profile)) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = primaryGold,
                        indicatorColor = primaryGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
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
                            ResourceCard(resource = resource, primaryGold = primaryGold, cardBackground = cardBackground)
                        }
                    }
                }
                1 -> {
                    // تب لاگ‌ها (فعلا خالی)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.coming_soon), color = Color.Gray)
                    }
                }
                2 -> {
                    // تب پروفایل (فعلا خالی)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.coming_soon), color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceCard(resource: Resource, primaryGold: Color, cardBackground: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
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
                    .background(primaryGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = primaryGold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resource.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = resource.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (resource.accessLevel == "FULL") stringResource(id = R.string.access_full) else stringResource(id = R.string.access_read),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGold
                )
            }
        }
    }
}
