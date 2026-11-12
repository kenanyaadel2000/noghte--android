package com.example.noghte

data class AccessLog(
    val id: String,
    val action: String,
    val resourceName: String,
    val status: String,
    val timestamp: String
)

// مدل جدید برای کاربران
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String // "CEO", "MANAGER", "VIEWER"
)

object FakeData {
    // کاربر فعلی که لاگین کرده (فرض میکنیم مدیرعامل است)
    var currentUser = User("1", "علی محمدی", "ali@company.com", "CEO")

    // لیست کاربران برای پنل مدیریت
    val users = mutableListOf(
        currentUser,
        User("2", "رضا کریمی", "reza@company.com", "MANAGER"),
        User("3", "سارا احمدی", "sara@company.com", "VIEWER"),
        User("4", "محمد رضایی", "mohammad@company.com", "VIEWER")
    )

    val resources = listOf(
        Resource("1", "Financial API", "API", "دسترسی به API مالی برای گزارش‌گیری و تراکنش‌ها. این API امکان دریافت گزارش‌های مالی، مشاهده تراکنش‌ها و مدیریت حساب‌ها را فراهم می‌کند.", "READ", listOf("GET /api/v1/transactions", "GET /api/v1/accounts", "GET /api/v1/reports")),
        Resource("2", "Admin Panel", "WEB_APP", "پنل مدیریت سیستم برای کاربران با نقش ادمین. این پنل دسترسی کامل به تنظیمات سیستم و مدیریت کاربران را می‌دهد.", "FULL", listOf("GET /admin/dashboard", "POST /admin/users", "DELETE /admin/logs")),
        Resource("3", "Analytics DB", "DATABASE", "دیتابیس تحلیلی برای کوئری‌های گزارش‌گیری سنگین و پردازش داده‌های کلان.", "READ", listOf("SELECT * FROM reports", "SELECT * FROM metrics")),
        Resource("4", "GPU Cluster", "GPU", "خوشه پردازشی برای اجرای مدل‌های هوش مصنوعی و آموزش (Training) مدل‌ها.", "FULL", listOf("POST /api/v1/jobs/train", "GET /api/v1/jobs/status"))
    )

    val logs = listOf(
        AccessLog("1", "ورود به سیستم", "OIDC Login", "ALLOWED", "۱۰:۳۰ - ۱۴۰۳/۰۵/۲۵"),
        AccessLog("2", "درخواست دسترسی", "Financial API", "ALLOWED", "۱۰:۳۲ - ۱۴۰۳/۰۵/۲۵"),
        AccessLog("3", "مشاهده داشبورد", "Admin Panel", "DENIED", "۱۰:۴۵ - ۱۴۰۳/۰۵/۲۵"),
        AccessLog("4", "اجرای Job", "GPU Cluster", "ALLOWED", "۱۱:۰۰ - ۱۴۰۳/۰۵/۲۵"),
        AccessLog("5", "خروج از سیستم", "OIDC Logout", "ALLOWED", "۱۱:۱۵ - ۱۴۰۳/۰۵/۲۵")
    )
}
