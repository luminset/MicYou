package com.lanrhyme.micyou

import com.lanrhyme.micyou.plugin.AndroidPluginHostImpl
import com.lanrhyme.micyou.plugin.PluginHost

actual fun createPluginHost(
    audioEngine: AudioEngine,
    showSnackbarCallback: (String) -> Unit,
    showNotificationCallback: (String, String) -> Unit
): PluginHost {
    return AndroidPluginHostImpl(
        audioEngine = audioEngine,
        showSnackbarCallback = showSnackbarCallback,
        showNotificationCallback = showNotificationCallback
    )
}
