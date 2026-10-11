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

// BT04 - Bài 3: Tổng hợp for + if + nullable
// Tái sử dụng hàm xepLoai() và mauChu() từ file BT03.kt (đúng theo yêu cầu đề bài)

class SinhVienDiem(
    val name: String,
    val score: Double?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BT04Screen() {
    val danhSach = listOf(
        SinhVienDiem("Nguyễn Văn An", 9.0),
        SinhVienDiem("Trần Thị Bình", 7.2),
        SinhVienDiem("Lê Minh Chi", null),
        SinhVienDiem("Phạm Quốc Dũng", 4.5),
        SinhVienDiem("Võ Thanh Em", 5.8)
    )

    var tong = 0.0
    var soCoDiem = 0
    var soGioi = 0
    var soChuaCoDiem = 0
    var caoNhat = 0.0

    for (sv in danhSach) {
        if (sv.score == null) {
            soChuaCoDiem++
        } else {
            tong += sv.score
            soCoDiem++
            if (xepLoai(sv.score) == "Giỏi") {
                soGioi++
            }
            if (sv.score > caoNhat) {
                caoNhat = sv.score
            }
        }
    }

    val diemTB = if (soCoDiem > 0) tong / soCoDiem else 0.0
    val diemTBLamTron = Math.round(diemTB * 100) / 100.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bảng điểm lớp", color = Color.White) },
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
                    .background(Color(0xFF2C4170), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Điểm trung bình lớp", fontSize = 12.sp, color = Color.White)
                Text(
                    text = diemTBLamTron.toString(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Giỏi", fontSize = 11.sp, color = Color.White)
                        Text(soGioi.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Chưa có điểm", fontSize = 11.sp, color = Color.White)
                        Text(soChuaCoDiem.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cao nhất", fontSize = 11.sp, color = Color.White)
                        Text(caoNhat.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("DANH SÁCH · ${danhSach.size} SINH VIÊN", fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            for (sv in danhSach) {
                val loai = if (sv.score != null) xepLoai(sv.score) else "Chưa có điểm"
                val diemHienThi = if (sv.score != null) sv.score.toString() else "—"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(sv.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(loai, fontSize = 12.sp, color = mauChu(loai))
                    }
                    Text(diemHienThi, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
