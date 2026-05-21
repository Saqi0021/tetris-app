package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameState(
    val grid: Array<IntArray> = Array(20) { IntArray(10) { 0 } },
    val currentPiece: Tetromino? = null,
    val ghostPiece: Tetromino? = null,
    val nextPiece: TetrominoShape = TetrominoShape.I,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val score: Int = 0,
    val lines: Int = 0,
    val level: Int = 1
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as GameState
        if (!grid.contentDeepEquals(other.grid)) return false
        if (currentPiece != other.currentPiece) return false
        if (ghostPiece != other.ghostPiece) return false
        if (nextPiece != other.nextPiece) return false
        if (isGameOver != other.isGameOver) return false
        if (isPaused != other.isPaused) return false
        if (score != other.score) return false
        if (lines != other.lines) return false
        if (level != other.level) return false
        return true
    }
    
    override fun hashCode(): Int {
        var result = grid.contentDeepHashCode()
        result = 31 * result + (currentPiece?.hashCode() ?: 0)
        result = 31 * result + (ghostPiece?.hashCode() ?: 0)
        result = 31 * result + nextPiece.hashCode()
        result = 31 * result + isGameOver.hashCode()
        result = 31 * result + isPaused.hashCode()
        result = 31 * result + score
        result = 31 * result + lines
        result = 31 * result + level
        return result
    }
}

class TetrisViewModel : ViewModel() {
    private val board = TetrisBoard()
    private val _gameState = MutableStateFlow(GameState(grid = board.grid))
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var bag = getBag()
    
    init {
        startGame()
        viewModelScope.launch {
            gameLoop()
        }
    }

    private fun getBag(): List<TetrominoShape> = TetrominoShape.entries.shuffled()
    
    private fun getNextBagPiece(): TetrominoShape {
        if (bag.isEmpty()) {
            bag = getBag()
        }
        val piece = bag.first()
        bag = bag.drop(1)
        return piece
    }

    fun startGame() {
        board.clear()
        bag = getBag()
        val nextShape = getNextBagPiece()
        _gameState.value = GameState(
            grid = board.grid.map { it.clone() }.toTypedArray(),
            currentPiece = Tetromino(getNextBagPiece()),
            nextPiece = nextShape,
            isGameOver = false,
            score = 0,
            lines = 0,
            level = 1
        )
        updateGhostPiece()
    }

    private suspend fun gameLoop() {
        while (true) {
            val state = _gameState.value
            if (!state.isGameOver && !state.isPaused && state.currentPiece != null) {
                val delayTime = (1000 - (state.level - 1) * 100).coerceAtLeast(100).toLong()
                delay(delayTime)
                moveDown()
            } else {
                delay(100)
            }
        }
    }

    fun togglePause() {
        _gameState.value = _gameState.value.copy(isPaused = !_gameState.value.isPaused)
    }

    fun moveLeft() {
        val current = _gameState.value.currentPiece ?: return
        if (_gameState.value.isGameOver || _gameState.value.isPaused) return
        val moved = current.move(-1, 0)
        if (board.isValidPosition(moved)) {
            _gameState.value = _gameState.value.copy(currentPiece = moved)
            updateGhostPiece()
        }
    }

    fun moveRight() {
        val current = _gameState.value.currentPiece ?: return
        if (_gameState.value.isGameOver || _gameState.value.isPaused) return
        val moved = current.move(1, 0)
        if (board.isValidPosition(moved)) {
            _gameState.value = _gameState.value.copy(currentPiece = moved)
            updateGhostPiece()
        }
    }

    fun rotate() {
        val current = _gameState.value.currentPiece ?: return
        if (_gameState.value.isGameOver || _gameState.value.isPaused) return
        val rotated = board.attemptRotate(current)
        if (rotated != null) {
            _gameState.value = _gameState.value.copy(currentPiece = rotated)
            updateGhostPiece()
        }
    }

    fun moveDown() {
        val current = _gameState.value.currentPiece ?: return
        if (_gameState.value.isGameOver || _gameState.value.isPaused) return
        val moved = current.move(0, 1)
        if (board.isValidPosition(moved)) {
            _gameState.value = _gameState.value.copy(currentPiece = moved)
            updateGhostPiece() // Unnecessary but safe
        } else {
            lockPieceAndSpawnNext(current)
        }
    }

    fun hardDrop() {
        var current = _gameState.value.currentPiece ?: return
        if (_gameState.value.isGameOver || _gameState.value.isPaused) return
        while (board.isValidPosition(current.move(0, 1))) {
            current = current.move(0, 1)
        }
        lockPieceAndSpawnNext(current)
    }

    private fun updateGhostPiece() {
        var ghost = _gameState.value.currentPiece ?: return
        while (board.isValidPosition(ghost.move(0, 1))) {
            ghost = ghost.move(0, 1)
        }
        _gameState.value = _gameState.value.copy(ghostPiece = ghost)
    }

    private fun lockPieceAndSpawnNext(piece: Tetromino) {
        val cleared = board.placePiece(piece)
        
        var newScore = _gameState.value.score
        var newLines = _gameState.value.lines
        var newLevel = _gameState.value.level
        
        if (cleared > 0) {
            newLines += cleared
            val points = when(cleared) {
                1 -> 100
                2 -> 300
                3 -> 500
                else -> 800
            }
            newScore += points * newLevel
            newLevel = (newLines / 10) + 1
        }

        val nextShape = _gameState.value.nextPiece
        val newPiece = Tetromino(nextShape)
        
        if (!board.isValidPosition(newPiece)) {
            _gameState.value = _gameState.value.copy(
                grid = board.grid.map { it.clone() }.toTypedArray(),
                currentPiece = null,
                ghostPiece = null,
                isGameOver = true,
                score = newScore,
                lines = newLines,
                level = newLevel
            )
        } else {
            _gameState.value = _gameState.value.copy(
                grid = board.grid.map { it.clone() }.toTypedArray(),
                currentPiece = newPiece,
                nextPiece = getNextBagPiece(),
                score = newScore,
                lines = newLines,
                level = newLevel
            )
            updateGhostPiece()
        }
    }
}
