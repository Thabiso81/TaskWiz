buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")

        //safe args
        val nav_version = "2.7.6"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")




    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id("com.google.devtools.ksp") version "1.9.21-1.0.15" apply false

}