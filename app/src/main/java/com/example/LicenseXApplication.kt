package com.example

import android.app.Application
import com.example.data.local.LicenseXDatabase
import com.example.data.repository.LicenseXRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LicenseXApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: LicenseXDatabase by lazy { LicenseXDatabase.getInstance(this) }
    val repository: LicenseXRepository by lazy { LicenseXRepository(this, database) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            repository.initialize()
        }
    }
}
