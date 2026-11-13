package com.example.noghte

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceListScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateToSettings: () -> Unit,
    onResourceClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    var selectedTab by remember { mutableIntStateOf(0) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // فیلتر کردن منابع بر اساس متن جستجو
    val filteredResources = if (searchQuery.isEmpty()) {
        FakeData.resources
    } else {
        FakeData.resources.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            if (isSearchActive) {
                // نوار جستجو (وقتی فعاله)
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text(stringResource(id = R.string.search_placeholder)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            ),
                            singleLine = true
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            isSearchActive = false 
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = primaryColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
                )
            } else {
                // نوار بالا (حالت عادی)
                TopAppBar(
                    title = {
                        val title = when(selectedTab) {
                            0 -> stringResource(id = R.string.resources_title)
                            1 -> stringResource(id = R.string.tab_logs)
                            else -> stringResource(id = R.string.tab_profile)
                        }
                        Text(text = title, color = primaryColor, fontWeight = FontWeight.Bold)
                    },
                    actions = {
                        if (selectedTab == 0) {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = primaryColor)
                            }
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = primaryColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = surfaceColor) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_resources)) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black, selectedTextColor = primaryColor,
                        indicatorColor = primaryColor, unselectedIconColor = textColor.copy(alpha = 0.6f), unselectedTextColor = textColor.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_logs)) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black, selectedTextColor = primaryColor,
                        indicatorColor = primaryColor, unselectedIconColor = textColor.copy(alpha = 0.6f), unselectedTextColor = textColor.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_profile)) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black, selectedTextColor = primaryColor,
                        indicatorColor = primaryColor, unselectedIconColor = textColor.copy(alpha = 0.6f), unselectedTextColor = textColor.copy(alpha = 0.6f)
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (selectedTab) {
                0 -> {
                    if (filteredResources.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("منبعی یافت نشد", color = textColor)
                        }
                    } else {
                        ResourcesTab(primaryColor, surfaceColor, textColor, filteredResources, onResourceClick)
                    }
                }
                1 -> LogsTab(primaryColor, surfaceColor, textColor)
                2 -> ProfileTab(primaryColor, surfaceColor, textColor, onLogout)
            }
        }
    }
}

@Composable
fun ResourcesTab(
    primaryColor: Color,
    surfaceColor: Color,
    textColor: Color,
    resources: List<Resource>,
    onResourceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(resources) { resource ->
            ResourceCard(resource = resource, primaryColor = primaryColor, surfaceColor = surfaceColor, textColor = textColor, onClick = { onResourceClick(resource.id) })
        }
    }
}

// بقیه توابع (ResourceCard, LogsTab, ProfileTab) بدون تغییر باقی می‌مونن، 
// اما برای اینکه کد کامل باشه، اینجا می‌ذارمشون:

@Composable
fun ResourceCard(resource: Resource, primaryColor: Color, surfaceColor: Color, textColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val icon = when (resource.type) {
                "API" -> Icons.Default.Link
                "WEB_APP" -> Icons.Default.Language
                "DATABASE" -> Icons.Default.Storage
                else -> Icons.Default.Star
            }
            Box(modifier = Modifier.size(48.dp).background(primaryColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = primaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = resource.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = resource.description, fontSize = 12.sp, color = textColor.copy(alpha = 0.6f), maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (resource.accessLevel == "FULL") stringResource(id = R.string.access_full) else stringResource(id = R.string.access_read),
                    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor
                )
            }
        }
    }
}

@Composable
fun LogsTab(primaryColor: Color, surfaceColor: Color, textColor: Color) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(FakeData.logs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    val isAllowed = log.status == "ALLOWED"
                    val statusColor = if (isAllowed) Color(0xFF4CAF50) else Color(0xFFF44336)
                    val statusIcon = if (isAllowed) Icons.Default.CheckCircle else Icons.Default.Cancel

                    Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = log.action, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Text(text = log.resourceName, fontSize = 12.sp, color = textColor.copy(alpha = 0.6f))
                        Text(text = log.timestamp, fontSize = 10.sp, color = textColor.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTab(primaryColor: Color, surfaceColor: Color, textColor: Color, onLogout: () -> Unit) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (بخش آواتار و کارت اطلاعات بدون تغییر می‌مونه) ...
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(primaryColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor, modifier = Modifier.size(60.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = stringResource(id = R.string.profile_name), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
        Text(text = stringResource(id = R.string.profile_email), fontSize = 14.sp, color = textColor.copy(alpha = 0.6f))
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = stringResource(id = R.string.profile_role_label), color = textColor.copy(alpha = 0.6f))
                    Text(text = stringResource(id = R.string.profile_role_value), fontWeight = FontWeight.Bold, color = primaryColor)
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = textColor.copy(alpha = 0.1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = stringResource(id = R.string.profile_org_label), color = textColor.copy(alpha = 0.6f))
                    Text(text = stringResource(id = R.string.profile_org_value), fontWeight = FontWeight.Bold, color = textColor)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = {
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            AppwriteClient.account.deleteSession("current") // بستن سشن واقعی
                        }
                    } catch (e: Exception) {
                        // اگر ارور هم داد، باز هم کاربر رو ببریم بیرون
                    } finally {
                        onLogout()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F), contentColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.logout_button), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
