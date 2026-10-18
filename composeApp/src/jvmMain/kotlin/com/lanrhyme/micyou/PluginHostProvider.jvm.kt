package com.lanrhyme.micyou

import com.lanrhyme.micyou.plugin.PluginHost
import com.lanrhyme.micyou.plugin.PluginHostImpl

actual fun createPluginHost(
    audioEngine: AudioEngine,
    showSnackbarCallback: (String) -> Unit,
    showNotificationCallback: (String, String) -> Unit
): PluginHost {
    return PluginHostImpl(
        audioEngine = audioEngine,
        showSnackbarCallback = showSnackbarCallback,
        showNotificationCallback = showNotificationCallback,
        isDesktop = true
    )
}
