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
    private var boardX = 0.0
    private var boardY = 0.0
    private var frames = 0
    private var dirty = true

    private lateinit var blocks: Array<Bitmap>
    private lateinit var ghostBitmap: Bitmap
    private lateinit var boardBitmap: Bitmap
    private lateinit var nextBitmap: Bitmap
    private lateinit var board: Container
    private lateinit var preview: Container
    private lateinit var hud: Text
    private lateinit var overlay: Text

    override suspend fun SContainer.sceneMain() {
        val w = sceneWidth
        boardX = (w - TetrisEngine.COLUMNS * cell) / 2.0
        boardY = 220.0

        image(resourcesVfs["images/bg_game.png"].readBitmap()) {
            size(w.toDouble(), sceneHeight.toDouble())
        }
        blocks = (1..7).map { type ->
            resourcesVfs["images/tetris_$type.png"].readBitmap()
        }.toTypedArray()
        ghostBitmap = resourcesVfs["images/tetris_ghost.png"].readBitmap()
        boardBitmap = resourcesVfs["images/tetris_bg.png"].readBitmap()
        nextBitmap = resourcesVfs["images/tetris_next.png"].readBitmap()

        hud = text("", textSize = 18.0, color = Palette.text) {
            position(24.0, 220.0)
        }
        text("NEXT", textSize = 15.0, color = Palette.muted) {
            position(24.0, 430.0)
        }
        preview = container { position(24.0, 458.0) }
        text("PANAH gerak/rotasi", textSize = 14.0, color = Palette.muted) {
            position(24.0, 580.0)
        }
        text("SPACE drop | P pause", textSize = 14.0, color = Palette.muted) {
            position(24.0, 606.0)
        }
        text("R restart | ESC menu", textSize = 14.0, color = Palette.muted) {
            position(24.0, 632.0)
        }
        board = container { position(boardX, boardY) }
        overlay = text("", textSize = 20.0, color = Palette.food) {
            position(boardX - 10.0, boardY + 170.0)
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
        board.image(boardBitmap) {
            position(0.0, 0.0)
            size(TetrisEngine.COLUMNS * cell, TetrisEngine.ROWS * cell)
        }
        for (y in 0 until TetrisEngine.ROWS) {
            for (x in 0 until TetrisEngine.COLUMNS) {
                val type = engine.grid[y][x]
                if (type != 0) drawBlock(board, type, x, y)
            }
        }
        engine.ghost()?.let { ghost ->
            if (ghost != engine.current) {
                engine.cells(ghost).forEach { (x, y) ->
                    if (y >= 0) board.image(ghostBitmap) {
                        position(x * cell, y * cell)
                        size(cell, cell)
                    }
                }
            }
        }
        engine.current?.let { piece ->
            engine.cells(piece).forEach { (x, y) ->
                if (y >= 0) drawBlock(board, piece.type, x, y)
            }
        }

        preview.removeChildren()
        preview.image(nextBitmap) { size(88.0, 88.0) }
        engine.cells(engine.nextPiece).forEach { (x, y) ->
            preview.image(blocks[engine.nextPiece.type - 1]) {
                position(x * 12.0 + 14.0, y * 12.0 + 14.0)
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
