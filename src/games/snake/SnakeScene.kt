package ngga.ring.arcade.games.snake

import korlibs.event.Key
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.keys
import korlibs.korge.scene.Scene
import korlibs.korge.view.addUpdater
import korlibs.korge.view.container
import korlibs.korge.view.image
import korlibs.korge.view.position
import korlibs.korge.view.size
import korlibs.korge.view.solidRect
import korlibs.korge.view.text
import ngga.ring.arcade.core.Palette
import ngga.ring.arcade.games.snake.Direction
import ngga.ring.arcade.games.snake.SnakeEngine
import ngga.ring.arcade.ui.MenuScene

/** Renderer + input untuk Snake. Logika ada di [SnakeEngine]. */
class SnakeScene : Scene() {
    private val engine = SnakeEngine()
    private val cell = 24.0
    private var accumulator = 0.0

    override suspend fun SContainer.sceneMain() {
        val headBitmap = resourcesVfs["images/snake_head.png"].readBitmap()
        val bodyBitmap = resourcesVfs["images/snake_body.png"].readBitmap()
        val foodBitmap = resourcesVfs["images/food.png"].readBitmap()

        val hud = text("SCORE 0", textSize = 20.0, color = Palette.text) {
            position(68.0, 18.0)
        }
        val overlay = text("GAME OVER - tekan R", textSize = 22.0, color = Palette.food) {
            position(170.0, 250.0)
            visible = false
        }
        val board = container { position(68.0, 56.0) }

        keys {
            justDown(Key.LEFT) { engine.turn(Direction.LEFT) }
            justDown(Key.RIGHT) { engine.turn(Direction.RIGHT) }
            justDown(Key.UP) { engine.turn(Direction.UP) }
            justDown(Key.DOWN) { engine.turn(Direction.DOWN) }
            justDown(Key.R) { engine.reset(); accumulator = 0.0 }
            justDown(Key.ESCAPE) { sceneContainer.changeToAsync<MenuScene>() }
        }

        addUpdater { dt ->
            if (!engine.gameOver) {
                accumulator += dt.inWholeMilliseconds / 1000.0
                while (accumulator >= STEP_SECONDS) {
                    accumulator -= STEP_SECONDS
                    engine.step()
                }
            }
            hud.text = "SCORE ${engine.score}"
            overlay.visible = engine.gameOver

            board.removeChildren()
            board.solidRect(engine.columns * cell, engine.rows * cell, Palette.wall)
            engine.body.forEachIndexed { index, point ->
                val bitmap = if (index == 0) headBitmap else bodyBitmap
                board.image(bitmap) {
                    position(point.x * cell, point.y * cell)
                    size(cell, cell)
                }
            }
            board.image(foodBitmap) {
                position(engine.food.x * cell, engine.food.y * cell)
                size(cell, cell)
            }
        }
    }

    private companion object {
        const val STEP_SECONDS = 0.12
    }
}
