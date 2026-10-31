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

// ---------- Tab 1: Quản lý mượn sách ----------

// ---------- Tab 2: Danh sách sách ----------

// ---------- Tab 3: Sinh viên ----------
