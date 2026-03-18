package com.kyle.nanogptapp.data.settings

import android.content.Context

object SettingsGraph {
    private var repository: SettingsRepository? = null

    fun repository(context: Context): SettingsRepository {
        return synchronized(this) {
            repository ?: AndroidSettingsRepository(context).also { repository = it }
        }
    }
}
