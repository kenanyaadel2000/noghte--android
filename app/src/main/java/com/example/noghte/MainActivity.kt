package com.example.noghte

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // خواندن وضعیت ورود از حافظه گوشی
        val prefs = getSharedPreferences("NoghtePrefs", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)

        setContent {
            // تم تیره با رنگ اصلی طلایی
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFFD4AF37),
                    background = Color(0xFF121212),
                    surface = Color(0xFF121212)
                )
            ) {
                Surface {
                    AppNavigation(isAlreadyLoggedIn = isLoggedIn)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(isAlreadyLoggedIn: Boolean) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NoghtePrefs", Context.MODE_PRIVATE)

    // اگر قبلا لاگین کرده باشد، مستقیم میرود خانه
    var currentScreen by remember { mutableStateOf(if (isAlreadyLoggedIn) "home" else "login") }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                onLoginSuccess = {
                    // ذخیره وضعیت در حافظه که دفعه بعد لاگین را رد کند
                    prefs.edit().putBoolean("IS_LOGGED_IN", true).apply()
                    currentScreen = "home"
                },
                onNavigateToPrivacy = {
                    currentScreen = "privacy"
                }
            )
        }
        "privacy" -> {
            PrivacyScreen(
                onBackClick = { currentScreen = "login" }
            )
        }
        "home" -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.home_placeholder), 
                    fontSize = 24.sp, 
                    color = Color.White
                )
            }
        }
    }
}
