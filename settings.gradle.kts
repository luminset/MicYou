rootProject.name = "MicYou"

// 兼容模式检测：-Pmicyou.androidCompat=true
// 注意：PowerShell 中必须用引号包裹参数："-Pmicyou.androidCompat=true"
// 否则 PowerShell 会把 micyou.androidCompat 解析为 micyou（截断点号）
val androidCompat = gradle.startParameter.projectProperties.containsKey("micyou.androidCompat")

pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }

    // 兼容模式：覆盖 version catalog 中的版本号
    // 不调 from()，因为 Gradle 已自动从 libs.versions.toml 创建了 libs catalog
    // 这里只覆盖需要降级的版本，alias() 引用会自动使用降级后的版本
    versionCatalogs {
        create("libs") {
            if (androidCompat) {
                version("agp", "8.9.1")
                version("kotlin", "2.1.20")
                version("android-minSdk", "21")
                version("android-targetSdk", "29")
                version("androidx-activity", "1.8.2")
                version("androidx-core", "1.12.0")
                version("androidx-lifecycle", "2.8.0")
                version("compose-bom", "2024.09.00")
                version("kotlinx-coroutines", "1.9.0")
                version("kotlinx-datetime", "0.7.1")
                version("ktor", "3.0.3")
                version("kotlinx-serialization", "1.7.3")
                version("materialKolor", "1.7.1")
            }
        }
    }
}

include(":composeApp")
