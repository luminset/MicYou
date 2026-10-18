package com.lanrhyme.micyou.audio
import com.lanrhyme.micyou.R

import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.StringRes
import com.lanrhyme.micyou.audio.AndroidAudioSource
import com.lanrhyme.micyou.audio.AudioSourceOption
import com.lanrhyme.micyou.audio.getAudioSourceOptions

enum class AndroidAudioSource(@StringRes val labelRes: Int, val sourceId: Int, val minApiLevel: Int = 0) {
    Mic(R.string.audioSourceMic, MediaRecorder.AudioSource.MIC),
    VoiceCommunication(R.string.audioSourceVoiceCommunication, MediaRecorder.AudioSource.VOICE_COMMUNICATION),
    VoiceRecognition(R.string.audioSourceVoiceRecognition, MediaRecorder.AudioSource.VOICE_RECOGNITION),
    // VOICE_PERFORMANCE 需要 API 29+，使用硬编码值 10 避免低版本 NoClassDefFoundError
    VoicePerformance(R.string.audioSourceVoicePerformance, 10, minApiLevel = 29),
    Camcorder(R.string.audioSourceCamcorder, MediaRecorder.AudioSource.CAMCORDER),
    // UNPROCESSED 需要 API 24+，使用硬编码值 9 避免低版本崩溃
    Unprocessed(R.string.audioSourceUnprocessed, 9, minApiLevel = 24);

    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= minApiLevel
    }
}

data class AudioSourceOption(
    val name: String,
    @StringRes val labelRes: Int? = null,
    val label: String? = null
)

fun getAudioSourceOptions(): List<AudioSourceOption> {
    return AndroidAudioSource.entries
        .filter { it.isSupported() }
        .map { AudioSourceOption(it.name, it.labelRes) }
}