package com.example

// Tetromino representations and rotation logic
enum class TetrominoShape(val color: Long, val blocks: List<Pair<Int, Int>>) {
    I(0xFF00FFFF, listOf(Pair(-1, 0), Pair(0, 0), Pair(1, 0), Pair(2, 0))), // Cyan
    J(0xFF0000FF, listOf(Pair(-1, -1), Pair(-1, 0), Pair(0, 0), Pair(1, 0))), // Blue
    L(0xFFFF7F00, listOf(Pair(1, -1), Pair(-1, 0), Pair(0, 0), Pair(1, 0))), // Orange
    O(0xFFFFFF00, listOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1))), // Yellow
    S(0xFF00FF00, listOf(Pair(0, -1), Pair(1, -1), Pair(-1, 0), Pair(0, 0))), // Green
    T(0xFF800080, listOf(Pair(0, -1), Pair(-1, 0), Pair(0, 0), Pair(1, 0))), // Purple
    Z(0xFFFF0000, listOf(Pair(-1, -1), Pair(0, -1), Pair(0, 0), Pair(1, 0))) // Red
}

data class Tetromino(
    val shape: TetrominoShape,
    val x: Int = 4,
    val y: Int = 0,
    val state: Int = 0 // 0 = 0 deg, 1 = 90 deg, 2 = 180 deg, 3 = 270 deg
) {
    // Return relative positions based on rotation state
    fun getPositions(): List<Pair<Int, Int>> {
        if (shape == TetrominoShape.O) return shape.blocks
        
        return shape.blocks.map { (bx, by) ->
            when (state) {
                0 -> Pair(bx, by)
                1 -> Pair(-by, bx)
                2 -> Pair(-bx, -by)
                3 -> Pair(by, -bx)
                else -> Pair(bx, by)
            }
        }
    }

    // Return absolute grid coordinates
    fun getGlobalPositions(): List<Pair<Int, Int>> {
        return getPositions().map { (bx, by) -> Pair(x + bx, y + by) }
    }

    fun rotateCW(): Tetromino = copy(state = (state + 1) % 4)
    fun move(dx: Int, dy: Int): Tetromino = copy(x = x + dx, y = y + dy)
}
