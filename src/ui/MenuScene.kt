package ngga.ring.arcade.ui

import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.onClick
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import ngga.ring.arcade.core.Layout
import ngga.ring.arcade.core.Palette
import ngga.ring.arcade.games.snake.SnakeScene
import ngga.ring.arcade.games.tetris.TetrisScene
import ngga.ring.arcade.games.twozerofour.TwoZeroFourEightScene

/**
 * Menu utama. Satu kartu = satu target klik (menghindari beberapa handler
 * bertabrakan saat transisi scene).
 */
class MenuScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val cfg = Layout.compute(sceneWidth, sceneHeight)
        val root = container {
            position(cfg.offsetX, cfg.offsetY)
            scale(cfg.scale)
        }
        val w = cfg.width
        val h = cfg.height

        root.image(resourcesVfs["images/bg_menu.png"].readBitmap()) {
            size(w, h)
        }
        val cardBitmap = resourcesVfs["images/menu_card.png"].readBitmap()

        root.image(resourcesVfs["images/logo.png"].readBitmap()) {
            position((w - 280) / 2.0, if (cfg.tablet) 110.0 else 80.0)
            size(280.0, 70.0)
        }
        root.text("Pilih game jadul", textSize = if (cfg.tablet) 28.0 else 22.0, color = Palette.muted) {
            position((w - 200) / 2.0, if (cfg.tablet) 220.0 else 180.0)
        }

        val items = listOf(
            MenuItem("SNAKE", "Makan, tumbuh, jangan tabrak") {
                sceneContainer.changeToAsync<SnakeScene>()
            },
            MenuItem("2048", "Gabung angka sampai 2048") {
                sceneContainer.changeToAsync<TwoZeroFourEightScene>()
            },
            MenuItem("TETRIS", "Susun balok, habisi baris") {
                sceneContainer.changeToAsync<TetrisScene>()
            }
        )

        val cardW = if (cfg.tablet) 560.0 else 400.0
        val cardH = if (cfg.tablet) 84.0 else 64.0
        val startY = if (cfg.tablet) 340.0 else 260.0
        val gap = if (cfg.tablet) 140.0 else 96.0

        items.forEachIndexed { index, item ->
            val y = startY + index * gap
            val card = root.container { position((w - cardW) / 2.0, y) }
            card.image(cardBitmap) {
                size(cardW, cardH)
                onClick { item.launch() }
            }
            card.text(item.title, textSize = if (cfg.tablet) 32.0 else 26.0, color = Palette.accent) {
                position(20.0, if (cfg.tablet) 24.0 else 19.0)
            }
            card.text(item.subtitle, textSize = if (cfg.tablet) 18.0 else 14.0, color = Palette.muted) {
                position(if (cfg.tablet) 230.0 else 190.0, if (cfg.tablet) 34.0 else 26.0)
            }
        }

        root.text("Ketuk kartu untuk mulai", textSize = 16.0, color = Palette.muted) {
            position((w - 230) / 2.0, h - 60.0)
        }
    }
}

data class MenuItem(
    val title: String,
    val subtitle: String,
    val launch: () -> Unit
)
