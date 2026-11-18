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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    val isAdminOrCeo = FakeData.currentUser.role == "ADMIN" || FakeData.currentUser.role == "CEO"

    // ✅ فیلتر کردن منابع: ویوورها نباید Admin Panel رو ببینن
val baseResources = if (FakeData.currentUser.role == "VIEWER") {
    FakeData.resources.filter { it.id != "2" } // ID 2 همون Admin Panel هست
} else {
    FakeData.resources
}

val filteredResources = if (searchQuery.isEmpty()) {
    baseResources
} else {
    baseResources.filter { it.nameRes.toString().contains(searchQuery, ignoreCase = true) }
}

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            if (isSearchActive) {
                TopAppBar(
                    title = { TextField(value = searchQuery, onValueChange = { searchQuery = it }, placeholder = { Text(stringResource(id = R.string.search_placeholder)) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White), singleLine = true) },
                    navigationIcon = { IconButton(onClick = { isSearchActive = false; searchQuery = "" }) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
                )
            } else {
                TopAppBar(
                    title = {
                        val title = when(selectedTab) {
                            0 -> stringResource(id = R.string.tab_resources)
                            1 -> stringResource(id = R.string.tab_logs)
                            2 -> stringResource(id = R.string.tab_profile)
                            else -> stringResource(id = R.string.admin_panel_title)
                        }
                        Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
                    },
                    actions = {
                        if (selectedTab == 0) {
                            IconButton(onClick = { isSearchActive = true }) { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White) }
                        }
                        IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = surfaceColor) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_resources)) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, selectedTextColor = primaryColor, indicatorColor = primaryColor, unselectedIconColor = textColor.copy(alpha = 0.6f), unselectedTextColor = textColor.copy(alpha = 0.6f))
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_logs)) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, selectedTextColor = primaryColor, indicatorColor = primaryColor, unselectedIconColor = textColor.copy(alpha = 0.6f), unselectedTextColor = textColor.copy(alpha = 0.6f))
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.tab_profile)) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, selectedTextColor = primaryColor, indicatorColor = primaryColor, unselectedIconColor = textColor.copy(alpha = 0.6f), unselectedTextColor = textColor.copy(alpha = 0.6f))
                )
                
                // ✅ تب جدید فقط برای ادمین و مدیرعامل
                if (isAdminOrCeo) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                        label = { Text(stringResource(id = R.string.admin_tab)) },
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, selectedTextColor = primaryColor, indicatorColor = primaryColor, unselectedIconColor = textColor.copy(alpha = 0.6f), unselectedTextColor = textColor.copy(alpha = 0.6f))
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (selectedTab) {
                0 -> {
                    if (filteredResources.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(id = R.string.no_resources_found), color = textColor) }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(filteredResources) { resource -> ResourceCard(resource, primaryColor, surfaceColor, textColor, onClick = { onResourceClick(resource.id) }) }
                        }
                    }
                }
                1 -> LogsTab(primaryColor, surfaceColor, textColor)
                2 -> ProfileTab(primaryColor, surfaceColor, textColor, onLogout)
                3 -> AdminPanelScreen(onBackClick = { selectedTab = 0 }) // ✅ لود کردن پنل ادمین در تب
            }
        }
    }
}

// ... (بقیه توابع ResourceCard, LogsTab, ProfileTab بدون تغییر باقی می‌مونن)
// از خط زیر به بعد همون کدهای قبلی رو کپی کن (چون تغییری نکردن)
@Composable
fun ResourceCard(resource: Resource, primaryColor: Color, surfaceColor: Color, textColor: Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = surfaceColor), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val icon = when (resource.type) { "API" -> Icons.Default.Link; "WEB_APP" -> Icons.Default.Language; "DATABASE" -> Icons.Default.Storage; else -> Icons.Default.Star }
            Box(modifier = Modifier.size(40.dp).background(primaryColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = primaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(id = resource.nameRes), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(id = resource.descRes), fontSize = 12.sp, color = textColor.copy(alpha = 0.6f), maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                val (accessText, accessColor) = when (FakeData.currentUser.role) { "CEO" -> Pair(stringResource(id = R.string.access_full), primaryColor); "ADMIN", "MANAGER" -> Pair(stringResource(id = R.string.access_admin), primaryColor); else -> Pair(stringResource(id = R.string.access_denied), Color.Red) }
                Text(accessText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accessColor)
            }
        }
    }
}

@Composable
fun LogsTab(primaryColor: Color, surfaceColor: Color, textColor: Color) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (FakeData.currentUser.role == "VIEWER") {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(id = R.string.no_access_message), color = textColor, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) { Text(stringResource(id = R.string.request_access_button), color = Color.White) }
                    }
                }
            }
        }
        items(FakeData.logs) { log ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = surfaceColor), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    val isAllowed = log.status == "ALLOWED"
                    Icon(if (isAllowed) Icons.Default.CheckCircle else Icons.Default.Cancel, contentDescription = null, tint = if (isAllowed) Color(0xFF4CAF50) else Color(0xFFF44336), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(id = log.actionRes), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Text(log.resourceName, fontSize = 12.sp, color = textColor.copy(alpha = 0.6f))
                        Text(log.timestamp, fontSize = 10.sp, color = textColor.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTab(primaryColor: Color, surfaceColor: Color, textColor: Color, onLogout: () -> Unit) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = primaryColor), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(FakeData.currentUser.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(FakeData.currentUser.email, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = surfaceColor), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.role_label), color = textColor.copy(alpha = 0.6f))
                    }
                    val translatedRole = when (FakeData.currentUser.role) { "CEO" -> stringResource(id = R.string.role_ceo); "ADMIN" -> stringResource(id = R.string.role_admin); else -> stringResource(id = R.string.role_viewer) }
                    Text(translatedRole, fontWeight = FontWeight.Bold, color = primaryColor)
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = textColor.copy(alpha = 0.1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.org_label), color = textColor.copy(alpha = 0.6f))
                    }
                    Text(stringResource(id = R.string.org_value), fontWeight = FontWeight.Bold, color = textColor)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(onClick = { scope.launch { try { withContext(Dispatchers.IO) { if (FakeData.currentUser.id != "guest") AppwriteClient.account.deleteSession("current") } } catch (e: Exception) {} finally { onLogout() } } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F), contentColor = Color.White), shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(id = R.string.logout_button), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
