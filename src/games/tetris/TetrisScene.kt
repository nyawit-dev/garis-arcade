package ngga.ring.arcade.games.tetris

import korlibs.event.Key
import korlibs.image.bitmap.Bitmap
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.keys
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import ngga.ring.arcade.core.Palette
import ngga.ring.arcade.ui.MenuScene

/** Render + input Tetris. Semua aturan ada di [TetrisEngine]. */
class TetrisScene : Scene() {
    private val engine = TetrisEngine()
    private val cell = 20.0
    private val boardX = 214.0
    private val boardY = 44.0
    private var frames = 0
    private var dirty = true

    private lateinit var blocks: Array<Bitmap>
    private lateinit var board: Container
    private lateinit var preview: Container
    private lateinit var hud: Text
    private lateinit var overlay: Text

    override suspend fun SContainer.sceneMain() {
        blocks = (1..7).map { type ->
            resourcesVfs["images/tetris_$type.png"].readBitmap()
        }.toTypedArray()

        hud = text("", textSize = 16.0, color = Palette.text) {
            position(24.0, 44.0)
        }
        text("NEXT", textSize = 14.0, color = Palette.muted) {
            position(24.0, 190.0)
        }
        preview = container { position(24.0, 214.0) }
        text("PANAH gerak/rotasi | SPACE drop", textSize = 12.0, color = Palette.muted) {
            position(24.0, 320.0)
        }
        text("P pause | R restart | ESC menu", textSize = 12.0, color = Palette.muted) {
            position(24.0, 342.0)
        }
        board = container { position(boardX, boardY) }
        overlay = text("", textSize = 20.0, color = Palette.food) {
            position(boardX + 24.0, boardY + 210.0)
            visible = false
        }

        keys {
            justDown(Key.LEFT) { engine.move(-1); dirty = true }
            justDown(Key.RIGHT) { engine.move(1); dirty = true }
            justDown(Key.UP) { engine.rotate(); dirty = true }
            justDown(Key.DOWN) { engine.softDrop(); dirty = true }
            justDown(Key.SPACE) { engine.hardDrop(); dirty = true }
            justDown(Key.P) { engine.togglePause(); dirty = true }
            justDown(Key.R) { engine.reset(); frames = 0; dirty = true }
            justDown(Key.ESCAPE) { sceneContainer.changeToAsync<MenuScene>() }
        }

        addFastUpdater {
            frames++
            if (!engine.paused && !engine.gameOver && frames >= framesPerStep()) {
                frames = 0
                engine.step()
                dirty = true
            }
            if (dirty) {
                render()
                dirty = false
            }
        }
        render()
    }

    private fun framesPerStep(): Int = when {
        engine.level >= 10 -> 3
        engine.level >= 6 -> 4
        engine.level >= 3 -> 5
        else -> 7
    }

    private fun render() {
        hud.text = "SCORE ${engine.score}\nLINES ${engine.lines}\nLEVEL ${engine.level}"
        board.removeChildren()
        board.solidRect(
            TetrisEngine.COLUMNS * cell,
            TetrisEngine.ROWS * cell,
            Palette.wall
        )
        for (y in 0 until TetrisEngine.ROWS) {
            for (x in 0 until TetrisEngine.COLUMNS) {
                val type = engine.grid[y][x]
                if (type != 0) drawBlock(board, type, x, y)
            }
        }
        engine.current?.let { piece ->
            engine.cells(piece).forEach { (x, y) ->
                if (y >= 0) drawBlock(board, piece.type, x, y)
            }
        }

        preview.removeChildren()
        engine.cells(engine.nextPiece).forEach { (x, y) ->
            preview.image(blocks[engine.nextPiece.type - 1]) {
                position(x * 12.0, y * 12.0)
                size(12.0, 12.0)
            }
        }

        overlay.visible = engine.gameOver || engine.paused
        overlay.text = when {
            engine.gameOver -> "GAME OVER - R"
            engine.paused -> "PAUSE - P"
            else -> ""
        }
    }

    private fun drawBlock(parent: Container, type: Int, x: Int, y: Int) {
        parent.image(blocks[type - 1]) {
            position(x * cell, y * cell)
            size(cell, cell)
        }
    }
}
