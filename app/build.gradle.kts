plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.quickcartadmin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.quickcartadmin"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // for sp and dp
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    //Navigation component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    //lifecycle
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //firebase
    implementation(libs.firebase.database)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.analytics)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.play.services.auth)
    implementation(libs.google.firebase.storage.ktx)
    implementation(libs.google.firebase.database.ktx)
    implementation(libs.firebase.messaging.ktx)
    //lottie
    implementation(libs.android.lottie)
    //image slider
    implementation(libs.imageslideshow)
    //shimmer effect
    implementation(libs.shimmer)


    implementation(libs.glide)

    //retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)


}