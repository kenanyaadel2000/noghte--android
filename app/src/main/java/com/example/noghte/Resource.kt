package com.example.noghte

data class Resource(
    val id: String,
    val name: String,
    val type: String, // "API", "WEB_APP", "DATABASE", "GPU"
    val description: String,
    val accessLevel: String, // "READ", "FULL"
    val allowedActions: List<String> = emptyList() // جدید
)
