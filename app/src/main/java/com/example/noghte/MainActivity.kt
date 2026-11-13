package com.example.noghte

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ۱. شروع اتصال به Appwrite
        AppwriteClient.init(applicationContext)

        // ۲. خواندن تنظیمات ذخیره شده (زبان، تم و وضعیت لاگین)
        val prefs = getSharedPreferences("NoghtePrefs", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)
        val appLanguage = prefs.getString("APP_LANG", "fa") ?: "fa"
        
        // ۳. اعمال زبان
        val locale = Locale(appLanguage)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        setContent {
            // ۴. وضعیت تم (پیش‌فرض تیره است)
            var isDarkTheme by remember { mutableStateOf(prefs.getBoolean("IS_DARK_THEME", true)) }

            // ۵. تعیین جهت چیدمان (راست‌چین یا چپ‌چین) بر اساس زبان
            val layoutDirection = if (appLanguage == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr

            // 🎨 تعریف پالت رنگی تیره (همون تم فعلی)
            val darkColors = darkColorScheme(
                primary = Color(0xFFD4AF37), // طلایی
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                onBackground = Color.White,
                onSurface = Color.White
            )

            // 🎨 تعریف پالت رنگی روشن
            val lightColors = lightColorScheme(
                primary = Color(0xFFD4AF37), // طلایی
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
    
    // وضعیت صفحه فعلی
    var currentScreen by remember { mutableStateOf(if (isAlreadyLoggedIn) "home" else "login") }
    
    // متغیرهای موقت برای نگه داشتن اطلاعات بین صفحه‌ها
    var selectedResourceId by remember { mutableStateOf<String?>(null) }
    var pendingUserEmail by remember { mutableStateOf("") }

    // تابع تغییر زبان
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

    // تابع تغییر تم
    val toggleTheme: () -> Unit = {
        onThemeChange(!isDarkTheme)
    }

    // ناوبری بین صفحات
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
                onNavigateToRegister = { currentScreen = "register" },
                onGuestLogin = {
                    // ورود مهمان: کاربر فیک VIEWER
                    FakeData.currentUser = User("guest", "کاربر مهمان", "guest@noghte.com", "VIEWER")
                    prefs.edit().putBoolean("IS_LOGGED_IN", true).apply()
                    currentScreen = "home"
                }
            )
        }
        
        "register" -> {
            RegisterScreen(
                onBackClick = { currentScreen = "login" },
                onNavigateToEmailSent = { email ->
                    pendingUserEmail = email
                    currentScreen = "email_sent"
                }
            )
        }
        
        "email_sent" -> {
            EmailSentScreen(
                email = pendingUserEmail,
                onBackToLogin = { currentScreen = "login" }
            )
        }
        
        "privacy" -> {
            PrivacyScreen(
                onBackClick = { currentScreen = "login" }
            )
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
                    // پاک کردن وضعیت لاگین و رفتن به صفحه ورود
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
