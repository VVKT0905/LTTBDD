package com.example.tuan_4

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// BT02 - Hệ thống Quản lý Thư viện
// - Tạo danh sách sách
// - Tạo danh sách sinh viên
// - Cho phép sinh viên mượn sách và hiển thị thông tin sách

val XanhThuVien = Color(0xFF1E4FB5)
val DoThuVien = Color(0xFF9B1C31)
val NenXam = Color(0xFFE0E0E0)

// ---------- Lớp dữ liệu (OOP) ----------

data class Sach(
    val ma: String,
    val ten: String,
    val tacGia: String,
    val namXuatBan: Int
)

data class SinhVien(
    val ma: String,
    val ten: String
)

// Lớp ThuVien quản lý toàn bộ dữ liệu và nghiệp vụ mượn/trả.
// Dùng mutableStateListOf / mutableStateMapOf để giao diện tự cập nhật khi dữ liệu đổi.
class ThuVien {
    val dsSach: SnapshotStateList<Sach> = mutableStateListOf(
        Sach("S01", "Sách 01 - Lập trình Kotlin", "Nguyễn Văn B", 2023),
        Sach("S02", "Sách 02 - Jetpack Compose", "Trần Thị C", 2024),
        Sach("S03", "Sách 03 - Cấu trúc dữ liệu", "Lê Văn D", 2021),
        Sach("S04", "Sách 04 - Cơ sở dữ liệu", "Phạm Thị E", 2022)
    )

    val dsSinhVien: SnapshotStateList<SinhVien> = mutableStateListOf(
        SinhVien("SV01", "Nguyen Van A"),
        SinhVien("SV02", "Tran Thi B"),
        SinhVien("SV03", "Le Van C")
    )

    // Mã sách -> mã sinh viên đang mượn. Sách không có trong map là sách còn trong thư viện
    private val muonSach = mutableStateMapOf("S01" to "SV01", "S02" to "SV01")

    fun themSach(ten: String, tacGia: String, nam: Int) {
        val ma = "S" + (dsSach.size + 1).toString().padStart(2, '0')
        dsSach.add(Sach(ma, ten, tacGia, nam))
    }

    fun themSinhVien(ten: String) {
        val ma = "SV" + (dsSinhVien.size + 1).toString().padStart(2, '0')
        dsSinhVien.add(SinhVien(ma, ten))
    }

    fun nguoiMuon(sach: Sach): SinhVien? {
        val maSV = muonSach[sach.ma] ?: return null
        return dsSinhVien.find { it.ma == maSV }
    }

    fun sachDangMuon(sv: SinhVien): List<Sach> =
        dsSach.filter { muonSach[it.ma] == sv.ma }

    fun sachConTrong(): List<Sach> =
        dsSach.filter { !muonSach.containsKey(it.ma) }

    fun muon(sv: SinhVien, sach: Sach): Boolean {
        if (muonSach.containsKey(sach.ma)) return false // sách đã có người mượn
        muonSach[sach.ma] = sv.ma
        return true
    }

    fun tra(sach: Sach) {
        muonSach.remove(sach.ma)
    }
}

// ---------- Màn hình chính + thanh điều hướng đáy ----------

data class TabThuVien(val route: String, val nhan: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

val CAC_TAB = listOf(
    TabThuVien("quan_ly", "Quản lý", Icons.Filled.Home)
)

@Composable
fun BT02Screen() {
    val thuVien = remember { ThuVien() }
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val routeHienTai = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                CAC_TAB.forEach { tab ->
                    NavigationBarItem(
                        selected = routeHienTai == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.nhan) },
                        label = { Text(tab.nhan) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = XanhThuVien,
                            selectedTextColor = XanhThuVien
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "quan_ly",
            modifier = Modifier.padding(padding)
        ) {
            composable("quan_ly") { QuanLyScreen(thuVien) }
        }
    }
}

@Composable
fun TieuDeThuVien(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

// ---------- Tab 1: Quản lý mượn sách ----------

@Composable
fun QuanLyScreen(thuVien: ThuVien) {
    var svDangChon by remember { mutableStateOf(thuVien.dsSinhVien.first()) }
    var hienChonSV by remember { mutableStateOf(false) }
    var hienMuonSach by remember { mutableStateOf(false) }
    var sachXem by remember { mutableStateOf<Sach?>(null) }

    val sachCuaSV = thuVien.sachDangMuon(svDangChon)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        TieuDeThuVien("Hệ thống\nQuản lý Thư viện")

        Text("Sinh viên", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = svDangChon.ten,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { hienChonSV = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = XanhThuVien)
            ) {
                Text("Thay đổi")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Danh sách sách", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(NenXam, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (sachCuaSV.isEmpty()) {
                Text(
                    "Bạn chưa mượn quyển sách nào\nNhấn 'Thêm' để bắt đầu hành trình đọc sách!",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(sachCuaSV, key = { it.ma }) { sach ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .clickable { sachXem = sach }
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Bỏ tick = trả sách
                            Checkbox(
                                checked = true,
                                onCheckedChange = { thuVien.tra(sach) },
                                colors = CheckboxDefaults.colors(checkedColor = DoThuVien)
                            )
                            Text(sach.ten)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { hienMuonSach = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(180.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = XanhThuVien)
        ) {
            Text("Thêm", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    // Hộp thoại chọn sinh viên
    if (hienChonSV) {
        AlertDialog(
            onDismissRequest = { hienChonSV = false },
            title = { Text("Chọn sinh viên") },
            text = {
                LazyColumn {
                    items(thuVien.dsSinhVien, key = { it.ma }) { sv ->
                        Text(
                            "${sv.ma} - ${sv.ten}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    svDangChon = sv
                                    hienChonSV = false
                                }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { hienChonSV = false }) { Text("Đóng") } }
        )
    }

    // Hộp thoại mượn sách: chỉ hiện sách còn trong thư viện
    if (hienMuonSach) {
        val conTrong = thuVien.sachConTrong()
        AlertDialog(
            onDismissRequest = { hienMuonSach = false },
            title = { Text("Mượn sách cho ${svDangChon.ten}") },
            text = {
                if (conTrong.isEmpty()) {
                    Text("Thư viện đã hết sách để mượn.")
                } else {
                    LazyColumn {
                        items(conTrong, key = { it.ma }) { sach ->
                            Text(
                                "${sach.ma} - ${sach.ten}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        thuVien.muon(svDangChon, sach)
                                        hienMuonSach = false
                                    }
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { hienMuonSach = false }) { Text("Đóng") } }
        )
    }

    sachXem?.let { sach ->
        ThongTinSachDialog(sach, thuVien.nguoiMuon(sach), onDong = { sachXem = null })
    }
}

@Composable
fun ThongTinSachDialog(sach: Sach, nguoiMuon: SinhVien?, onDong: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDong,
        title = { Text(sach.ten) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Mã sách: ${sach.ma}")
                Text("Tác giả: ${sach.tacGia}")
                Text("Năm xuất bản: ${sach.namXuatBan}")
                Text(
                    if (nguoiMuon == null) "Trạng thái: Còn trong thư viện"
                    else "Trạng thái: ${nguoiMuon.ten} đang mượn"
                )
            }
        },
        confirmButton = { TextButton(onClick = onDong) { Text("Đóng") } }
    )
}

// ---------- Tab 2: Danh sách sách ----------

// ---------- Tab 3: Sinh viên ----------
