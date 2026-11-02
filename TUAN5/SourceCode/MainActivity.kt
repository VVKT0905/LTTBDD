package com.example.tuan_5

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val xinQuyenThongBao =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 13+ phải xin quyền mới hiện được thông báo (Cloud Messaging)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            xinQuyenThongBao.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        FirebaseHelper.khoiDong(this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                // Đã đăng nhập từ lần trước thì vào thẳng trang chủ
                val manDau = if (FirebaseAuth.getInstance().currentUser != null) "home" else "login"

                NavHost(navController = navController, startDestination = manDau) {
                    composable("login") {
                        LoginScreen(onThanhCong = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        })
                    }
                    composable("home") {
                        HomeScreen(onMoHoSo = { navController.navigate("profile") })
                    }
                    composable("profile") {
                        ProfileScreen(
                            onBack = { navController.popBackStack() },
                            onDangXuat = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
