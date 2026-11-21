package ngga.ring.arcade.ui

import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.onClickSuspend
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import ngga.ring.arcade.core.Palette
import ngga.ring.arcade.games.snake.SnakeScene
import ngga.ring.arcade.games.tetris.TetrisScene
import ngga.ring.arcade.games.twozerofour.TwoZeroFourEightScene

/** Menu utama. Semua posisi dihitung dari sceneWidth/sceneHeight (virtual 480x720). */
class MenuScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val w = sceneWidth
        val h = sceneHeight

        image(resourcesVfs["images/bg_menu.png"].readBitmap()) {
            size(w.toDouble(), h.toDouble())
        }
        val cardBitmap = resourcesVfs["images/menu_card.png"].readBitmap()

        image(resourcesVfs["images/logo.png"].readBitmap()) {
            position((w - 280) / 2.0, 80.0)
            size(280.0, 70.0)
        }
        text("Pilih game jadul", textSize = 22.0, color = Palette.muted) {
            position((w - 180) / 2.0, 180.0)
        }

        val items = listOf(
            MenuItem("SNAKE", "Makan, tumbuh, jangan tabrak") {
                sceneContainer.changeTo<SnakeScene>()
            },
            MenuItem("2048", "Gabung angka sampai 2048") {
                sceneContainer.changeTo<TwoZeroFourEightScene>()
            },
            MenuItem("TETRIS", "Susun balok, habisi baris") {
                sceneContainer.changeTo<TetrisScene>()
            }
        )

        val cardW = 400.0
        val cardH = 64.0
        items.forEachIndexed { index, item ->
            val y = 260.0 + index * 96.0
            val card = container { position((w - cardW) / 2.0, y) }
            card.image(cardBitmap) {
                size(cardW, cardH)
                onClickSuspend { item.launch() }
            }
            card.text(item.title, textSize = 26.0, color = Palette.accent) {
                position(20.0, 19.0)
                onClickSuspend { item.launch() }
            }
            card.text(item.subtitle, textSize = 14.0, color = Palette.muted) {
                position(190.0, 26.0)
                onClickSuspend { item.launch() }
            }
        }

        text("Ketuk kartu untuk mulai", textSize = 16.0, color = Palette.muted) {
            position((w - 220) / 2.0, 650.0)
        }
    }
}

data class MenuItem(
    val title: String,
    val subtitle: String,
    val launch: suspend () -> Unit
)
