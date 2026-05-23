plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Fix: move any accidentally placed resource folder `NutriKali_logo` into a valid res folder
// This avoids AAPT2 errors like "The file name must end with .xml" when an unknown res
// subdirectory is present. The task is safe: it only runs if the expected source file exists.
tasks.register("fixNutriKaliLogo", Copy::class) {
    val srcFile = file("src/main/res/NutriKali_logo/NutriKali_LOGO.png")
    if (srcFile.exists()) {
        from(srcFile)
        into(file("src/main/res/drawable-nodpi"))
        rename("NutriKali_LOGO.png", "logonutri.png")
        doLast {
            // remove the now-empty folder
            delete(file("src/main/res/NutriKali_logo"))
            println("Moved NutriKali logo to res/drawable-nodpi/logonutri.png and removed old folder")
        }
    } else {
        doLast {
            println("No NutriKali logo found in src/main/res/NutriKali_logo. Skipping fixNutriKaliLogo task.")
        }
    }
}

// Ensure the fix runs before resource merging / preBuild
tasks.named("preBuild") {
    dependsOn("fixNutriKaliLogo")
}

android {
    namespace = "com.example.nutrikaliapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.nutrikaliapp"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Retrofit y Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
// Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
// Material Design
    implementation("com.google.android.material:material:1.9.0")

// Coil   --- segun debe funcionar
    implementation("io.coil-kt:coil:2.5.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}