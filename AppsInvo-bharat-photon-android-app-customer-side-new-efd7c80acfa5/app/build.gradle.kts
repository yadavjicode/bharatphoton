@file:Suppress("DEPRECATION")

import org.gradle.api.JavaVersion.VERSION_17

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    kotlin("kapt")
}
val BASEURL: String by project
val RAZORPAY_KEY: String by project

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("C:\\Android Studio Projects\\AppsInvo-bharat-photon-android-app-customer-side-new-ed8e927c250e\\app\\src\\main\\bharatphoton.jks")
            storePassword = "Android@123"
            keyAlias = "key0"
            keyPassword = "Android@123"
        }
    }
    namespace = "com.app.bharatphoton"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.app.bharatphoton"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 20
        versionName = "1.2.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "RAZORPAY_KEY", "$RAZORPAY_KEY")
        resValue("string", "razorpaykey", RAZORPAY_KEY)
        buildConfigField("String", "BASE_URL", BASEURL)


    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = VERSION_17
        targetCompatibility = VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


// OTP View -----------------------------------------------------------------------------------
    implementation(libs.otp.view)

// Retrofit & OkHttp --------------------------------------------------------------------------
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

// Coil ---------------------------------------------------------------------------------------
    implementation(libs.coil)

// Coroutine Lifecycle Scope ------------------------------------------------------------------
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

// Kotlin Coroutine ---------------------------------------------------------------------------
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)


// Navigation Components ----------------------------------------------------------------------
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

// Circular Imageview -------------------------------------------------------------------------
    implementation(libs.circleimageview)

// responsive Size ----------------------------------------------------------------------------
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

//    Country Code Picker --------------------------------------------------------------------------
    implementation(libs.ccp)

//    Dagger Hilt --------------------------------------------------------------------------------
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

//   Runtime Permission --------------------------------------------------------------------------
    implementation(libs.kpermissions)

//   Paging -------------------------------------------------------------------------------------
    implementation(libs.androidx.paging.runtime.ktx)

//    AWS SDK --------------------------------------------------------------------------------------
    implementation(libs.aws.android.sdk.s3)

//    BlurView -------------------------------------------------------------------------------------
    implementation(libs.blurview)

//    Youtube View ---------------------------------------------------------------------------------
    //noinspection UseTomlInstead
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:13.0.0")
// Loading Button ----------------------------------------------------------------------------------
    implementation(libs.loadingbutton)

// Bottom Sheet ------------------------------------------------------------------------------------
    implementation(libs.smoothbottombar)

// Dot Indicator -----------------------------------------------------------------------------------
    implementation(libs.dotsindicator.v50)


// Rating Bar --------------------------------------------------------------------------------------
    implementation(libs.library)

// Loader ------------------------------------------------------------------------------------------
    implementation(libs.android.spinkit)

// Glide ---------------------------------------------------------------------------------------
    implementation(libs.glide)

// Firebase---------------------------------------------------------------------------------------
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.core)
    implementation(libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))

// swipe refresh======================================================================================
    implementation(libs.androidx.swiperefreshlayout)
// Razor Pay =========================================================================================
    implementation(libs.razorpay.checkout)

// implementation(libs.facebook.android.sdk)
    implementation(libs.facebook.login.v1702)

// MP Graph
    implementation(libs.mpandroidchart)
    // Animation
    implementation(libs.android.animations.kotlin)
    // Image Zoom Effect
    implementation(libs.photoview)

}

configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
}



kapt {
    correctErrorTypes = true
}

