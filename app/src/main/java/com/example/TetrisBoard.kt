package com.example

class TetrisBoard {
    val width = 10
    val height = 20
    var grid: Array<IntArray> = Array(height) { IntArray(width) { 0 } }

    fun isValidPosition(piece: Tetromino): Boolean {
        for ((cx, cy) in piece.getGlobalPositions()) {
            if (cx !in 0 until width || cy >= height) return false
            if (cy >= 0 && grid[cy][cx] != 0) return false // Already occupied
        }
        return true
    }

    // Attempts to rotate and applies basic wall kicks
    fun attemptRotate(piece: Tetromino): Tetromino? {
        val rotated = piece.rotateCW()
        if (isValidPosition(rotated)) return rotated

        // Basic wall kicks: try shifting left or right, or up
        val kicks = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(-2, 0), Pair(2, 0))
        for (kick in kicks) {
            val kicked = rotated.move(kick.first, kick.second)
            if (isValidPosition(kicked)) return kicked
        }
        return null
    }

    // Place a piece on the board and return the cleared lines count
    fun placePiece(piece: Tetromino): Int {
        for ((cx, cy) in piece.getGlobalPositions()) {
            if (cy in 0 until height && cx in 0 until width) {
                grid[cy][cx] = piece.shape.color.toInt()
            }
        }
        return clearLines()
    }

    private fun clearLines(): Int {
        var cleared = 0
        val newGrid = Array(height) { IntArray(width) { 0 } }
        var destY = height - 1

        for (y in height - 1 downTo 0) {
            if (grid[y].any { it == 0 }) {
                // Line is not full, copy it to new grid
                newGrid[destY--] = grid[y].clone()
            } else {
                cleared++
            }
        }
        grid = newGrid
        return cleared
    }

    fun clear() {
        grid = Array(height) { IntArray(width) { 0 } }
    }
}
