package com.example.noghte

import androidx.annotation.StringRes

// ۱. مدل کاربر با فیلدهای جدید
data class User(
    val id: String, // آیدی Appwrite
    val name: String,
    val email: String,
    val role: String, // CEO, ADMIN, VIEWER
    val hasRequestedAccess: Boolean = false,
    val twoFactorEnabled: Boolean = false
)

// ۲. مدل منابع با فیلدهای جدید
data class Resource(
    val id: String,
    @StringRes val nameRes: Int,
    val type: String, // API, WEB_APP, DATABASE, GPU
    @StringRes val descRes: Int,
    val requiredRole: String, // نقش مورد نیاز (مثلا VIEWER)
    val lastAccess: String, // مثلا "۱۰:۳۰ - ۱۴۰۳/۰۵/۲۵"
    val status: String, // "ACTIVE" یا "INACTIVE"
    val allowedActions: List<String> = emptyList()
)

// ۳. مدل لاگ‌ها
data class AccessLog(
    val id: String,
    @StringRes val actionRes: Int,
    val resourceName: String,
    val status: String, // ALLOWED, DENIED
    val timestamp: String,
    val type: String // LOGIN, LOGOUT, REQUEST, JOB
)

object FakeData {
    // کاربر فعلی (حالا با شناسه رندوم ثابت و وضعیت 2FA)
    var currentUser = User(
        id = "usr_8f3a2b1c", 
        name = "علی محمدی", 
        email = "ali.mohammadi@company.com", 
        role = "CEO",
        hasRequestedAccess = false,
        twoFactorEnabled = true
    )

    val users = mutableListOf(
        currentUser,
        User("usr_2a4b6c8d", "رضا کریمی", "reza@company.com", "ADMIN"),
        User("usr_9e8d7c6b", "سارا احمدی", "sara@company.com", "VIEWER", hasRequestedAccess = true),
        User("usr_1f2e3d4c", "محمد رضایی", "mohammad@company.com", "VIEWER")
    )

    // منابع جدید طبق لیست تو و طرح استاد
    val resources = listOf(
        Resource(
            id = "1",
            nameRes = R.string.res_fin_name,
            type = "API",
            descRes = R.string.res_fin_desc,
            requiredRole = "viewer",
            lastAccess = "۱۰:۳۰ - ۱۴۰۳/۰۵/۲۵",
            status = "ACTIVE",
            allowedActions = listOf("GET /api/v1/transactions", "GET /api/v1/accounts", "GET /api/v1/reports")
        ),
        Resource(
            id = "2",
            nameRes = R.string.res_admin_name,
            type = "WEB_APP",
            descRes = R.string.res_admin_desc,
            requiredRole = "admin",
            lastAccess = "۱۰:۱۵ - ۱۴۰۳/۰۵/۲۵",
            status = "ACTIVE",
            allowedActions = listOf("GET /admin/dashboard", "POST /admin/users", "DELETE /admin/logs")
        ),
        Resource(
            id = "3",
            nameRes = R.string.res_db_name,
            type = "DATABASE",
            descRes = R.string.res_db_desc,
            requiredRole = "viewer",
            lastAccess = "۰۹:۰۰ - ۱۴۰۳/۰۵/۲۴",
            status = "ACTIVE",
            allowedActions = listOf("SELECT * FROM reports", "SELECT * FROM metrics")
        ),
        Resource(
            id = "4",
            nameRes = R.string.res_gpu_name,
            type = "GPU",
            descRes = R.string.res_gpu_desc,
            requiredRole = "admin",
            lastAccess = "۰۸:۳۰ - ۱۴۰۳/۰۵/۲۴",
            status = "INACTIVE",
            allowedActions = listOf("POST /api/v1/jobs/train", "GET /api/v1/jobs/status")
        )
    )

    // لاگ‌ها با فیلد Type برای فیلتر کردن
    val logs = listOf(
        AccessLog("1", R.string.log_login, "OIDC Login", "ALLOWED", "۱۰:۳۰ - ۱۴۰۳/۰۵/۲۵", "LOGIN"),
        AccessLog("2", R.string.log_req_access, "Financial API", "ALLOWED", "۱۰:۳۲ - ۱۴۰۳/۰۵/۲۵", "REQUEST"),
        AccessLog("3", R.string.log_view_dash, "Admin Panel", "DENIED", "۱۰:۴۵ - ۱۴۰۳/۰۵/۲۵", "REQUEST"),
        AccessLog("4", R.string.log_run_job, "GPU Cluster", "ALLOWED", "۱۱:۰۰ - ۱۴۰۳/۰۵/۲۵", "JOB"),
        AccessLog("5", R.string.log_logout, "OIDC Logout", "ALLOWED", "۱۱:۱۵ - ۱۴۰۳/۰۵/۲۵", "LOGOUT")
    )
}
