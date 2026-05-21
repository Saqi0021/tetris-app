package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NextPiecePreview(shape: TetrominoShape) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2B2930), RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFF49454F), RoundedCornerShape(24.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "NEXT",
            color = Color(0xFFCAC4D0),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFF121212), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(40.dp)) {
                val blockSize = size.width / 4f
                val positions = shape.blocks
                
                val minX = positions.minOf { it.first }
                val maxX = positions.maxOf { it.first }
                val minY = positions.minOf { it.second }
                val maxY = positions.maxOf { it.second }
                
                val offsetX = (4f - (maxX - minX + 1)) / 2f - minX
                val offsetY = (4f - (maxY - minY + 1)) / 2f - minY

                for ((bx, by) in positions) {
                    drawBlock(
                        (bx + offsetX).toInt(),
                        (by + offsetY).toInt(),
                        blockSize,
                        Color(shape.color)
                    )
                }
            }
        }
    }
}
