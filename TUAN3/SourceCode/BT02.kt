package com.example.tuan_3

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.random.Random

// BT02 - Thực hành 2: Navigation + LazyColumn
// So sánh Column và LazyColumn khi hiển thị 1.000.000 dòng sinh ngẫu nhiên

val CAC_CAU_NOI = listOf(
    "The only way to do great work is to love what you do.",
    "Stay hungry, stay foolish.",
    "Simplicity is the ultimate sophistication.",
    "Talk is cheap. Show me the code.",
    "First, solve the problem. Then, write the code.",
    "Code is like humor. When you have to explain it, it's bad."
)

// Sinh nội dung ngẫu nhiên cho dòng thứ index.
// Dùng index làm seed nên cùng một dòng luôn ra cùng nội dung khi cuộn qua lại,
// và không phải tạo sẵn 1.000.000 chuỗi trong bộ nhớ.
fun noiDungNgauNhien(index: Int): String {
    return CAC_CAU_NOI[Random(index).nextInt(CAC_CAU_NOI.size)]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BT02Screen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "chon") {
        composable("chon") {
            ChonLoaiDanhSach(
                onColumn = { navController.navigate("column") },
                onLazyColumn = { navController.navigate("lazy") }
            )
        }
        composable("column") {
            ManHinhDanhSach("Column", onBack = { navController.popBackStack() }) {
                // Column tạo TẤT CẢ 1.000.000 dòng ngay lập tức -> app bị treo/đứng rất lâu, có thể bị crash
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0 until SO_DONG) {
                        TheDong(i)
                    }
                }
            }
        }
        composable("lazy") {
            ManHinhDanhSach("LazyColumn", onBack = { navController.popBackStack() }) {
                // LazyColumn chỉ tạo các dòng đang thấy trên màn hình -> mở ngay, cuộn mượt
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(count = SO_DONG) { i ->
                        TheDong(i)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChonLoaiDanhSach(onColumn: () -> Unit, onLazyColumn: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose List Type", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = XanhChinh,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onColumn,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Column (1,000,000 items)")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onLazyColumn,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("LazyColumn (1,000,000 items)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManHinhDanhSach(tieuDe: String, onBack: () -> Unit, noiDung: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tieuDe, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = XanhChinh,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            noiDung()
        }
    }
}

@Composable
fun TheDong(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${index + 1}", fontWeight = FontWeight.Bold)
                Text(noiDungNgauNhien(index), fontSize = 13.sp, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}
