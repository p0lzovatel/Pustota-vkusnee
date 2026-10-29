 import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Модели данных ---
data class AuthUser(val name: String, val email: String)

enum class AuthMode { Login, Register, Forgot }

// --- Основной экран ---
@Composable
fun AuthPage(onAuth: (AuthUser) -> Unit) {
    val context = LocalContext.current
    var mode by remember { mutableStateOf(AuthMode.Login) }
    var userName by remember { mutableStateOf("") }
    
    // Данные для форм
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var isLoginLoading by remember { mutableStateOf(false) }

    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regAgree by remember { mutableStateOf(false) }
    var regErrors by remember { mutableStateOf(mapOf<String, String>()) }
    var isRegLoading by remember { mutableStateOf(false) }

    var forgotEmail by remember { mutableStateOf("") }
    var isForgotLoading by remember { mutableStateOf(false) }
    var isForgotSent by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // --- Вспомогательные функции (логика) ---
    fun handleLoginSubmit() {
        if (loginPassword.length < 4) {
            loginError = "Неверный email или пароль"
            return
        }
        isLoginLoading = true
        coroutineScope.launch {
            delay(800)
            isLoginLoading = false
            onAuth(AuthUser(name = loginEmail.split("@").firstOrNull() ?: "User", email = loginEmail))
        }
    }

    fun validateRegister(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (regName.trim().isEmpty()) errors["name"] = "Введите имя"
        if (!regEmail.contains("@")) errors["email"] = "Введите корректный email"
        if (regPassword.length < 8) errors["password"] = "Минимум 8 символов"
        if (!regAgree) errors["agree"] = "Нужно согласие"
        return errors
    }

    fun handleRegisterSubmit() {
        val errors = validateRegister()
        if (errors.isNotEmpty()) {
            regErrors = errors
            return
        }
        regErrors = emptyMap()
        isRegLoading = true
        coroutineScope.launch {
            delay(1000)
            isRegLoading = false
            onAuth(AuthUser(regName.trim(), regEmail.trim()))
        }
    }

    fun handleForgotSubmit() {
        isForgotLoading = true
        coroutineScope.launch {
            delay(900)
            isForgotLoading = false
            isForgotSent = true
        }
    }

    // --- UI Компоненты ---
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(1f)) {
            // Левая панель (декоративная, как в оригинале)
            Box(
                modifier = Modifier
                    .weight(0.52f)
                    .fillMaxHeight()
                    .background(Color.Black) // Заглушка вместо картинки
                    .clip(RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)) // Имитация скругления
            ) {
                // Здесь можно вставить Image(painter = painterResource(...))
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Логотип
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Yellow, RoundedCornerShape(50))
                        ) {
                            Text("ПВ", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Пустота вкуснее", color = Color.White, fontSize = 18.sp, style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                    }

                    // Основной текст
                    Column {
                        Text(
                            text = "Еда, которая\nне должна\nисчезнуть",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 40.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Ресторан и маркет продуктов с истекающим сроком годности.\nСкидки до 70%. Ничего не выбрасывается.",
                            color = Color(0xFF888888),
                            fontSize = 14.sp
                        )
                        
                        // Цитата и статистика (упрощенно)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("«Самое вкусное блюдо — то, которое могло быть выброшено...»", color = Color(0xFFFFFFFF), fontSize = 12.sp, style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                        Text("- Илья Сорин", color = Color(0xFF888888), fontSize = 10.sp)
                    }
                }
            }

            // Правая панель (формы)
            Column(
                modifier = Modifier
                    .weight(0.48f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Мобильный логотип
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(32.dp).background(Color.Yellow, RoundedCornerShape(50))
                    ) {
                        Text("ПВ", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Пустота вкуснее", color = Color.White, fontSize = 16.sp, style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                }

                Spacer(modifier = Modifier.height(32.dp))
                Box(modifier = Modifier.widthIn(max = 350.dp)) {
                    renderAuthContent(
                        mode = mode,
                        onSetMode = { mode = it },
                        loginEmail = loginEmail,
                        loginPassword = loginPassword,
                        loginError = loginError,
                        isLoginLoading = isLoginLoading,
                        onLoginSubmit = ::handleLoginSubmit,
                        regName = regName,
                        regEmail = regEmail,
                        regPhone = regPhone,
                        regPassword = regPassword,
                        regAgree = regAgree,
                        regErrors = regErrors,
                        isRegLoading = isRegLoading,
                        onRegisterSubmit = ::handleRegisterSubmit,
                        forgotEmail = forgotEmail,
                        isForgotLoading = isForgotLoading,
                        isForgotSent = isForgotSent,
                        onForgotSubmit = ::handleForgotSubmit,
                        onBack = { mode = AuthMode.Login }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

// --- Рендерер контента (переключатель форм) ---
@Composable
private fun renderAuthContent(
    mode: AuthMode,
    onSetMode: (AuthMode) -> Unit,
    loginEmail: String,
    loginPassword: String,
    loginError: String,
    isLoginLoading: Boolean,
    onLoginSubmit: () -> Unit,
    regName: String,
    regEmail: String,
    regPhone: String,
    regPassword: String,
    regAgree: Boolean,
    regErrors: Map<String, String>,
    isRegLoading: Boolean,
    onRegisterSubmit: () -> Unit,
    forgotEmail: String,
    isForgotLoading: Boolean,
    isForgotSent: Boolean,
    onBack: () -> Unit
) {
    Column {
        // Заголовки
        val title = when (mode) {
            AuthMode.Login -> "Добро пожаловать"
            AuthMode.Register -> "Создать аккаунт"
            AuthMode.Forgot -> "Сброс пароля"
        }
        val sub = when (mode) {
            AuthMode.Login -> "Войдите, чтобы бронировать столики и покупать в маркете"
            AuthMode.Register -> "Зарегистрируйтесь — и получите скидку 10% на первый заказ"
            AuthMode.Forgot -> ""
        }

        Text(text = title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        if (sub.isNotBlank()) {
            Text(text = sub, color = Color(0xFFAAAAAA), fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Переключатель вкладок (Login/Register)
        if (mode != AuthMode.Forgot) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xFF222222), RoundedCornerShape(8.dp)),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(Pair(AuthMode.Login, "Войти"), Pair(AuthMode.Register, "Регистрация"))
                    .forEach { (m, label) ->
                        val isActive = mode == m
                        Button(
                            onClick = { onSetMode(m) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isActive) Color.Yellow else Color.Transparent,
                                contentColor = if (isActive) Color.Black else Color(0xFFAAAAAA)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(label, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Формы
        when (mode) {
            AuthMode.Login -> LoginForm(
                email = loginEmail, password = loginPassword, error = loginError, isLoading = isLoginLoading, onSubmit = onLoginSubmit
            )
            AuthMode.Register -> RegisterForm(
                name = regName, email = regEmail, phone = regPhone, password = regPassword, agree = regAgree, errors = regErrors, isLoading = isRegLoading, onSubmit = onRegisterSubmit
            )
            AuthMode.Forgot -> ForgotForm(
                email = forgotEmail, isLoading = isForgotLoading, isSent = isForgotSent, onSubmit = onForgotSubmit, onBack = onBack
            )
        }

        // Соц. сети (упрощенно)
        if (mode != AuthMode.Forgot) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Divider(color = Color(0xFF444444), modifier = Modifier.weight(1f))
                Text("или", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                Divider(color = Color(0xFF444444), modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            listOf("Войти через VK", "Войти через Google").forEach { text ->
                OutlinedButton(
                    onClick = {},
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF444444)),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(text, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            val hintText = if (mode == AuthMode.Login) "Нет аккаунта? Зарегистрироваться" else "Уже есть аккаунт? Войти"
            TextButton(onClick = { onSetMode(if (mode == AuthMode.Login) AuthMode.Register else AuthMode.Login) }) {
                Text(hintText, color = Color.Yellow)
            }
        }
    }
}

// --- Компоненты форм ---

// ... (предыдущий код AuthPage и renderAuthContent остается без изменений) ...

// --- Компоненты форм (продолжение) ---

@Composable
private fun LoginForm(
    email: String,
    password: String,
    error: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email", color = Color(0xFF666666)) },
            placeholder = { Text("you@example.com") },
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        var showPassword by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Пароль", color = Color(0xFF666666)) },
            placeholder = { Text("••••••••") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFFAAAAAA)
                    )
                }
            },
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = {}, // В реальном проекте нужен callback onForgot
            modifier = Modifier.align(Alignment.End),
            contentColor = Color(0xFFAAAAAA)
        ) {
            Text("Забыли пароль?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow, contentColor = Color.Black),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Входим…", color = Color.Black)
            } else {
                Text("Войти", color = Color.Black)
            }
        }
    }
}

@Composable
private fun RegisterForm(
    name: String,
    email: String,
    phone: String,
    password: String,
    agree: Boolean,
    errors: Map<String, String>,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAgreeChange: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Сетка Имя/Телефон
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Имя") },
                colors = textFieldColors(),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Телефон") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = textFieldColors(),
                modifier = Modifier.weight(1f)
            )
        }
        if (errors["name"].isNotNullOrBlank()) {
            Text(errors["name"]!!, color = Color.Red, fontSize = 10.sp)
        }

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            placeholder = { Text("you@example.com") },
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        if (errors["email"].isNotNullOrBlank()) {
            Text(errors["email"]!!, color = Color.Red, fontSize = 10.sp)
        }

        var showPassword by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Пароль") },
            placeholder = { Text("••••••••") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                }
            },
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        if (errors["password"].isNotNullOrBlank()) {
            Text(errors["password"]!!, color = Color.Red, fontSize = 10.sp)
        }

        // Индикатор силы пароля
        if (password.isNotEmpty()) {
            val strength = minOf(4, password.length / 2)
            val labels = listOf("слабый", "средний", "хороший", "сильный")
            val currentLabel = labels.getOrElse(strength - 1) { "слабый" }
            
            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                repeat(4) { i ->
                    val color = when {
                        i < strength && strength < 2 -> Color.Red
                        i < strength && strength < 3 -> Color.Yellow
                        i < strength -> Color.Green
                        else -> Color(0xFF333333)
                    }
                    Box(modifier = Modifier.size(4.dp).background(color).clip(RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(currentLabel, color = Color(0xFFAAAAAA), fontSize = 10.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }

        // Чекбокс согласия
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Checkbox(
                checked = agree,
                onCheckedChange = { onAgreeChange() },
                checkmarkColor = Color.Yellow,
                uncheckedColor = Color(0xFF444444),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Соглашаюсь с условиями использования и политикой конфиденциальности",
                color = Color(0xFFAAAAAA),
                fontSize = 12.sp,
                style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.Underline)
            )
        }
        if (errors["agree"].isNotNullOrBlank()) {
            Text(errors["agree"]!!, color = Color.Red, fontSize = 10.sp)
        }

        Button(
            onClick = onSubmit,
            enabled = !isLoading && agree,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow, contentColor = Color.Black),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                Text("Создаём аккаунт…", color = Color.Black)
            } else {
                Text("Создать аккаунт", color = Color.Black)
            }
        }
    }
}

@Composable
private fun ForgotForm(
    email: String,
    isLoading: Boolean,
    isSent: Boolean,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    if (isSent) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Green, RoundedCornerShape(50))
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Text("Письмо отправлено", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(
                "Если адрес $email зарегистрирован у нас, вы получите ссылку для сброса пароля.",
                color = Color(0xFFAAAAAA),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Yellow)) {
                Text("Вернуться к входу")
            }
        }
    } else {
        Column {
            Text(
                "Введите email, с которым регистрировались — мы пришлём ссылку для сброса пароля.",
                color = Color(0xFFAAAAAA),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                placeholder = { Text("you@example.com") },
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onSubmit,
                enabled = !isLoading && email.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                    Text("Отправляем…", color = Color.Black)
                } else {
                    Text("Сбросить пароль", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBack, contentColor = Color(0xFFAAAAAA)) {
                Text("← Назад к входу")
            }
        }
    }
}

// Вспомогательные функции

private fun textFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    focusedBorderColor = Color.Yellow,
    unfocusedBorderColor = Color(0xFF444444),
    textColor = Color.White,
    labelColor = Color(0xFFAAAAAA),
    placeholderColor = Color(0xFF555555),
    cursorColor = Color.Yellow
)

// --- PREVIEW (для тестирования в Android Studio) ---
@Preview(showBackground = true)
@Composable
fun AuthPagePreview() {
    MaterialTheme {
        Box(modifier = Modifier.background(Color.Black)) {
            AuthPage(onAuth = { user -> println("Auth success: ${user.name}, ${user.email}") })
        }
    }
}
