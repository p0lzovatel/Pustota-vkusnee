package com.example.pustota_vkusnee.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pustota_vkusnee.AppColors

// --- Модели данных ---
data class UserData(val name: String, val email: String)

data class ProfileData(
    val name: String,
    val email: String,
    val phone: String,
    val city: String,
    val birthday: String
)

data class Notifications(
    val orders: Boolean,
    val promos: Boolean,
    val expiry: Boolean,
    val newsletter: Boolean
)

data class PasswordData(val current: String, val next: String, val confirm: String)

data class OrderItem(val name: String, val qty: Int, val price: Int, val img: String)
data class Order(
    val id: String,
    val date: String,
    val status: String,
    val items: List<OrderItem>,
    val total: Int,
    val delivery: String
)

data class Reservation(
    val id: String,
    val date: String,
    val time: String,
    val guests: Int,
    val status: String,
    val comment: String
)

val MOCK_ORDERS = listOf(
    Order("ПВ-2024-118", "28 сентября 2024", "delivered",
        listOf(
            OrderItem("Йогурт греческий 4%", 2, 94, ""),
            OrderItem("Томаты черри", 1, 79, "")
        ), 267, "Самовывоз"),
    Order("ПВ-2024-099", "19 сентября 2024", "delivered",
        listOf(
            OrderItem("Хлеб «Бородинский»", 1, 44, ""),
            OrderItem("Суп тыквенный", 2, 109, ""),
            OrderItem("Брокколи", 1, 64, "")
        ), 326, "Доставка"),
    Order("ПВ-2024-077", "4 сентября 2024", "cancelled",
        listOf(OrderItem("Сыр «Гауда» 45%", 1, 139, "")), 139, "Доставка")
)

val MOCK_RESERVATIONS = listOf(
    Reservation("Б-441", "5 октября 2024", "19:00", 3, "upcoming", "У окна, пожалуйста"),
    Reservation("Б-398", "14 сентября 2024", "13:00", 2, "past", ""),
    Reservation("Б-354", "2 августа 2024", "20:00", 5, "past", "День рождения")
)

// --- Вспомогательные компоненты ---

@Composable
fun StatusBadge(status: String) {
    val (label, color) = when (status) {
        "delivered" -> "Получен" to AppColors.safe
        "cancelled" -> "Отменён" to AppColors.urgent
        "upcoming" -> "Подтверждено" to AppColors.amber
        "past" -> "Завершено" to AppColors.muted
        else -> status to AppColors.muted
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun Toggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) AppColors.amber else AppColors.border)
            .clickable { onCheckedChange(!checked) }
            .padding(4.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
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
    var currentTab by remember { mutableStateOf(ProfileTab.Profile) }

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

    var notifications by remember {
        mutableStateOf(Notifications(orders = true, promos = false, expiry = true, newsletter = false))
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var passwordForm by remember {
        mutableStateOf(PasswordData(current = "", next = "", confirm = ""))
    }
    var passwordSaved by remember { mutableStateOf(false) }

    var orderFilter by remember { mutableStateOf("all") }

    val initials = user.name
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .take(2)
        .joinToString("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.bg)
    ) {
        // 1. ВЕРХНЯЯ ПАНЕЛЬ (Header)
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
                Spacer(modifier = Modifier.width(4.dp))
                Text("На главную", color = AppColors.muted, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("·", color = AppColors.border)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Пустота вкуснее", color = AppColors.cream, fontStyle = FontStyle.Italic, fontSize = 14.sp)
            }
            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = AppColors.muted
                ),
                border = BorderStroke(1.dp, AppColors.border),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Выйти", fontSize = 10.sp)
            }
        }

        // 2. HERO-ПОЛОСА (Аватар + Статус + Баллы)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.surface)
        ) {
            // Узор (имитация полосок на фоне)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AppColors.amber.copy(alpha = 0.05f), Color.Transparent)
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AppColors.amber.copy(alpha = 0.2f))
                        .border(2.dp, AppColors.amber.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        color = AppColors.amber,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, color = AppColors.cream, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(user.email, color = AppColors.muted, fontSize = 14.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Surface(
                            color = AppColors.amber.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AppColors.amber.copy(alpha = 0.2f))
                        ) {
                            Text(
                                "★ Статус: Regular",
                                color = AppColors.amber,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text("С нами с сентября 2024", color = AppColors.muted, fontSize = 12.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("2 390", color = AppColors.amber, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("бонусных баллов", color = AppColors.muted, fontSize = 12.sp)
                }
            }
        }

        // 3. ТАБЫ (Навигация)
        ScrollableTabRow(
            selectedTabIndex = currentTab.ordinal,
            containerColor = AppColors.surface,
            contentColor = AppColors.amber,
            edgePadding = 16.dp,
            divider = { HorizontalDivider(color = AppColors.border, thickness = 1.dp) }
        ) {
            val tabs = listOf(
                ProfileTab.Profile to "Профиль",
                ProfileTab.Orders to "Заказы (${MOCK_ORDERS.size})",
                ProfileTab.Reservations to "Брони (${MOCK_RESERVATIONS.count { it.status == "upcoming" }})",
                ProfileTab.Settings to "Настройки"
            )
            tabs.forEach { (tab, label) ->
                Tab(
                    selected = currentTab == tab,
                    onClick = { currentTab = tab },
                    text = {
                        Text(
                            label,
                            fontWeight = FontWeight.SemiBold,
                            color = if (currentTab == tab) AppColors.amber else AppColors.muted
                        )
                    }
                )
            }
        }

        // 4. КОНТЕНТ (Зависит от вкладки)
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            when (currentTab) {
                ProfileTab.Profile -> ProfileTabContent(
                    form = profileForm,
                    saved = savedMessage,
                    onSave = { savedMessage = it },
                    onUpdateForm = { profileForm = it }
                )
                ProfileTab.Orders -> OrdersTabContent(
                    filter = orderFilter,
                    onFilterChange = { orderFilter = it }
                )
                ProfileTab.Reservations -> ReservationsTabContent()
                ProfileTab.Settings -> SettingsTabContent(
                    notifications = notifications,
                    showDeleteConfirm = showDeleteConfirm,
                    passwordForm = passwordForm,
                    passwordSaved = passwordSaved,
                    onUpdate = { n, del, p, ps ->
                        notifications = n
                        showDeleteConfirm = del
                        passwordForm = p
                        passwordSaved = ps
                    },
                    onLogout = onLogout
                )
            }
        }
    }
}

// --- ВКЛАДКА: ПРОФИЛЬ ---

@Composable
fun ProfileTabContent(
    form: ProfileData,
    saved: Boolean,
    onSave: (Boolean) -> Unit,
    onUpdateForm: (ProfileData) -> Unit
) {
    var localForm by remember(form) { mutableStateOf(form) }
    val initials = localForm.name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")

    Column(modifier = Modifier.fillMaxWidth()) {

        // Левая карточка (Аватар и Карта лояльности)
        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.surface),
            border = BorderStroke(1.dp, AppColors.border),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(AppColors.amber.copy(alpha = 0.2f))
                        .border(2.dp, AppColors.amber.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, color = AppColors.amber, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(localForm.name, color = AppColors.cream, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(localForm.email, color = AppColors.muted, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = AppColors.border)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("118", color = AppColors.amber, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("заказов", color = AppColors.muted, fontSize = 10.sp)
                    }
                    VerticalDivider(color = AppColors.border, modifier = Modifier.height(30.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("2 390", color = AppColors.amber, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("баллов", color = AppColors.muted, fontSize = 10.sp)
                    }
                }
            }
        }

        // Карта лояльности (янтарная)
        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.amber),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                Column {
                    Text("КАРТА ГОСТЯ", color = AppColors.bg.copy(alpha = 0.6f), fontSize = 10.sp, letterSpacing = 1.sp)
                    Text("2 390 баллов", color = AppColors.bg, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("1 балл = 1 ₽ при следующей покупке в маркете. Баллы сгорают через 90 дней.", color = AppColors.bg.copy(alpha = 0.7f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = 0.478f,
                        color = AppColors.bg,
                        trackColor = AppColors.bg.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("2 390 из 5 000 до Silver", color = AppColors.bg.copy(alpha = 0.6f), fontSize = 10.sp)
                        Text("48%", color = AppColors.bg.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                }
            }
        }

        // Правая карточка (Форма)
        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.surface),
            border = BorderStroke(1.dp, AppColors.border),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Личные данные", color = AppColors.cream, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = localForm.name,
                    onValueChange = { localForm = localForm.copy(name = it) },
                    label = { Text("Имя", fontSize = 12.sp) },
                    colors = profileTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = localForm.phone,
                    onValueChange = { localForm = localForm.copy(phone = it) },
                    label = { Text("Телефон", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = profileTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = localForm.email,
                    onValueChange = { localForm = localForm.copy(email = it) },
                    label = { Text("Email", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = profileTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = localForm.city,
                    onValueChange = { localForm = localForm.copy(city = it) },
                    label = { Text("Город", fontSize = 12.sp) },
                    colors = profileTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = localForm.birthday,
                    onValueChange = { localForm = localForm.copy(birthday = it) },
                    label = { Text("Дата рождения", fontSize = 12.sp) },
                    colors = profileTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = AppColors.border)
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            onUpdateForm(localForm)
                            onSave(true)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Сохранить", fontWeight = FontWeight.SemiBold)
                    }
                    if (saved) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = AppColors.safe, modifier = Modifier.size(16.dp))
                            Text("Сохранено", color = AppColors.safe, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// --- ВКЛАДКА: ЗАКАЗЫ ---

@Composable
fun OrdersTabContent(filter: String, onFilterChange: (String) -> Unit) {
    val filtered = if (filter == "all") MOCK_ORDERS else MOCK_ORDERS.filter { it.status == filter }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            listOf("all", "delivered", "cancelled").forEach { f ->
                val label = when (f) {
                    "all" -> "Все"
                    "delivered" -> "Получены"
                    else -> "Отменены"
                }
                val isSelected = filter == f
                Button(
                    onClick = { onFilterChange(f) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) AppColors.amber else AppColors.surface2,
                        contentColor = if (isSelected) AppColors.bg else AppColors.muted
                    ),
                    shape = RoundedCornerShape(4.dp),
                    border = if (!isSelected) BorderStroke(1.dp, AppColors.border) else null,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Заказов с таким статусом нет", color = AppColors.muted, textAlign = TextAlign.Center)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                filtered.forEach { order -> OrderCard(order = order) }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppColors.surface),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, AppColors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Шапка заказа
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(AppColors.surface), // Чтобы перекрыть границы
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.id, color = AppColors.cream, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${order.date} · ${order.delivery}", color = AppColors.muted, fontSize = 12.sp)
                }
                StatusBadge(status = order.status)
                Column(horizontalAlignment = Alignment.End) {
                    Text("${order.total} ₽", color = AppColors.amber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${order.items.size} товара", color = AppColors.muted, fontSize = 11.sp)
                }
            }
            HorizontalDivider(color = AppColors.border)
            // Товары
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                order.items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(AppColors.surface2)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, color = AppColors.cream, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${item.qty} шт. × ${item.price} ₽", color = AppColors.muted, fontSize = 11.sp)
                        }
                        Text("${item.qty * item.price} ₽", color = AppColors.cream, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
            if (order.status == "delivered") {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {},
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = AppColors.amber)
                ) {
                    Text("Купить снова →", fontSize = 13.sp)
                }
            }
        }
    }
}

// --- ВКЛАДКА: БРОНИ ---

@Composable
fun ReservationsTabContent() {
    val upcoming = MOCK_RESERVATIONS.filter { it.status == "upcoming" }
    val past = MOCK_RESERVATIONS.filter { it.status == "past" }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (upcoming.isNotEmpty()) {
            Text(
                text = "ПРЕДСТОЯЩИЕ",
                color = AppColors.amber,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                upcoming.forEach { r -> ReservationCard(r = r, isUpcoming = true) }
            }
        }

        if (past.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "ИСТОРИЯ ПОСЕЩЕНИЙ",
                color = AppColors.muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                past.forEach { r -> ReservationCard(r = r, isUpcoming = false) }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = AppColors.border)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("+ Новая бронь", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ReservationCard(r: Reservation, isUpcoming: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isUpcoming) AppColors.surface else AppColors.surface.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isUpcoming) AppColors.amber.copy(alpha = 0.3f) else AppColors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Блок даты
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isUpcoming) AppColors.amber else AppColors.surface2),
                contentAlignment = Alignment.Center
            ) {
                val dateParts = r.date.split(" ")
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(dateParts.firstOrNull() ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (isUpcoming) AppColors.bg else AppColors.muted)
                    Text(dateParts.drop(1).joinToString(" "), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isUpcoming) AppColors.bg.copy(alpha = 0.8f) else AppColors.muted.copy(alpha = 0.6f))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(r.time, color = AppColors.cream, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(r.status)
                }
                val guestsText = "${r.guests} ${
                    when (r.guests) {
                        1 -> "гость"
                        in 2..4 -> "гостя"
                        else -> "гостей"
                    }
                }${if (r.comment.isNotBlank()) " · $r.comment" else ""}"
                Text(guestsText, color = AppColors.muted, fontSize = 13.sp)
                Text("Бронь ${r.id} · Цветной бульвар, 12", color = AppColors.muted, fontSize = 11.sp)
            }

            if (isUpcoming) {
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {},
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.urgent),
                        border = BorderStroke(1.dp, AppColors.urgent),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Отменить", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = {},
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.cream),
                        border = BorderStroke(1.dp, AppColors.border),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Изменить", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// --- ВКЛАДКА: НАСТРОЙКИ ---

@Composable
fun SettingsTabContent(
    notifications: Notifications,
    showDeleteConfirm: Boolean,
    passwordForm: PasswordData,
    passwordSaved: Boolean,
    onUpdate: (Notifications, Boolean, PasswordData, Boolean) -> Unit,
    onLogout: () -> Unit
) {
    var localNotifs by remember(notifications) { mutableStateOf(notifications) }
    var localPass by remember(passwordForm) { mutableStateOf(passwordForm) }
    var showDelete by remember(showDeleteConfirm) { mutableStateOf(showDeleteConfirm) }
    var passSaved by remember(passwordSaved) { mutableStateOf(passwordSaved) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Уведомления
        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.surface),
            border = BorderStroke(1.dp, AppColors.border),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Уведомления", color = AppColors.cream, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                listOf(
                    "Статус заказов" to "orders",
                    "Скоро истекает срок" to "expiry",
                    "Акции и скидки" to "promos",
                    "Рассылка" to "newsletter"
                ).forEach { (label, field) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(label, color = AppColors.cream, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                when (field) {
                                    "orders" -> "Когда заказ принят, отправлен или готов к выдаче"
                                    "expiry" -> "Напоминание за 2 часа до исчезновения товара"
                                    "promos" -> "Новые спецпредложения и распродажи"
                                    else -> "Еженедельный дайджест о меню и событиях"
                                },
                                color = AppColors.muted, fontSize = 11.sp
                            )
                        }
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
            }
        }

        // Смена пароля
        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.surface),
            border = BorderStroke(1.dp, AppColors.border),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Изменить пароль", color = AppColors.cream, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = localPass.current,
                    onValueChange = { localPass = localPass.copy(current = it) },
                    label = { Text("Текущий пароль") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    colors = profileTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = localPass.next,
                    onValueChange = { localPass = localPass.copy(next = it) },
                    label = { Text("Новый пароль") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    colors = profileTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = localPass.confirm,
                    onValueChange = { localPass = localPass.copy(confirm = it) },
                    label = { Text("Повторите новый") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    colors = profileTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            if (localPass.next == localPass.confirm) {
                                passSaved = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Обновить пароль", fontWeight = FontWeight.SemiBold)
                    }
                    if (passSaved) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Обновлено", color = AppColors.safe, fontSize = 14.sp)
                    }
                }
            }
        }

        // Опасная зона
        Card(
            colors = CardDefaults.cardColors(containerColor = AppColors.surface),
            border = BorderStroke(1.dp, AppColors.urgent.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Опасная зона", color = AppColors.cream, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Удаление аккаунта необратимо. История заказов, баллы и брони будут удалены без возможности восстановления.", color = AppColors.muted, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(16.dp))
                if (!showDelete) {
                    OutlinedButton(
                        onClick = { showDelete = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.urgent),
                        border = BorderStroke(1.dp, AppColors.urgent.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Удалить аккаунт", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .background(AppColors.urgent.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .border(1.dp, AppColors.urgent.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(16.dp)
                    ) {
                        Text("Вы уверены? Это действие необратимо.", color = AppColors.urgent, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showDelete = false },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.muted),
                                border = BorderStroke(1.dp, AppColors.border),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Отмена")
                            }
                            Button(
                                onClick = {
                                    showDelete = false
                                    onLogout() // Выход из аккаунта
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.urgent, contentColor = AppColors.cream),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Удалить навсегда")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Стили полей ---

@Composable
private fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AppColors.amber,
    unfocusedBorderColor = AppColors.border,
    focusedTextColor = AppColors.cream,
    unfocusedTextColor = AppColors.cream,
    focusedLabelColor = AppColors.amber,
    unfocusedLabelColor = AppColors.muted,
    cursorColor = AppColors.amber,
    focusedContainerColor = AppColors.surface2,
    unfocusedContainerColor = AppColors.surface2
)
