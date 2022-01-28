import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  dependencies {
    classpath("org.junit.platform:junit-platform-gradle-plugin:1.2.0")
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      events("started", "passed", "skipped", "failed")
      showStandardStreams = false
    }
  }

}
plugins {
  kotlin("jvm") version "1.6.10"
  id("com.squareup.wire") version "4.0.1"
  application
}

repositories {
  maven(url = "https://plugins.gradle.org/m2/")
  mavenCentral()
}

dependencies {
  implementation("com.squareup.misk:misk:0.22.0")
  implementation("com.squareup.misk:misk-actions:0.22.0")
  implementation("com.squareup.misk:misk-admin:0.22.0")
  implementation("com.squareup.misk:misk-core:0.22.0")
  implementation("com.squareup.misk:misk-inject:0.22.0")
  implementation("com.squareup.misk:misk-prometheus:0.22.0")

  testImplementation("com.squareup.misk:misk-testing:0.22.0")
  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.2")
  testImplementation("org.assertj:assertj-core:3.21.0")
}

sourceSets {
  val main by getting {
    java.srcDir("src/main/kotlin/")
  }
}

val jar by tasks.getting(Jar::class) {
  manifest {
    attributes("Main-Class" to "com.squareup.exemplar.ExemplarServiceKt")
  }
  classifier = "unshaded"
}

application {
    mainClassName = "com.squareup.exemplar.ExemplarServiceKt"
}

sourceSets {
  val main by getting {
    java.srcDir("$buildDir/generated/source/wire/")
  }
}

wire {
  sourcePath {
    srcDir("src/main/proto/")
  }

  kotlin {
    javaInterop = true
  }
}

