import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.KorgeDisplayMode
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.Anchor
import korlibs.math.geom.ScaleMode
import korlibs.math.geom.Size
import ngga.ring.arcade.ui.MenuScene

/**
 * Ukuran virtual tetap (portrait 480x720). KorGE menskalakannya ke layar HP
 * mana pun lewat SHOW_ALL, jadi semua scene bisa pakai sceneWidth/sceneHeight.
 */
suspend fun main() = Korge(
    windowSize = Size(480, 720),
    virtualSize = Size(480, 720),
    displayMode = KorgeDisplayMode(ScaleMode.SHOW_ALL, Anchor.CENTER, clipBorders = false),
    backgroundColor = Colors["#101018"],
    title = "Garis Arcade"
) {
    try {
        sceneContainer().changeTo { MenuScene() }
    } catch (t: Throwable) {
        // Jangan force-close: tampilkan pesan kalau inisialisasi scene gagal.
        korlibs.korge.view.text("Gagal memulai: ${t.message}", textSize = 20.0) {
            position(24.0, 60.0)
        }
    }
}
