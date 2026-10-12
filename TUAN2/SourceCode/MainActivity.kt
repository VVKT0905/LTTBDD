package com.example.tuan_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Chọn 1 bài muốn chạy (bỏ dấu // dòng tương ứng):
                // BT01Screen()
                // BT02Screen()
                // BT03Screen()
                BT04Screen()
            }
        }
    }
}
