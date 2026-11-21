# Garis Arcade

Game jadul (retro) berbasis **KorGE** (Kotlin Multiplatform). Semua build dan rilis dilakukan di GitHub Actions.

## Game
- **Snake** — panah untuk arah, `R` restart, `ESC` menu
- **2048** — panah untuk geser, `R` restart, `ESC` menu
- **Tetris** — panah kiri/kanan, `UP` rotasi, `DOWN` soft drop, `SPACE` hard drop, `P` pause, `R` restart, `ESC` menu

## Struktur
```
src/
  main.kt                     entry point + ukuran window
  core/Palette.kt             warna terpusat
  ui/MenuScene.kt             menu, daftar game
  games/
    snake/
      SnakeEngine.kt          logika murni (tanpa view)
      SnakeScene.kt           render + input
    tetris/
      TetrisEngine.kt         logika murni (grid 10x20, 7-bag, line clear)
      TetrisScene.kt          render + input
    twozerofour/
      Board2048.kt            logika murni (tanpa view)
      TwoZeroFourEightScene.kt render + input
resources/images/             aset pixel-art buatan sendiri
```

Menambah game: buat `Engine` (logika murni) + `Scene` (render/input), lalu daftarkan di `MenuScene.items`.

## Build & rilis
Push tag `v*` → GitHub Actions build APK signed → GitHub Release. JDK 21 diperlukan oleh plugin KorGE.
