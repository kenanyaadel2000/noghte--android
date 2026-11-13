package com.example.noghte

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.appwrite.ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onNavigateToEmailSent: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("ثبت‌نام در نقطه", color = primaryColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("ساخت حساب جدید", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("نام و نام خانوادگی") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("ایمیل سازمانی") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("رمز عبور") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    if (email.isBlank() || password.isBlank() || name.isBlank()) {
                        errorMessage = "لطفاً همه فیلدها را پر کنید."
                        return@Button
                    }
                    
                    isLoading = true
                    errorMessage = null
                    
                    scope.launch {
                        try {
                            // ۱. ساخت کاربر
                            val newUser = withContext(Dispatchers.IO) {
                                AppwriteClient.account.create(
                                    userId = ID.unique(),
                                    email = email,
                                    password = password,
                                    name = name
                                )
                            }

                            // ۲. لاگین موقت برای دسترسی به متد ارسال ایمیل
                            withContext(Dispatchers.IO) {
                                AppwriteClient.account.createEmailPasswordSession(email, password)
                            }

                            // ۳. ارسال لینک تایید
                            // ✅ تغییر به createVerification برای نسخه اندروید
                            withContext(Dispatchers.IO) {
                                AppwriteClient.account.createVerification(url = "https://noghte.com/verify")
                            }

                            // ۴. ذخیره در دیتابیس
                            withContext(Dispatchers.IO) {
                                AppwriteClient.databases.createDocument(
                                    databaseId = AppwriteClient.DATABASE_ID,
                                    collectionId = AppwriteClient.TABLE_ID,
                                    documentId = ID.unique(),
                                    data = mapOf(
                                        "name" to name,
                                        "email" to email,
                                        "role" to "VIEWER"
                                    )
                                )
                            }

                            // ۵. لاگ اوت کردن
                            withContext(Dispatchers.IO) {
                                AppwriteClient.account.deleteSession("current")
                            }

                            isLoading = false
                            onNavigateToEmailSent(email)
                            
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = "خطا در ثبت‌نام: ${e.message}"
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("ثبت‌نام", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
