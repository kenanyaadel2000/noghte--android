package com.example.noghte

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToPrivacy: () -> Unit
) {
    val primaryGold = Color(0xFFD4AF37)
    val darkBackground = Color(0xFF121212)
    val textColor = Color.White

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
        color = darkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
                    .background(color = primaryGold, shape = RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎯", fontSize = 60.sp)
            }

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
                color = Color.LightGray,
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
                    colors = CheckboxDefaults.colors(checkedColor = primaryGold, uncheckedColor = Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.agree_checkbox),
                    fontSize = 14.sp,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // این بخش تغییر کرد تا دقیقاً در یک خط و وسط‌چین باشد
            Text(
                text = stringResource(id = R.string.login_privacy_agree),
                fontSize = 14.sp,
                color = primaryGold,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                maxLines = 1, // اجبار به ماندن در یک خط
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPrivacy() }
            )
        }
    }
}
