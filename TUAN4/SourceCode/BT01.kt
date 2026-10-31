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

// Lớp cha trừu tượng cho hình minh họa của mỗi bước. Mỗi lớp con tự vẽ hình riêng (đa hình)
abstract class HinhMinhHoa {
    @Composable
    abstract fun Ve()
}

class HinhDanhSachViec : HinhMinhHoa() {
    @Composable
    override fun Ve() {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DongViecGia(daXong = true)
            DongViecGia(daXong = false)
            DongViecGia(daXong = false)
        }
    }
}

class HinhNhomViec : HinhMinhHoa() {
    @Composable
    override fun Ve() {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TheNhom("Học tập", Color(0xFFDDE4FF), XanhUth)
            TheNhom("Cuộc sống", Color(0xFFFFE8D6), Color(0xFFE8590C))
        }
    }
}

class HinhNhacViec : HinhMinhHoa() {
    @Composable
    override fun Ve() {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = XanhUth)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Ôn bài Toán lúc 19:00",
                fontSize = 11.sp,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

// Một bước giới thiệu
data class BuocGioiThieu(
    val tieuDe: String,
    val moTa: String,
    val hinh: HinhMinhHoa
)

val CAC_BUOC = listOf(
    BuocGioiThieu(
        "Mọi việc, gọn trong một chỗ",
        "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
        HinhDanhSachViec()
    ),
    BuocGioiThieu(
        "Chia theo Học tập và Cuộc sống",
        "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần lo gì.",
        HinhNhomViec()
    ),
    BuocGioiThieu(
        "Nhắc đúng lúc",
        "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
        HinhNhacViec()
    )
)

// ---------- Navigation ----------

@Composable
fun BT01Screen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ManHinh.Start.route) {
        composable(ManHinh.Start.route) {
            StartScreen(onXong = {
                // Xóa màn Start khỏi back stack để bấm Back không quay lại splash
                navController.navigate(ManHinh.Onboarding.route) {
                    popUpTo(ManHinh.Start.route) { inclusive = true }
                }
            })
        }
        composable(ManHinh.Onboarding.route) {
            OnboardingScreen(onBatDau = {
                navController.navigate(ManHinh.TrangChu.route) {
                    popUpTo(ManHinh.Onboarding.route) { inclusive = true }
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
fun OnboardingScreen(onBatDau: () -> Unit) {
    var buoc by remember { mutableIntStateOf(0) }
    val hienTai = CAC_BUOC[buoc]
    val laBuocCuoi = buoc == CAC_BUOC.lastIndex

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Thanh trên: logo + Bỏ qua
            Row(verticalAlignment = Alignment.CenterVertically) {
                LogoUthTodo(kichThuoc = 24, nen = XanhUth, mauIcon = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("UTH ", fontWeight = FontWeight.Bold)
                Text("Todo", color = XanhUth, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Bỏ qua",
                    color = ChuXam,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onBatDau() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(200.dp)
                    .background(XanhUthNhat, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                hienTai.hinh.Ve()
            }
            Spacer(modifier = Modifier.weight(1f))

            Text(hienTai.tieuDe, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(hienTai.moTa, fontSize = 14.sp, color = ChuXam)
            Spacer(modifier = Modifier.height(24.dp))

            // Chấm chỉ bước hiện tại
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CAC_BUOC.indices.forEach { i ->
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (i == buoc) 20.dp else 6.dp)
                            .background(
                                if (i == buoc) XanhUth else Color(0xFFD1D5DB),
                                CircleShape
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { if (laBuocCuoi) onBatDau() else buoc++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = XanhUth)
            ) {
                Text(if (laBuocCuoi) "Bắt đầu ngay" else "Tiếp tục", fontWeight = FontWeight.SemiBold)
            }
        }
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

// Các mảnh nhỏ dùng trong hình minh họa
@Composable
fun DongViecGia(daXong: Boolean) {
    Row(
        modifier = Modifier
            .width(140.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(if (daXong) XanhUth else Color.White, CircleShape)
                .border(1.dp, if (daXong) XanhUth else Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (daXong) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .height(6.dp)
                .fillMaxWidth()
                .background(Color(0xFFE5E7EB), CircleShape)
        )
    }
}

@Composable
fun TheNhom(ten: String, nen: Color, mauChu: Color) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(
            ten,
            fontSize = 10.sp,
            color = mauChu,
            modifier = Modifier
                .background(nen, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(6.dp)
                .fillMaxWidth()
                .background(Color(0xFFE5E7EB), CircleShape)
        )
    }
}
