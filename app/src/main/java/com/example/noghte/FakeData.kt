package com.example.noghte

object FakeData {
    val resources = listOf(
        Resource(
            id = "1", 
            name = "Financial API", 
            type = "API", 
            description = "دسترسی به API مالی برای گزارش‌گیری و تراکنش‌ها. این API امکان دریافت گزارش‌های مالی، مشاهده تراکنش‌ها و مدیریت حساب‌ها را فراهم می‌کند.", 
            accessLevel = "READ",
            allowedActions = listOf("GET /api/v1/transactions", "GET /api/v1/accounts", "GET /api/v1/reports")
        ),
        Resource(
            id = "2", 
            name = "Admin Panel", 
            type = "WEB_APP", 
            description = "پنل مدیریت سیستم برای کاربران با نقش ادمین. این پنل دسترسی کامل به تنظیمات سیستم و مدیریت کاربران را می‌دهد.", 
            accessLevel = "FULL",
            allowedActions = listOf("GET /admin/dashboard", "POST /admin/users", "DELETE /admin/logs")
        ),
        Resource(
            id = "3", 
            name = "Analytics DB", 
            type = "DATABASE", 
            description = "دیتابیس تحلیلی برای کوئری‌های گزارش‌گیری سنگین و پردازش داده‌های کلان.", 
            accessLevel = "READ",
            allowedActions = listOf("SELECT * FROM reports", "SELECT * FROM metrics")
        ),
        Resource(
            id = "4", 
            name = "GPU Cluster", 
            type = "GPU", 
            description = "خوشه پردازشی برای اجرای مدل‌های هوش مصنوعی و آموزش (Training) مدل‌ها.", 
            accessLevel = "FULL",
            allowedActions = listOf("POST /api/v1/jobs/train", "GET /api/v1/jobs/status")
        )
    )
}
