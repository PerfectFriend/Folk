package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(dynamicColor = false) { // Disable dynamic color to enforce our premium custom colors
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFF05010B)
        ) {
          val appViewModel: AppViewModel = viewModel()
          val isFirstLaunch by appViewModel.isFirstLaunch.collectAsState()

          if (isFirstLaunch) {
            LanguageSelectionScreen(viewModel = appViewModel) {
              appViewModel.completeFirstLaunch()
            }
          } else {
            MainAppScreen(viewModel = appViewModel, onNavigateBackToLanguages = {
              appViewModel.selectLanguage("English")
            })
          }
        }
      }
    }
  }
}
