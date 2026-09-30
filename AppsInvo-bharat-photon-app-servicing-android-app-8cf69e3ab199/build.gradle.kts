// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
//    id("com.google.dagger.hilt.android") version "2.49" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
buildscript {
    repositories {
        jcenter() // this line need
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
    }
    dependencies {
        classpath(libs.google.services)
        classpath("com.android.tools.build:gradle:8.5.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.3")
    }
}
