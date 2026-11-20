import korlibs.korge.gradle.*

plugins {
    alias(libs.plugins.korge)
}

korge {
    id = "dev.nyawit.garisarcade"
    targetJvm()
    targetAndroid()
    serializationJson()
}
