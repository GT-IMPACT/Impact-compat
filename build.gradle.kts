
plugins {
    alias(libs.plugins.buildconfig)
    groovy
    id("minecraft")
    id("publish")
}

repositories {
    maven("https://maven.accident.space/repository/maven-public/")
    maven("http://jenkins.usrv.eu:8081/nexus/content/groups/public/") { isAllowInsecureProtocol = true }
    maven("https://jitpack.io")
    maven("https://cursemaven.com")
    maven("https://maven.ic2.player.to/") { metadataSources { mavenPom(); artifact() } }
    mavenCentral()
    mavenLocal()
}

val modId: String by extra
val modName: String by extra
val modGroup: String by extra

buildConfig {
    packageName("space.impact.$modId")
    buildConfigField("String", "MODID", "\"${modId}\"")
    buildConfigField("String", "MODNAME", "\"${modName}\"")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
    buildConfigField("String", "GROUPNAME", "\"${modGroup}\"")
    useKotlinOutput { topLevelConstants = true }
}

dependencies {
    api("com.github.GTNewHorizons:ModularUI:1.1.10:dev")
    api("space.impact:packet_network:1.1.+:dev")
    api("space.impact:GT5-Unofficial:5.09.44.5:dev") {
        exclude("com.github.GTNewHorizons", "waila")
    }
    implementation(fileTree(mapOf("dir" to "libs/", "include" to listOf("*.jar"))))
}