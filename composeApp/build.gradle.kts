import java.util.Properties
import java.io.File

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

val aifadianApiToken: String = run {
    val localProps = Properties()
    val localFile = File(rootDir, "local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { localProps.load(it) }
    }
    localProps.getProperty("AIFADIAN_API_TOKEN", "")
}

val aifadianUserId: String = run {
    val localProps = Properties()
    val localFile = File(rootDir, "local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { localProps.load(it) }
    }
    localProps.getProperty("AIFADIAN_USER_ID", "")
}

android {
    namespace = "com.lanrhyme.micyou"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.lanrhyme.micyou"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = project.property("project.version.code").toString().toInt()
        versionName = project.property("project.version").toString()
        buildConfigField("String", "AIFADIAN_API_TOKEN", "\"$aifadianApiToken\"")
        buildConfigField("String", "AIFADIAN_USER_ID", "\"$aifadianUserId\"")

        // [API 21+ 兼容] 显式启用 MultiDex
        // Android 5.0 (API 21) 虽宣称 ART 原生支持 MultiDex，但部分厂商设备 (如华为 P7 API 22)
        // 存在 DEX 加载 bug：第二个 DEX 中的合成类会抛 NoClassDefFoundError。
        // 显式声明 multiDexEnabled + 集成 multidex 库可强制让系统加载全部 DEX。
        multiDexEnabled = true

        // 32位架构支持 (兼容旧版手机)
        ndk {
            abiFilters += listOf("armeabi-v7a", "x86")
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    val keystorePath = providers.environmentVariable("ANDROID_KEYSTORE_PATH").orNull
    val keystorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
    val keyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
    val keyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull

    val hasReleaseSigning =
        !keystorePath.isNullOrEmpty() &&
        !keystorePassword.isNullOrEmpty() &&
        !keyAlias.isNullOrEmpty() &&
        !keyPassword.isNullOrEmpty()

    signingConfigs {
        create("release") {
            if (hasReleaseSigning) {
                storeFile = file(keystorePath!!)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false

            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        // [API 21+ 兼容] 启用核心库脱糖 (core library desugaring)
        // 让低版本 Android 设备可用 Java 8+ 标准库 API (如 java.util.function、
        // AtomicLong.updateAndGet、java.time 等)。与 multidex 搭配防御。
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

// 强制将 androidx.activity 降级到 1.8.2 (支持 minSdk 21)
// FileKit 0.13.0 传递依赖 activity 1.12.4 (要求 minSdk 23)，需要强制降级
configurations.all {
    resolutionStrategy {
        force("androidx.activity:activity:1.8.2")
        force("androidx.activity:activity-compose:1.8.2")
        force("androidx.activity:activity-ktx:1.8.2")
    }
}

dependencies {
    // [API 21+ 兼容] MultiDex 库：强制 ART 加载全部 DEX 文件
    // Android 5.0+ 虽原生支持 MultiDex，但厂商 ROM (如华为 EMUI 3.1 API 22)
    // 存在 DEX 加载遗漏 bug，需显式集成此库 + Application.install() 调用。
    implementation("androidx.multidex:multidex:2.0.1")

    // [API 21+ 兼容] 核心库脱糖依赖：提供 Java 8+ 标准库 API 在低版本 Android 上的实现
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.ktor.network)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.protobuf)
    implementation(libs.kotlinx.datetime)
    implementation("dev.chrisbanes.haze:haze:1.0.0")
    implementation(libs.materialKolor)
    implementation(libs.concentus)

    debugImplementation(libs.androidx.compose.ui.tooling)
}

tasks.register("prepareKotlinBuildScriptModel") {}
