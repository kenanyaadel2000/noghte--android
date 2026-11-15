package com.example.noghte

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    isDarkTheme: Boolean,
    onLoginSuccess: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onLanguageChange: () -> Unit,
    onThemeToggle: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onGuestLogin: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val context = LocalContext.current 

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(0.5f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(targetValue = 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onLanguageChange) {
                    Icon(Icons.Default.Language, contentDescription = "Language", tint = primaryColor)
                }
                IconButton(onClick = onThemeToggle) {
                    Icon(if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = "Theme", tint = primaryColor)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(120.dp).scale(scale.value).clip(RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = if (isDarkTheme) R.drawable.ic_logo_dark else R.drawable.ic_logo_light),
                        contentDescription = "Noghte Logo",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(id = R.string.app_name), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(id = R.string.login_subtitle), fontSize = 14.sp, color = textColor.copy(alpha = 0.7f))

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.email_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor, focusedTextColor = textColor, unfocusedTextColor = textColor),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text(stringResource(id = R.string.password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor, focusedTextColor = textColor, unfocusedTextColor = textColor),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(errorMessage!!, color = Color.Red, fontSize = 12.sp, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { 
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = context.getString(R.string.error_empty_fields) 
                            return@Button
                        }
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            try {
                                withContext(Dispatchers.IO) { AppwriteClient.account.createEmailPasswordSession(email, password) }
                                val currentUser = withContext(Dispatchers.IO) { AppwriteClient.account.get() }
                                if (!currentUser.emailVerification) {
                                    try { withContext(Dispatchers.IO) { AppwriteClient.account.createVerification(url = "https://noghte.com/verify") } } catch (e: Exception) {}
                                    withContext(Dispatchers.IO) { AppwriteClient.account.deleteSession("current") }
                                    throw Exception(context.getString(R.string.error_not_verified))
                                }
                                var userRole = "VIEWER"
                                try {
                                    val docs = withContext(Dispatchers.IO) {
                                        AppwriteClient.databases.listDocuments(
                                            databaseId = AppwriteClient.DATABASE_ID,
                                            collectionId = AppwriteClient.TABLE_ID,
                                            queries = listOf(io.appwrite.Query.equal("email", email))
                                        )
                                    }
                                    if (docs.documents.isNotEmpty()) userRole = docs.documents.first().data["role"]?.toString() ?: "VIEWER"
                                } catch (e: Exception) {}
                                FakeData.currentUser = User(currentUser.id, currentUser.name, currentUser.email, userRole)
                                isLoading = false
                                onLoginSuccess()
                            } catch (e: Exception) {
                                isLoading = false
                                val msg = e.message ?: ""
                                errorMessage = when {
                                    msg.contains("Invalid `email`") || msg.contains("valid email") -> context.getString(R.string.error_invalid_email)
                                    msg.contains("not verified") -> context.getString(R.string.error_not_verified)
                                    msg.contains("Invalid credentials") || msg.contains("unauthorized") -> context.getString(R.string.error_invalid_credentials)
                                    else -> msg
                                }
                            }
                        }
                    },
                    enabled = !isLoading && isChecked,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.login_button_text), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val layoutDir = LocalLayoutDirection.current
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Checkbox(checked = isChecked, onCheckedChange = { isChecked = it }, colors = CheckboxDefaults.colors(checkedColor = primaryColor, uncheckedColor = textColor.copy(alpha = 0.5f)))
                    if (layoutDir == LayoutDirection.Rtl) {
                        Text(stringResource(id = R.string.terms_link), fontSize = 12.sp, color = primaryColor, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable { onNavigateToPrivacy() })
                        Text(stringResource(id = R.string.terms_suffix), fontSize = 12.sp, color = textColor)
                    } else {
                        Text(stringResource(id = R.string.terms_prefix), fontSize = 12.sp, color = textColor)
                        Text(stringResource(id = R.string.terms_link), fontSize = 12.sp, color = primaryColor, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable { onNavigateToPrivacy() })
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onGuestLogin) { Text(stringResource(id = R.string.guest_login_text), color = textColor.copy(alpha = 0.7f), fontSize = 14.sp) }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(onClick = onNavigateToRegister) { Text(stringResource(id = R.string.register_button_text), color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                }

                Spacer(modifier = Modifier.weight(1f))
                Text("Noghte Security | v1.0.0", fontSize = 10.sp, color = textColor.copy(alpha = 0.5f))
            }
        }
    }
}
