plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false  // Đảm bảo plugin google-services được thêm đúng
}

buildscript {
    repositories {
        google()  // Firebase repository
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")  // Phiên bản đúng của plugin google-services
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

