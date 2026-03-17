package com.kyle.nanogptapp.data.settings

import android.content.Context

object SettingsGraph {
    @Volatile
    private var repository: SettingsRepository? = null

    fun repository(context: Context): SettingsRepository {
        return repository ?: synchronized(this) {
            repository ?: SettingsRepository(context).also { repository = it }
        }
    }
}
