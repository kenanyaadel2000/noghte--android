package com.example.noghte

import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalLayoutDirection // ✅ اضافه شد
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection // ✅ اضافه شد
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("NoghtePrefs", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)
        val appLanguage = prefs.getString("APP_LANG", "fa") ?: "fa"
        
        val locale = Locale(appLanguage)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        setContent {
            val layoutDirection = if (appLanguage == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
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
}

@Composable
fun AppNavigation(isAlreadyLoggedIn: Boolean) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NoghtePrefs", Context.MODE_PRIVATE)
    var currentScreen by remember { mutableStateOf(if (isAlreadyLoggedIn) "home" else "login") }

    val changeLanguage = {
        val currentLang = prefs.getString("APP_LANG", "fa")
        val newLang = if (currentLang == "fa") "en" else "fa"
        prefs.edit().putString("APP_LANG", newLang).apply()
        
        val locale = Locale(newLang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        
        (context as? ComponentActivity)?.recreate()
    }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                onLoginSuccess = {
                    prefs.edit().putBoolean("IS_LOGGED_IN", true).apply()
                    currentScreen = "home"
                },
                onNavigateToPrivacy = { currentScreen = "privacy" },
                onLanguageChange = changeLanguage
            )
        }
        "privacy" -> {
            PrivacyScreen(onBackClick = { currentScreen = "login" })
        }
        "home" -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = R.string.home_placeholder), fontSize = 24.sp, color = Color.White)
            }
        }
    }
}
