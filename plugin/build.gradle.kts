plugins {
    `java-gradle-plugin`
    id("org.jetbrains.kotlin.jvm") version "1.4.31"
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.15.0"
}

repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

group = "de.kwerber.ghpub"
version = "0.2"

gradlePlugin {
    val ghpub by plugins.creating {
        id = "de.kwerber.ghpub"
        displayName = "GradleGithubPublisher"
        description = "Use this plugin to publish maven artifacts to a github repository"
        implementationClass = "de.kwerber.gghp.GradleGithubPublisherPlugin"
    }
}

pluginBundle {
    website = "https://github.com/kwerber/"
    tags = listOf("github", "publish", "maven")
    vcsUrl = "https://github.com/kwerber/GradleGithubPublisher"
}

// Add a source set for the functional test suite
val functionalTestSourceSet = sourceSets.create("functionalTest") { }

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

// Add a task to run the functional tests
val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

tasks.check {
    // Run the functional tests as part of `check`
    dependsOn(functionalTest)
}

publishing {
    repositories {
        maven {
            name = "github-repo"
            url = projectDir.resolve("build").resolve("local_maven_repo").toURI()
        }
    }
}