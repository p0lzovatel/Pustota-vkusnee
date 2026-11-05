package com.example.pustota_vkusnee.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Layout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues

// Lazy Grid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import kotlinx.coroutines.delay

// Shape
import androidx.compose.foundation.shape.RoundedCornerShape

// Material3
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

// Animation
import androidx.compose.animation.core.spring

import com.example.pustota_vkusnee.data.models.Product
import com.example.pustota_vkusnee.data.models.CATS
import com.example.pustota_vkusnee.data.models.Level
import com.example.pustota_vkusnee.data.models.countdown
import com.example.pustota_vkusnee.data.models.PRODUCTS

@Composable
fun MarketScreen(onReserve: () -> Unit) {
    var cat by remember { mutableStateOf("все") }
    var tick by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000) // 30 секунд
            tick++
        }
    }

    val visibleProducts = if (cat == "все") PRODUCTS else PRODUCTS.filter { it.cat == cat }

    Column(modifier = Modifier.padding(16.dp)) {
        // Заголовок
        Text(text = "Маркет", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Легенда срочности
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                "Срочно — меньше 6 ч" to Color.Red,
                "Сегодня — до 24 ч" to Color.Yellow,
                "Несколько дней" to Color.Green
            ).forEach { (label, color) ->
                Box(
                    modifier = Modifier
                        .background(color.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp))
                        .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = label, color = color, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Категории (Tabs)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CATS.forEach { c ->
                val isSelected = cat == c
                Text(
                    text = c.replaceFirstChar { it.uppercase() },
                    color = if (isSelected) Color.White else Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { cat = c }
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Сетка товаров
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(visibleProducts) { product ->
                ProductCard(product = product, tick = tick)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, tick: Int) {
    var added by remember { mutableStateOf(false) }
    
    // Эмуляция анимации добавления
    if (added) {
        LaunchedEffect(Unit) {
            delay(2000)
            added = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Изображение и бейдж
            Box(modifier = Modifier.height(150.dp).fillMaxWidth()) {
                // Image(painter = painterResource(product.img), contentDescription = null)
                Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) // заглушка
                
                // UrgencyBadge
                val cd = remember(tick) { countdown(product.expiresAt) }
                val badgeColor = when (cd.level) {
                    is Level.Critical -> Color.Red
                    is Level.Warning -> Color.Yellow
                    else -> Color.Green
                }
                
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(4.dp).background(badgeColor, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = cd.text, color = Color.Black, fontSize = 10.sp)
                }
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = product.name, fontWeight = FontWeight.Bold)
                Text(text = product.weight, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${product.price} ₽", color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = " ${product.origPrice} ₽", textDecoration = TextDecoration.LineThrough, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
                
                Button(
                    onClick = { added = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (added) Color.Green else Color.Transparent,
                        contentColor = if (added) Color.White else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = if (added) "✓ В корзине" else "Купить")
                }
            }
        }
    }
}

