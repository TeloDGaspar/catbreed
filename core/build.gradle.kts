import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ksp)
}

// Read the TheCatAPI key from local.properties (gitignored) or an env var (for CI).
// Falls back to an empty string so the app still builds and runs keyless.
val catApiKey: String = run {
    val properties = Properties()
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { properties.load(it) }
    }
    properties.getProperty("CAT_API_KEY")
        ?: System.getenv("CAT_API_KEY")
        ?: ""
}

android {
    namespace = "com.telogaspar.catbreed.core"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "CAT_API_KEY", "\"$catApiKey\"")
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
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose — api() so consumers inherit Color, Brush, CompositionLocal
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Networking — exposed via api() so feature modules can build their own services
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    // Room — api() so feature modules can see the DAOs and entities
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}