import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.tasks.Exec

plugins {
    java
    kotlin("jvm") version "2.3.20"
}

version = "1.0"

// 混合源码：Java 和 Kotlin 都在 src/ 中
sourceSets.main {
    java.srcDirs("src")
}

repositories {
    mavenCentral()

    ivy {
        url = uri("https://github.com/")
        patternLayout {
            artifact("/[organisation]/[module]/releases/download/[revision]/dependencies.jar")
        }
        metadataSources {
            artifact()
        }
    }

    ivy {
        url = uri("https://github.com/")
        patternLayout {
            artifact("/[organisation]/[module]/releases/download/master/[revision].jar")
        }
        metadataSources {
            artifact()
        }
    }

    ivy {
        url = uri("https://github.com/")
        patternLayout {
            artifact("/[organisation]/[module]/releases/download/[revision]/jabel.jar")
        }
        metadataSources {
            artifact()
        }
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_17
}

// 使用新的 compilerOptions DSL
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)  // 新语法
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

// 变量定义
val mindustryVersion = "v155.4"
val jabelVersion = "93fde537c7"
val isWindows = System.getProperty("os.name").lowercase().contains("windows")
val sdkRoot = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")

// Java 8 兼容
allprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("--release", "8"))
    }
}

dependencies {
    val useLatest = false
    compileOnly(if (useLatest) "Anuken:MindustryBuilds:latest" else "Anuken:Mindustry:$mindustryVersion")
    annotationProcessor("Anuken:jabel:v1.0.0")

    // Kotlin 标准库
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.3.20")
}

// 使用 Exec 任务类型
tasks.register<Exec>("jarAndroid") {
    dependsOn("jar")

    doFirst {
        if (sdkRoot.isNullOrEmpty() || !File(sdkRoot).exists()) {
            throw GradleException("未找到有效的 Android SDK. 请确保 ANDROID_HOME 设置为您的 Android SDK 目录。")
        }

        val platformRoot = File("$sdkRoot/platforms/").listFiles()
            ?.sortedByDescending { it.name }
            ?.firstOrNull { File(it, "android.jar").exists() }

        if (platformRoot == null) {
            throw GradleException("未找到 android.jar. 请确保您已安装 Android 平台。")
        }

        val dependencies = (
            configurations.compileClasspath.get().files +
            configurations.runtimeClasspath.get().files +
            setOf(File(platformRoot, "android.jar"))
        ).joinToString(" ") { "--classpath ${it.path}" }

        val d8 = if (isWindows) "d8.bat" else "d8"

        // 修复 3: 使用 layout.buildDirectory 替代废弃的 buildDir [[21]][[26]]
        workingDir = layout.buildDirectory.get().asFile.resolve("libs")
        commandLine(
            d8,
            *dependencies.split(" ").toTypedArray(),
            "--min-api", "14",
            "--output", "${project.name}Android.jar",
            "${project.name}Desktop.jar"
        )
    }
}

// jar 任务
tasks.jar {
    archiveFileName.set("${project.name}Desktop.jar")

    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })

    from(projectDir) {
        include("mod.hjson")
    }

    from("assets/") {
        include("**")
    }

    from(sourceSets.main.get().output)
}

// deploy 任务
tasks.register<Jar>("deploy") {
    dependsOn("jarAndroid", "jar")
    archiveFileName.set("${project.name}.jar")

    from({
        zipTree(layout.buildDirectory.get().file("libs/${project.name}Desktop.jar").asFile)
        zipTree(layout.buildDirectory.get().file("libs/${project.name}Android.jar").asFile)
    })

    doLast {
        delete(layout.buildDirectory.get().file("libs/${project.name}Android.jar").asFile)
    }
}
