import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.screenshot)
    jacoco
}

// Region: Versioning Configuration
val gitCommitCount = providers.exec {
    commandLine("git", "rev-list", "--count", "HEAD")
}.standardOutput.asText.map { it.trim().toIntOrNull() ?: 1 }.orElse(1).get()

val customVersionCode = providers.gradleProperty("VERSION_CODE").orNull?.toIntOrNull()
val customVersionName = providers.gradleProperty("VERSION_NAME").orNull

// Region: Android Configuration
android {
    namespace = "dev.mnascimentos.aureole"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "dev.mnascimentos.aureole"
        minSdk = 28
        targetSdk = 37
        versionCode = customVersionCode ?: gitCommitCount
        versionName = if (customVersionName != null) {
            if (customVersionName.endsWith(".$gitCommitCount")) customVersionName else "$customVersionName.$gitCommitCount"
        } else {
            "0.1.$gitCommitCount"
        }

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
            applicationIdSuffix = ".debug"
            enableUnitTestCoverage = true
        }
        release {
            val keystoreFile = rootProject.file("release.keystore")
            if (keystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
        disable += "InvalidFragmentVersionForActivityResult"
        lintConfig = file("lint.xml")
    }
}

// Region: Kotlin Compilation
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

// Region: Code Quality Configuration (Detekt & Jacoco)
detekt {
    buildUponDefaultConfig = true
    autoCorrect = true
    allRules = false
    ignoreFailures = false
    config.setFrom(file("$projectDir/config/detekt/detekt.yml"))
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
    reports {
        txt.required.set(true)
        txt.outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.txt"))
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.html"))
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.withType<Test>().configureEach {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    description = "Generates JaCoCo code coverage report for unit tests."
    group = "Reporting"
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*_Impl*.*",
        "**/*Dao*.*",
        "**/*Database*.*",
        "dev/mnascimentos/aureole/data/db/*"
    )

    val javaClasses = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
        exclude(fileFilter)
    }
    val kotlinClasses = fileTree(layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
        exclude(fileFilter)
    }

    classDirectories.setFrom(files(javaClasses, kotlinClasses))
    sourceDirectories.setFrom(files("$projectDir/src/main/java", "$projectDir/src/main/kotlin"))
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include(
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                "jacoco/testDebugUnitTest.exec"
            )
        }
    )
}

// Region: Dependencies
dependencies {
    // Quality & Lint Plugins
    lintChecks(libs.compose.lint.checks)
    detektPlugins(libs.detekt.formatting)

    // Jetpack Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // AndroidX Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Database (Room)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Dependency Injection (Koin)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // UI & Utility Libraries
    implementation(libs.gson)
    implementation(libs.coil.compose)
    implementation(libs.haze)
    implementation(libs.haze.materials)

    // Play Services & Firebase
    implementation(libs.playAppUpdate)
    implementation(libs.playAppUpdateKtx)
    val firebaseBom = platform(libs.firebase.bom)
    implementation(firebaseBom)
    implementation(libs.firebase.crashlytics)

    // Testing - Unit Tests
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.koin.test)
    testImplementation(composeBom)
    testImplementation(libs.androidx.compose.ui.test.junit4)

    // Testing - Instrumentation Tests
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Testing - Screenshot Tests
    screenshotTestImplementation(libs.screenshot.validation.api)
    screenshotTestImplementation(libs.androidx.compose.ui.tooling)

    // Debugging Tools
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
