package com.example.tuan_5

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// BT01 - Thực hành Firebase Authentication
// - Đăng nhập bằng Google, Email/Mật khẩu, hoặc dùng thử (ẩn danh)
// - Đăng nhập lỗi -> thông báo lỗi
// - Đăng nhập thành công -> thông báo thành công và vào trang chủ
// - Từ trang chủ nhấn vào avatar -> màn hình thông tin cá nhân

val XanhTodo = Color(0xFF3B5BDB)
val XanhTodoNhat = Color(0xFFEDF0FB)
val ChuPhu = Color(0xFF6B7280)
val NenTrang = Color(0xFFF7F8FC)

// ---------- Xử lý đăng nhập ----------

object AuthHelper {
    private val auth = FirebaseAuth.getInstance()

    suspend fun dangNhapEmail(email: String, matKhau: String) {
        auth.signInWithEmailAndPassword(email, matKhau).await()
    }

    suspend fun dangKyEmail(email: String, matKhau: String) {
        auth.createUserWithEmailAndPassword(email, matKhau).await()
    }

    suspend fun dangNhapAnDanh() {
        auth.signInAnonymously().await()
    }

    // Google: Credential Manager lấy ID token, rồi đổi sang tài khoản Firebase
    suspend fun dangNhapGoogle(context: Context) {
        val tuyChon = GetSignInWithGoogleOption.Builder(
            context.getString(R.string.default_web_client_id) // sinh tự động từ google-services.json
        ).build()
        val yeuCau = GetCredentialRequest.Builder()
            .addCredentialOption(tuyChon)
            .build()

        val ketQua = CredentialManager.create(context).getCredential(context, yeuCau)
        val credential = ketQua.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
            auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
        } else {
            throw IllegalStateException("Không nhận được tài khoản Google")
        }
    }

    suspend fun dangXuat(context: Context) {
        auth.signOut()
        // Xóa tài khoản Google đã nhớ để lần sau được chọn lại
        CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
    }

    // Đổi lỗi Firebase sang câu tiếng Việt dễ hiểu
    fun thongBaoLoi(e: Exception): String = when (e) {
        is FirebaseAuthInvalidUserException -> "Tài khoản không tồn tại hoặc đã bị khóa"
        is FirebaseAuthWeakPasswordException -> "Mật khẩu quá yếu (tối thiểu 6 ký tự)"
        is FirebaseAuthInvalidCredentialsException -> "Sai email hoặc mật khẩu"
        is FirebaseAuthUserCollisionException -> "Email này đã được đăng ký"
        is FirebaseNetworkException -> "Không có kết nối mạng"
        is GetCredentialCancellationException -> "Bạn đã hủy đăng nhập Google"
        is NoCredentialException -> "Máy chưa có tài khoản Google nào"
        else -> e.message ?: "Đã có lỗi xảy ra"
    }
}

fun tenHienThi(user: FirebaseUser?): String = when {
    user == null -> "bạn"
    !user.displayName.isNullOrBlank() -> user.displayName!!
    !user.email.isNullOrBlank() -> user.email!!.substringBefore("@")
    else -> "Khách"
}

fun chuVietTat(ten: String): String =
    ten.split(" ").filter { it.isNotBlank() }.takeLast(2).joinToString("") { it.first().uppercase() }

@Composable
fun Avatar(ten: String, kichThuoc: Int, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .size(kichThuoc.dp)
            .background(XanhTodoNhat, CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(chuVietTat(ten), color = XanhTodo, fontWeight = FontWeight.Bold, fontSize = (kichThuoc / 2.6).sp)
    }
}

// ---------- Màn hình đăng nhập ----------

@Composable
fun LoginScreen(onThanhCong: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var matKhau by remember { mutableStateOf("") }
    var dangXuLy by remember { mutableStateOf(false) }
    var loi by remember { mutableStateOf<String?>(null) }

    // Chạy một cách đăng nhập, bắt lỗi và báo kết quả
    fun thucHien(cach: String, hanhDong: suspend () -> Unit) {
        scope.launch {
            dangXuLy = true
            try {
                hanhDong()
                FirebaseHelper.ghiSuKienDangNhap(context, cach)
                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                onThanhCong()
            } catch (e: Exception) {
                loi = AuthHelper.thongBaoLoi(e)
            } finally {
                dangXuLy = false
            }
        }
    }

    fun kiemTraEmail(): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            loi = "Email không hợp lệ"
            return false
        }
        if (matKhau.length < 6) {
            loi = "Mật khẩu tối thiểu 6 ký tự"
            return false
        }
        return true
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(XanhTodo, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Checklist, null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Chào mừng đến với\nUTH Todo", fontSize = 26.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.", color = ChuPhu)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = matKhau,
                onValueChange = { matKhau = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mật khẩu") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        if (kiemTraEmail()) thucHien("email") { AuthHelper.dangNhapEmail(email.trim(), matKhau) }
                    },
                    enabled = !dangXuLy,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = XanhTodo)
                ) { Text("Đăng nhập") }
                OutlinedButton(
                    onClick = {
                        if (kiemTraEmail()) thucHien("email_dang_ky") { AuthHelper.dangKyEmail(email.trim(), matKhau) }
                    },
                    enabled = !dangXuLy,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Tạo tài khoản") }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("  hoặc  ", color = ChuPhu, fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { thucHien("google") { AuthHelper.dangNhapGoogle(context) } },
                enabled = !dangXuLy,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("G", fontWeight = FontWeight.Bold, color = Color(0xFFDB4437))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Tiếp tục với Google", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Dùng thử không cần tài khoản",
                color = XanhTodo,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(enabled = !dangXuLy) { thucHien("an_danh") { AuthHelper.dangNhapAnDanh() } }
                    .padding(8.dp)
            )
            if (dangXuLy) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
                fontSize = 11.sp,
                color = ChuPhu,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Thông báo lỗi
    loi?.let {
        AlertDialog(
            onDismissRequest = { loi = null },
            title = { Text("Đăng nhập thất bại") },
            text = { Text(it) },
            confirmButton = { TextButton(onClick = { loi = null }) { Text("Đã hiểu") } }
        )
    }
}

// ---------- Màn hình thông tin cá nhân ----------
