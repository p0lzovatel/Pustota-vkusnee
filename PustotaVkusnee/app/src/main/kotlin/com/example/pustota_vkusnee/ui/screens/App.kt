package com.example.pustota_vkusnee.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pustota_vkusnee.AppColors
import kotlinx.coroutines.delay

// ==========================================
// 1. МОДЕЛИ ДАННЫХ И ХЕЛПЕРЫ
// ==========================================

data class MenuItem(
    val name: String,
    val desc: String,
    val price: Int,
    val tags: List<String>,
    val img: String
)

data class MarketProduct(
    val id: Int,
    val name: String,
    val weight: String,
    val origPrice: Int,
    val price: Int,
    val discount: Int,
    val expiresAt: Long,
    val cat: String,
    val img: String
)

enum class UrgencyLevel { Critical, Warning, Ok }

fun countdown(targetMillis: Long): Pair<String, UrgencyLevel> {
    val ms = targetMillis - System.currentTimeMillis()
    if (ms <= 0) return "истёк" to UrgencyLevel.Critical
    val hrs = (ms / 3_600_000).toInt()
    val min = ((ms % 3_600_000) / 60_000).toInt()
    return when {
        hrs < 6 -> (if (hrs > 0) "${hrs}ч ${min}м" else "${min}м") to UrgencyLevel.Critical
        hrs < 24 -> "${hrs} ч" to UrgencyLevel.Warning
        else -> "${hrs / 24} д." to UrgencyLevel.Ok
    }
}

val MENU = listOf(
    MenuItem(
        "Крем-суп из тыквы",
        "Бархатный суп из сезонной тыквы с пюре из грецкого ореха, тыквенными семечками и трюфельным маслом",
        480,
        listOf("вегетарианское", "сезонное"),
        "https://images.unsplash.com/photo-1476718406336-bb5a9690ee2a?w=600&h=700&fit=crop&auto=format&q=80"
    ),
    MenuItem(
        "Рис «Пустота»",
        "Рис в тёмном даши-бульоне с кольцами кальмара, маринованным имбирём и кунжутным маслом",
        690,
        listOf("рыбное", "авторское"),
        "https://images.unsplash.com/photo-1761545832843-f551732204fb?w=600&h=700&fit=crop&auto=format&q=80"
    ),
    MenuItem(
        "Пирог из того, что есть",
        "Ежедневно меняющийся тарт с начинкой из продуктов, которые привезли этим утром — каждый раз другой",
        390,
        listOf("сезонное", "меняется каждый день"),
        "https://images.unsplash.com/photo-1679993387260-38824cf1a93a?w=600&h=700&fit=crop&auto=format&q=80"
    )
)

val PRODUCTS = listOf(
    MarketProduct(1, "Йогурт греческий 4%", "400 г", 189, 94, 50, System.currentTimeMillis() + 4 * 3600_000L + 1800_000L, "молочное", "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=320&h=320&fit=crop&auto=format&q=80"),
    MarketProduct(2, "Сыр «Гауда» 45%", "250 г", 279, 139, 50, System.currentTimeMillis() + 19 * 3600_000L, "молочное", "https://images.unsplash.com/photo-1452195100486-9cc805987862?w=320&h=320&fit=crop&auto=format&q=80"),
    MarketProduct(3, "Хлеб «Бородинский»", "400 г", 89, 44, 50, System.currentTimeMillis() + 9 * 3600_000L, "хлеб", "https://images.unsplash.com/photo-1549931319-a545dcf3bc73?w=320&h=320&fit=crop&auto=format&q=80"),
    MarketProduct(4, "Багет со злаками", "300 г", 129, 64, 50, System.currentTimeMillis() + 6 * 3600_000L, "хлеб", "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=320&h=320&fit=crop&auto=format&q=80"),
    MarketProduct(5, "Брокколи", "500 г", 129, 64, 50, System.currentTimeMillis() + 12 * 3600_000L, "овощи", "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=320&h=320&fit=crop&auto=format&q=80"),
    MarketProduct(6, "Томаты черри", "250 г", 159, 79, 50, System.currentTimeMillis() + 5 * 3600_000L, "овощи", "https://images.unsplash.com/photo-1542838132-92c53300491e?w=320&h=320&fit=crop&auto=format&q=80"),
    MarketProduct(7, "Суп тыквенный", "450 мл", 219, 109, 50, System.currentTimeMillis() + 3 * 3600_000L, "готовое", "https://images.unsplash.com/photo-1761545832874-02e1d41dc649?w=320&h=320&fit=crop&auto=format&q=80"),
    MarketProduct(8, "Котлеты домашние", "600 г", 239, 119, 50, System.currentTimeMillis() + 7 * 3600_000L, "готовое", "https://images.unsplash.com/photo-1529042410759-befb1204b468?w=320&h=320&fit=crop&auto=format&q=80")
)

val CATS = listOf("все", "молочное", "хлеб", "овощи", "готовое")

// ==========================================
// 2. ГЛАВНЫЙ ЭКРАН
// ==========================================

@Composable
fun MarketScreen(
    onReserve: () -> Unit = {},
    onGoToProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    userName: String? = null
) {
    var tick by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            tick++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.bg)
            .verticalScroll(scrollState)
    ) {
        NavBar(
            userName = userName,
            onProfile = onGoToProfile,
            onLogout = onLogout,
            onReserve = onReserve
        )
        Hero(onReserve = onReserve, onGoToMarket = {})
        About()
        MenuSection()
        MarketSection(tick = tick)
        Values()
        ReservationSection()
        Footer()
    }
}

// ==========================================
// 3. СЕКЦИИ
// ==========================================

@Composable
fun NavBar(userName: String?, onProfile: () -> Unit, onLogout: () -> Unit, onReserve: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.surface.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).background(AppColors.amber, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text("ПВ", color = AppColors.bg, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Пустота вкуснее", color = AppColors.cream, fontStyle = FontStyle.Italic, fontSize = 16.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (userName != null) {
                Text(userName, color = AppColors.cream, modifier = Modifier.clickable { onProfile() }.padding(8.dp))
                Text("Выйти", color = AppColors.muted, fontSize = 12.sp, modifier = Modifier.clickable { onLogout() }.padding(8.dp))
            } else {
                Button(
                    onClick = onReserve,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Забронировать", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun Hero(onReserve: () -> Unit, onGoToMarket: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
    ) {
        // Фоновое изображение
        AsyncImage(
            model = "https://images.unsplash.com/photo-1469234496837-d0101f54be3e?w=1600&h=1200&fit=crop&auto=format&q=80",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Затемнение
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AppColors.bg.copy(alpha = 0.8f),
                            AppColors.bg.copy(alpha = 0.95f),
                            AppColors.bg
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(32.dp).height(1.dp).background(AppColors.amber))
                Spacer(modifier = Modifier.width(8.dp))
                Text("МОСКВА, ЦВЕТНОЙ БУЛЬВАР, 12", color = AppColors.amber, fontSize = 10.sp, letterSpacing = 2.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("ПУСТОТА", color = AppColors.cream, fontSize = 64.sp, fontWeight = FontWeight.Black, lineHeight = 64.sp)
            Text("вкуснее", color = AppColors.amber, fontSize = 64.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, lineHeight = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Ресторан, где каждое блюдо — спасённая история. Мы готовим из продуктов с истекающим сроком и продаём их со скидкой до 70%.",
                color = AppColors.muted,
                fontSize = 14.sp,
                modifier = Modifier.width(300.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onReserve, colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg), shape = RoundedCornerShape(4.dp)) {
                    Text("Забронировать", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onGoToMarket, border = BorderStroke(1.dp, AppColors.border), shape = RoundedCornerShape(4.dp)) {
                    Text("В маркет →", color = AppColors.cream)
                }
            }
        }
    }
}

@Composable
fun About() {
    Column(modifier = Modifier.fillMaxWidth().background(AppColors.surface).padding(24.dp)) {
        Text("МАНИФЕСТ", color = AppColors.amber, fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Пустота не в тарелке.", color = AppColors.cream, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Пустота — в мусорных баках.", color = AppColors.amber, fontSize = 32.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(16.dp))

        // Картинка полок с продуктами
        AsyncImage(
            model = "https://images.unsplash.com/photo-1604719312566-8912e9227c6a?w=700&h=900&fit=crop&auto=format&q=80",
            contentDescription = "Полки с продуктами",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(vertical = 16.dp)
                .border(1.dp, AppColors.border, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Text("Мы открылись, потому что не могли смотреть, как магазины выбрасывают тонны абсолютно съедобных продуктов — только потому что на упаковке написана «неудобная» дата. Наш ресторан готовит из них, наш маркет продаёт их.", color = AppColors.muted, fontSize = 14.sp, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Каждое утро мы получаем партию от партнёров-поставщиков. Шеф смотрит, что пришло — и строит вокруг этого меню дня. Остатки попадают в маркет со скидкой до 70%. Ничего не уходит в отходы.", color = AppColors.muted, fontSize = 14.sp, lineHeight = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column { Text("12", color = AppColors.amber, fontSize = 24.sp, fontWeight = FontWeight.Bold); Text("партнёров", color = AppColors.muted, fontSize = 10.sp) }
            Column { Text("2.4 т", color = AppColors.amber, fontSize = 24.sp, fontWeight = FontWeight.Bold); Text("спасено", color = AppColors.muted, fontSize = 10.sp) }
            Column { Text("70%", color = AppColors.amber, fontSize = 24.sp, fontWeight = FontWeight.Bold); Text("макс. скидка", color = AppColors.muted, fontSize = 10.sp) }
        }
    }
}

@Composable
fun MenuSection() {
    Column(modifier = Modifier.fillMaxWidth().background(AppColors.bg).padding(24.dp)) {
        Text("ЧТО ГОТОВИМ", color = AppColors.amber, fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Меню меняется.", color = AppColors.cream, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Философия — нет.", color = AppColors.cream, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(24.dp))
        MENU.forEach { item ->
            Column(modifier = Modifier.fillMaxWidth().border(1.dp, AppColors.border).padding(16.dp)) {
                // Изображение блюда
                AsyncImage(
                    model = item.img,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(AppColors.surface2),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.tags.forEach { tag ->
                        Text(tag.uppercase(), color = AppColors.muted, fontSize = 9.sp, modifier = Modifier.border(1.dp, AppColors.border, RoundedCornerShape(12.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.name, color = AppColors.cream, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(item.desc, color = AppColors.muted, fontSize = 13.sp, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("${item.price} ₽", color = AppColors.amber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("В заказ →", color = AppColors.cream, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MarketSection(tick: Int) {
    var cat by remember { mutableStateOf("все") }
    val visible = if (cat == "все") PRODUCTS else PRODUCTS.filter { it.cat == cat }

    Column(modifier = Modifier.fillMaxWidth().background(AppColors.surface).padding(24.dp)) {
        Text("МАРКЕТ", color = AppColors.amber, fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("До истечения", color = AppColors.cream, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("срока годности", color = AppColors.amber, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            LegendItem("Срочно", Color.Red)
            LegendItem("Сегодня", Color.Yellow)
            LegendItem("Несколько дней", Color.Green)
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            items(CATS) { c ->
                val isSelected = cat == c
                Text(
                    text = c.replaceFirstChar { it.uppercase() },
                    color = if (isSelected) AppColors.bg else AppColors.muted,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .background(if (isSelected) AppColors.amber else AppColors.surface2, RoundedCornerShape(4.dp))
                        .clickable { cat = c }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            visible.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    rowItems.forEach { p -> MarketProductCard(p, tick, Modifier.weight(1f)) }
                    if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp)).border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(modifier = Modifier.size(4.dp).background(color, RoundedCornerShape(50)))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = color, fontSize = 10.sp)
    }
}

@Composable
fun MarketProductCard(p: MarketProduct, tick: Int, modifier: Modifier = Modifier) {
    var added by remember { mutableStateOf(false) }
    val (cdText, level) = countdown(p.expiresAt)
    val badgeColor = when (level) {
        UrgencyLevel.Critical -> Color.Red
        UrgencyLevel.Warning -> Color.Yellow
        UrgencyLevel.Ok -> Color.Green
    }

    Column(modifier = modifier.background(AppColors.bg, RoundedCornerShape(8.dp)).border(1.dp, AppColors.border, RoundedCornerShape(8.dp)).padding(12.dp)) {
        // Изображение продукта
        Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(AppColors.surface2, RoundedCornerShape(4.dp))) {
            AsyncImage(
                model = p.img,
                contentDescription = p.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Бейдж срочности
            Box(modifier = Modifier.align(Alignment.TopStart).padding(4.dp).background(badgeColor, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                Text(cdText, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(p.name, color = AppColors.cream, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(p.weight, color = AppColors.muted, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${p.price} ₽", color = AppColors.amber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text("${p.origPrice} ₽", color = AppColors.muted, fontSize = 10.sp, textDecoration = TextDecoration.LineThrough)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { added = true },
            colors = ButtonDefaults.buttonColors(containerColor = if (added) Color.Green.copy(alpha = 0.2f) else Color.Transparent, contentColor = if (added) Color.Green else AppColors.amber),
            border = BorderStroke(1.dp, if (added) Color.Green else AppColors.amber),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth().height(32.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(if (added) "✓ В корзине" else "Купить", fontSize = 11.sp)
        }
    }
}

@Composable
fun Values() {
    Column(modifier = Modifier.fillMaxWidth().background(AppColors.amber).padding(24.dp)) {
        listOf(
            Triple("1/3", "мировой еды теряется", "Ежегодно 1,3 млрд тонн продуктов уничтожается, пока сотни миллионов человек голодают."),
            Triple("8%", "мировых выбросов CO₂", "Пищевые отходы создают столько же парниковых газов, сколько весь авиатранспорт планеты."),
            Triple("70%", "скидки у нас", "Максимальная скидка, которую вы получаете, покупая продукт, который иначе был бы выброшен.")
        ).forEach { (num, title, body) ->
            Text(num, color = AppColors.bg, fontSize = 40.sp, fontWeight = FontWeight.Black)
            Text(title, color = AppColors.bg, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(body, color = AppColors.bg.copy(alpha = 0.7f), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ReservationSection() {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("2") }
    var comment by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().background(AppColors.bg).padding(24.dp)) {
        Text("РЕЗЕРВАЦИЯ", color = AppColors.amber, fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Забронировать стол", color = AppColors.cream, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        if (sent) {
            Column(modifier = Modifier.fillMaxWidth().background(AppColors.surface).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✓", color = Color.Green, fontSize = 32.sp)
                Text("Готово!", color = AppColors.cream, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Мы позвоним вам для подтверждения.", color = AppColors.muted, fontSize = 14.sp)
                Text("Сделать ещё бронь", color = AppColors.amber, modifier = Modifier.padding(top = 16.dp).clickable { sent = false })
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it }, label = { Text("Имя") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.amber, unfocusedBorderColor = AppColors.border, focusedTextColor = AppColors.cream, unfocusedTextColor = AppColors.cream, focusedLabelColor = AppColors.amber),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it }, label = { Text("Телефон") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.amber, unfocusedBorderColor = AppColors.border, focusedTextColor = AppColors.cream, unfocusedTextColor = AppColors.cream, focusedLabelColor = AppColors.amber),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = guests, onValueChange = { guests = it }, label = { Text("Гостей") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.amber, unfocusedBorderColor = AppColors.border, focusedTextColor = AppColors.cream, unfocusedTextColor = AppColors.cream, focusedLabelColor = AppColors.amber),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = comment, onValueChange = { comment = it }, label = { Text("Комментарий") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.amber, unfocusedBorderColor = AppColors.border, focusedTextColor = AppColors.cream, unfocusedTextColor = AppColors.cream, focusedLabelColor = AppColors.amber),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { sent = true }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
                    shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Забронировать стол", fontWeight = FontWeight.Bold) }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("КОНТАКТЫ", color = AppColors.amber, fontSize = 12.sp, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Цветной бульвар, 12", color = AppColors.cream, fontSize = 16.sp)
        Text("+7 (495) 000-00-00", color = AppColors.muted, fontSize = 14.sp)
        Text("hello@pustota.ru", color = AppColors.muted, fontSize = 14.sp)
    }
}

@Composable
fun Footer() {
    Column(modifier = Modifier.fillMaxWidth().background(AppColors.surface).padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(24.dp).background(AppColors.amber, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                Text("ПВ", color = AppColors.bg, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Пустота вкуснее", color = AppColors.cream, fontStyle = FontStyle.Italic, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("© 2024 — Ресторан с осознанным подходом к еде", color = AppColors.muted, fontSize = 11.sp)
    }
}
