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

// ---------- Màn hình đăng nhập ----------

// ---------- Màn hình thông tin cá nhân ----------
