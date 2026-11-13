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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.appwrite.ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(3000)
            onBackClick()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.register_button_text), color = primaryColor, fontWeight = FontWeight.Bold) },
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
            Text(stringResource(id = R.string.create_account_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.name_label)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email_label)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp, textAlign = TextAlign.Center)
            }

            if (successMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = successMessage!!,
                    color = Color(0xFF4CAF50),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    if (email.isBlank() || password.isBlank() || name.isBlank()) {
                        errorMessage = "لطفاً همه فیلدها را پر کنید.\nPlease fill all fields."
                        return@Button
                    }
                    
                    isLoading = true
                    errorMessage = null
                    successMessage = null
                    
                    scope.launch {
                        try {
                            try {
                                withContext(Dispatchers.IO) {
                                    AppwriteClient.account.deleteSession("current")
                                }
                            } catch (e: Exception) {}

                            withContext(Dispatchers.IO) {
                                AppwriteClient.account.create(
                                    userId = ID.unique(),
                                    email = email,
                                    password = password,
                                    name = name
                                )
                            }

                            withContext(Dispatchers.IO) {
                                AppwriteClient.account.createEmailPasswordSession(email, password)
                            }

                            try {
                                withContext(Dispatchers.IO) {
                                    AppwriteClient.account.createVerification(url = "https://noghte.com/verify")
                                }
                            } catch (e: Exception) {}

                            try {
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
                            } catch (e: Exception) {}

                            withContext(Dispatchers.IO) {
                                AppwriteClient.account.deleteSession("current")
                            }

                            isLoading = false
                            successMessage = "ثبت‌نام انجام شد. بعد از تایید ادمین حساب شما فعال می‌شود.\nRegistration successful. Activated after admin approval."
                            
                        } catch (e: Exception) {
                            isLoading = false
                            // ✅ ترجمه ارور ثبت‌نام
                            val msg = e.message ?: ""
                            errorMessage = when {
                                msg.contains("already exists") -> "این ایمیل قبلاً ثبت‌نام کرده است.\nThis email is already registered."
                                msg.contains("Invalid `email`") || msg.contains("valid email") -> "فرمت ایمیل وارد شده صحیح نیست.\nInvalid email format."
                                else -> msg
                            }
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
                    Text(stringResource(id = R.string.register_button_text), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
