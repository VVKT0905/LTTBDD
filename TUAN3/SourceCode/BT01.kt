package com.example.tuan_3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// BT01 - Thực hành 1: Navigation + LazyColumn
// Luồng: Root -> List<Lazy> -> Detail, Detail -> List -> Root, Detail -> Root

val XanhChinh = Color(0xFF2196F3)
val XanhNhat = Color(0xFFBBDEFB)

const val CAU_NOI = "The only way to do great work is to love what you do."
const val TAC_GIA = "Steve Jobs"
const val SO_DONG = 1_000_000

@Composable
fun BT01Screen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "root") {
        composable("root") {
            RootScreen(onPush = { navController.navigate("list") })
        }
        composable("list") {
            ListScreen(
                onBack = { navController.popBackStack() },
                onItemClick = { so -> navController.navigate("detail/$so") }
            )
        }
        composable(
            route = "detail/{so}",
            arguments = listOf(navArgument("so") { type = NavType.IntType })
        ) { backStackEntry ->
            val so = backStackEntry.arguments?.getInt("so") ?: 1
            DetailScreen(
                so = so,
                onBack = { navController.popBackStack() },
                // Quay thẳng về Root, xóa List và Detail khỏi back stack
                onBackToRoot = { navController.popBackStack("root", inclusive = false) }
            )
        }
    }
}

// Định dạng số thứ tự: 1 -> "01", 1000000 -> "1.000.000"
fun dinhDangSo(so: Int): String {
    return if (so < 10) "0$so" else "%,d".format(so).replace(',', '.')
}

@Composable
fun RootScreen(onPush: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.Hexagon,
                contentDescription = "Logo",
                tint = Color(0xFF3DDC84),
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Navigation", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "is a framework that simplifies the implementation of navigation " +
                        "between different UI components (activities, fragments, or composables) in an app",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onPush,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = XanhChinh)
            ) {
                Text("PUSH", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Thanh tiêu đề có nút quay lại, dùng chung cho List và Detail
@Composable
fun ThanhTieuDe(tieuDe: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(XanhChinh, RoundedCornerShape(10.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Quay lại",
                tint = Color.White
            )
        }
        Text(
            text = tieuDe,
            modifier = Modifier.align(Alignment.Center),
            color = XanhChinh,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ListScreen(onBack: () -> Unit, onItemClick: (Int) -> Unit) {
    Scaffold(topBar = { ThanhTieuDe("LazyColumn", onBack) }) { padding ->
        // LazyColumn chỉ tạo các dòng đang hiện trên màn hình nên 1.000.000 dòng vẫn mượt
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(count = SO_DONG) { index ->
                val so = index + 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(XanhNhat, RoundedCornerShape(12.dp))
                        .clickable { onItemClick(so) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${dinhDangSo(so)} | $CAU_NOI",
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Black, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Xem chi tiết",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailScreen(so: Int, onBack: () -> Unit, onBackToRoot: () -> Unit) {
    Scaffold(topBar = { ThanhTieuDe("Detail", onBack) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\"$CAU_NOI\"",
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(XanhNhat, XanhChinh)),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"$CAU_NOI\"",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    lineHeight = 38.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(TAC_GIA, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Dòng số ${dinhDangSo(so)}", fontSize = 12.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onBackToRoot,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = XanhChinh)
            ) {
                Text("BACK TO ROOT", fontWeight = FontWeight.Bold)
            }
        }
    }
}
