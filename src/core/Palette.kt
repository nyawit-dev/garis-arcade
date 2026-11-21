package ngga.ring.arcade.core

import korlibs.image.color.Colors

/** Warna terpusat supaya semua game punya look yang konsisten. */
object Palette {
    val background = Colors["#101018"]
    val panel = Colors["#202038"]
    val accent = Colors["#4bd6c5"]
    val text = Colors.WHITE
    val muted = Colors["#8b90a6"]
    val snake = Colors["#7ddb63"]
    val snakeHead = Colors["#b6f36a"]
    val food = Colors["#ff6b6b"]
    val wall = Colors["#2f3150"]

    fun tileText(value: Int) = when (value) {
        16, 32, 64, 128, 256, 512, 1024, 2048 -> Colors["#1a1a2e"]
        else -> Colors.WHITE
    }

    fun tile(value: Int) = when (value) {
        0 -> Colors["#2f3150"]
        2 -> Colors["#3b5b7a"]
        4 -> Colors["#3f7d76"]
        8 -> Colors["#4aa88f"]
        16 -> Colors["#8fbf6a"]
        32 -> Colors["#d4b85a"]
        64 -> Colors["#e08b4c"]
        128 -> Colors["#e06b5a"]
        256 -> Colors["#d95c8d"]
        512 -> Colors["#a96ad4"]
        1024 -> Colors["#6a8ce0"]
        else -> Colors["#4bd6c5"]
    }
}
