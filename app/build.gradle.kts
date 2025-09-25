plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "noorofgratitute.com"
    compileSdk = 35

    defaultConfig {
        applicationId = "noorofgratitute.com"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation (libs.mpandroidchart)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.work.runtime)
    implementation(libs.support.annotations)
    implementation(libs.firebase.database)
    implementation(libs.swiperefreshlayout)
    implementation(libs.play.services.location)
    implementation(libs.recyclerview)
    implementation(libs.coordinatorlayout)
    implementation(libs.security.crypto)
    implementation(libs.annotation)
    implementation(libs.support.v4)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.gson)
    implementation (libs.retrofit.v290)
    implementation (libs.converter.gson.v290)
    implementation (libs.logging.interceptor)
    implementation (libs.okhttp)
    implementation (libs.cardview)
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.1") {
        exclude(group = "androidx.versionedparcelable", module = "versionedparcelable")}
    implementation (libs.glide)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.google.firebase:firebase-storage:20.2.1") // Firebase Storage
    implementation ("com.google.firebase:firebase-database:20.2.1") // Firebase Realtime Database

    implementation (libs.play.services.auth)
    implementation ("com.facebook.android:facebook-android-sdk:16.3.0")

    implementation ("com.facebook.android:facebook-login:16.3.0")

    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    implementation ("com.google.firebase:firebase-messaging:20.2.4")
    implementation ("androidx.work:work-runtime:2.5.0")

    implementation ("com.google.android.material:material:1.7.0")

    // Jetpack Credential Manager core library
    implementation ("androidx.credentials:credentials:1.5.0"  )

    // Play-services integration for Credential Manager
    implementation ("androidx.credentials:credentials-play-services-auth:1.5.0")
    // Google ID helper library for “Sign in with Google”
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    // Firebase Authentication
    implementation ("com.google.firebase:firebase-auth:22.3.0")
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")
    implementation ("com.facebook.android:facebook-android-sdk:latest.release")
    implementation ("com.google.firebase:firebase-messaging:23.4.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.google.android.material:material:1.10.0")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor( "androidx.room:room-compiler:2.6.1" )
    implementation ("androidx.room:room-ktx:2.6.1")
    implementation ("androidx.work:work-runtime:2.10.3")
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    implementation("com.google.firebase:firebase-appcheck-debug")
    implementation ("com.prolificinteractive:material-calendarview:1.4.3")
    implementation ("com.github.msarhan:ummalqura-calendar:2.0.2")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")


}