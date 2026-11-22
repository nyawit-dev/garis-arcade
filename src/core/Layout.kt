package ngga.ring.arcade.core

import kotlin.math.min

/**
 * Dua preset layout: HP (480x720) dan tablet (800x1200).
 * Semua scene menggambar dalam koordinat preset lalu root container
 * diskalakan ke ukuran layar sebenarnya.
 */
object Layout {
    data class Config(
        val width: Double,
        val height: Double,
        val scale: Double,
        val tablet: Boolean,
        val offsetX: Double,
        val offsetY: Double
    )

    fun compute(screenW: Int, screenH: Int): Config {
        val h = screenH.coerceAtLeast(1)
        val tablet = screenW >= 800 || screenH >= 1000 ||
            (screenW.toDouble() / h > 1.2 && screenW >= 700)
        val width = if (tablet) 800.0 else 480.0
        val height = if (tablet) 1200.0 else 720.0
        val scale = min(screenW / width, screenH / height).coerceAtLeast(0.1)
        return Config(
            width = width,
            height = height,
            scale = scale,
            tablet = tablet,
            offsetX = (screenW - width * scale) / 2.0,
            offsetY = (screenH - height * scale) / 2.0
        )
    }
}
