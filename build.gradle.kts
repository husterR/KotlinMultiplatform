buildscript {
    dependencies {
        classpath(libs.kotlinx.atomicfu.gradle.plugin)
    }
}

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.firebase.crashlytics).apply(false)
}

apply(rootProject.file("ktlint.gradle"))