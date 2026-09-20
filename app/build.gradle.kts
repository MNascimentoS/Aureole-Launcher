import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
    jacoco
}

val gitCommitCount = providers.exec {
    commandLine("git", "rev-list", "--count", "HEAD")
}.standardOutput.asText.map { it.trim().toIntOrNull() ?: 1 }.orElse(1).get()

val customVersionName = providers.gradleProperty("VERSION_NAME").orNull

android {
    namespace = "dev.mnascimentos.aureole"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "dev.mnascimentos.aureole"
        minSdk = 28
        targetSdk = 37
        versionCode = gitCommitCount
        versionName = customVersionName ?: "0.1.$gitCommitCount"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = rootProject.file("release.keystore")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("KEY_ALIAS") ?: ""
                keyPassword = System.getenv("KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            val keystoreFile = rootProject.file("release.keystore")
            if (keystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(file("$projectDir/config/detekt/detekt.yml"))
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "11"
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)

    implementation(composeBom)
    implementation(libs.gson)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.coil.compose)
    implementation(libs.haze)
    implementation(libs.haze.materials)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.playAppUpdate)
    implementation(libs.playAppUpdateKtx)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*", "**/*_Impl*.*", "**/*Dao*.*",
        "**/*Database*.*", "dev/mnascimentos/aureole/data/db/*"
    )
    val debugTree = fileTree("$projectDir/build/intermediates/javac/debug/classes") {
        exclude(fileFilter)
    }
    val kotlinDebugTree = fileTree("$projectDir/build/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "$projectDir/src/main/java"

    classDirectories.setFrom(files(debugTree, kotlinDebugTree))
    sourceDirectories.setFrom(files(mainSrc))
    executionData.setFrom(fileTree(projectDir) {
        include("build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec", "build/jacoco/testDebugUnitTest.exec")
    })
}
