package ngga.ring.arcade.games.twozerofour

import korlibs.event.Key
import korlibs.korge.input.keys
import korlibs.korge.scene.Scene
import korlibs.korge.view.SolidRect
import korlibs.korge.view.Text
import korlibs.korge.view.container
import korlibs.korge.view.position
import korlibs.korge.view.solidRect
import korlibs.korge.view.text
import ngga.ring.arcade.core.Palette
import ngga.ring.arcade.ui.MenuScene

/** Renderer + input untuk 2048. Logika ada di [Board2048]. */
class TwoZeroFourEightScene : Scene() {
    private val board = Board2048()
    private val cell = 84.0

    private lateinit var tiles: Array<Array<SolidRect>>
    private lateinit var labels: Array<Array<Text>>
    private lateinit var hud: Text
    private lateinit var overlay: Text

    override suspend fun SContainer.sceneMain() {
        hud = text("SCORE 0", textSize = 20.0, color = Palette.text) {
            position(152.0, 18.0)
        }
        overlay = text("", textSize = 22.0, color = Palette.food) {
            position(170.0, 430.0)
            visible = false
        }

        val grid = container { position(152.0, 62.0) }
        tiles = Array(board.size) { row ->
            Array(board.size) { col ->
                grid.solidRect(cell - 6.0, cell - 6.0, Palette.tile(0)) {
                    position(col * cell, row * cell)
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
                tiles[row][col].color = Palette.tile(value)
                labels[row][col].text = if (value == 0) "" else value.toString()
            }
        }
        overlay.visible = !board.canMove()
        if (overlay.visible) overlay.text = "HABIS - tekan R"
    }
}
