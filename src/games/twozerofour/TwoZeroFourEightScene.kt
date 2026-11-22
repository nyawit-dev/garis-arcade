package ngga.ring.arcade.games.twozerofour

import korlibs.event.Key
import korlibs.image.bitmap.*
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.keys
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import ngga.ring.arcade.core.Layout
import ngga.ring.arcade.core.Palette
import ngga.ring.arcade.ui.MenuScene

/** Renderer + input 2048. Logika ada di [Board2048]. */
class TwoZeroFourEightScene : Scene() {
    private val board = Board2048()

    private lateinit var tiles: Array<Array<Image>>
    private lateinit var labels: Array<Array<Text>>
    private lateinit var hud: Text
    private lateinit var overlay: Text
    private lateinit var tileBitmaps: Map<Int, Bitmap>

    override suspend fun SContainer.sceneMain() {
        val cfg = Layout.compute(sceneWidth, sceneHeight)
        val root = container {
            position(cfg.offsetX, cfg.offsetY)
            scale(cfg.scale)
        }
        val cell = if (cfg.tablet) 130.0 else 84.0
        val gridSize = board.size * cell
        val gridX = (cfg.width - gridSize) / 2.0
        val gridY = if (cfg.tablet) 420.0 else 240.0

        root.image(resourcesVfs["images/bg_game.png"].readBitmap()) {
            size(cfg.width, cfg.height)
        }
        val values = listOf(0, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048)
        tileBitmaps = values.associateWith { value ->
            resourcesVfs["images/tile_$value.png"].readBitmap()
        }
        val boardBitmap = resourcesVfs["images/board_2048.png"].readBitmap()

        hud = root.text("SCORE 0", textSize = if (cfg.tablet) 32.0 else 24.0, color = Palette.text) {
            position(gridX, gridY - 120.0)
        }
        root.text("Geser angka | R restart | ESC menu", textSize = if (cfg.tablet) 18.0 else 14.0, color = Palette.muted) {
            position(gridX, gridY - 70.0)
        }
        overlay = root.text("", textSize = if (cfg.tablet) 28.0 else 22.0, color = Palette.food) {
            position(gridX + 20.0, gridY + gridSize + 40.0)
            visible = false
        }

        val grid = root.container { position(gridX, gridY) }
        grid.image(boardBitmap) { position(0.0, 0.0); size(gridSize, gridSize) }
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
                grid.text("", textSize = if (cfg.tablet) 44.0 else 30.0, color = Palette.text) {
                    position(col * cell + cell * 0.31, row * cell + cell * 0.29)
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
                tiles[row][col].bitmap = tileBitmaps[value]!!.slice()
                labels[row][col].text = if (value == 0) "" else value.toString()
                labels[row][col].color = Palette.tileText(value)
            }
        }
        overlay.visible = !board.canMove()
        if (overlay.visible) overlay.text = "HABIS - tekan R"
    }
}
