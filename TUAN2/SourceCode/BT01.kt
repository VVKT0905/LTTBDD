package com.example.tuan_2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// BT01 - Thực hành 3: Máy tính đơn giản
@Composable
fun BT01Screen() {
    var soA by remember { mutableStateOf("") }
    var soB by remember { mutableStateOf("") }
    var ketQua by remember { mutableStateOf("") }

    fun tinh(phepToan: String) {
        val a = soA.toDoubleOrNull()
        val b = soB.toDoubleOrNull()

        if (a == null || b == null) {
            ketQua = "Vui lòng nhập 2 số hợp lệ"
        } else if (phepToan == "/" && b == 0.0) {
            ketQua = "Không thể chia cho 0"
        } else {
            val kq = when (phepToan) {
                "+" -> a + b
                "-" -> a - b
                "*" -> a * b
                "/" -> a / b
                else -> 0.0
            }

            ketQua = if (kq % 1.0 == 0.0) {
                kq.toLong().toString()
            } else {
                kq.toString()
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Thực hành 03",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = soA,
                onValueChange = { soA = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nhập số thứ nhất") },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { tinh("+") },
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD94848)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("+", fontSize = 22.sp, color = Color.White)
                }

                Button(
                    onClick = { tinh("-") },
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDDA848)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("-", fontSize = 22.sp, color = Color.White)
                }

                Button(
                    onClick = { tinh("*") },
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B3FDB)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("*", fontSize = 22.sp, color = Color.White)
                }

                Button(
                    onClick = { tinh("/") },
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1C1E)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("/", fontSize = 22.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = soB,
                onValueChange = { soB = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nhập số thứ hai") },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = if (ketQua.isEmpty()) "Kết quả: " else "Kết quả: $ketQua",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
