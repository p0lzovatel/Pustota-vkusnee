package com.example.pustota_vkusnee.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pustota_vkusnee.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Модели данных ---
data class AuthUser(val name: String, val email: String)

enum class AuthMode { Login, Register, Forgot }

// --- Основной экран ---
@Composable
fun AuthPage(onAuth: (AuthUser) -> Unit) {
    var mode by remember { mutableStateOf(AuthMode.Login) }

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

    fun handleLoginSubmit() {
        loginError = ""
        if (loginPassword.length < 4) {
            loginError = "Неверный email или пароль"
            return
        }
        isLoginLoading = true
        coroutineScope.launch {
            delay(800)
            isLoginLoading = false
            onAuth(
                AuthUser(
                    name = loginEmail.split("@").firstOrNull() ?: "User",
                    email = loginEmail
                )
            )
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

    // --- Разметка (только форма, без левой панели) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.bg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Логотип
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .background(AppColors.amber, RoundedCornerShape(50))
            ) {
                Text("ПВ", color = AppColors.bg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Пустота вкуснее",
                color = AppColors.cream,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Box(modifier = Modifier.widthIn(max = 350.dp)) {
            renderAuthContent(
                mode = mode,
                onSetMode = { mode = it },
                loginEmail = loginEmail,
                loginPassword = loginPassword,
                loginError = loginError,
                isLoginLoading = isLoginLoading,
                onLoginEmailChange = { loginEmail = it },
                onLoginPasswordChange = { loginPassword = it },
                onLoginSubmit = ::handleLoginSubmit,
                regName = regName,
                regEmail = regEmail,
                regPhone = regPhone,
                regPassword = regPassword,
                regAgree = regAgree,
                regErrors = regErrors,
                isRegLoading = isRegLoading,
                onRegNameChange = { regName = it },
                onRegEmailChange = { regEmail = it },
                onRegPhoneChange = { regPhone = it },
                onRegPasswordChange = { regPassword = it },
                onRegAgreeChange = { regAgree = it },
                onRegisterSubmit = ::handleRegisterSubmit,
                forgotEmail = forgotEmail,
                isForgotLoading = isForgotLoading,
                isForgotSent = isForgotSent,
                onForgotEmailChange = { forgotEmail = it },
                onForgotSubmit = ::handleForgotSubmit,
                onBack = { mode = AuthMode.Login }
            )
        }
    }
}

// --- Рендерер контента ---
@Composable
private fun renderAuthContent(
    mode: AuthMode,
    onSetMode: (AuthMode) -> Unit,
    loginEmail: String,
    loginPassword: String,
    onLoginEmailChange: (String) -> Unit,
    onLoginPasswordChange: (String) -> Unit,
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
    onRegNameChange: (String) -> Unit,
    onRegEmailChange: (String) -> Unit,
    onRegPhoneChange: (String) -> Unit,
    onRegPasswordChange: (String) -> Unit,
    onRegAgreeChange: (Boolean) -> Unit,
    onRegisterSubmit: () -> Unit,
    forgotEmail: String,
    isForgotLoading: Boolean,
    isForgotSent: Boolean,
    onForgotEmailChange: (String) -> Unit,
    onForgotSubmit: () -> Unit,
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

        Text(
            text = title,
            color = AppColors.cream,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        if (sub.isNotBlank()) {
            Text(
                text = sub,
                color = AppColors.muted,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Переключатель вкладок
        if (mode != AuthMode.Forgot) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(AppColors.surface2, RoundedCornerShape(4.dp))
                    .border(1.dp, AppColors.border, RoundedCornerShape(4.dp)),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(AuthMode.Login to "Войти", AuthMode.Register to "Регистрация")
                    .forEach { (m, label) ->
                        val isActive = mode == m
                        Button(
                            onClick = { onSetMode(m) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isActive) AppColors.amber else Color.Transparent,
                                contentColor = if (isActive) AppColors.bg else AppColors.muted
                            ),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.weight(1f).fillMaxHeight().padding(2.dp)
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
                email = loginEmail,
                password = loginPassword,
                error = loginError,
                isLoading = isLoginLoading,
                onEmailChange = onLoginEmailChange,
                onPasswordChange = onLoginPasswordChange,
                onForgot = { onSetMode(AuthMode.Forgot) },
                onSubmit = onLoginSubmit
            )

            AuthMode.Register -> RegisterForm(
                name = regName,
                email = regEmail,
                phone = regPhone,
                password = regPassword,
                agree = regAgree,
                errors = regErrors,
                isLoading = isRegLoading,
                onNameChange = onRegNameChange,
                onEmailChange = onRegEmailChange,
                onPhoneChange = onRegPhoneChange,
                onPasswordChange = onRegPasswordChange,
                onAgreeChange = { onRegAgreeChange(!regAgree) },
                onSubmit = onRegisterSubmit
            )

            AuthMode.Forgot -> ForgotForm(
                email = forgotEmail,
                isLoading = isForgotLoading,
                isSent = isForgotSent,
                onEmailChange = onForgotEmailChange,
                onSubmit = onForgotSubmit,
                onBack = onBack
            )
        }

        // Кнопки соцсетей и подсказки
        if (mode != AuthMode.Forgot) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(color = AppColors.border, modifier = Modifier.weight(1f))
                Text("или", color = AppColors.muted, fontSize = 12.sp, letterSpacing = 1.sp)
                HorizontalDivider(color = AppColors.border, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))
            listOf("🔷 Войти через VK", "🔵 Войти через Google").forEach { text ->
                OutlinedButton(
                    onClick = {},
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AppColors.surface,
                        contentColor = AppColors.cream
                    ),
                    border = BorderStroke(1.dp, AppColors.border),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text, color = AppColors.cream, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            val hintText = if (mode == AuthMode.Login) "Нет аккаунта? Зарегистрироваться" else "Уже есть аккаунт? Войти"
            TextButton(
                onClick = { onSetMode(if (mode == AuthMode.Login) AuthMode.Register else AuthMode.Login) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(hintText, color = AppColors.amber, fontSize = 12.sp)
            }
        }
    }
}

// --- Формы ---

@Composable
private fun LoginForm(
    email: String,
    password: String,
    error: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgot: () -> Unit,
    onSubmit: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email", color = AppColors.muted, fontSize = 12.sp) },
            placeholder = { Text("you@example.com", color = AppColors.muted.copy(alpha = 0.4f)) },
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        var showPassword by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Пароль", color = AppColors.muted, fontSize = 12.sp) },
            placeholder = { Text("••••••••", color = AppColors.muted.copy(alpha = 0.4f)) },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = AppColors.muted
                    )
                }
            },
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = AppColors.urgent,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.urgent.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                    .border(1.dp, AppColors.urgent.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onForgot,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.textButtonColors(contentColor = AppColors.muted)
        ) {
            Text("Забыли пароль?", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AppColors.bg, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Входим…", color = AppColors.bg, fontWeight = FontWeight.Bold)
            } else {
                Text("Войти", color = AppColors.bg, fontWeight = FontWeight.Bold)
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
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Имя", fontSize = 12.sp, color = AppColors.muted) },
                colors = authTextFieldColors(),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Телефон", fontSize = 12.sp, color = AppColors.muted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = authTextFieldColors(),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.weight(1f)
            )
        }
        if (!errors["name"].isNullOrBlank()) {
            Text(errors["name"]!!, color = AppColors.urgent, fontSize = 10.sp)
        }

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email", fontSize = 12.sp, color = AppColors.muted) },
            placeholder = { Text("you@example.com", color = AppColors.muted.copy(alpha = 0.4f)) },
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        if (!errors["email"].isNullOrBlank()) {
            Text(errors["email"]!!, color = AppColors.urgent, fontSize = 10.sp)
        }

        var showPassword by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Пароль", fontSize = 12.sp, color = AppColors.muted) },
            placeholder = { Text("••••••••", color = AppColors.muted.copy(alpha = 0.4f)) },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = AppColors.muted
                    )
                }
            },
            colors = authTextFieldColors(),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        )
        if (!errors["password"].isNullOrBlank()) {
            Text(errors["password"]!!, color = AppColors.urgent, fontSize = 10.sp)
        }

        // Индикатор надёжности пароля
        if (password.isNotEmpty()) {
            val strength = minOf(4, password.length / 2)
            val labels = listOf("слабый", "средний", "хороший", "сильный")
            val currentLabel = labels.getOrElse(strength - 1) { "слабый" }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { i ->
                    val color = when {
                        i < strength && strength < 2 -> AppColors.urgent
                        i < strength && strength < 3 -> AppColors.gold
                        i < strength -> AppColors.safe
                        else -> AppColors.border
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(color, RoundedCornerShape(2.dp))
                    )
                }
                Text(
                    currentLabel,
                    color = AppColors.muted,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Кастомный чекбокс
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable { onAgreeChange() }
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (agree) AppColors.amber else Color.Transparent)
                    .border(1.dp, if (agree) AppColors.amber else AppColors.border, RoundedCornerShape(2.dp))
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                if (agree) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = AppColors.bg,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Соглашаюсь с условиями использования и политикой конфиденциальности",
                color = AppColors.muted,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                textDecoration = TextDecoration.Underline
            )
        }
        if (!errors["agree"].isNullOrBlank()) {
            Text(errors["agree"]!!, color = AppColors.urgent, fontSize = 10.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading && agree,
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AppColors.bg, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Создаём аккаунт…", color = AppColors.bg, fontWeight = FontWeight.Bold)
            } else {
                Text("Создать аккаунт", color = AppColors.bg, fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .background(AppColors.safe.copy(alpha = 0.15f), RoundedCornerShape(50))
                    .border(1.dp, AppColors.safe.copy(alpha = 0.3f), RoundedCornerShape(50))
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = AppColors.safe, modifier = Modifier.size(32.dp))
            }
            Text("Письмо отправлено", color = AppColors.cream, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Если адрес $email зарегистрирован у нас, вы получите ссылку для сброса пароля.",
                color = AppColors.muted,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(contentColor = AppColors.amber)
            ) {
                Text("Вернуться к входу")
            }
        }
    } else {
        Column {
            Text(
                "Введите email, с которым регистрировались — мы пришлём ссылку для сброса пароля.",
                color = AppColors.muted,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email", fontSize = 12.sp, color = AppColors.muted) },
                placeholder = { Text("you@example.com", color = AppColors.muted.copy(alpha = 0.4f)) },
                colors = authTextFieldColors(),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSubmit,
                enabled = !isLoading && email.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.amber, contentColor = AppColors.bg),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AppColors.bg, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Отправляем…", color = AppColors.bg, fontWeight = FontWeight.Bold)
                } else {
                    Text("Сбросить пароль", color = AppColors.bg, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(contentColor = AppColors.muted)
            ) {
                Text("← Назад к входу", fontSize = 12.sp)
            }
        }
    }
}

// --- Стили полей ---

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
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
