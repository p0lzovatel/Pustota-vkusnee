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
        // Заголовок (упрощенно)
        Text(text = "Маркет", style = MaterialTheme.typography.headlineLarge)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Легенда срочности (аналог urgency legend)
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
                    text = c.capitalize(),
                    color = if (isSelected) Color.White else Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .background(if (isSelected) Color.Amber else Color.Transparent, RoundedCornerShape(8.dp))
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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultAnimationSpec = spring())
    ) {
        Column {
            // Изображение и бейдж (здесь просто заглушка для Image)
            Box(modifier = Modifier.height(150.dp).fillMaxWidth()) {
                // Image(painter = painterResource(product.img), contentDescription = null)
                Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) // заглушка
                
                // UrgencyBadge
                val cd = countdown(product.expiresAt)
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
                    Text(text = "${product.price} ₽", color = Color.Amber, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = " ${product.origPrice} ₽", textDecoration = TextDecoration.LineThrough, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
                
                Button(
                    onClick = { added = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (added) Color.Green else Color.Transparent,
                        contentColor = if (added) Color.White else Color.Amber
                    )
                ) {
                    Text(text = if (added) "✓ В корзине" else "Купить")
                }
            }
        }
    }
}

