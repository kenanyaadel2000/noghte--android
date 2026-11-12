package com.example.noghte

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases

object AppwriteClient {
    lateinit var client: Client
    lateinit var account: Account
    lateinit var databases: Databases

    // ✅ مقادیر ثابت پروژه (از روی پنل خودت)
    private const val PROJECT_ID = "6af5d79f00263e570ad8"
    private const val ENDPOINT = "https://fra.cloud.appwrite.io/v1"
    
    // ✅ دیتابیس و جدول شما
    const val DATABASE_ID = "6af5dd120018c6538654" 
    const val TABLE_ID = "6af5dd480025a56fc4ba" 

    fun init(context: Context) {
        if (!this::client.isInitialized) {
            client = Client(context)
                .setEndpoint(ENDPOINT)
                .setProject(PROJECT_ID)
            account = Account(client)
            databases = Databases(client)
        }
    }
}
