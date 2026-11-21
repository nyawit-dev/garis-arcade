package ngga.ring.arcade.games.snake

import korlibs.event.Key
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.keys
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import ngga.ring.arcade.core.Palette
import ngga.ring.arcade.games.snake.Direction
import ngga.ring.arcade.games.snake.SnakeEngine
import ngga.ring.arcade.ui.MenuScene

/** Renderer + input untuk Snake. Logika ada di [SnakeEngine]. */
class SnakeScene : Scene() {
    private val engine = SnakeEngine()
    private val cell = 20.0
    private var frames = 0

    override suspend fun SContainer.sceneMain() {
        val w = sceneWidth
        val boardW = engine.columns * cell
        val boardH = engine.rows * cell
        val boardX = (w - boardW) / 2.0
        val boardY = 240.0

        image(resourcesVfs["images/bg_game.png"].readBitmap()) {
            size(w.toDouble(), sceneHeight.toDouble())
        }
        image(resourcesVfs["images/board_frame_snake.png"].readBitmap()) {
            position(boardX - 8.0, boardY - 8.0)
            size(boardW + 16.0, boardH + 16.0)
        }
        val headBitmaps = mapOf(
            Direction.UP to resourcesVfs["images/snake_head_up.png"].readBitmap(),
            Direction.DOWN to resourcesVfs["images/snake_head_down.png"].readBitmap(),
            Direction.LEFT to resourcesVfs["images/snake_head_left.png"].readBitmap(),
            Direction.RIGHT to resourcesVfs["images/snake_head_right.png"].readBitmap()
        )
        val bodyBitmap = resourcesVfs["images/snake_body.png"].readBitmap()
        val foodBitmap = resourcesVfs["images/food.png"].readBitmap()
        val glowBitmap = resourcesVfs["images/food_glow.png"].readBitmap()

        val hud = text("SCORE 0", textSize = 24.0, color = Palette.text) {
            position(boardX, 120.0)
        }
        text("PANAH arah | R restart | ESC menu", textSize = 14.0, color = Palette.muted) {
            position(boardX, 170.0)
        }
        val overlay = text("GAME OVER - R", textSize = 24.0, color = Palette.food) {
            position(boardX + boardW / 2 - 90.0, boardY + boardH / 2)
            visible = false
        }
        val board = container { position(boardX, boardY) }

        keys {
            justDown(Key.LEFT) { engine.turn(Direction.LEFT) }
            justDown(Key.RIGHT) { engine.turn(Direction.RIGHT) }
            justDown(Key.UP) { engine.turn(Direction.UP) }
            justDown(Key.DOWN) { engine.turn(Direction.DOWN) }
            justDown(Key.R) { engine.reset(); frames = 0 }
            justDown(Key.ESCAPE) { sceneContainer.changeToAsync<MenuScene>() }
        }

        addFastUpdater {
            frames++
            if (!engine.gameOver && frames >= FRAMES_PER_STEP) {
                frames = 0
                engine.step()
            }
            hud.text = "SCORE ${engine.score}"
            overlay.visible = engine.gameOver

            board.removeChildren()
            board.solidRect(engine.columns * cell, engine.rows * cell, Palette.wall)
            engine.body.forEachIndexed { index, point ->
                val bitmap = if (index == 0) (headBitmaps[engine.direction] ?: bodyBitmap) else bodyBitmap
                board.image(bitmap) {
                    position(point.x * cell, point.y * cell)
                    size(cell, cell)
                }
            }
            board.image(glowBitmap) {
                position(engine.food.x * cell - 2.0, engine.food.y * cell - 2.0)
                size(28.0, 28.0)
            }
            board.image(foodBitmap) {
                position(engine.food.x * cell, engine.food.y * cell)
                size(cell, cell)
            }
        }
    }

    private companion object {
        const val FRAMES_PER_STEP = 7
    }
}
