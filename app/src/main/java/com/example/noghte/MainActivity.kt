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
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppwriteClient.init(applicationContext)
        // شروع اتصال به Appwrit
        
        val prefs = getSharedPreferences("NoghtePrefs", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)
        val appLanguage = prefs.getString("APP_LANG", "fa") ?: "fa"
        
        val locale = Locale(appLanguage)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        setContent {
            // وضعیت تم (پیش‌فرض تیره است)
            var isDarkTheme by remember { mutableStateOf(prefs.getBoolean("IS_DARK_THEME", true)) }

            val layoutDirection = if (appLanguage == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr

            // تعریف پالت رنگی تیره
            val darkColors = darkColorScheme(
                primary = Color(0xFFD4AF37),
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                onBackground = Color.White,
                onSurface = Color.White
            )

            // تعریف پالت رنگی روشن
            val lightColors = lightColorScheme(
                primary = Color(0xFFD4AF37),
                background = Color(0xFFF9F9F9),
                surface = Color.White,
                onBackground = Color.Black,
                onSurface = Color.Black
            )

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MaterialTheme(
                    colorScheme = if (isDarkTheme) darkColors else lightColors
                ) {
                    Surface {
                        AppNavigation(
                            isAlreadyLoggedIn = isLoggedIn,
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { 
                                isDarkTheme = it
                                prefs.edit().putBoolean("IS_DARK_THEME", it).apply()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    isAlreadyLoggedIn: Boolean,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("NoghtePrefs", Context.MODE_PRIVATE)
    var currentScreen by remember { mutableStateOf(if (isAlreadyLoggedIn) "home" else "login") }
    var selectedResourceId by remember { mutableStateOf<String?>(null) }

    val changeLanguage: () -> Unit = {
        val currentLang = prefs.getString("APP_LANG", "fa")
        val newLang = if (currentLang == "fa") "en" else "fa"
        prefs.edit().putString("APP_LANG", newLang).apply()
        
        val locale = Locale(newLang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        
        (context as? ComponentActivity)?.recreate()
    }

    val toggleTheme: () -> Unit = {
        onThemeChange(!isDarkTheme)
    }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                isDarkTheme = isDarkTheme,
                onLoginSuccess = {
                    prefs.edit().putBoolean("IS_LOGGED_IN", true).apply()
                    currentScreen = "home"
                },
                onNavigateToPrivacy = { currentScreen = "privacy" },
                onLanguageChange = changeLanguage,
                onThemeToggle = toggleTheme,
                onNavigateToRegister = { currentScreen = "register" } // ✅ رفتن به ثبت نام
            )
        }
        "register" -> {
            RegisterScreen(onBackClick = { currentScreen = "login" }) // ✅ صفحه ثبت نام
        }
        "privacy" -> {
            PrivacyScreen(onBackClick = { currentScreen = "login" })
        }
        "home" -> {
            ResourceListScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateToSettings = { currentScreen = "settings" },
                onResourceClick = { id ->
                    selectedResourceId = id
                    currentScreen = "detail"
                },
                onLogout = {
                    prefs.edit().putBoolean("IS_LOGGED_IN", false).apply()
                    currentScreen = "login"
                }
            )
        }
        "settings" -> {
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onLanguageChange = changeLanguage,
                onBackClick = { currentScreen = "home" }
            )
        }
        "detail" -> {
            ResourceDetailScreen(
                resourceId = selectedResourceId ?: "",
                onBackClick = { currentScreen = "home" }
            )
        }
    }
}
