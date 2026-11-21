package ngga.ring.arcade.games.twozerofour

import kotlin.random.Random

enum class MoveDirection { LEFT, RIGHT, UP, DOWN }

/** Logika 2048 murni Kotlin, tanpa view. */
class Board2048(
    val size: Int = 4,
    private val random: Random = Random.Default
) {
    var cells: Array<IntArray> = Array(size) { IntArray(size) }
        private set
    var score: Int = 0
        private set
    var won: Boolean = false
        private set

    init {
        reset()
    }

    fun reset() {
        cells = Array(size) { IntArray(size) }
        score = 0
        won = false
        repeat(2) { addRandomTile() }
    }

    fun move(direction: MoveDirection): Boolean {
        val before = Array(size) { cells[it].copyOf() }
        for (i in 0 until size) {
            val line = (0 until size).map { j ->
                when (direction) {
                    MoveDirection.LEFT -> cells[i][j]
                    MoveDirection.RIGHT -> cells[i][size - 1 - j]
                    MoveDirection.UP -> cells[j][i]
                    MoveDirection.DOWN -> cells[size - 1 - j][i]
                }
            }
            val merged = merge(line)
            for (j in 0 until size) {
                when (direction) {
                    MoveDirection.LEFT -> cells[i][j] = merged[j]
                    MoveDirection.RIGHT -> cells[i][size - 1 - j] = merged[j]
                    MoveDirection.UP -> cells[j][i] = merged[j]
                    MoveDirection.DOWN -> cells[size - 1 - j][i] = merged[j]
                }
            }
        }
        var changed = false
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (before[i][j] != cells[i][j]) changed = true
            }
        }
        if (changed) addRandomTile()
        return changed
    }

    fun canMove(): Boolean {
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (cells[i][j] == 0) return true
                if (i + 1 < size && cells[i][j] == cells[i + 1][j]) return true
                if (j + 1 < size && cells[i][j] == cells[i][j + 1]) return true
            }
        }
        return false
    }

    private fun merge(input: List<Int>): List<Int> {
        val values = input.filter { it != 0 }.toMutableList()
        val output = mutableListOf<Int>()
        var i = 0
        while (i < values.size) {
            if (i + 1 < values.size && values[i] == values[i + 1]) {
                val merged = values[i] * 2
                output.add(merged)
                score += merged
                if (merged == 2048) won = true
                i += 2
            } else {
                output.add(values[i])
                i++
            }
        }
        while (output.size < size) output.add(0)
        return output
    }

    private fun addRandomTile() {
        val empty = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (cells[i][j] == 0) empty.add(i to j)
            }
        }
        if (empty.isEmpty()) return
        val (i, j) = empty.random(random)
        cells[i][j] = if (random.nextDouble() < 0.9) 2 else 4
    }
}
