package com.example.pustota_vkusnee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import com.example.pustota_vkusnee.ui.screens.AuthPage
import com.example.pustota_vkusnee.ui.screens.AuthUser
import com.example.pustota_vkusnee.ui.screens.MarketScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    var currentUser by remember { mutableStateOf<AuthUser?>(null) }

    if (currentUser == null) {
        // Экран авторизации
        AuthPage(
            onAuth = { user ->
                currentUser = user //
            }
        )
    } else {
        MarketScreen(
            onReserve = {
            }
        )
    }
}

@Composable
fun RootContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Приложение запущено",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}
