package ngga.ring.arcade.games.snake

import korlibs.event.Key
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.keys
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import ngga.ring.arcade.core.Layout
import ngga.ring.arcade.core.Palette
import ngga.ring.arcade.ui.MenuScene

/** Renderer + input Snake. Logika ada di [SnakeEngine]. */
class SnakeScene : Scene() {
    private val engine = SnakeEngine()
    private var frames = 0

    override suspend fun SContainer.sceneMain() {
        val cfg = Layout.compute(sceneWidth, sceneHeight)
        val root = container {
            position(cfg.offsetX, cfg.offsetY)
            scale(cfg.scale)
        }
        val cell = if (cfg.tablet) 30.0 else 20.0
        val boardW = engine.columns * cell
        val boardH = engine.rows * cell
        val boardX = (cfg.width - boardW) / 2.0
        val boardY = if (cfg.tablet) 400.0 else 240.0

        root.image(resourcesVfs["images/bg_game.png"].readBitmap()) {
            size(cfg.width, cfg.height)
        }
        root.image(resourcesVfs["images/board_frame_snake.png"].readBitmap()) {
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

        val hud = root.text("SCORE 0", textSize = if (cfg.tablet) 32.0 else 24.0, color = Palette.text) {
            position(boardX, boardY - 120.0)
        }
        root.text("PANAH arah | R restart | ESC menu", textSize = if (cfg.tablet) 18.0 else 14.0, color = Palette.muted) {
            position(boardX, boardY - 70.0)
        }
        val overlay = root.text("GAME OVER - R", textSize = if (cfg.tablet) 30.0 else 24.0, color = Palette.food) {
            position(boardX + boardW / 2 - 100.0, boardY + boardH / 2)
            visible = false
        }
        val board = root.container { position(boardX, boardY) }

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
            board.solidRect(boardW, boardH, Palette.wall)
            engine.body.forEachIndexed { index, point ->
                val bitmap = if (index == 0) (headBitmaps[engine.direction] ?: bodyBitmap) else bodyBitmap
                board.image(bitmap) {
                    position(point.x * cell, point.y * cell)
                    size(cell, cell)
                }
            }
            board.image(glowBitmap) {
                position(engine.food.x * cell - 2.0, engine.food.y * cell - 2.0)
                size(cell + 8.0, cell + 8.0)
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
