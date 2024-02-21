import java.util.UUID

plugins {
    id("com.android.library")
}

android {
    namespace = "com.example.vosk_models"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

    }

    buildFeatures {
        buildConfig = false
    }

    sourceSets {
        named("main") {
            assets.srcDirs("$buildDir/generated/assets")
        }
    }
}

tasks.register("genUUID") {
    val uuid = UUID.randomUUID().toString()
    val odir = file("$buildDir/generated/assets/model-en-us")
    val ofile = file("$odir/uuid")

    doLast {
        mkdir(odir)
        ofile.writeText(uuid)
    }
}

tasks.named("preBuild") {
    dependsOn("genUUID")
}