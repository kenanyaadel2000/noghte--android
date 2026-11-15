package com.example.noghte

import androidx.annotation.StringRes

data class Resource(
    val id: String,
    @StringRes val nameRes: Int, // ✅ استفاده از آیدی متن
    val type: String,
    @StringRes val descRes: Int, // ✅ استفاده از آیدی متن
    val accessLevel: String,
    val allowedActions: List<String> = emptyList()
)

data class AccessLog(
    val id: String,
    @StringRes val actionRes: Int, // ✅ استفاده از آیدی متن
    val resourceName: String,
    val status: String,
    val timestamp: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String 
)

object FakeData {
    var currentUser = User("1", "علی محمدی", "ali@company.com", "CEO")

    val users = mutableListOf(
        currentUser,
        User("2", "رضا کریمی", "reza@company.com", "MANAGER"),
        User("3", "سارا احمدی", "sara@company.com", "VIEWER"),
        User("4", "محمد رضایی", "mohammad@company.com", "VIEWER")
    )

    val resources = listOf(
        Resource("1", R.string.res_fin_name, "API", R.string.res_fin_desc, "READ", listOf("GET /api/v1/transactions", "GET /api/v1/accounts", "GET /api/v1/reports")),
        Resource("2", R.string.res_admin_name, "WEB_APP", R.string.res_admin_desc, "FULL", listOf("GET /admin/dashboard", "POST /admin/users", "DELETE /admin/logs")),
        Resource("3", R.string.res_db_name, "DATABASE", R.string.res_db_desc, "READ", listOf("SELECT * FROM reports", "SELECT * FROM metrics")),
        Resource("4", R.string.res_gpu_name, "GPU", R.string.res_gpu_desc, "FULL", listOf("POST /api/v1/jobs/train", "GET /api/v1/jobs/status"))
    )

    val logs = listOf(
        AccessLog("1", R.string.log_login, "OIDC Login", "ALLOWED", "۱۰:۳۰ - ۱۴۰۳/۰۵/۲۵"),
        AccessLog("2", R.string.log_req_access, "Financial API", "ALLOWED", "۱۰:۳۲ - ۱۴۰۳/۰۵/۲۵"),
        AccessLog("3", R.string.log_view_dash, "Admin Panel", "DENIED", "۱۰:۴۵ - ۱۴۰۳/۰۵/۲۵"),
        AccessLog("4", R.string.log_run_job, "GPU Cluster", "ALLOWED", "۱۱:۰۰ - ۱۴۰۳/۰۵/۲۵"),
        AccessLog("5", R.string.log_logout, "OIDC Logout", "ALLOWED", "۱۱:۱۵ - ۱۴۰۳/۰۵/۲۵")
    )
}
