import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.KorgeDisplayMode
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.Anchor
import korlibs.math.geom.ScaleMode
import korlibs.math.geom.Size
import ngga.ring.arcade.ui.MenuScene

/** EXACT membuat sceneWidth/sceneHeight mengikuti layar asli HP/tablet. */
suspend fun main() = Korge(
    windowSize = Size(480, 720),
    virtualSize = Size(480, 720),
    displayMode = KorgeDisplayMode(ScaleMode.EXACT, Anchor.TOP_LEFT, clipBorders = false),
    backgroundColor = Colors["#101018"],
    title = "Garis Arcade"
) {
    sceneContainer().changeTo { MenuScene() }
}
