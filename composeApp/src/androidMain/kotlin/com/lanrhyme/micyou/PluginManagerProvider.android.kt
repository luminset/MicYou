package com.lanrhyme.micyou

import com.lanrhyme.micyou.plugin.AndroidPluginManager
import com.lanrhyme.micyou.plugin.Plugin
import com.lanrhyme.micyou.plugin.PluginHost
import com.lanrhyme.micyou.plugin.PluginInfo
import com.lanrhyme.micyou.plugin.PluginSettingsProvider
import com.lanrhyme.micyou.plugin.PluginUIProvider
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class AndroidPluginManagerProvider(private val manager: AndroidPluginManager) : PluginManagerProvider {
    override val plugins: StateFlow<List<PluginInfo>> = manager.plugins
    
    override fun scanPlugins() = manager.scanPlugins()
    
    override fun importPlugin(pluginFilePath: String): Result<PluginInfo> = manager.importPlugin(File(pluginFilePath))
    
    override fun enablePlugin(pluginId: String): Result<Unit> = manager.enablePlugin(pluginId)
    
    override fun disablePlugin(pluginId: String): Result<Unit> = manager.disablePlugin(pluginId)
    
    override fun deletePlugin(pluginId: String): Result<Unit> = manager.deletePlugin(pluginId)
    
    override fun getPlugin(pluginId: String): Plugin? = manager.getPlugin(pluginId)
    
    override fun getPluginSettingsProvider(pluginId: String): PluginSettingsProvider? = manager.getPluginSettingsProvider(pluginId)
    
    override fun getPluginUIProvider(pluginId: String): PluginUIProvider? = manager.getPluginUIProvider(pluginId)
}

actual fun createPluginManager(
    pluginsDirPath: String,
    pluginHost: PluginHost,
    appLanguageProvider: () -> String,
    appStringProvider: ((String) -> String)?
): PluginManagerProvider? {
    return AndroidPluginManagerProvider(
        AndroidPluginManager(
            pluginsDir = File(pluginsDirPath),
            pluginHost = pluginHost,
            appLanguageProvider = appLanguageProvider,
            appStringProvider = appStringProvider
        )
    )
}

actual fun getPluginsDirPath(): String {
    val context = ContextHelper.getContext()
        ?: return ""
    return File(context.filesDir, "plugins").absolutePath
}
