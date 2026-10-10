package com.example.tuan_2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// BT03 - Bài 2: Câu lệnh if / else

fun xepLoai(diem: Double): String {
    if (diem < 0 || diem > 10) {
        return "Không hợp lệ"
    } else if (diem >= 8.0) {
        return "Giỏi"
    } else if (diem >= 6.5) {
        return "Khá"
    } else if (diem >= 5.0) {
        return "Trung bình"
    } else {
        return "Yếu"
    }
}

fun mauChu(loai: String): Color {
    if (loai == "Giỏi") {
        return Color(0xFF2E7D32)
    } else if (loai == "Khá") {
        return Color(0xFF1E5BB8)
    } else if (loai == "Trung bình") {
        return Color(0xFFB7791F)
    } else if (loai == "Yếu") {
        return Color(0xFFC0392B)
    } else {
        return Color.Gray
    }
}

fun mauNen(loai: String): Color {
    if (loai == "Giỏi") {
        return Color(0xFFDDF3DF)
    } else if (loai == "Khá") {
        return Color(0xFFE0EBFA)
    } else if (loai == "Trung bình") {
        return Color(0xFFFCEFD2)
    } else if (loai == "Yếu") {
        return Color(0xFFFBE0DD)
    } else {
        return Color(0xFFE9EAEE)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BT03Screen() {
    val diem = 8.5
    val loai = xepLoai(diem)

    val diemKiemTra = listOf(9.0, 7.0, 5.5, 3.0, 11.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xếp loại học lực", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2C4170))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF3F4F8))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Điểm trung bình", fontSize = 13.sp, color = Color.Gray)
                Text(diem.toString(), fontSize = 48.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(mauNen(loai), RoundedCornerShape(8.dp))
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = loai,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = mauChu(loai)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("KIỂM TRA THÊM", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                for (i in diemKiemTra.indices) {
                    val d = diemKiemTra[i]
                    val l = xepLoai(d)

                    val diemHienThi = if (d % 1.0 == 0.0) d.toInt().toString() else d.toString()

                    if (i > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFEEEEEE))
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Điểm $diemHienThi", fontSize = 14.sp)

                        Box(
                            modifier = Modifier
                                .background(mauNen(l), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(l, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = mauChu(l))
                        }
                    }
                }
            }
        }
    }
}
