package com.lanrhyme.micyou

import android.media.MediaRecorder
import android.os.Build

enum class AndroidAudioSource(val label: String, val sourceId: Int, val minApiLevel: Int = 0) {
    Mic("MIC (默认)", MediaRecorder.AudioSource.MIC),
    VoiceCommunication("VOICE_COMMUNICATION (VoIP优化)", MediaRecorder.AudioSource.VOICE_COMMUNICATION),
    VoiceRecognition("VOICE_RECOGNITION (语音识别)", MediaRecorder.AudioSource.VOICE_RECOGNITION),
    // VOICE_PERFORMANCE 需要 API 29+，使用硬编码值避免低版本崩溃
    VoicePerformance("VOICE_PERFORMANCE (低延迟)", 10, minApiLevel = 29),
    Camcorder("CAMCORDER (摄像机)", MediaRecorder.AudioSource.CAMCORDER),
    // UNPROCESSED 需要 API 24+，使用硬编码值避免低版本崩溃
    Unprocessed("UNPROCESSED (原始音频)", 9, minApiLevel = 24);

    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= minApiLevel
    }

    companion object {
        /**
         * 获取当前设备支持的音频源列表
         */
        fun getSupportedSources(): List<AndroidAudioSource> {
            return entries.filter { it.isSupported() }
        }
    }
}
