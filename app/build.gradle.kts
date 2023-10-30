import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dev.rikka.tools.refine") version "4.4.0"
}

val localProperties = Properties()
if (rootProject.file("local.properties").canRead()) localProperties.load(rootProject.file("local.properties").inputStream())

android {
    namespace = "statusbar.lyric"
    compileSdk = 34
    val buildTime = System.currentTimeMillis()
    defaultConfig {
        applicationId = "statusbar.lyric"
        minSdk = 26
        targetSdk = 34
        versionCode = 202
        versionName = "6.0.2"
        aaptOptions.cruncherEnabled = false
        aaptOptions.useNewCruncher = false
        buildConfigField("long", "BUILD_TIME", "$buildTime")
        buildConfigField("int", "API_VERSION", "5")
        buildConfigField("int", "CONFIG_VERSION", "3")
    }
    val config = localProperties.getProperty("androidStoreFile")?.let {
        signingConfigs.create("config") {
            storeFile = file(it)
            storePassword = localProperties.getProperty("androidStorePassword")
            keyAlias = localProperties.getProperty("androidKeyAlias")
            keyPassword = localProperties.getProperty("androidKeyPassword")
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    buildTypes {
        all {
            signingConfig = config ?: signingConfigs["debug"]
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    packaging {
        resources {
            excludes += "**"
        }
    }
    buildFeatures {
        viewBinding = true
        aidl = true
    }
    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName = "StatusBarLyric-$versionName-$versionCode-$name-$buildTime.apk"
        }
    }
}

kotlin {
    sourceSets.all {
        languageSettings { languageVersion = "2.0" }
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")
    implementation("dev.rikka.hidden:compat:4.3.1")
    compileOnly("dev.rikka.hidden:stub:4.3.1")

    implementation(project(":blockmiui"))
    implementation(project(":xtoast"))
    implementation("com.github.kyuubiran:EzXHelper:2.0.7")
    implementation("com.github.xiaowine:Lyric-Getter-Api:5.0.5")
}
