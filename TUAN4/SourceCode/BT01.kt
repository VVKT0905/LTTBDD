package com.example.tuan_4

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

// BT01 - UTH Todo V2: Navigation + OOP
// Start (splash) -> Onboarding 3 bước -> Trang chủ

val XanhUth = Color(0xFF3B5BDB)
val XanhUthNhat = Color(0xFFEDF0FB)
val ChuXam = Color(0xFF6B7280)

// ---------- OOP ----------

// Các màn hình trong app. sealed class: chỉ có đúng 3 màn này, không tạo thêm được ở nơi khác
sealed class ManHinh(val route: String) {
    object Start : ManHinh("start")
    object Onboarding : ManHinh("onboarding")
    object TrangChu : ManHinh("trang_chu")
}

// ---------- Navigation ----------

@Composable
fun BT01Screen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ManHinh.Start.route) {
        composable(ManHinh.Start.route) {
            StartScreen(onXong = {
                // Xóa màn Start khỏi back stack để bấm Back không quay lại splash
                navController.navigate(ManHinh.TrangChu.route) {
                    popUpTo(ManHinh.Start.route) { inclusive = true }
                }
            })
        }
        composable(ManHinh.TrangChu.route) {
            TrangChuTodo()
        }
    }
}

// ---------- Giao diện ----------

@Composable
fun LogoUthTodo(kichThuoc: Int, nen: Color, mauIcon: Color) {
    Box(
        modifier = Modifier
            .size(kichThuoc.dp)
            .background(nen, RoundedCornerShape((kichThuoc / 4).dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.Checklist,
            contentDescription = "UTH Todo",
            tint = mauIcon,
            modifier = Modifier.size((kichThuoc * 0.6).dp)
        )
    }
}

@Composable
fun StartScreen(onXong: () -> Unit) {
    var tienDo by remember { mutableFloatStateOf(0f) }

    // Chạy thanh tiến độ khoảng 2 giây rồi chuyển màn
    LaunchedEffect(Unit) {
        while (tienDo < 1f) {
            delay(40)
            tienDo += 0.02f
        }
        onXong()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(XanhUth)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        LogoUthTodo(kichThuoc = 96, nen = Color.White, mauIcon = XanhUth)
        Spacer(modifier = Modifier.height(20.dp))
        Text("UTH Todo", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Việc học, việc nhà — gọn gàng mỗi ngày", color = Color.White, fontSize = 13.sp)
        Spacer(modifier = Modifier.weight(1f))
        LinearProgressIndicator(
            progress = { tienDo.coerceAtMost(1f) },
            modifier = Modifier.width(100.dp),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Phiên bản 2.1.0", color = Color.White, fontSize = 11.sp)
    }
}

@Composable
fun TrangChuTodo() {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoUthTodo(kichThuoc = 72, nen = XanhUth, mauIcon = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Trang chủ UTH Todo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Bạn đã hoàn thành phần giới thiệu", color = ChuXam)
        }
    }
}
