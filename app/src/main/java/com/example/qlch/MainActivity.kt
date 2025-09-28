package com.example.qlch

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable  // Nhập hàm để định nghĩa điểm đến composable trong điều hướng
import androidx.navigation.compose.rememberNavController  // Nhập hàm để tạo và ghi nhớ NavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppNavigation("home")
        }
    }
}

@Composable  // Đánh dấu hàm này là một thành phần UI Composable
fun MyAppNavigation(startDestination: String) {  // Định nghĩa hàm điều hướng với tham số cho điểm đến bắt đầu
    val navController = rememberNavController()  // Tạo và ghi nhớ NavController để quản lý điều hướng

    NavHost(navController = navController, startDestination = startDestination) {  // Thiết lập NavHost với bộ điều khiển và điểm bắt đầu

        composable("home") {  // Định nghĩa điểm đến "login"
        }
    }
}
