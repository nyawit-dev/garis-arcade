package ngga.ring.arcade.games.twozerofour

import korlibs.event.Key
import korlibs.korge.input.keys
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import ngga.ring.arcade.core.Palette
import ngga.ring.arcade.ui.MenuScene

/** Renderer + input untuk 2048. Logika ada di [Board2048]. */
class TwoZeroFourEightScene : Scene() {
    private val board = Board2048()
    private val cell = 84.0

    private lateinit var tiles: Array<Array<Image>>
    private lateinit var labels: Array<Array<Text>>
    private lateinit var hud: Text
    private lateinit var overlay: Text
    private lateinit var tileBitmaps: Map<Int, korlibs.image.bitmap.Bitmap>

    override suspend fun SContainer.sceneMain() {
        image(resourcesVfs["images/bg_game.png"].readBitmap()) { position(0.0, 0.0) }
        val values = listOf(0, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048)
        tileBitmaps = values.associateWith { value ->
            resourcesVfs["images/tile_$value.png"].readBitmap()
        }
        val boardBitmap = resourcesVfs["images/board_2048.png"].readBitmap()

        hud = text("SCORE 0", textSize = 20.0, color = Palette.text) {
            position(152.0, 18.0)
        }
        overlay = text("", textSize = 22.0, color = Palette.food) {
            position(170.0, 430.0)
            visible = false
        }

        val grid = container { position(152.0, 62.0) }
        grid.image(boardBitmap) { position(0.0, 0.0); size(348.0, 348.0) }
        tiles = Array(board.size) { row ->
            Array(board.size) { col ->
                grid.image(tileBitmaps[0]!!) {
                    position(col * cell, row * cell)
                    size(cell - 6.0, cell - 6.0)
                }
            }
        }
        labels = Array(board.size) { row ->
            Array(board.size) { col ->
                grid.text("", textSize = 30.0, color = Palette.text) {
                    position(col * cell + 24.0, row * cell + 22.0)
                }
            }
        }

        keys {
            justDown(Key.LEFT) { applyMove(MoveDirection.LEFT) }
            justDown(Key.RIGHT) { applyMove(MoveDirection.RIGHT) }
            justDown(Key.UP) { applyMove(MoveDirection.UP) }
            justDown(Key.DOWN) { applyMove(MoveDirection.DOWN) }
            justDown(Key.R) { board.reset(); render() }
            justDown(Key.ESCAPE) { sceneContainer.changeToAsync<MenuScene>() }
        }
        render()
    }

    private fun applyMove(direction: MoveDirection) {
        board.move(direction)
        render()
    }

    private fun render() {
        hud.text = "SCORE ${board.score}"
        for (row in 0 until board.size) {
            for (col in 0 until board.size) {
                val value = board.cells[row][col]
                tiles[row][col].bitmap = tileBitmaps[value]!!
                labels[row][col].text = if (value == 0) "" else value.toString()
                labels[row][col].color = Palette.tileText(value)
            }
        }
        overlay.visible = !board.canMove()
        if (overlay.visible) overlay.text = "HABIS - tekan R"
    }
}
