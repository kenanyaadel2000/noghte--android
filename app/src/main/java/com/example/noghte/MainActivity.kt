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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

            // 🎨 پالت رنگی تیره (طبق PDF استاد: سبز فیروزه‌ای و بنفش)
            val darkColors = darkColorScheme(
                primary = Color(0xFF00897B), // سبز فیروزه‌ای
                secondary = Color(0xFF7F52FF), // بنفش
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                onBackground = Color.White,
                onSurface = Color.White
            )

            // 🎨 پالت رنگی روشن (طبق PDF استاد: سبز فیروزه‌ای و بنفش)
            val lightColors = lightColorScheme(
                primary = Color(0xFF00897B), // سبز فیروزه‌ای
                secondary = Color(0xFF7F52FF), // بنفش
                background = Color(0xFFF9F9F9),
                surface = Color.White,
                onBackground = Color.Black,
                onSurface = Color.Black
            )

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MaterialTheme(
                    colorScheme = if (isDarkTheme) darkColors else lightColors
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
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
                onNavigateToAdminPanel = { currentScreen = "admin_panel" },
                onBackClick = { currentScreen = "home" }
            )
        }
        
        "admin_panel" -> {
            AdminPanelScreen(
                onBackClick = { currentScreen = "settings" }
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
