plugins {
    id("com.android.application")
}

android {

    namespace = "com.example.remindertugasmahasiswa"

    compileSdk = 35

    defaultConfig {

        applicationId = "com.example.remindertugasmahasiswa"

        minSdk = 24
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation("com.google.android.material:material:1.12.0")

    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("androidx.recyclerview:recyclerview:1.4.0")

    implementation("androidx.cardview:cardview:1.0.0")

    implementation("androidx.activity:activity:1.10.1")

    implementation("androidx.lifecycle:lifecycle-runtime:2.8.7")

    // ROOM DATABASE
    implementation("androidx.room:room-runtime:2.7.1")

    annotationProcessor("androidx.room:room-compiler:2.7.1")
    implementation("com.applandeo:material-calendar-view:1.9.2")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

}