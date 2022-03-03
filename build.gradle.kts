import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

buildscript {
	repositories {
		mavenCentral()
	}
}

plugins {
	id("org.springframework.boot") version "2.6.4" apply false
	id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
	kotlin("jvm") version Versions.KOTLIN_VERSION
	kotlin("plugin.spring") version "1.6.10" apply false
	id("nebula.netflixoss") version "10.5.1"
	id("org.jmailen.kotlinter") version "3.6.0"
	// kotlin("kapt") version Versions.KOTLIN_VERSION apply false
	id("com.netflix.dgs.codegen") version "5.1.17" apply false
	idea
	eclipse
}

allprojects {
	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("nebula.netflixoss")
	}

	group = "com.anthaathi"
	version = "0.0.1-SNAPSHOT"

	java.sourceCompatibility = JavaVersion.VERSION_11

	repositories {
		mavenCentral()
	}

	configurations {
		compileOnly {
			extendsFrom(configurations.annotationProcessor.get())
		}
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "11"
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}

val springServices by extra(
	listOf(
		project(":anthaathi-engine-ui-builder"),
		project(":anthaathi-engine-jpa-graphql-mapper"),
	)
)

configure(subprojects.filter { it in springServices }) {
	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("org.jetbrains.kotlin.plugin.spring")
		plugin("nebula.netflixoss")
		plugin("org.jmailen.kotlinter")
	}

	dependencies {
		implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
		implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:${ Versions.DGS_VERSION }"))
		implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
		testImplementation("org.assertj:assertj-core:3.11.1")
		testImplementation("io.mockk:mockk:1.12.3")
		testImplementation("io.github.origin-energy:java-snapshot-testing-junit5:3.2.+")
		testImplementation("org.slf4j:slf4j-simple:2.0.0-alpha6")
	}

	tasks.named("generateLock") {
		doFirst {
			project.configurations.filter { it.name.contains("DependenciesMetadata") }.forEach {
				it.isCanBeResolved = false
			}
		}
	}
}

val graphqlServices by extra(
	listOf(
		project(":anthaathi-engine-ui-builder"),
	)
)

configure(subprojects.filter { it in graphqlServices }) {
	apply {
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
		plugin("com.netflix.dgs.codegen")
	}

	// This is hack and somehow working one
	val developmentOnly = configurations.getByName("developmentOnly")

	dependencies {
		annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
		developmentOnly("org.springframework.boot:spring-boot-devtools")
		developmentOnly("org.springframework.boot:spring-boot-starter-actuator")
		runtimeOnly("io.micrometer:micrometer-registry-prometheus")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
	}
}
