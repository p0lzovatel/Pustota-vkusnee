 package com.example.myapp // <-- Замени на имя своего пакета

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapp.ui.theme.AppTheme // <-- Сюда импортируй тему из предыдущего ответа
import com.example.myapp.MainScreen       // <-- Сюда импортируй свой главный экран (аналог App.jsx)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // АНАЛОГ: ReactDOM.createRoot(...).render(...)
        setContent {
            // Обертка темы (аналог импорта './index.css' и глобальных стилей)
            AppTheme {
                // Аналог: <App />
                MainScreen()
            }
        }
    }
}
