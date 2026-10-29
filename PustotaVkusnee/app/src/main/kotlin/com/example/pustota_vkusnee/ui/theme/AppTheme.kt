 package com.example.myapp // <-- Замени на имя своего пакета

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.R // <-- Убедись, что этот импорт есть (ресурсы)

// ==========================================
// ЧАСТЬ 1: ПАЛИТРА (Аналог @theme { --color-... })
// ==========================================
object AppColors {
    val bg = Color(0xFF0F0E0B)       // --color-bg
    val surface = Color(0xFF17150F) // --color-surface
    val surface2 = Color(0xFF1E1C17) // --color-surface-2
    val amber = Color(0xFFFFD700)   // --color-amber (чуть светлее для читаемости)
    val gold = Color(0xFFF5E6C8)     // --color-gold
    val cream = Color(0xFFF5F0E8)    // --color-cream
    val muted = Color(0xFF9A8F7A)    // --color-muted
    val border = Color(0xFF2E2B22)   // --color-border
    val urgent = Color(0xFFFF5252)   // --color-urgent
    val safe = Color(0xFF52B788)     // --color-safe
}

// ==========================================
// ЧАСТЬ 2: ШРИФТЫ (Аналог @import url...)
// ==========================================
// ВАЖНО: Для работы этих строк файлы шрифтов должны лежать в res/font/
// Если файлов нет, код упадет. Для теста раскомментируй строки с SystemFonts ниже.
val playfairFont = FontFamily(Font(R.font.playfair_display))
val notoSansFont = FontFamily(Font(R.font.noto_sans))

/* 
   АЛЬТЕРНАТИВА (если шрифтов еще нет в проекте):
   val playfairFont = FontFamily(
       Font(androidx.compose.ui.res.fontResource(id = R.font.playfair_display), weight = FontWeight.Bold)
   )
   val notoSansFont = FontFamily.Default // Использует системный шрифт
*/

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
// ЧАСТЬ 3: ГЛОБАЛЬНАЯ ТЕМА (Аналог html { ... })
// ==========================================
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            // Принудительно задаем цвета, чтобы они совпадали с CSS, 
            // даже если система светлая
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
// ЧАСТЬ 4: ГЛАВНЫЙ ЭКРАН (Аналог body / html)
// ==========================================
@Composable
fun MainScreen() {
    // Аналог: html { background-color: #0f0e0b; color: #f5f0e8; }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.bg)
    ) {
        // Аналог: scroll-behavior: smooth;
        // verticalScroll автоматически дает плавный скролл и полосу прокрутки
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            
            // Пример контента, использующего твои стили
            ScreenContent()
        }
    }
}

@Composable
fun ScreenContent() {
    Spacer(modifier = Modifier.height(40.dp))
    
    // Заголовок (Playfair Display)
    Text(
        text = "Пустота вкуснее",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .align(Alignment.CenterHorizontally)
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Подзаголовок (Noto Sans)
    Text(
        text = "Еда, которая не должна исчезнуть",
        color = AppColors.muted,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(40.dp))

    // Карточка (имитация секции)
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
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Начать покупки", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Много контента для проверки скролла
    repeat(10) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Секция контента №${it + 1}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// ==========================================
// ЧАСТЬ 5: ТОЧКА ВХОДА (MainActivity)
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }
}
