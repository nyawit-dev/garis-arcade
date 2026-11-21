package ngga.ring.arcade.games.tetris

import kotlin.random.Random

data class Tetromino(
    val type: Int,
    val rotation: Int = 0,
    val x: Int = 3,
    val y: Int = 0
)

/**
 * Logika Tetris murni Kotlin: grid 10x20, 7-bag randomizer, rotasi dalam kotak 4x4,
 * line clear, skor dan level. Tidak ada import view/framework.
 */
class TetrisEngine(private val random: Random = Random.Default) {
    companion object {
        const val COLUMNS = 10
        const val ROWS = 20
        private val LINE_SCORES = intArrayOf(0, 100, 300, 500, 800)
        private val SHAPES = mapOf(
            1 to listOf(0 to 1, 1 to 1, 2 to 1, 3 to 1), // I
            2 to listOf(1 to 0, 2 to 0, 1 to 1, 2 to 1), // O
            3 to listOf(1 to 0, 0 to 1, 1 to 1, 2 to 1), // T
            4 to listOf(1 to 0, 2 to 0, 0 to 1, 1 to 1), // S
            5 to listOf(0 to 0, 1 to 0, 1 to 1, 2 to 1), // Z
            6 to listOf(0 to 0, 0 to 1, 1 to 1, 2 to 1), // J
            7 to listOf(2 to 0, 0 to 1, 1 to 1, 2 to 1)  // L
        )
    }

    var grid: Array<IntArray> = Array(ROWS) { IntArray(COLUMNS) }
        private set
    var current: Tetromino? = null
        private set
    var nextPiece: Tetromino = Tetromino(1)
        private set
    var score: Int = 0
        private set
    var lines: Int = 0
        private set
    var level: Int = 1
        private set
    var gameOver: Boolean = false
        private set
    var paused: Boolean = false
        private set

    private var bag = mutableListOf<Int>()

    init {
        nextPiece = drawPiece()
        spawn()
    }

    fun reset() {
        grid = Array(ROWS) { IntArray(COLUMNS) }
        bag.clear()
        score = 0
        lines = 0
        level = 1
        gameOver = false
        paused = false
        nextPiece = drawPiece()
        spawn()
    }

    fun togglePause() {
        if (!gameOver) paused = !paused
    }

    fun move(dx: Int): Boolean {
        val piece = current ?: return false
        if (paused || gameOver) return false
        val candidate = piece.copy(x = piece.x + dx)
        if (!isValid(candidate)) return false
        current = candidate
        return true
    }

    fun rotate(): Boolean {
        val piece = current ?: return false
        if (paused || gameOver) return false
        if (piece.type == 2) return true // O tidak berubah
        for (kick in listOf(0, -1, 1, -2, 2)) {
            val candidate = piece.copy(rotation = (piece.rotation + 1) % 4, x = piece.x + kick)
            if (isValid(candidate)) {
                current = candidate
                return true
            }
        }
        return false
    }

    /** Gravity: satu langkah ke bawah. Return false kalau piece terkunci. */
    fun step(): Boolean {
        val piece = current ?: return false
        if (paused || gameOver) return false
        val candidate = piece.copy(y = piece.y + 1)
        if (isValid(candidate)) {
            current = candidate
            return true
        }
        lock(piece)
        clearLines()
        spawn()
        return false
    }

    /** Posisi jatuh bayangan (ghost) tanpa mengubah state. */
    fun ghost(): Tetromino? {
        val piece = current ?: return null
        var candidate = piece
        while (true) {
            val next = candidate.copy(y = candidate.y + 1)
            if (!isValid(next)) break
            candidate = next
        }
        return candidate
    }

    fun softDrop() {
        step()
    }

    fun hardDrop() {
        val piece = current ?: return
        if (paused || gameOver) return
        var dropped = piece
        while (true) {
            val candidate = dropped.copy(y = dropped.y + 1)
            if (!isValid(candidate)) break
            dropped = candidate
        }
        score += (dropped.y - piece.y) * 2
        lock(dropped)
        clearLines()
        spawn()
    }

    fun cells(piece: Tetromino): List<Pair<Int, Int>> {
        val base = SHAPES[piece.type].orEmpty()
        return base.map { (bx, by) ->
            var x = bx
            var y = by
            repeat(piece.rotation % 4) {
                val nx = 3 - y
                y = x
                x = nx
            }
            (piece.x + x) to (piece.y + y)
        }
    }

    private fun isValid(piece: Tetromino): Boolean = cells(piece).all { (x, y) ->
        x in 0 until COLUMNS && y < ROWS && (y < 0 || grid[y][x] == 0)
    }

    private fun lock(piece: Tetromino) {
        cells(piece).forEach { (x, y) ->
            if (y in 0 until ROWS && x in 0 until COLUMNS) grid[y][x] = piece.type
        }
    }

    private fun clearLines() {
        var cleared = 0
        var y = ROWS - 1
        while (y >= 0) {
            if (grid[y].all { it != 0 }) {
                for (x in 0 until COLUMNS) {
                    for (row in y downTo 1) grid[row][x] = grid[row - 1][x]
                    grid[0][x] = 0
                }
                cleared++
                lines++
            } else {
                y--
            }
        }
        if (cleared > 0) {
            score += LINE_SCORES[cleared.coerceAtMost(4)] * level
            level = 1 + lines / 10
        }
    }

    private fun spawn() {
        current = nextPiece
        nextPiece = drawPiece()
        if (!isValid(current!!)) gameOver = true
    }

    private fun drawPiece(): Tetromino = Tetromino(drawType())

    private fun drawType(): Int {
        if (bag.isEmpty()) bag = (1..7).shuffled(random).toMutableList()
        return bag.removeAt(0)
    }
}
