package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameScreen(viewModel: TetrisViewModel) {
    val state by viewModel.gameState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1B1F)) // Sleek Theme Background
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LEVEL %02d".format(state.level),
                    color = Color(0xFFD0BCFF),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "TETRIS",
                    color = Color(0xFFE6E1E5),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Refresh Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF313033))
                        .clickable { viewModel.startGame() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = Color(0xFFD0BCFF))
                }
                // Pause Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF313033))
                        .clickable { viewModel.togglePause() },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isPaused) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color(0xFFD0BCFF))
                    } else {
                        Text("||", color = Color(0xFFD0BCFF), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }
        }

        // Main Game Area (Board + Sidebar)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Game Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF0A0A0A), RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFF49454F), RoundedCornerShape(24.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Calculate correct aspect ratio (10x20)
                Box(modifier = Modifier.aspectRatio(0.5f).fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val blockSize = size.width / 10f

                        // Draw grid background lines
                        val numCols = 10
                        val numRows = 20
                        val gridColor = Color.White.copy(alpha = 0.05f)
                        for (i in 0..numCols) {
                            drawLine(
                                color = gridColor,
                                start = Offset(i * blockSize, 0f),
                                end = Offset(i * blockSize, size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (i in 0..numRows) {
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, i * blockSize),
                                end = Offset(size.width, i * blockSize),
                                strokeWidth = 1f
                            )
                        }

                        // Draw settled blocks
                        for (y in 0 until 20) {
                            for (x in 0 until 10) {
                                if (state.grid[y][x] != 0) {
                                    drawBlock(x, y, blockSize, Color(state.grid[y][x]))
                                }
                            }
                        }

                        // Draw ghost piece
                        state.ghostPiece?.let { piece ->
                            for ((bx, by) in piece.getGlobalPositions()) {
                                drawBlockPlaceholder(bx, by, blockSize, Color(piece.shape.color).copy(alpha = 0.3f))
                            }
                        }

                        // Draw active piece
                        state.currentPiece?.let { piece ->
                            for ((bx, by) in piece.getGlobalPositions()) {
                                if (by >= 0) { // Only draw if on screen
                                    drawBlock(bx, by, blockSize, Color(piece.shape.color))
                                }
                            }
                        }
                    }

                    // Game Over / Paused Overlays
                    if (state.isGameOver) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("GAME OVER", color = Color(0xFFEADDFF), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        }
                    } else if (state.isPaused) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("PAUSED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        }
                    }
                }
            }
            
            // Sidebar
            Column(
                modifier = Modifier.width(96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NextPiecePreview(state.nextPiece)
                ScoreBoard(state.score, state.level, state.lines)
            }
        }
        
        // Controls (D-Pad style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top row (Rotate)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF381E72))
                        .clickable { viewModel.rotate() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Rotate", tint = Color(0xFFD0BCFF), modifier = Modifier.size(32.dp))
                }
                
                // Bottom row (Left, Down, Right)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF313033))
                            .clickable { viewModel.moveLeft() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = Color(0xFFD0BCFF), modifier = Modifier.size(32.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFEADDFF))
                            .clickable { viewModel.hardDrop() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Hard Drop", tint = Color(0xFF21005D), modifier = Modifier.size(32.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF313033))
                            .clickable { viewModel.moveRight() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = Color(0xFFD0BCFF), modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

fun DrawScope.drawBlock(x: Int, y: Int, blockSize: Float, color: Color) {
    val left = x * blockSize
    val top = y * blockSize
    
    drawRoundRect(
        color = color,
        topLeft = Offset(left + 2f, top + 2f),
        size = Size(blockSize - 4f, blockSize - 4f),
        cornerRadius = CornerRadius(4f)
    )
}

fun DrawScope.drawBlockPlaceholder(x: Int, y: Int, blockSize: Float, color: Color) {
    val left = x * blockSize
    val top = y * blockSize
    
    drawRoundRect(
        color = color,
        topLeft = Offset(left + 2f, top + 2f),
        size = Size(blockSize - 4f, blockSize - 4f),
        style = Stroke(width = 2f),
        cornerRadius = CornerRadius(4f)
    )
}

