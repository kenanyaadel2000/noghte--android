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
import androidx.compose.ui.draw.scale // ✅ اضافه شد
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(onBackClick: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    var usersList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    var searchQuery by remember { mutableStateOf("") }
    var sortByRole by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    fun fetchUsers() {
        scope.launch {
            isLoading = true
            try {
                val response = withContext(Dispatchers.IO) {
                    AppwriteClient.databases.listDocuments(
                        databaseId = AppwriteClient.DATABASE_ID,
                        collectionId = AppwriteClient.TABLE_ID
                    )
                }
                usersList = response.documents.map { it.data }
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "خطا در دریافت اطلاعات: ${e.message}"
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchUsers()
        FakeData.logs.add(0, AccessLog(
            id = UUID.randomUUID().toString(),
            actionRes = R.string.admin_login_log_action,
            resourceName = "Admin Panel",
            status = "ALLOWED",
            timestamp = "همین الان",
            type = "LOGIN"
        ))
    }

    val filteredAndSortedUsers = usersList
        .filter { 
            it["name"].toString().contains(searchQuery, ignoreCase = true) || 
            it["email"].toString().contains(searchQuery, ignoreCase = true) 
        }
        .sortedBy { 
            if (sortByRole) it["role"].toString() else it["name"].toString() 
        }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.admin_panel_title), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(id = R.string.search_users_placeholder), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = primaryColor) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = textColor.copy(alpha = 0.3f),
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !sortByRole,
                    onClick = { sortByRole = false },
                    label = { Text(stringResource(id = R.string.sort_by_name), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = primaryColor, selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = sortByRole,
                    onClick = { sortByRole = true },
                    label = { Text(stringResource(id = R.string.sort_by_role), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = primaryColor, selectedLabelColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            } else if (filteredAndSortedUsers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("کاربری یافت نشد", color = textColor)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAndSortedUsers) { userData ->
                        val name = userData["name"]?.toString() ?: "Unknown"
                        val email = userData["email"]?.toString() ?: "No Email"
                        val currentRole = userData["role"]?.toString() ?: "VIEWER"
                        val docId = userData["\$id"]?.toString() ?: ""
                        val hasAccess = userData["hasAccess"] as? Boolean ?: false // ✅ استخراج وضعیت دسترسی

                        AdminUserCard(
                            name = name,
                            email = email,
                            currentRole = currentRole,
                            hasAccess = hasAccess, // ✅ پاس داده شد
                            primaryColor = primaryColor,
                            surfaceColor = surfaceColor,
                            textColor = textColor,
                            onRoleChanged = { newRole ->
                                scope.launch {
                                    try {
                                        withContext(Dispatchers.IO) {
                                            AppwriteClient.databases.updateDocument(
                                                databaseId = AppwriteClient.DATABASE_ID,
                                                collectionId = AppwriteClient.TABLE_ID,
                                                documentId = docId,
                                                data = mapOf("role" to newRole)
                                            )
                                        }
                                        fetchUsers()
                                    } catch (e: Exception) {}
                                }
                            },
                            onAccessChanged = { newAccess ->
                             // ۱. آپدیت دیتابیس Appwrite (اگر ستون رو ساخته باشی)
                             scope.launch {
                              try {
                                withContext(Dispatchers.IO) {
                                AppwriteClient.databases.updateDocument(
                                databaseId = AppwriteClient.DATABASE_ID,
                                collectionId = AppwriteClient.TABLE_ID,
                                documentId = docId,
                                data = mapOf("hasAccess" to newAccess)
                                )
                                }
                                } catch (e: Exception) {
            // اگر ستون در Appwrite نبود، ارور رو نادیده بگیر
        }
    }
    
    // ۲. آپدیت لیست محلی برای اینکه UI فوراً تغییر کنه
    val updatedList = usersList.map { user ->
        if (user["\$id"] == docId) {
            user.toMutableMap().apply { this["hasAccess"] = newAccess }
        } else {
            user
        }
    }
    usersList = updatedList
}
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserCard(
    name: String,
    email: String,
    currentRole: String,
    hasAccess: Boolean,
    primaryColor: Color,
    surfaceColor: Color,
    textColor: Color,
    onRoleChanged: (String) -> Unit,
    onAccessChanged: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    val roles = if (FakeData.currentUser.role == "CEO") listOf("VIEWER", "ADMIN", "CEO") else listOf(currentRole)
    val canChangeAccess = FakeData.currentUser.role == "CEO" || (FakeData.currentUser.role == "ADMIN" && currentRole == "VIEWER")

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = surfaceColor), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(primaryColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                Text(text = email, fontSize = 12.sp, color = textColor.copy(alpha = 0.6f))
                
                Text(
                    text = if (hasAccess) "دسترسی دارد" else "دسترسی ندارد",
                    fontSize = 10.sp,
                    color = if (hasAccess) Color(0xFF4CAF50) else Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Box {
                    Button(
                        onClick = { if (FakeData.currentUser.role == "CEO") expanded = true },
                        enabled = FakeData.currentUser.role == "CEO",
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor.copy(alpha = 0.1f), contentColor = primaryColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(text = currentRole, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        roles.forEach { role ->
                            DropdownMenuItem(text = { Text(role) }, onClick = {
                                expanded = false
                                if (role != currentRole) onRoleChanged(role)
                            })
                        }
                    }
                }
                
                Switch(
                    checked = hasAccess,
                    onCheckedChange = { if (canChangeAccess) onAccessChanged(it) },
                    enabled = canChangeAccess,
                    modifier = Modifier.scale(0.7f),
                    colors = SwitchDefaults.colors(checkedThumbColor = primaryColor)
                )
            }
        }
    }
}
