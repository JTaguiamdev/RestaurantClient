import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val envProperties = Properties().apply {
    val envFile = rootProject.file(".env/.env")
    if (envFile.exists()) {
        envFile.inputStream().use { load(it) }
    }
}

val apiBaseUrl = envProperties.getProperty("API_BASE_URL")
    ?: System.getenv("API_BASE_URL")
    ?: error("API_BASE_URL is not configured. Add it to .env/.env or as an environment variable.")

val buildConfigBaseUrl = apiBaseUrl.trim().replace("\"", "\\\"")
val debugForceAdminRole = (project.findProperty("forceAdminRole") as? String)?.equals("true", ignoreCase = true) ?: false
val debugForceAdminUsernameRaw = (project.findProperty("forceAdminUsername") as? String)?.trim() ?: ""
val debugForceAdminUsername = debugForceAdminUsernameRaw.replace("\"", "\\\"")

android {
    namespace = "com.restaurantclient"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.restaurantclient"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.restaurantclient.HiltTestRunner"
        buildConfigField("String", "BASE_URL", "\"$buildConfigBaseUrl\"")
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "FORCE_ADMIN_ROLE", debugForceAdminRole.toString())
            buildConfigField("String", "FORCE_ADMIN_USERNAME", "\"$debugForceAdminUsername\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "FORCE_ADMIN_ROLE", "false")
            buildConfigField("String", "FORCE_ADMIN_USERNAME", "\"\"")
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
        buildConfig = true
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Retrofit & Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // ViewModel & LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Hilt (for dependency injection)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Secure Storage
    implementation(libs.androidx.security.crypto)
    
    // BlurView for glassmorphism
    implementation(libs.blurview)
    
    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.core.testing)
}
