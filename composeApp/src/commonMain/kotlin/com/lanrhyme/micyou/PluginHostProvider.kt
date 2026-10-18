package com.lanrhyme.micyou

import com.lanrhyme.micyou.plugin.PluginHost

expect fun createPluginHost(
    audioEngine: AudioEngine,
    showSnackbarCallback: (String) -> Unit,
    showNotificationCallback: (String, String) -> Unit
): PluginHost
