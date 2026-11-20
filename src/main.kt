import korlibs.korge.Korge
import korlibs.korge.scene.Scene
import korlibs.korge.scene.sceneContainer
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
        text("GARIS ARCADE", textSize = 42.0, color = Colors.WHITE) {
            x = 92.0
            y = 140.0
        }
        text("KorGE - Kotlin Multiplatform", textSize = 20.0, color = Colors["#a0a0c0"]) {
            x = 96.0
            y = 210.0
        }
    }
}
