package com.leson.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import dagger.hilt.android.AndroidEntryPoint
import com.leson.pos.data.repo.Repo

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Repo.init(applicationContext)
    setContent { AppNav() }
  }
}