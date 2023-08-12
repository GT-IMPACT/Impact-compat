@file:Suppress("UnstableApiUsage")

import net.minecraftforge.gradle.common.BaseExtension
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils.getFile

plugins {
    id("forge")
    kotlin("jvm")
}

configure<BaseExtension> {
    commonMinecraft(project)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val modName: String by extra
val modId: String by extra
val modGroup: String by extra

val useMixins: String by extra
val forceUseMixins: String by extra

tasks.processResources.configure {
    inputs.property("version", project.version)
    inputs.property("mcversion", "1.7.10")
    filesMatching("mcmod.info") {
        expand(
            "minecraftVersion" to "1.7.10",
            "modId" to modId,
            "modVersion" to project.version,
            "modName" to modName,
        )
    }
    if (useMixins.toBoolean()) {
        dependsOn(tasks["generateAssets"])
    }
}

tasks.register("devJar", Jar::class) {
    from(sourceSets["main"].output)
    archiveClassifier.set("dev")
}

tasks.named<Jar>("jar").configure {
    dependsOn(tasks["devJar"])
}

dependencies {
    implementation(libs.forgelin)
    if (useMixins.toBoolean()) {
        annotationProcessor(libs.processor.asmdebug)
        annotationProcessor(libs.processor.guava)
        annotationProcessor(libs.processor.gson)
        val uniMixins = "io.github.legacymoddingmc:unimixins:0.1.7.1:dev"
        annotationProcessor(uniMixins)
        implementation(uniMixins)
    }
}

if (useMixins.toBoolean() || forceUseMixins.toBoolean()) {
    apply(from = "mixins.gradle")
}
