    plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        // Google services Gradle plugin
        id("com.google.gms.google-services")
        id ("kotlin-kapt")
    }

    android {
        namespace = "seoultech.itm.timntims"
        compileSdk = 34

        defaultConfig {
            applicationId = "seoultech.itm.timntims"
            minSdk = 29
            targetSdk = 33
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        packagingOptions {
            exclude("META-INF/DEPENDENCIE")
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
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
        buildFeatures {
            viewBinding = true
        }

        packagingOptions {
            resources {
                excludes += "/META-INF/DEPENDENCIES"
            }
        }
    }

    dependencies {
        implementation ("org.apache.opennlp:opennlp-tools:1.9.3")
        implementation("androidx.core:core-ktx:1.9.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.10.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("com.google.firebase:firebase-database:20.3.0")
        implementation("com.google.firebase:firebase-auth:22.3.0")
        implementation("androidx.preference:preference:1.2.0")
        implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
        implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

        implementation("com.squareup.okhttp3:okhttp:4.10.0")
        implementation("org.pytorch:pytorch_android_lite:1.9.0")
        implementation("org.pytorch:pytorch_android_torchvision:1.9.0")

        // Import the Firebase BoM
        implementation(platform("com.google.firebase:firebase-bom:32.5.0"))

        // TODO: Add the dependencies for Firebase products you want to use
        // When using the BoM, don't specify versions in Firebase dependencies
        implementation("com.google.firebase:firebase-analytics")

        // Import the BoM for the Firebase platform
        implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

        // Add the dependency for the Realtime Database library
        // When using the BoM, you don't specify versions in Firebase library dependencies
        implementation("com.google.firebase:firebase-database-ktx")

        //google calendar
        implementation ("com.google.api-client:google-api-client:2.0.0")
        implementation ("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
        implementation ("com.google.apis:google-api-services-calendar:v3-rev20220715-2.0.0")

        implementation ("androidx.media:media:1.3.1")
        implementation ("androidx.legacy:legacy-support-v4:1.0.0")
        implementation ("com.google.android.gms:play-services-auth:20.7.0")
        implementation("pub.devrel:easypermissions:3.0.0")
    //    implementation ("pub.devrel:easypermissions:3.0.0")
        implementation("com.google.api-client:google-api-client-android:1.22.0") {
    //        exclude group: org.apache.httpcomponents'
        }
    //    implementation ("com.google.api-client:google-api-client-jackson2:1.43.2")
        implementation ("com.fasterxml.jackson.core:jackson-databind:2.12.3")
        implementation(group = "com.google.http-client", name = "google-http-client-jackson2", version = "1.43.2")
        implementation ("joda-time:joda-time:2.10.13")

        implementation ("com.applandeo:material-calendar-view:1.9.0-rc04")
        // implementation ("com.github.prolificinteractive:material-calendarview:2.0.1")

        implementation("androidx.room:room-runtime:2.6.1")
        annotationProcessor("androidx.room:room-compiler:2.6.1")
        kapt("androidx.room:room-compiler:2.6.1")
        implementation ("androidx.room:room-ktx:2.3.0")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0-RC")

        // Glide
        implementation ("com.github.bumptech.glide:glide:4.16.0") // replace 4.x.x with the latest version
        annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0") // replace 4.x.x with the latest version

        // Firebase
        implementation ("com.google.firebase:firebase-storage-ktx")

        implementation ("com.github.yalantis:ucrop:2.2.6")

    }