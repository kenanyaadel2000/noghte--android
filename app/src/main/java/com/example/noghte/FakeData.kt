package com.example.noghte

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val hasRequestedAccess: Boolean = false,
    val twoFactorEnabled: Boolean = false
)

data class Resource(
    val id: String,
    @StringRes val nameRes: Int,
    val type: String,
    @StringRes val descRes: Int,
    val requiredRole: String,
    val lastAccess: String,
    val status: String,
    val allowedActions: List<String> = emptyList()
)

data class AccessLog(
    val id: String,
    @StringRes val actionRes: Int,
    val resourceName: String,
    val status: String, // ALLOWED, DENIED, PENDING
    val timestamp: String,
    val type: String // LOGIN, LOGOUT, REQUEST, JOB
)

object FakeData {
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

    val resources = listOf(
        Resource("1", R.string.res_fin_name, "API", R.string.res_fin_desc, "viewer", "۱۰:۳۰ - ۱۴۰۳/۰۵/۲۵", "ACTIVE", listOf("GET /api/v1/transactions", "GET /api/v1/accounts", "GET /api/v1/reports")),
        Resource("2", R.string.res_admin_name, "WEB_APP", R.string.res_admin_desc, "admin", "۱۰:۱۵ - ۱۴۰۳/۰۵/۲۵", "ACTIVE", listOf("GET /admin/dashboard", "POST /admin/users", "DELETE /admin/logs")),
        Resource("3", R.string.res_db_name, "DATABASE", R.string.res_db_desc, "viewer", "۰۹:۰۰ - ۱۴۰۳/۰۵/۲۴", "ACTIVE", listOf("SELECT * FROM reports", "SELECT * FROM metrics")),
        Resource("4", R.string.res_gpu_name, "GPU", R.string.res_gpu_desc, "admin", "۰۸:۳۰ - ۱۴۰۳/۰۵/۲۴", "INACTIVE", listOf("POST /api/v1/jobs/train", "GET /api/v1/jobs/status"))
    )

    // ✅ استفاده از mutableStateListOf برای اینکه بشه لاگ جدید اضافه کرد
    val logs = mutableStateListOf(
        AccessLog("1", R.string.log_login, "OIDC Login", "ALLOWED", "۱۰:۳۰ - ۱۴۰۳/۰۵/۲۵", "LOGIN"),
        AccessLog("2", R.string.log_req_access, "Financial API", "ALLOWED", "۱۰:۳۲ - ۱۴۰۳/۰۵/۲۵", "REQUEST"),
        AccessLog("3", R.string.log_view_dash, "Admin Panel", "DENIED", "۱۰:۴۵ - ۱۴۰۳/۰۵/۲۵", "REQUEST"),
        AccessLog("4", R.string.log_run_job, "GPU Cluster", "ALLOWED", "۱۱:۰۰ - ۱۴۰۳/۰۵/۲۵", "JOB"),
        AccessLog("5", R.string.log_logout, "OIDC Logout", "ALLOWED", "۱۱:۱۵ - ۱۴۰۳/۰۵/۲۵", "LOGOUT")
    )
}
