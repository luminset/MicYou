package com.lanrhyme.micyou

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 错误日志记录工具
 * 将错误信息写入文件以便调试
 */
object ErrorLogger {
    private var context: Context? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val fileDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    
    fun init(ctx: Context) {
        context = ctx.applicationContext
    }
    
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        val ctx = context ?: return
        
        try {
            val logDir = File(ctx.getExternalFilesDir(null) ?: ctx.filesDir, "error_logs")
            if (!logDir.exists()) logDir.mkdirs()
            
            val logFile = File(logDir, "error_${fileDateFormat.format(Date())}.log")
            val timestamp = dateFormat.format(Date())
            
            val logEntry = buildString {
                appendLine("[$timestamp] [$tag] $message")
                throwable?.let {
                    appendLine("Exception: ${it.javaClass.name}: ${it.message}")
                    it.stackTrace?.take(20)?.forEach { frame ->
                        appendLine("    at $frame")
                    }
                }
                appendLine("---")
            }
            
            FileOutputStream(logFile, true).use { fos ->
                fos.write(logEntry.toByteArray(Charsets.UTF_8))
            }
            
            // 同时输出到logcat
            android.util.Log.e(tag, message, throwable)
        } catch (e: Exception) {
            android.util.Log.e("ErrorLogger", "Failed to write error log", e)
        }
    }
    
    fun logInfo(tag: String, message: String) {
        val ctx = context ?: return
        
        try {
            val logDir = File(ctx.getExternalFilesDir(null) ?: ctx.filesDir, "error_logs")
            if (!logDir.exists()) logDir.mkdirs()
            
            val logFile = File(logDir, "error_${fileDateFormat.format(Date())}.log")
            val timestamp = dateFormat.format(Date())
            
            val logEntry = "[$timestamp] [$tag] INFO: $message\n"
            
            FileOutputStream(logFile, true).use { fos ->
                fos.write(logEntry.toByteArray(Charsets.UTF_8))
            }
        } catch (e: Exception) {
            android.util.Log.e("ErrorLogger", "Failed to write info log", e)
        }
    }
    
    fun getLogFilePath(): String? {
        val ctx = context ?: return null
        val logFile = File(ctx.getExternalFilesDir(null) ?: ctx.filesDir, "error_logs/error_${fileDateFormat.format(Date())}.log")
        return if (logFile.exists()) logFile.absolutePath else null
    }
}
