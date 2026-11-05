package com.example.pustota_vkusnee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// ЧАСТЬ 1: ПАЛИТРА
// ==========================================
object AppColors {
    val bg = Color(0xFF0F0E0B)
    val surface = Color(0xFF17150F)
    val surface2 = Color(0xFF1E1C17)
    val amber = Color(0xFFD4893A) 
    val gold = Color(0xFFE8B04B)  
    val cream = Color(0xFFF5F0E8)
    val muted = Color(0xFF9A8F7A)
    val border = Color(0xFF2E2B22)
    val urgent = Color(0xFFE05252) 
    val safe = Color(0xFF52B788)
}

// ==========================================
// ЧАСТЬ 2: ШРИФТЫ
// ==========================================

val playfairFont = FontFamily.Serif
val notoSansFont = FontFamily.SansSerif

val customTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = notoSansFont,
        color = AppColors.cream,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = playfairFont,
        color = AppColors.amber,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    labelMedium = TextStyle(
        fontFamily = notoSansFont,
        color = AppColors.muted,
        fontSize = 12.sp
    )
)

// ==========================================
// ЧАСТЬ 3: ГЛОБАЛЬНАЯ ТЕМА
// ==========================================
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = AppColors.bg,
            surface = AppColors.surface2,
            onSurface = AppColors.cream,
            primary = AppColors.amber,
            secondary = AppColors.gold,
            error = AppColors.urgent,
            outline = AppColors.border
        ),
        typography = customTypography,
        content = content
    )
}

// ==========================================
// ЧАСТЬ 4: ГЛАВНЫЙ ЭКРАН
// ==========================================
@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenContent()
        }
    }
}

@Composable
fun ScreenContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Пустота вкуснее",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Еда, которая не должна исчезнуть",
            color = AppColors.muted,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.surface2),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Скидки до 70%",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Мы спасаем продукты с истекающим сроком годности, чтобы вы могли наслаждаться вкусной едой и не переплачивать.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.muted
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Действие */ },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Начать покупки", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        repeat(10) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Секция контента №${it + 1}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
