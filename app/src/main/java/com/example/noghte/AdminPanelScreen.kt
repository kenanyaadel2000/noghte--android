package com.example.noghte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun AdminPanelScreen(onBackClick: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    var usersList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
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
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(color = primaryColor, modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.align(Alignment.Center).padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.users_list_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(usersList) { userData ->
                        val name = userData["name"]?.toString() ?: "Unknown"
                        val email = userData["email"]?.toString() ?: "No Email"
                        val currentRole = userData["role"]?.toString() ?: "VIEWER"
                        val docId = userData["\$id"]?.toString() ?: ""

                        AdminUserCard(
                            name = name,
                            email = email,
                            currentRole = currentRole,
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
    primaryColor: Color,
    surfaceColor: Color,
    textColor: Color,
    onRoleChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("VIEWER", "ADMIN", "CEO")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(primaryColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                Text(text = email, fontSize = 12.sp, color = textColor.copy(alpha = 0.6f))
            }

            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor.copy(alpha = 0.1f), contentColor = primaryColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = currentRole, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                expanded = false
                                if (role != currentRole) {
                                    onRoleChanged(role)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
