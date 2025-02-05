package net.adoptopenjdk.api.packages

import io.quarkus.test.junit.QuarkusTest
import net.adoptopenjdk.api.v3.models.Architecture
import net.adoptopenjdk.api.v3.models.HeapSize
import net.adoptopenjdk.api.v3.models.ImageType
import net.adoptopenjdk.api.v3.models.JvmImpl
import net.adoptopenjdk.api.v3.models.OperatingSystem
import net.adoptopenjdk.api.v3.models.Project
import net.adoptopenjdk.api.v3.models.ReleaseType
import net.adoptopenjdk.api.v3.models.Vendor
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

@QuarkusTest
class InstallerPathTest : PackageEndpointTest() {

    override fun getPath(): String {
        return "/v3/installer"
    }

    @Test
    fun latestDoesRedirectToBinary() {
        val path = getLatestPath(11, ReleaseType.ga, OperatingSystem.windows, Architecture.x64, ImageType.jdk, JvmImpl.hotspot, HeapSize.normal, Vendor.adoptopenjdk, Project.jdk)
        performRequest(path)
            .then()
            .statusCode(307)
            .header("Location", Matchers.startsWith("https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/"))
    }

    @Test
    fun latestDoesRedirectToBinaryNoProject() {
        val path = "${getPath()}/latest/11/ga/windows/x64/jdk/openj9/normal/adoptopenjdk"
        performRequest(path)
            .then()
            .statusCode(307)
            .header("Location", Matchers.startsWith("https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/"))
    }

    @Test
    fun latestDoesNotRedirectToBinary() {
        val path = getLatestPath(8, ReleaseType.ga, OperatingSystem.windows, Architecture.x64, ImageType.jdk, JvmImpl.hotspot, HeapSize.normal, Vendor.adoptopenjdk, Project.valhalla)
        performRequest(path)
            .then()
            .statusCode(404)
    }

    @Test
    fun noExistantLatestRequestGives404() {
        val path = getLatestPath(4, ReleaseType.ga, OperatingSystem.windows, Architecture.x64, ImageType.jdk, JvmImpl.hotspot, HeapSize.normal, Vendor.adoptopenjdk, Project.valhalla)
        performRequest(path)
            .then()
            .statusCode(404)
    }

    @Test
    fun versionRequestRedirects() {
        val path = getVersionPath("jdk8u212-b04", OperatingSystem.windows, Architecture.x64, ImageType.jdk, JvmImpl.hotspot, HeapSize.normal, Vendor.adoptopenjdk, Project.jdk)
        performRequest(path)
            .then()
            .statusCode(307)
            .header("Location", Matchers.startsWith("https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u212-b04"))
    }

    @Test
    fun nonExistantVersionRequestGives404() {
        val path = getVersionPath("fooBar", OperatingSystem.windows, Architecture.x64, ImageType.jdk, JvmImpl.hotspot, HeapSize.normal, Vendor.adoptopenjdk, Project.jdk)
        performRequest(path)
            .then()
            .statusCode(404)
    }
}
