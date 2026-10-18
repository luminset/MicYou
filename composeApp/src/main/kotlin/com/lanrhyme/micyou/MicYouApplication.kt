package com.lanrhyme.micyou

import android.app.Application
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.multidex.MultiDex
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 全局 Application 类
 * - 安装 MultiDex (修复 API 21-22 厂商设备 DEX 加载 bug)
 * - 设置全局未捕获异常处理器
 * - 崩溃日志记录到外部存储 crash 目录
 * - 兼容 Android 5.0+ (API 21+)
 */
class MicYouApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // [API 21+ 兼容] 显式安装 MultiDex
        // Android 5.0 (API 21) 虽宣称 ART 原生支持 MultiDex，但部分厂商 ROM
        // (如华为 EMUI 3.1 / P7-L09 API 22) 存在 DEX 加载遗漏 bug，
        // 会导致第二个 DEX 中的 $$ExternalSyntheticLambda* 合成类触发 NoClassDefFoundError。
        // 在 Application.onCreate() 之前（最早 hook 点）安装 MultiDex 可确保
        // 所有类加载路径被正确注册。
        try {
            MultiDex.install(this)
        } catch (_: Throwable) {
            // 安装失败时静默忽略：最坏情况退化为系统默认 MultiDex 行为
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 安装全局未捕获异常处理器
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                // 记录崩溃日志到文件
                val crashDir = File(getExternalFilesDir(null), "crash")
                if (!crashDir.exists()) {
                    crashDir.mkdirs()
                }
                val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
                val logFile = File(crashDir, "crash_${timestamp}.txt")
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                pw.println("=== MicYou Crash Report ===")
                pw.println("Time: ${Date()}")
                pw.println("Thread: ${thread.name}")
                pw.println("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                pw.println("Android API: ${Build.VERSION.SDK_INT}")
                pw.println("App Version: ${packageManager.getPackageInfo(packageName, 0).versionName}")
                pw.println("============================")
                pw.println()
                throwable.printStackTrace(pw)
                pw.println()
                pw.println("=== Thread Stack ===")
                thread.stackTrace.forEach { pw.println(it.toString()) }
                pw.flush()
                logFile.writeText(sw.toString())

                // 显示 Toast 提示
                Toast.makeText(this, "MicYou 遇到错误，已记录日志", Toast.LENGTH_LONG).show()
            } catch (_: Exception) {
                // 如果连日志都写不了，至少不要吞掉原始异常
            }

            // 交给系统默认处理器终止进程
            previousHandler?.uncaughtException(thread, throwable)
        }
    }
}
