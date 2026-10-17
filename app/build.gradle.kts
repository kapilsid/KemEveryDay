import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

// Play upload-key credentials live in an untracked keystore.properties (see keystore.properties.example).
val releaseSigningFile = rootProject.file("keystore.properties")
val releaseSigningProperties = Properties().apply {
    if (releaseSigningFile.isFile) releaseSigningFile.inputStream().use(::load)
}
val releaseSigningKeys = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
val hasReleaseSigning = releaseSigningFile.isFile && releaseSigningKeys.all {
    !releaseSigningProperties.getProperty(it).isNullOrBlank()
}

if (releaseSigningFile.isFile && !hasReleaseSigning) {
    error("keystore.properties must define: ${releaseSigningKeys.joinToString()}")
}

// Debug builds use Google's public AdMob test IDs; release builds use the Kemistri Everyday AdMob app.
// bundleRelease refuses to build if the release values are ever set back to test IDs.
val admobTestAppId = "ca-app-pub-3940256099942544~3347511713"
val admobTestInterstitialId = "ca-app-pub-3940256099942544/1033173712"
val admobReleaseAppId = "ca-app-pub-8151554078712608~5277588777"
val admobReleaseInterstitialId = "ca-app-pub-8151554078712608/2651425439"

android {
    namespace = "uk.edbetter.kemdaily"
    compileSdk = 36

    // The app has its own language picker. Keep every translation in the base APK/AAB so changing
    // language works immediately without asking Play to download another language split.
    bundle {
        language {
            enableSplit = false
        }
    }

    defaultConfig {
        applicationId = "uk.edbetter.kemdaily"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(releaseSigningProperties.getProperty("storeFile"))
                storePassword = releaseSigningProperties.getProperty("storePassword")
                keyAlias = releaseSigningProperties.getProperty("keyAlias")
                keyPassword = releaseSigningProperties.getProperty("keyPassword")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    buildTypes {
        debug {
            manifestPlaceholders["admobAppId"] = admobTestAppId
            buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"$admobTestInterstitialId\"")
        }
        release {
            manifestPlaceholders["admobAppId"] = admobReleaseAppId
            buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"$admobReleaseInterstitialId\"")
            isMinifyEnabled = true
            isShrinkResources = true
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

val verifyReleaseSigning = tasks.register("verifyReleaseSigning") {
    doLast {
        check(hasReleaseSigning) {
            "Release signing is not configured. Copy keystore.properties.example to keystore.properties and add the Play upload-key credentials."
        }
        check(admobReleaseAppId != admobTestAppId && admobReleaseInterstitialId != admobTestInterstitialId) {
            "Release AdMob IDs are still Google's test IDs. Set admobReleaseAppId and admobReleaseInterstitialId in app/build.gradle.kts."
        }
    }
}

tasks.configureEach {
    if (name == "bundleRelease") dependsOn(verifyReleaseSigning)
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.05.01")
    val firebaseBom = platform("com.google.firebase:firebase-bom:35.0.1")
    implementation(composeBom)
    implementation(firebaseBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("com.google.android.gms:play-services-ads:24.9.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    debugImplementation("com.google.firebase:firebase-appcheck-debug")
    debugImplementation("androidx.compose.ui:ui-tooling")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
