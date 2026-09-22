// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.screenshot) apply false
}

tasks.register<Copy>("installGitHooks") {
    description = "Installs Git hooks from .githooks directory"
    group = "git hooks"
    from(file(".githooks"))
    into(file(".git/hooks"))
    filePermissions {
        user {
            read = true
            write = true
            execute = true
        }
        other {
            read = true
            execute = true
        }
        group {
            read = true
            execute = true
        }
    }
}