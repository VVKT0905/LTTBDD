package com.example.tuan_4

import android.net.Uri
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// BT03 - SmartTasks: Quên mật khẩu (Nav - Data Flow)
// Forget Password (email) -> Verify Code (otp) -> Reset Password (pass) -> Confirm
// Bấm Submit ở Confirm: quay về màn 1 và hiển thị email, otp, pass nhận được từ màn 4

val XanhSmart = Color(0xFF4A90E2)
val NenO = Color(0xFFF5F6F8)
const val DO_DAI_OTP = 5

@Composable
fun BT03Screen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "forget") {

        composable("forget") { entry ->
            // Đọc kết quả màn 4 gửi về qua savedStateHandle của chính màn này
            val ketQua by entry.savedStateHandle
                .getStateFlow<List<String>?>("ket_qua", null)
                .collectAsState()

            ForgetPasswordScreen(
                ketQua = ketQua,
                onNext = { email -> navController.navigate("verify/${Uri.encode(email)}") }
            )
        }

        composable(
            "verify/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { entry ->
            val email = entry.arguments?.getString("email").orEmpty()
            VerifyCodeScreen(
                email = email,
                onBack = { navController.popBackStack() },
                onNext = { }
            )
        }

    }
}

// ---------- Thành phần dùng chung ----------

@Composable
fun LogoUth() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "UTH",
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = Color(0xFF1B7A82)
        )
        Text(
            "UNIVERSITY\nOF TRANSPORT\nHOCHIMINH CITY",
            fontSize = 8.sp,
            lineHeight = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC0392B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("SmartTasks", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = XanhSmart)
    }
}

@Composable
fun KhungManHinh(
    tieuDe: String,
    moTa: String,
    onBack: (() -> Unit)?,
    noiDung: @Composable ColumnScope.() -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.height(40.dp)) {
                if (onBack != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(XanhSmart, RoundedCornerShape(12.dp))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Quay lại", tint = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoUth()
                Spacer(modifier = Modifier.height(24.dp))
                Text(tieuDe, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(moTa, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(24.dp))
            noiDung()
        }
    }
}

@Composable
fun ONhap(
    giaTri: String,
    onDoi: (String) -> Unit,
    goiY: String,
    icon: ImageVector,
    laMatKhau: Boolean = false,
    chiDoc: Boolean = false
) {
    OutlinedTextField(
        value = giaTri,
        onValueChange = onDoi,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(goiY) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        singleLine = true,
        readOnly = chiDoc,
        shape = RoundedCornerShape(10.dp),
        visualTransformation = if (laMatKhau) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (laMatKhau) KeyboardType.Password else KeyboardType.Email
        )
    )
}

@Composable
fun NutXanh(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(containerColor = XanhSmart)
    ) {
        Text(text, fontSize = 16.sp)
    }
}

@Composable
fun Loi(text: String) {
    if (text.isNotEmpty()) {
        Text(text, color = Color.Red, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

// ---------- Màn 1: Forget Password ----------

@Composable
fun ForgetPasswordScreen(ketQua: List<String>?, onNext: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var loi by remember { mutableStateOf("") }

    KhungManHinh(
        tieuDe = "Forget Password?",
        moTa = "Enter your Email, we will send you a verification code.",
        onBack = null
    ) {
        ONhap(email, { email = it; loi = "" }, "Your Email", Icons.Outlined.Email)
        Loi(loi)
        Spacer(modifier = Modifier.height(24.dp))
        NutXanh("Next") {
            if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                onNext(email.trim())
            } else {
                loi = "Email không hợp lệ"
            }
        }

        // Hiển thị dữ liệu nhận về từ màn Confirm
        if (ketQua != null) {
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCE8F8), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Dữ liệu nhận từ màn hình 4", fontWeight = FontWeight.Bold, color = Color(0xFFC0392B))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Email: ${ketQua[0]}")
                Text("OTP: ${ketQua[1]}")
                Text("Password: ${ketQua[2]}")
            }
        }
    }
}

// ---------- Màn 2: Verify Code ----------

@Composable
fun VerifyCodeScreen(email: String, onBack: () -> Unit, onNext: (String) -> Unit) {
    var otp by remember { mutableStateOf("") }
    var loi by remember { mutableStateOf("") }

    KhungManHinh(
        tieuDe = "Verify Code",
        moTa = "Enter the code\nwe just sent you on your registered Email\n($email)",
        onBack = onBack
    ) {
        // Một ô nhập ẩn, hiển thị thành 5 ô vuông
        BasicTextField(
            value = otp,
            onValueChange = { moi ->
                if (moi.length <= DO_DAI_OTP && moi.all { it.isDigit() }) {
                    otp = moi
                    loi = ""
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = TextStyle(color = Color.Transparent),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(DO_DAI_OTP) { i ->
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(NenO, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(otp.getOrNull(i)?.toString() ?: "", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )
        Loi(loi)
        Spacer(modifier = Modifier.height(24.dp))
        NutXanh("Next") {
            if (otp.length == DO_DAI_OTP) onNext(otp) else loi = "Nhập đủ $DO_DAI_OTP số"
        }
    }
}

// ---------- Màn 3: Reset Password ----------

// ---------- Màn 4: Confirm ----------
