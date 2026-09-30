plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
}
val BASEURL: String by project

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("C:\\Android Studio Projects\\BharatPhotonServiceSide\\app\\src\\main\\bharatphotonservice.jks")
            storePassword = "Android@123"
            keyPassword = "Android@123"
            keyAlias = "bharatphotonservicekey"
        }
    }
    namespace = "app.bharatphoton.service"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.bharatphoton.service"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 8
        versionName = "1.0.7"
        setProperty("archivesBaseName", "BPE Service")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", BASEURL)
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG", "true")
            buildConfigField("String", "BUILD_TYPE", "\"debug\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("boolean", "DEBUG", "false")
            buildConfigField("String", "BUILD_TYPE", "\"release\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //sdp
    implementation(libs.sdp.android)
    //Glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    //AWS database storage
    implementation(libs.aws.android.sdk.s3)
    implementation(libs.core)
    //json converter
    implementation(libs.converter.gson)
    //retrofit
    implementation(libs.retrofit)
    //interceptor
    implementation(libs.logging.interceptor)
    //Firebase FCM
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.play.services.auth)
    //ExoPlayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    //Image Zoom
    implementation(libs.photoview)
    implementation(libs.lightcompressor)
    //Shimmer layout effect
    implementation(libs.shimmer)
    //Firebase crashlytics
    implementation(libs.firebase.crashlytics)
}