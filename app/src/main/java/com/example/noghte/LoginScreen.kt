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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
    val primaryGold = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val scale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLanguageChange) {
                    Icon(Icons.Default.Language, contentDescription = "Language", tint = primaryGold)
                }
                IconButton(onClick = onThemeToggle) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Theme",
                        tint = primaryGold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = if (isDarkTheme) R.drawable.ic_logo_dark else R.drawable.ic_logo_light),
                    contentDescription = "Noghte Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale.value)
                        .clip(RoundedCornerShape(32.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGold
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ✅ استفاده از stringResource برای برچسب‌ها
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.email_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryGold, focusedLabelColor = primaryGold),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(id = R.string.password_label)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryGold, focusedLabelColor = primaryGold),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { 
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "لطفاً ایمیل و رمز عبور را وارد کنید."
                            return@Button
                        }
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    AppwriteClient.account.createEmailPasswordSession(email, password)
                                }
                                
                                val currentUser = withContext(Dispatchers.IO) {
                                    AppwriteClient.account.get()
                                }

                                if (!currentUser.emailVerification) {
                                    try {
                                        withContext(Dispatchers.IO) {
                                            AppwriteClient.account.createVerification(url = "https://noghte.com/verify")
                                        }
                                    } catch (e: Exception) {}
                                    
                                    withContext(Dispatchers.IO) {
                                        AppwriteClient.account.deleteSession("current")
                                    }
                                    
                                    throw Exception("حساب شما هنوز توسط ادمین تایید نشده است. لطفا منتظر بمانید تایید شود.")
                                }

                                FakeData.currentUser = User(
                                    id = currentUser.id,
                                    name = currentUser.name,
                                    email = currentUser.email,
                                    role = "VIEWER" 
                                )

                                isLoading = false
                                onLoginSuccess()
                            } catch (e: Exception) {
                                isLoading = false
                                // ✅ ترجمه ارورهای Appwrite به فارسی/انگلیسی
                                val msg = e.message ?: ""
                                errorMessage = when {
                                    msg.contains("Invalid `email`") || msg.contains("valid email") -> "فرمت ایمیل وارد شده صحیح نیست.\nInvalid email format."
                                    msg.contains("not verified") || msg.contains("تایید نشده") -> "حساب شما هنوز توسط ادمین تایید نشده است.\nAccount not approved by admin."
                                    msg.contains("Invalid credentials") || msg.contains("unauthorized") -> "ایمیل یا رمز عبور اشتباه است.\nInvalid credentials."
                                    else -> msg
                                }
                            }
                        }
                    },
                    enabled = !isLoading && isChecked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryGold,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
                        disabledContentColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(id = R.string.login_button_text), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // ✅ جایگاه چک‌باکس و قوانین (دقیقاً بالای دکمه‌ها)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = primaryGold, uncheckedColor = textColor.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // کلمه "قوانین" کلیک‌پذیر و طلایی
                    Text(
                        text = stringResource(id = R.string.terms_link),
                        fontSize = 14.sp,
                        color = primaryGold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onNavigateToPrivacy() }
                    )
                    // ادامه جمله "را می‌پذیرم"
                    Text(
                        text = stringResource(id = R.string.terms_suffix),
                        fontSize = 14.sp,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ دکمه‌های پایین (مهمان و ثبت‌نام)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onGuestLogin) {
                        Text(text = stringResource(id = R.string.guest_login_text), color = textColor.copy(alpha = 0.7f), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(onClick = onNavigateToRegister) {
                        Text(text = stringResource(id = R.string.register_button_text), color = primaryGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
