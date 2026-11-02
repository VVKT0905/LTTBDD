package com.example.tuan_5

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.time.LocalDate

// BT02 - Tích hợp các dịch vụ Firebase vào UTH Todo
// - Authentication : đăng nhập (BT01)
// - Cloud Firestore: lưu danh sách việc theo từng tài khoản, đồng bộ realtime
// - Analytics      : ghi sự kiện đăng nhập, thêm việc, hoàn thành việc
// - Remote Config  : đổi câu động viên trên trang chủ từ Firebase Console, không cần cập nhật app
// - Cloud Messaging: nhận thông báo đẩy (gửi thử từ Console bằng FCM token hoặc topic "uth_todo")
// - Crashlytics    : ghi nhận crash, gắn với user id

const val NHOM_HOC_TAP = "Học tập"
const val NHOM_CUOC_SONG = "Cuộc sống"
const val TAG = "UTHTodo"

data class Viec(
    val id: String = "",
    val tieuDe: String = "",
    val nhom: String = NHOM_HOC_TAP,
    val gio: String = "",
    val ngay: String = "",
    val daXong: Boolean = false
)

// ---------- Các dịch vụ Firebase ----------

object FirebaseHelper {
    private const val KEY_THONG_DIEP = "thong_diep_trang_chu"

    fun khoiDong(context: Context) {
        // Remote Config: giá trị mặc định khi chưa tải được từ server
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setDefaultsAsync(mapOf(KEY_THONG_DIEP to "Cố lên, từng việc một!"))
        remoteConfig.fetchAndActivate()

        // Cloud Messaging: đăng ký nhận thông báo theo chủ đề
        FirebaseMessaging.getInstance().subscribeToTopic("uth_todo")

        // Crashlytics: gắn crash với tài khoản đang đăng nhập
        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseCrashlytics.getInstance().setUserId(it.uid)
        }
        taoKenhThongBao(context)
    }

    fun thongDiep(): String = FirebaseRemoteConfig.getInstance().getString(KEY_THONG_DIEP)

    fun ghiSuKien(context: Context, ten: String, thamSo: Map<String, String> = emptyMap()) {
        val bundle = Bundle()
        thamSo.forEach { (k, v) -> bundle.putString(k, v) }
        FirebaseAnalytics.getInstance(context).logEvent(ten, bundle)
    }

    fun ghiSuKienDangNhap(context: Context, cach: String) {
        ghiSuKien(context, FirebaseAnalytics.Event.LOGIN, mapOf(FirebaseAnalytics.Param.METHOD to cach))
        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseCrashlytics.getInstance().setUserId(it.uid)
        }
    }

    fun layFcmToken(onCo: (String) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { onCo(it) }
            .addOnFailureListener { onCo("Không lấy được token: ${it.message}") }
    }

    fun thuCrash() {
        FirebaseCrashlytics.getInstance().log("Người dùng bấm nút thử Crashlytics")
        throw RuntimeException("Crash thử nghiệm cho Firebase Crashlytics")
    }

    fun taoKenhThongBao(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val kenh = NotificationChannel("nhac_viec", "Nhắc việc", NotificationManager.IMPORTANCE_HIGH)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(kenh)
        }
    }
}

// ---------- Firestore: users/{uid}/tasks/{id} ----------

object ViecRepository {
    private fun bang() = FirebaseFirestore.getInstance()
        .collection("users")
        .document(FirebaseAuth.getInstance().currentUser?.uid ?: "khach")
        .collection("tasks")

    fun them(tieuDe: String, nhom: String, gio: String) {
        bang().add(
            mapOf(
                "tieuDe" to tieuDe,
                "nhom" to nhom,
                "gio" to gio,
                "ngay" to homNay(),
                "daXong" to false,
                "taoLuc" to FieldValue.serverTimestamp()
            )
        )
    }

    fun doiTrangThai(viec: Viec) {
        bang().document(viec.id).update("daXong", !viec.daXong)
    }

    fun xoa(viec: Viec) {
        bang().document(viec.id).delete()
    }

    // Lắng nghe thay đổi realtime. Trả về hàm hủy lắng nghe
    fun langNghe(onDoi: (List<Viec>) -> Unit): () -> Unit {
        val dangKy = bang().addSnapshotListener { snapshot, loi ->
            if (loi != null) {
                Log.e(TAG, "Firestore lỗi", loi)
                return@addSnapshotListener
            }
            val ds = snapshot?.documents?.map { doc ->
                Viec(
                    id = doc.id,
                    tieuDe = doc.getString("tieuDe").orEmpty(),
                    nhom = doc.getString("nhom") ?: NHOM_HOC_TAP,
                    gio = doc.getString("gio").orEmpty(),
                    ngay = doc.getString("ngay").orEmpty(),
                    daXong = doc.getBoolean("daXong") ?: false
                )
            }.orEmpty()
            onDoi(ds.sortedBy { it.gio })
        }
        return { dangKy.remove() }
    }
}

fun homNay(): String = LocalDate.now().toString() // dạng 2026-11-03

fun ngayTiengViet(): String {
    val thu = listOf("Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy", "Chủ Nhật")
    val d = LocalDate.now()
    return "${thu[d.dayOfWeek.value - 1]}, ${d.dayOfMonth} tháng ${d.monthValue}"
}

// Danh sách việc của tài khoản hiện tại, tự cập nhật khi Firestore thay đổi
@Composable
fun rememberDanhSachViec(): List<Viec> {
    var ds by remember { mutableStateOf(listOf<Viec>()) }
    DisposableEffect(Unit) {
        val huy = ViecRepository.langNghe { ds = it }
        onDispose { huy() }
    }
    return ds
}

// ---------- Trang chủ ----------

@Composable
fun HomeScreen(onMoHoSo: () -> Unit) {
    val context = LocalContext.current
    val ten = tenHienThi(FirebaseAuth.getInstance().currentUser)
    val viecHomNay = rememberDanhSachViec().filter { it.ngay == homNay() }
    val daXong = viecHomNay.count { it.daXong }
    val tong = viecHomNay.size
    val tiLe = if (tong == 0) 0f else daXong.toFloat() / tong

    var hienThem by remember { mutableStateOf(false) }
    var fcmToken by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = NenTrang,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { hienThem = true },
                shape = CircleShape,
                containerColor = XanhTodo,
                contentColor = Color.White
            ) { Icon(Icons.Filled.Add, "Thêm việc") }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Đầu trang: avatar, lời chào, chuông
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(ten, 44, onClick = onMoHoSo)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ngayTiengViet(), fontSize = 12.sp, color = ChuPhu)
                        Text("Chào $ten!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { FirebaseHelper.layFcmToken { fcmToken = it } }) {
                        Icon(Icons.Outlined.Notifications, "Thông báo")
                    }
                }
            }

            // Thẻ tiến độ
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(XanhTodo, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tiến độ hôm nay", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                        Text("$daXong/$tong việc", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        // Câu này lấy từ Remote Config
                        Text(FirebaseHelper.thongDiep(), color = Color.White, fontSize = 12.sp)
                    }
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { tiLe },
                            modifier = Modifier.size(64.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f),
                            strokeWidth = 6.dp
                        )
                        Text("${(tiLe * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Hai thẻ nhóm
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TheNhomViec(NHOM_HOC_TAP, Icons.Outlined.School, XanhTodo,
                        viecHomNay.count { it.nhom == NHOM_HOC_TAP && !it.daXong }, Modifier.weight(1f))
                    TheNhomViec(NHOM_CUOC_SONG, Icons.Outlined.Home, Color(0xFFE8590C),
                        viecHomNay.count { it.nhom == NHOM_CUOC_SONG && !it.daXong }, Modifier.weight(1f))
                }
            }

            item {
                Text("Việc hôm nay", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp))
            }

            if (viecHomNay.isEmpty()) {
                item {
                    Text("Chưa có việc nào. Bấm + để thêm.", color = ChuPhu)
                }
            }

            items(viecHomNay, key = { it.id }) { viec ->
                DongViec(
                    viec = viec,
                    onTick = {
                        ViecRepository.doiTrangThai(viec)
                        if (!viec.daXong) FirebaseHelper.ghiSuKien(context, "hoan_thanh_viec", mapOf("nhom" to viec.nhom))
                    },
                    onXoa = { ViecRepository.xoa(viec) }
                )
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }

    if (hienThem) {
        ThemViecDialog(
            onDong = { hienThem = false },
            onThem = { tieuDe, nhom, gio ->
                ViecRepository.them(tieuDe, nhom, gio)
                FirebaseHelper.ghiSuKien(context, "them_viec", mapOf("nhom" to nhom))
                hienThem = false
            }
        )
    }

    // Hiện FCM token để gửi thông báo thử từ Firebase Console
    fcmToken?.let { token ->
        AlertDialog(
            onDismissRequest = { fcmToken = null },
            title = { Text("Cloud Messaging") },
            text = {
                Text("FCM token của máy này (dùng để gửi thông báo thử từ Firebase Console):\n\n$token", fontSize = 12.sp)
            },
            confirmButton = {
                TextButton(onClick = {
                    val clipboard = context.getSystemService(ClipboardManager::class.java)
                    clipboard.setPrimaryClip(ClipData.newPlainText("FCM token", token))
                    Toast.makeText(context, "Đã sao chép", Toast.LENGTH_SHORT).show()
                    fcmToken = null
                }) { Text("Sao chép") }
            },
            dismissButton = { TextButton(onClick = { fcmToken = null }) { Text("Đóng") } }
        )
    }
}

@Composable
fun TheNhomViec(ten: String, icon: ImageVector, mau: Color, conLai: Int, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFEDEFF3), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(mau.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = mau, modifier = Modifier.size(18.dp)) }
        Spacer(modifier = Modifier.height(10.dp))
        Text(ten, fontWeight = FontWeight.SemiBold)
        Text("$conLai việc còn lại", fontSize = 12.sp, color = ChuPhu)
    }
}

@Composable
fun DongViec(viec: Viec, onTick: () -> Unit, onXoa: () -> Unit) {
    val mauNhom = if (viec.nhom == NHOM_HOC_TAP) XanhTodo else Color(0xFFE8590C)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .clickable { onTick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(if (viec.daXong) XanhTodo else Color.White, CircleShape)
                .border(1.5.dp, if (viec.daXong) XanhTodo else Color(0xFFCED4DA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (viec.daXong) Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                viec.tieuDe,
                fontWeight = FontWeight.Medium,
                color = if (viec.daXong) ChuPhu else Color.Black,
                textDecoration = if (viec.daXong) TextDecoration.LineThrough else null
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    viec.nhom,
                    fontSize = 11.sp,
                    color = mauNhom,
                    modifier = Modifier
                        .background(mauNhom.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                )
                if (viec.gio.isNotBlank()) Text("  ${viec.gio}", fontSize = 12.sp, color = ChuPhu)
            }
        }
        IconButton(onClick = onXoa) { Icon(Icons.Outlined.Delete, "Xóa", tint = ChuPhu) }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color(0xFFCED4DA))
    }
}

@Composable
fun ThemViecDialog(onDong: () -> Unit, onThem: (String, String, String) -> Unit) {
    var tieuDe by remember { mutableStateOf("") }
    var nhom by remember { mutableStateOf(NHOM_HOC_TAP) }
    var gio by remember { mutableStateOf("") }
    var loi by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDong,
        title = { Text("Thêm việc mới") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(tieuDe, { tieuDe = it }, label = { Text("Tên việc") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(NHOM_HOC_TAP, NHOM_CUOC_SONG).forEach { n ->
                        FilterChip(selected = nhom == n, onClick = { nhom = n }, label = { Text(n) })
                    }
                }
                OutlinedTextField(gio, { gio = it }, label = { Text("Giờ (vd 14:00)") }, singleLine = true)
                if (loi.isNotEmpty()) Text(loi, color = Color.Red, fontSize = 13.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val gioHopLe = gio.isBlank() || Regex("^([01]?\\d|2[0-3]):[0-5]\\d$").matches(gio.trim())
                when {
                    tieuDe.isBlank() -> loi = "Nhập tên việc"
                    !gioHopLe -> loi = "Giờ không hợp lệ (HH:mm)"
                    else -> onThem(tieuDe.trim(), nhom, gio.trim())
                }
            }) { Text("Thêm") }
        },
        dismissButton = { TextButton(onClick = onDong) { Text("Hủy") } }
    )
}

// ---------- Cloud Messaging: nhận thông báo đẩy ----------
