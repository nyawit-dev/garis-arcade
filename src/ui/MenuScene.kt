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

/**
 * Menu utama. Menambah game baru cukup menambah satu [MenuItem] ke [items]
 * dan membuat Scene game tersebut.
 */
class MenuScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        image(resourcesVfs["images/logo.png"].readBitmap()) {
            position(sceneWidth / 2 - 128, 40.0)
        }
        text("Pilih game jadul", textSize = 20.0, color = Palette.muted) {
            position(sceneWidth / 2 - 76, 108.0)
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

        items.forEachIndexed { index, item ->
            val y = 148.0 + index * 72.0
            val card = container { position(sceneWidth / 2 - 190, y) }
            card.solidRect(380.0, 56.0, Palette.panel) {
                onClickSuspend { item.launch() }
            }
            card.text(item.title, textSize = 24.0, color = Palette.accent) {
                position(18.0, 10.0)
                onClickSuspend { item.launch() }
            }
            card.text(item.subtitle, textSize = 13.0, color = Palette.muted) {
                position(150.0, 20.0)
                onClickSuspend { item.launch() }
            }
        }

        text("Klik kartu untuk mulai", textSize = 14.0, color = Palette.muted) {
            position(sceneWidth / 2 - 92, 424.0)
        }
    }
}

data class MenuItem(
    val title: String,
    val subtitle: String,
    val launch: suspend () -> Unit
)
