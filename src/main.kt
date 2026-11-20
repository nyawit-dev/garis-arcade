import korlibs.korge.Korge
import korlibs.korge.scene.Scene
import korlibs.korge.scene.sceneContainer
import korlibs.korge.view.centerX
import korlibs.korge.view.text
import korlibs.image.color.Colors
import korlibs.math.geom.Size

suspend fun main() = Korge(
    windowSize = Size(512, 512),
    backgroundColor = Colors["#1a1a2e"],
    title = "Garis Arcade"
) {
    sceneContainer().changeTo { MenuScene() }
}

class MenuScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        text("GARIS ARCADE", size = 42f, color = Colors.WHITE) {
            centerX()
            y = 140.0
        }
        text("KorGE • Kotlin Multiplatform", size = 20f, color = Colors["#a0a0c0"]) {
            centerX()
            y = 210.0
        }
    }
}
