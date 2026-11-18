package com.example.noghte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceDetailScreen(resourceId: String, onBackClick: () -> Unit) {
    val resource = FakeData.resources.find { it.id == resourceId }
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (resource != null) stringResource(id = resource.nameRes) else "", 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = { 
                    IconButton(onClick = onBackClick) { 
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) 
                    } 
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        }
    ) { paddingValues ->
        if (resource != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ۱. هدر رنگی منبع
                Card(
                    modifier = Modifier.fillMaxWidth(), 
                    colors = CardDefaults.cardColors(containerColor = primaryColor), 
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp), 
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val icon = when (resource.type) { 
                            "API" -> Icons.Default.Link
                            "WEB_APP" -> Icons.Default.Language
                            "DATABASE" -> Icons.Default.Storage
                            else -> Icons.Default.Star 
                        }
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)), 
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(id = resource.nameRes), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(resource.type, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }

                // ۲. کارت توضیحات
                Card(
                    modifier = Modifier.fillMaxWidth(), 
                    colors = CardDefaults.cardColors(containerColor = surfaceColor), 
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("توضیحات", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = resource.descRes), fontSize = 14.sp, color = textColor.copy(alpha = 0.8f), lineHeight = 22.sp)
                    }
                }

                // ۳. کارت عملیات مجاز
                Card(
                    modifier = Modifier.fillMaxWidth(), 
                    colors = CardDefaults.cardColors(containerColor = surfaceColor), 
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("عملیات مجاز", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(12.dp))
                        resource.allowedActions.forEach { action ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), 
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(action, fontSize = 13.sp, color = textColor.copy(alpha = 0.9f))
                            }
                        }
                    }
                }

                // ۴. کارت اطلاعات دسترسی (جدید - طبق عکس استاد)
                Card(
                    modifier = Modifier.fillMaxWidth(), 
                    colors = CardDefaults.cardColors(containerColor = surfaceColor), 
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("اطلاعات دسترسی", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // نقش مورد نیاز
                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(id = R.string.required_role_label), fontSize = 14.sp, color = textColor.copy(alpha = 0.7f))
                            Box(
                                modifier = Modifier
                                    .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(resource.requiredRole, fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // آخرین دسترسی
                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(id = R.string.last_access_label), fontSize = 14.sp, color = textColor.copy(alpha = 0.7f))
                            Text(resource.lastAccess, fontSize = 12.sp, color = textColor)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // وضعیت (فعال / غیرفعال)
                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(id = R.string.status_label), fontSize = 14.sp, color = textColor.copy(alpha = 0.7f))
                            val isActive = resource.status == "ACTIVE"
                            val statusColor = if (isActive) Color(0xFF4CAF50) else Color.Red
                            val statusText = if (isActive) stringResource(id = R.string.status_active) else stringResource(id = R.string.status_inactive)
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(statusText, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ۵. دکمه درخواست دسترسی
                Button(
                    onClick = { /* TODO: منطق درخواست */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.request_access_btn), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
