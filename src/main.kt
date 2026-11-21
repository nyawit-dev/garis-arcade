import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.Size
import ngga.ring.arcade.ui.MenuScene

suspend fun main() = Korge(
    windowSize = Size(640, 480),
    backgroundColor = Colors["#101018"],
    title = "Garis Arcade"
) {
    sceneContainer().changeTo { MenuScene() }
}
