package ngga.ring.arcade.games.snake

import kotlin.random.Random

data class Point(val x: Int, val y: Int)

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

    fun isOpposite(other: Direction) = dx + other.dx == 0 && dy + other.dy == 0
}

/**
 * Logika Snake murni Kotlin: tidak tahu apa-apa soal gambar atau frame.
 * Mudah dites dan dipakai ulang di platform lain.
 */
class SnakeEngine(
    val columns: Int = 20,
    val rows: Int = 16,
    private val random: Random = Random.Default
) {
    var body: List<Point> = listOf(Point(columns / 2, rows / 2))
        private set
    var direction: Direction = Direction.RIGHT
        private set
    var food: Point = Point(4, 4)
        private set
    var score: Int = 0
        private set
    var gameOver: Boolean = false
        private set

    init {
        food = spawnFood()
    }

    fun turn(next: Direction) {
        if (!gameOver && !next.isOpposite(direction)) direction = next
    }

    /** Satu langkah logika. Return true kalau ular masih hidup. */
    fun step(): Boolean {
        if (gameOver) return false
        val head = body.first()
        val next = Point(
            (head.x + direction.dx + columns) % columns,
            (head.y + direction.dy + rows) % rows
        )
        if (next in body) {
            gameOver = true
            return false
        }
        val grows = next == food
        body = if (grows) listOf(next) + body else listOf(next) + body.dropLast(1)
        if (grows) {
            score += 10
            food = spawnFood()
        }
        return true
    }

    fun reset() {
        body = listOf(Point(columns / 2, rows / 2))
        direction = Direction.RIGHT
        score = 0
        gameOver = false
        food = spawnFood()
    }

    private fun spawnFood(): Point {
        var candidate: Point
        do {
            candidate = Point(random.nextInt(columns), random.nextInt(rows))
        } while (candidate in body)
        return candidate
    }
}
