package com.lanrhyme.micyou.ui.background

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import com.lanrhyme.micyou.ui.background.BackgroundSettings
import com.lanrhyme.micyou.util.ContextHelper
import com.lanrhyme.micyou.util.Logger
import kotlin.coroutines.resume

data class BackgroundSettings(
    val imagePath: String = "",
    val brightness: Float = 0.5f,
    val blurRadius: Float = 0f,
    val cardOpacity: Float = 1f,
    val enableHazeEffect: Boolean = false
) {
    val hasCustomBackground: Boolean
        get() = imagePath.isNotEmpty()
}

object BackgroundImagePicker {
    // 由 MainActivity 设置: 启动图片选择器
    @Volatile
    var launchImagePicker: (() -> Unit)? = null

    // 当前等待结果的回调
    @Volatile
    private var pendingCallback: ((String?) -> Unit)? = null

    // 由 MainActivity 的 ActivityResultLauncher 回调调用
    fun onImagePicked(uri: String?) {
        pendingCallback?.invoke(uri)
        pendingCallback = null
    }

    fun pickImage(scope: CoroutineScope, onResult: (String?) -> Unit) {
        scope.launch {
            try {
                val launcher = launchImagePicker
                if (launcher == null) {
                    Logger.e("BackgroundImagePicker", "Image picker launcher not registered")
                    onResult(null)
                    return@launch
                }

                // 挂起协程，等待用户选择图片
                val uriStr = suspendCancellableCoroutine { cont ->
                    pendingCallback = { uri -> cont.resume(uri) }
                    launcher()
                }

                if (uriStr == null) {
                    onResult(null)
                    return@launch
                }

                val uri = Uri.parse(uriStr)
                val savedPath = copyToInternalStorage(uri)
                onResult(savedPath)
            } catch (e: Exception) {
                Logger.e("BackgroundImagePicker", "Failed to pick image", e)
                onResult(null)
            }
        }
    }

    private suspend fun copyToInternalStorage(uri: Uri): String? {
        return try {
            val context = ContextHelper.getContext() ?: return null
            val extension = getExtensionFromUri(context, uri)
            val backgroundDir = File(context.filesDir, "backgrounds")
            if (!backgroundDir.exists()) {
                backgroundDir.mkdirs()
            }
            val fileName = "custom_background.$extension"
            val outputFile = File(backgroundDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            outputFile.absolutePath
        } catch (e: Exception) {
            Logger.e("BackgroundImagePicker", "Failed to copy image to internal storage", e)
            null
        }
    }

    private fun getExtensionFromUri(context: Context, uri: Uri): String {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                val displayName = cursor.getString(nameIndex)
                val lastDot = displayName.lastIndexOf('.')
                if (lastDot >= 0 && lastDot < displayName.length - 1) {
                    return displayName.substring(lastDot + 1).lowercase()
                }
            }
        }
        val mimeType = context.contentResolver.getType(uri)
        return android.webkit.MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType) ?: "jpg"
    }
}
