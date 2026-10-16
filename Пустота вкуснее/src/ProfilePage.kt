package com.example.myapp.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.ui.theme.AppColors // Импортируй свои цвета сюда

// --- МОК-ДАННЫЕ (Аналог const ORDERS = [...] в JS) ---
data class OrderItem(val name: String, val qty: Int, val price: Int, val img: String)
data class Order(
    val id: String,
    val date: String,
    val status: String, // delivered, cancelled
    val items: List<OrderItem>,
    val total: Int,
    val delivery: String
)

data class Reservation(
    val id: String,
    val date: String,
    val time: String,
    val guests: Int,
    val status: String, // upcoming, past
    val comment: String
)

val MOCK_ORDERS = listOf(
    Order("ПВ-2024-118", "28 сентября 2024", "delivered",
        listOf(OrderItem("Йогурт греческий 4%", 2, 94, ""), OrderItem("Томаты черри", 1, 79, "")), 267, "Самовывоз"),
    Order("ПВ-2024-099", "19 сентября 2024", "delivered",
        listOf(OrderItem("Хлеб «Бородинский»", 1, 44, ""), OrderItem("Суп тыквенный", 2, 109, "")), 326, "Доставка"),
    Order("ПВ-2024-077", "4 сентября 2024", "cancelled",
        listOf(OrderItem("Сыр «Гауда» 45%", 1, 139, "")), 139, "Доставка")
    )
)

val MOCK_RESERVATIONS = listOf(
    Reservation("Б-441", "5 октября 2024", "19:00", 3, "upcoming", "У окна, пожалуйста"),
    Reservation("Б-398", "14 сентября 2024", "13:00", 2, "past", ""),
    Reservation("Б-354", "2 августа 2024", "20:00", 5, "past", "День рождения")
)

// --- Вспомогательные компоненты (Аналог Toggle, StatusBadge) ---

@Composable
fun StatusBadge(status: String) {
    var label by remember { mutableStateOf("") }
    var style by remember { mutableStateOf(AppColors.muted) }
    
    when(status) {
        "delivered" -> { label = "Получен"; style = AppColors.safe }
        "cancelled" -> { label = "Отменён"; style = AppColors.urgent }
        "upcoming" -> { label = "Подтверждено"; style = AppColors.amber }
        "past" -> { label = "Завершено"; style = AppColors.muted }
        else -> { label = status; style = AppColors.muted }
    }

    Surface(
        color = style.copy(alpha = 0.1f),
        border = androidx.compose.ui.graphics.Stroke(1.dp, style.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = style,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(8.dp, 4.dp)
        )
    }
}

@Composable
fun Toggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp, 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) AppColors.amber else AppColors.border)
            .clickable { onCheckedChange(!checked) }
            .padding(end = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = if (checked) 16.dp else 0.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(AppColors.cream)
        )
    }
}

// --- ГЛАВНЫЙ КОМПОНЕНТ ---

enum class ProfileTab { Profile, Orders, Reservations, Settings }

@Composable
fun ProfilePage(
    user: UserData,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    // Состояние табов
    var currentTab by remember { mutableStateOf(ProfileTab.Profile) }
    val context = LocalContext.current

    // Данные для профиля (аналог useState в ProfileTab)
    var profileForm by remember(user) { 
        mutableStateOf(
            ProfileData(
                name = user.name,
                email = user.email,
                phone = "+7 (916) 000-00-00",
                city = "Москва",
                birthday = "1990-06-15"
            )
        ) 
    }
    var savedMessage by remember { mutableStateOf(false) }

    // Данные для настроек (аналог useState в SettingsTab)
    var notifications by remember { 
        mutableStateOf(Notifications(orders = true, promos = false, expiry = true, newsletter = false)) 
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var passwordForm by remember { 
        mutableStateOf(PasswordData(current = "", next = "", confirm = "")) 
    }
    var passwordSaved by remember { mutableStateOf(false) }

    // Фильтр заказов
    var orderFilter by remember { mutableStateOf("all") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.bg)
    ) {
        // 1. HEADER (Верхняя панель)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(AppColors.surface.copy(alpha = 0.95f))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Назад", tint = AppColors.muted)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("На главную", color = AppColors.muted, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Divider(modifier = Modifier.height(16.dp).width(1.dp), color = AppColors.border)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Пустота вкуснее", color = AppColors.cream, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = AppColors.muted
                ),
                border = androidx.compose.ui.graphics.Stroke(1.dp, AppColors.border),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Выйти", fontSize = 10.sp)
            }
        }

        // 2. HERO STRIP (Аватар + Баллы)
        val initials = user.name.split(" ").map { it.firstOrNull()?.uppercase() ?: "" }.take(2).joinToString("")
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(0, 0, 16.dp, 16.dp)) // Скругление только снизу
                .background(
                    Brush.linearGradient(
                        colors = listOf(AppColors.surface, AppColors.surface2),
                        start = Offset(0f, 0f),
                        end = Offset(0f, 100f)
                    )
                )
                .border(1.dp, AppColors.border, RoundedCornerShape(0, 0, 16.dp, 16.dp))
        ) {
            // Декоративная текстура (упрощенно через градиент или просто цвет)
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AppColors.amber.copy(alpha = 0.2f))
                            .border(2.dp, AppColors.amber.copy(alpha = 0.4f))
                    ) {
                        Text(initials, color = AppColors.amber, fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(user.name, color = AppColors.cream, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text(user.email, color = AppColors.muted, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            Chip(
                                colors = ChipDefaults.chipColors(containerColor = AppColors.amber.copy(alpha = 0.1f)),
                                content = { Text("★ Статус: Regular", color = AppColors.amber) }
                            )
                            Text("С нами с сентября 2024", color = AppColors.muted, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    // Блок баллов (скрыт на мобильных, виден на планшетах - здесь упростим)
                    Text("2 390", color = AppColors.amber, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("бонусных баллов", color = AppColors.muted, fontSize = 10.sp)
                }
            }
        }

        // 3. ТАБЫ (TabRow)
        TabRow(
            selectedTabIndex = currentTab.ordinal,
            containerColor = AppColors.surface,
            indicator = { tabPosition ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPosition)
                        .height(3.dp)
                        .background(AppColors.amber)
                )
            },
            divider = { Divider(color = AppColors.border, thickness = 1.dp) }
        ) {
            listOf(
                ProfileTab.Profile to "Профиль",
                ProfileTab.Orders to "Заказы (${MOCK_ORDERS.size})",
                ProfileTab.Reservations to "Брони (${MOCK_RESERVATIONS.count { it.status == "upcoming" }})",
                ProfileTab.Settings to "Настройки"
            ).forEachIndexed { index, (tab, label) ->
                Tab(
                    selected = currentTab == tab,
                    onClick = { currentTab = tab },
                    text = { Text(label, color = if (currentTab == tab) AppColors.amber else AppColors.muted) },
                    selectedContentColor = AppColors.amber,
                    unselectedContentColor = AppColors.muted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // 4. КОНТЕНТ (Switch по табам)
        Box(modifier = Modifier.weight(1f).padding(16.dp)) {
            when (currentTab) {
                ProfileTab.Profile -> ProfileContent(profileForm, savedMessage) { savedMessage = it }
                ProfileTab.Orders -> OrdersContent(orderFilter) { orderFilter = it }
                ProfileTab.Reservations -> ReservationsContent()
                ProfileTab.Settings -> SettingsContent(notifications, showDeleteConfirm, passwordForm, passwordSaved) { showDeleteConfirm = it; passwordSaved = it }
            }
        }
    }
}

// --- ПОД-КОМПОНЕНТЫ (Логика вкладок) ---

@Composable
fun ProfileContent(
    form: ProfileData,
    saved: Boolean,
    onSave: (Boolean) -> Unit,
    onUpdateForm: (ProfileData) -> Unit
) {
    var localForm by remember(form) { mutableStateOf(form) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Личные данные",
            color = AppColors.cream,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.marginBottom(24.dp)
        )

        GridRow {
            GridCell(weight = 1f) {
                OutlinedTextField(
                    value = localForm.name,
                    onValueChange = { localForm = localForm.copy(name = it) },
                    label = { Text("Имя", color = AppColors.muted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppColors.amber,
                        unfocusedBorderColor = AppColors.border,
                        textColor = AppColors.cream
                    ),
                    modifier = Modifier.fillMaxWidth().padding(end = 8.dp)
                )
            }
            GridCell(weight = 1f) {
                OutlinedTextField(
                    value = localForm.phone,
                    onValueChange = { localForm = localForm.copy(phone = it) },
                    label = { Text("Телефон", color = AppColors.muted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppColors.amber,
                        unfocusedBorderColor = AppColors.border,
                        textColor = AppColors.cream
                    ),
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GridRow {
            GridCell(weight = 1f) {
                OutlinedTextField(
                    value = localForm.email,
                    onValueChange = { localForm = localForm.copy(email = it) },
                    label = { Text("Email", color = AppColors.muted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppColors.amber,
                        unfocusedBorderColor = AppColors.border,
                        textColor = AppColors.cream
                    ),
                    modifier = Modifier.fillMaxWidth().padding(end = 8.dp)
                )
            }
            GridCell(weight = 1f) {
                OutlinedTextField(
                    value = localForm.city,
                    onValueChange = { localForm = localForm.copy(city = it) },
                    label = { Text("Город", color = AppColors.muted) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AppColors.amber,
                        unfocusedBorderColor = AppColors.border,
                        textColor = AppColors.cream
                    ),
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = localForm.birthday,
            onValueChange = { localForm = localForm.copy(birthday = it) },
            label = { Text("Дата рождения", color = AppColors.muted) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppColors.amber,
                unfocusedBorderColor = AppColors.border,
                textColor = AppColors.cream
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    onUpdateForm(localForm)
                    onSave(true)
                    // В реальном проекте тут будет запрос к API
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text("Сохранить", fontWeight = FontWeight.SemiBold)
            }

            if (saved) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_save), // Или свой векторный ресурс
                        contentDescription = null,
                        tint = AppColors.safe
                    )
                    Text("Сохранено", color = AppColors.safe, fontSize = 14.sp)
                }
            }
        }
    }
}

// --- ORDERS CONTENT ---

@Composable
fun OrdersContent(filter: String, onFilterChange: (String) -> Unit) {
    val filtered = if (filter == "all") MOCK_ORDERS else MOCK_ORDERS.filter { it.status == filter }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            listOf("all", "delivered", "cancelled").forEach { f ->
                val label = when (f) {
                    "all" -> "Все"
                    "delivered" -> "Получены"
                    else -> "Отменены"
                }
                val isSelected = filter == f
                OutlinedButton(
                    onClick = { onFilterChange(f) },
                    colors = if (isSelected)
                        ButtonDefaults.outlinedButtonColors(containerColor = AppColors.amber, contentColor = AppColors.bg)
                    else
                        ButtonDefaults.outlinedButtonColors(containerColor = AppColors.surface2, contentColor = AppColors.muted),
                    border = androidx.compose.ui.graphics.Stroke(1.dp, if (isSelected) AppColors.amber else AppColors.border),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(label, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp).wrapContentSize(Alignment.Center)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 48.sp)
                    Text("Заказов с таким статусом нет", color = AppColors.muted, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filtered) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Surface(
        color = AppColors.surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.ui.graphics.Stroke(1.dp, AppColors.border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.id, color = AppColors.cream, fontWeight = FontWeight.Medium)
                    Text("${order.date} · ${order.delivery}", color = AppColors.muted, fontSize = 12.sp)
                }
                StatusBadge(status = order.status)
                Text("${order.total} ₽", color = AppColors.amber, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${order.items.size} ${if (order.items.size == 1) "товар" else "товара"}", color = AppColors.muted, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(order.items) { item ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(AppColors.surface2)) {
                            // Тут можно вставить Image(painter = rememberImagePainter(item.img)) если есть Coil/Glide
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, color = AppColors.cream, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${item.qty} шт. × ${item.price} ₽", color = AppColors.muted, fontSize = 12.sp)
                        }
                        Text("${item.qty * item.price} ₽", color = AppColors.cream, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (order.status == "delivered") {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {}) {
                    Text("Купить снова →", color = AppColors.amber)
                }
            }
        }
    }
}

// --- RESERVATIONS CONTENT ---

@Composable
fun ReservationsContent() {
    val upcoming = MOCK_RESERVATIONS.filter { it.status == "upcoming" }
    val past = MOCK_RESERVATIONS.filter { it.status == "past" }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (upcoming.isNotEmpty()) {
            Text(
                text = "Предстоящие",
                color = AppColors.amber,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                textTransform = TextTransform.Uppercase,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(upcoming) { r ->
                    ReservationCard(r = r, isUpcoming = true)
                }
            }
        }

        if (past.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "История посещений",
                color = AppColors.muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                textTransform = TextTransform.Uppercase,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(past) { r ->
                    ReservationCard(r = r, isUpcoming = false)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = AppColors.border)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_input_add), contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Новая бронь", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// --- ПРОДОЛЖЕНИЕ ReservationCard ---

@Composable
fun ReservationCard(r: Reservation, isUpcoming: Boolean) {
    Surface(
        color = if (isUpcoming) AppColors.surface else AppColors.surface.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp),
        border = if (isUpcoming)
            androidx.compose.ui.graphics.Stroke(1.dp, AppColors.amber.copy(alpha = 0.3f))
        else
            androidx.compose.ui.graphics.Stroke(1.dp, AppColors.border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val dateParts = r.date.split(" ")
            val day = dateParts.firstOrNull() ?: ""
            val rest = dateParts.drop(1).joinToString(" ")

            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isUpcoming) AppColors.amber else AppColors.surface2)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(day, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = if (isUpcoming) AppColors.bg else AppColors.muted)
                        Text(rest, fontSize = 10.sp, color = if (isUpcoming) AppColors.bg.copy(alpha = 0.8f) else AppColors.muted.copy(alpha = 0.6f))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                        Text(r.time, color = AppColors.cream, fontWeight = FontWeight.SemiBold)
                        StatusBadge(r.status)
                    }
                    val guestsText = "${r.guests} ${
                        when (r.guests) {
                            1 -> "гость"
                            in 2..4 -> "гостя"
                            else -> "гостей"
                        }
                    }${if (r.comment.isNotBlank()) " · $r.comment" else ""}"
                    Text(guestsText, color = AppColors.muted, fontSize = 13.sp, lineHeight = 18.sp)
                    Text("Бронь ${r.id} · Цветной бульвар, 12", color = AppColors.muted.copy(alpha = 0.6f), fontSize = 11.sp)
                }

                if (isUpcoming) {
                    Column(horizontalArrangement = Arrangement.End) {
                        OutlinedButton(
                            onClick = { /* Отмена */ },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = AppColors.urgent
                            ),
                            border = androidx.compose.ui.graphics.Stroke(1.dp, AppColors.urgent),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text("Отменить", fontSize = 12.sp, color = AppColors.urgent)
                        }
                        OutlinedButton(
                            onClick = { /* Изменить */ },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = AppColors.surface2,
                                contentColor = AppColors.cream
                            ),
                            border = androidx.compose.ui.graphics.Stroke(1.dp, AppColors.border),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Изменить", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// --- SETTINGS CONTENT (Вкладка Настройки) ---

@Composable
fun SettingsContent(
    notifications: Notifications,
    showDeleteConfirm: Boolean,
    passwordForm: PasswordData,
    passwordSaved: Boolean,
    onUpdate: (Notifications, Boolean, PasswordData, Boolean) -> Unit
) {
    var localNotifs by remember(notifications) { mutableStateOf(notifications) }
    var localPass by remember(passwordForm) { mutableStateOf(passwordForm) }
    var showDelete by remember { mutableStateOf(showDeleteConfirm) }
    var passSaved by remember { mutableStateOf(passwordSaved) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Секция уведомлений
        Text("Уведомления", color = AppColors.cream, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.marginBottom(16.dp))

        listOf(
            "Заказы" to "orders",
            "Акции и промо" to "promos",
            "Срок годности товаров" to "expiry",
            "Рассылка" to "newsletter"
        ).forEach { (label, field) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(label, color = AppColors.cream, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Toggle(
                    checked = when (field) {
                        "orders" -> localNotifs.orders
                        "promos" -> localNotifs.promos
                        "expiry" -> localNotifs.expiry
                        else -> localNotifs.newsletter
                    },
                    onCheckedChange = {
                        localNotifs = when (field) {
                            "orders" -> localNotifs.copy(orders = it)
                            "promos" -> localNotifs.copy(promos = it)
                            "expiry" -> localNotifs.copy(expiry = it)
                            else -> localNotifs.copy(newsletter = it)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = AppColors.border, thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))

        // Секция смены пароля
        Text("Смена пароля", color = AppColors.cream, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.marginBottom(16.dp))

        OutlinedTextField(
            value = localPass.current,
            onValueChange = { localPass = localPass.copy(current = it) },
            label = { Text("Текущий пароль", color = AppColors.muted) },
            isError = false,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppColors.amber,
                unfocusedBorderColor = AppColors.border,
                textColor = AppColors.cream
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = localPass.next,
            onValueChange = { localPass = localPass.copy(next = it) },
            label = { Text("Новый пароль", color = AppColors.muted) },
            isError = false,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AppColors.amber,
                unfocusedBorderColor = AppColors.border,
                textColor = AppColors.cream
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = localPass.confirm,
            onValueChange = { localPass = localPass.copy(confirm = it) },
            label = { Text("Подтверждение", color = AppColors.muted) },
            isError = localPass.next != localPass.confirm && localPass.next.isNotEmpty(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (localPass.next != localPass.confirm) AppColors.urgent else AppColors.amber,
                unfocusedBorderColor = AppColors.border,
                textColor = AppColors.cream
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Button(
                onClick = {
                    // Тут должна быть валидация и запрос к API
                    if (localPass.next == localPass.confirm) {
                        passSaved = true
                        // onUpdate(...)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Сохранить пароль", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            if (passSaved) {
                Text("Сохранено", color = AppColors.safe, fontSize = 12.sp, modifier = Modifier.alignByBaseline())
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = AppColors.border, thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка удаления аккаунта
        Text("Безопасность и удаление", color = AppColors.cream, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.marginBottom(12.dp))
        
        Button(
            onClick = { showDelete = true },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.urgent, contentColor = AppColors.bg),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_delete), contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Удалить аккаунт", fontWeight = FontWeight.SemiBold)
            }
        }

        if (showDelete) {
            AlertDialog(
                onDismissRequest = { showDelete = false },
                title = { Text("Вы уверены?", color = AppColors.urgent) },
                text = { Text("Это действие нельзя отменить. Все ваши данные, заказы и баллы будут безвозвратно удалены.") },
                confirmButton = {
                    Button(onClick = { 
                        showDelete = false 
                        /* Тут логика удаления */
                    }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.urgent)) {
                        Text("Да, удалить", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(onClick = { showDelete = false }) {
                        Text("Отмена")
                    }
                },
                backgroundColor = AppColors.surface,
                contentColor = AppColors.cream
            )
        }
    }
}
