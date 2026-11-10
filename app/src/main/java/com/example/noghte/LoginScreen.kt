package com.example.noghte

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    isDarkTheme: Boolean, // دریافت وضعیت تم از MainActivity
    onLoginSuccess: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onLanguageChange: () -> Unit
) {
    // استفاده از رنگ‌های تم برای هماهنگی با حالت روشن و تیره
    val primaryGold = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    var isLoading by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }

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

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(3000)
            isLoading = false
            onLoginSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // دکمه تغییر زبان در بالای صفحه
            TextButton(
                onClick = onLanguageChange,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text(
                    text = "🌐 " + stringResource(id = R.string.change_language),
                    color = primaryGold,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // لوگو که بر اساس تم (روشن/تیره) عکسش عوض میشه
                Image(
                    painter = painterResource(id = if (isDarkTheme) R.drawable.ic_logo_dark else R.drawable.ic_logo_light),
                    contentDescription = "Noghte Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale.value)
                        .clip(RoundedCornerShape(32.dp)) 
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.login_subtitle),
                    fontSize = 16.sp,
                    color = textColor.copy(alpha = 0.7f), // کمی محو تر برای زیبایی
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { isLoading = true },
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
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.login_button),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = primaryGold, 
                            uncheckedColor = textColor.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.agree_checkbox),
                        fontSize = 14.sp,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.login_privacy_agree),
                    fontSize = 14.sp,
                    color = primaryGold,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPrivacy() }
                )
            }
        }
    }
}
