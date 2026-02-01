import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
}

android {
    namespace = "nodomain.aditya1875more.stashly"
    compileSdk = 36

    defaultConfig {
        applicationId = "nodomain.aditya1875more.stashly"
        minSdk = 25
        targetSdk = 36
        versionCode = 18
        versionName = "2.0.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "distribution"

    productFlavors {

        create("play") {
            dimension = "distribution"
            applicationIdSuffix = ".play"
            versionNameSuffix = "-play"
            buildConfigField("boolean", "USE_FIREBASE", "true")
        }

        create("foss") {
            dimension = "distribution"
            isDefault = true
            buildConfigField("boolean", "USE_FIREBASE", "false")
        }
    }

    sourceSets {
        getByName("play") {
            java.srcDir("src/play/java")
            res.srcDir("src/play/res")
            manifest.srcFile("src/play/AndroidManifest.xml")
        }
        getByName("foss") {
            java.srcDir("src/foss/java")
            res.srcDir("src/foss/res")
            manifest.srcFile("src/foss/AndroidManifest.xml")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    packaging {
        resources {
            excludes += "META-INF/versions/**"
        }
    }
}

// Future-proof: apply Google services only for Play builds
if (gradle.startParameter.taskNames.any { it.lowercase().contains("play") }) {
    apply(plugin = "com.google.gms.google-services")
}

dependencies {

    implementation(libs.re2j)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.jsoup)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3.window.size.class1)
    implementation(libs.coil.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.browser)

    implementation(libs.lottie.compose)
    implementation(libs.moshi)
    implementation(libs.okhttp)
    implementation(libs.moshi.kotlin)
    implementation(libs.materialKolor)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.logging.interceptor)
    implementation(libs.jetbrains.compose.navigation)
    implementation(libs.colorpicker.compose)
    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.placeholder)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.auth)

    implementation(libs.retrofit)
    implementation(libs.coil)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.datetime)

    add("playImplementation", libs.firebase.analytics)
    add("playImplementation", platform(libs.firebase.bom))
    add("playImplementation", libs.firebase.messaging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.whenTaskAdded {
    if (name.contains("ArtProfile")) {
        enabled = false
    }
}