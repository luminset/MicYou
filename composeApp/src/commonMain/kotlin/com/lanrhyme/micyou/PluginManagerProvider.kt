package com.lanrhyme.micyou

import com.lanrhyme.micyou.plugin.PluginHost
import com.lanrhyme.micyou.plugin.PluginInfo
import com.lanrhyme.micyou.plugin.PluginUIProvider
import kotlinx.coroutines.flow.StateFlow

interface PluginManagerProvider {
    val plugins: StateFlow<List<PluginInfo>>
    fun scanPlugins()
    fun importPlugin(pluginFilePath: String): Result<PluginInfo>
    fun enablePlugin(pluginId: String): Result<Unit>
    fun disablePlugin(pluginId: String): Result<Unit>
    fun deletePlugin(pluginId: String): Result<Unit>
    fun getPlugin(pluginId: String): Any?
    fun getPluginSettingsProvider(pluginId: String): Any?
    fun getPluginUIProvider(pluginId: String): PluginUIProvider?
}

expect fun createPluginManager(
    pluginsDirPath: String,
    pluginHost: PluginHost,
    appLanguageProvider: () -> String = { "en" },
    appStringProvider: ((String) -> String)? = null
): PluginManagerProvider?

expect fun getPluginsDirPath(): String
