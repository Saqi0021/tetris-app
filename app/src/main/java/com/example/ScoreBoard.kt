package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreBoard(score: Int, level: Int, lines: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2B2930), RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFF49454F), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ScoreItem("SCORE", String.format("%06d", score))
        Spacer(modifier = Modifier.height(16.dp))
        ScoreItem("LINES", String.format("%03d", lines))
    }
}

@Composable
fun ScoreItem(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            color = Color(0xFFCAC4D0),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = Color(0xFFD0BCFF),
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
