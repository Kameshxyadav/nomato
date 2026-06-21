package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Dining
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.style.TextAlign
import com.example.data.NomatoRepository
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.PastOrder
import com.example.data.Restaurant
import com.example.data.SavedAddress
import com.example.ui.NomatoViewModel
import com.example.ui.NotificationItem
import com.example.ui.theme.NomatoGold
import com.example.ui.theme.NomatoRed
import com.example.ui.theme.NomatoRedLight
import com.example.ui.theme.NomatoVegGreen
import com.example.ui.theme.ScreenBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SoftGrey
import kotlin.random.Random
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHubScreen(
    viewModel: NomatoViewModel,
    onRestaurantSelected: () -> Unit,
    onCartSelected: () -> Unit,
    onTrackingSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val cartItems by viewModel.cartItems.collectAsState()
    val liveOrder by viewModel.liveOrder.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        bottomBar = {
            Column {
                // Sizzling Active Cart floating footer indicator
                if (cartItems.isNotEmpty() && selectedTab != 1) { // Hide if on favorites, etc.
                    val cartRestaurant by viewModel.cartRestaurant.collectAsState()
                    val totalQty = cartItems.sumOf { it.quantity }
                    val subTotal = cartItems.sumOf { it.totalPrice }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NomatoRed)
                            .clickable { onCartSelected() }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .testTag("floating_cart_bar"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$totalQty item${if (totalQty > 1) "s" else ""} • ₹${subTotal.toInt()}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "From ${cartRestaurant?.name}",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "View Cart",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = "Run to cart",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Sizzling Active Live Tracking Floating Banner
                if (liveOrder != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NomatoGold)
                            .clickable { onTrackingSelected() }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("floating_tracking_bar"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Active signal",
                                tint = Slate900,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Order Active! ETA: ${liveOrder?.etaMinutes} mins (${liveOrder?.status?.name?.replace("_", " ")})",
                                color = Slate900,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "Track Driver →",
                            color = Slate900,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Navigation Bar
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("Deliver") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NomatoRed,
                            unselectedIconColor = Slate500,
                            selectedTextColor = NomatoRed,
                            unselectedTextColor = Slate500,
                            indicatorColor = NomatoRedLight
                        ),
                        icon = { Icon(Icons.Default.Home, contentDescription = "Deliver Tab") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("Faves") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NomatoRed,
                            unselectedIconColor = Slate500,
                            selectedTextColor = NomatoRed,
                            unselectedTextColor = Slate500,
                            indicatorColor = NomatoRedLight
                        ),
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites Tab") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("History") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NomatoRed,
                            unselectedIconColor = Slate500,
                            selectedTextColor = NomatoRed,
                            unselectedTextColor = Slate500,
                            indicatorColor = NomatoRedLight
                        ),
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "History Tab") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NomatoRed,
                            unselectedIconColor = Slate500,
                            selectedTextColor = NomatoRed,
                            unselectedTextColor = Slate500,
                            indicatorColor = NomatoRedLight
                        ),
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile Tab") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DeliverTab(viewModel, onRestaurantSelected)
                1 -> FavoritesTab(viewModel, onRestaurantSelected)
                2 -> HistoryTab(viewModel)
                3 -> ProfileTab(viewModel)
            }
        }
    }
}

// --- TAB 1: DELIVER TAB ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliverTab(
    viewModel: NomatoViewModel,
    onRestaurantSelected: () -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val filteredList by viewModel.filteredRestaurants.collectAsState()
    val activeCuisine by viewModel.selectedCuisine.collectAsState()
    val isVegOnly by viewModel.vegFilter.collectAsState()
    val activeSort by viewModel.sortType.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var showAddressSelector by remember { mutableStateOf(false) }
    var showFilterSelector by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    val unreadNotifications = notifications.count { !it.isRead }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Top Location and Notification Header Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showAddressSelector = true }
                        .testTag("location_trigger"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pin icon",
                        tint = NomatoRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedAddress?.type ?: "Select Location",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Slate900
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "dropdown arrow",
                                tint = Slate900,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = selectedAddress?.area ?: "Click to add target address",
                            fontSize = 11.sp,
                            color = Slate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Notification Bell icon
                IconButton(
                    onClick = {
                        showNotificationDialog = true
                        viewModel.markNotificationsAsRead()
                    }
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifications > 0) {
                                Badge(containerColor = NomatoRed) {
                                    Text(text = "$unreadNotifications", color = Color.White)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Slate900
                        )
                    }
                }
            }
        }

        // 2. Search Text Input
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_restaurant_input"),
                    placeholder = { Text("Search for dishes, biryani, burgers...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "searching",
                            tint = Slate500
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showFilterSelector = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filters",
                                tint = NomatoRed
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Slate100.copy(alpha = 0.5f),
                        unfocusedContainerColor = Slate100.copy(alpha = 0.5f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }
        }

        // 3. Horizontal Cuisines quick carousel
        item {
            val cuisinesList = listOf(
                CuisineData("Biryani", 0xFFF39C12, "🍲"),
                CuisineData("Curries", 0xFFE67E22, "🥘"),
                CuisineData("Breakfast", 0xFF2ECC71, "🥞"),
                CuisineData("Desserts", 0xFF9B59B6, "🍨"),
                CuisineData("Breads", 0xFFE74C3C, "🫓")
            )
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "What's on your mind?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cuisinesList) { item ->
                        val isSelected = activeCuisine == item.name
                        Card(
                            onClick = { viewModel.selectCuisine(item.name) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NomatoRedLight else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier
                                .width(94.dp)
                                .height(88.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = item.emoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NomatoRed else Slate900
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Large Promo Banner Carousel
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NomatoRed)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Drawing decorative glowing spheres on banner
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.08f),
                                radius = size.width / 3f,
                                center = Offset(size.width * 0.9f, size.height * 0.5f)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.65f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "MONSOON RUSH",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = NomatoGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Sizzling 50% OFF",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Traditional tiffins & warm biryanis delivered within dry bags.",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Active quick filter badges
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Veg tag
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isVegOnly) NomatoVegGreen.copy(alpha = 0.15f) else Color.White,
                            shape = CircleShape
                        )
                        .border(
                            1.dp,
                            if (isVegOnly) NomatoVegGreen else SoftGrey,
                            CircleShape
                        )
                        .clickable { viewModel.toggleVegFilter() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .border(1.dp, NomatoVegGreen, RoundedCornerShape(2.dp))
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(NomatoVegGreen, CircleShape))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pure Veg",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isVegOnly) NomatoVegGreen else Slate900
                        )
                    }
                }

                // Cuisine clean indicator if selected
                if (activeCuisine != null) {
                    Box(
                        modifier = Modifier
                            .background(NomatoRedLight, CircleShape)
                            .border(1.dp, NomatoRed, CircleShape)
                            .clickable { viewModel.selectCuisine(null) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Cuisine: $activeCuisine ✕",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NomatoRed
                        )
                    }
                }
            }
        }

        // 6. Section Header
        item {
            Text(
                text = "Premium Restaurants Near You",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 10.dp)
            )
        }

        // Empty State Check
        if (filteredList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🥘", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Restaurants Match Filters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Try clearing your cuisine query or veg filters.",
                        fontSize = 13.sp,
                        color = Slate500,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 7. Restaurants Vertical list
        items(filteredList) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                isFavorite = viewModel.favorites.collectAsState().value.contains(restaurant.id),
                onFavoriteToggle = { viewModel.toggleFavorite(restaurant.id) },
                onClick = {
                    viewModel.selectRestaurant(restaurant)
                    onRestaurantSelected()
                }
            )
        }
    }

    // --- DIALOGS FOR DELIVER TAB ---

    // 1. Saved Addresses bottom sheet/selector
    if (showAddressSelector) {
        Dialog(onDismissRequest = { showAddressSelector = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Change Delivery Address",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    savedAddresses.forEach { address ->
                        val isSelected = selectedAddress?.id == address.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (isSelected) NomatoRedLight else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.selectSavedAddress(address.id)
                                    showAddressSelector = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Pin",
                                tint = if (isSelected) NomatoRed else Slate500
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = address.type,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = "${address.houseNumber}, ${address.area}",
                                    fontSize = 12.sp,
                                    color = Slate500
                                )
                            }
                        }
                        Divider(color = SoftGrey, modifier = Modifier.padding(vertical = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Quick add address triggers
                    var customHouse by remember { mutableStateOf("") }
                    var customArea by remember { mutableStateOf("") }
                    var customType by remember { mutableStateOf("Home") }

                    Text(text = "Quick Add Address", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = customHouse,
                        onValueChange = { customHouse = it },
                        placeholder = { Text("House/Flat No.") },
                        colors = TextFieldDefaults.colors(focusedIndicatorColor = NomatoRed),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = customArea,
                        onValueChange = { customArea = it },
                        placeholder = { Text("Street Area, Sector") },
                        colors = TextFieldDefaults.colors(focusedIndicatorColor = NomatoRed),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Home", "Work", "Other").forEach { type ->
                            OutlinedButton(
                                onClick = { customType = type },
                                border = OutlinedButtonDefaultsBorder(customType == type),
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(type, fontSize = 12.sp, color = if (customType == type) NomatoRed else Slate500)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (customHouse.isNotEmpty() && customArea.isNotEmpty()) {
                                viewModel.addSavedAddress(customType, customHouse, customArea)
                                customHouse = ""
                                customArea = ""
                                showAddressSelector = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Address")
                    }
                }
            }
        }
    }

    // 2. Filters details dialog
    if (showFilterSelector) {
        Dialog(onDismissRequest = { showFilterSelector = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Sort Restaurants By", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    val sortingOptions = listOf(
                        "RATING" to "Highest Rating ★",
                        "ETA" to "Fastest Delivery Time ⏱",
                        "PRICE_LOW" to "Cost: Low to High ₹",
                        "PRICE_HIGH" to "Cost: High to Low ₹"
                    )

                    sortingOptions.forEach { (key, label) ->
                        val isSelected = activeSort == key
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) NomatoRedLight else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.setSortType(key)
                                    showFilterSelector = false
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) NomatoRed else Slate900,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    // 3. Simple Dynamic trigger alert panel
    if (showNotificationDialog) {
        Dialog(onDismissRequest = { showNotificationDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Nomato Alerts", fontWeight = FontWeight.Black, fontSize = 18.sp, color = NomatoRed)
                        TextButton(onClick = { viewModel.simulateNotificationTrigger() }) {
                            Text("Mock Receipt", fontSize = 11.sp, color = NomatoRed)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    notifications.forEach { item ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                            Text(text = item.body, fontSize = 11.sp, color = Slate500)
                            Text(text = item.time, fontSize = 9.sp, color = Slate500.copy(alpha = 0.6f))
                            Divider(color = SoftGrey, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                    Button(
                        onClick = { showNotificationDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    ) {
                        Text("Dismiss Alerts")
                    }
                }
            }
        }
    }
}

@Composable
fun OutlinedButtonDefaultsBorder(isSelected: Boolean) = androidx.compose.foundation.BorderStroke(
    width = 1.dp,
    color = if (isSelected) NomatoRed else SoftGrey
)

// --- CARD RESTAURANT RENDER COMPOSABLE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("restaurant_card_${restaurant.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Hero abstract back drawing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(restaurant.gradientColors[0].toInt()),
                                Color(restaurant.gradientColors[1].toInt())
                            )
                        )
                    )
            ) {
                // Actual Image
                if (restaurant.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = restaurant.imageUrl,
                        contentDescription = "Restaurant Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Visual background icon overlay
                    Icon(
                        imageVector = Icons.Outlined.Dining,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(90.dp)
                    )
                }

                // Heart favorite tag icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .clickable { onFavoriteToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite toggle",
                        tint = if (isFavorite) NomatoRed else Slate500,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Veg flag indicator
                if (restaurant.isVegOnly) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .border(1.dp, NomatoVegGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("PURE VEG", color = NomatoVegGreen, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    }
                }

                // Top Discount bubble
                if (restaurant.offers.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(NomatoRed, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = restaurant.offers.first(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp
                        )
                    }
                }

                // Removed Icon overlay as it was integrated above
            }

            // Description info section
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Rating Star Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(NomatoVegGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${restaurant.rating}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = restaurant.cuisines.joinToString(" • "),
                    fontSize = 13.sp,
                    color = Slate500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = SoftGrey)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⏱ ${restaurant.deliveryTimeMins} mins • ${restaurant.distanceKm} km",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                    Text(
                        text = "₹${restaurant.costForTwo.toInt()} for two",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate700
                    )
                }
            }
        }
    }
}

// --- TAB 2: FAVORITES TAB ---
@Composable
fun FavoritesTab(
    viewModel: NomatoViewModel,
    onRestaurantSelected: () -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val allRestaurants = NomatoRepository.restaurants
    val faveRestaurants = allRestaurants.filter { favorites.contains(it.id) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Your Curated Favorites",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Slate900,
            modifier = Modifier.padding(16.dp)
        )

        if (faveRestaurants.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "❤️", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "No Favorites Yet", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Slate900)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap heart tags on restaurants map or cards to save culinary jewels here.",
                    fontSize = 13.sp,
                    color = Slate500,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 20.dp)) {
                items(faveRestaurants) { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        isFavorite = true,
                        onFavoriteToggle = { viewModel.toggleFavorite(restaurant.id) },
                        onClick = {
                            viewModel.selectRestaurant(restaurant)
                            onRestaurantSelected()
                        }
                    )
                }
            }
        }
    }
}

// --- TAB 3: ORDER HISTORY TAB ---
@Composable
fun HistoryTab(
    viewModel: NomatoViewModel
) {
    val pastOrders by viewModel.pastOrders.collectAsState()
    val context = LocalContext.current

    var selectedOrderForRating by remember { mutableStateOf<PastOrder?>(null) }
    var ratingInput by remember { mutableIntStateOf(5) }
    var reviewTextInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Your Order History",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Slate900,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            items(pastOrders) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(text = order.restaurant.name, fontWeight = FontWeight.Bold, color = Slate900)
                                Text(text = order.restaurant.locationName, fontSize = 12.sp, color = Slate500)
                            }
                            // Date / Price
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "₹${order.totalPrice.toInt()}", fontWeight = FontWeight.Bold, color = NomatoRed)
                                Text(text = order.dateString, fontSize = 11.sp, color = Slate500)
                            }
                        }

                        Divider(color = SoftGrey, modifier = Modifier.padding(vertical = 10.dp))

                        // Items list summary
                        order.items.forEach { item ->
                            Text(
                                text = "${item.quantity} x ${item.menuItem.name}",
                                fontSize = 12.sp,
                                color = Slate700,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (order.userRating != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Your rating: ", fontSize = 12.sp, color = Slate700)
                                repeat(order.userRating) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = NomatoGold, modifier = Modifier.size(14.dp))
                                }
                                if (order.userReview?.isNotEmpty() == true) {
                                    Text(
                                        text = " - \"${order.userReview}\"",
                                        fontSize = 11.sp,
                                        color = Slate500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    selectedOrderForRating = order
                                    ratingInput = 5
                                    reviewTextInput = ""
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Write Review & Rate ★", fontSize = 11.sp, color = NomatoRed)
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive ratings dialogues
    if (selectedOrderForRating != null) {
        Dialog(onDismissRequest = { selectedOrderForRating = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Rate meals from ${selectedOrderForRating?.restaurant?.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Star clicks row
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= ratingInput) NomatoGold else Slate500.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { ratingInput = star }
                            )
                        }
                    }
                    
                    Text(text = "Write your review", fontSize = 12.sp, color = Slate700, modifier = Modifier.padding(top = 8.dp))
                    TextField(
                        value = reviewTextInput,
                        onValueChange = { reviewTextInput = it },
                        placeholder = { Text("How was the cooking, hot packaging, spice?") },
                        colors = TextFieldDefaults.colors(focusedIndicatorColor = NomatoRed),
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            selectedOrderForRating?.orderId?.let { id ->
                                viewModel.submitRating(id, ratingInput, reviewTextInput)
                                Toast.makeText(context, "Thank you for the review!", Toast.LENGTH_SHORT).show()
                                selectedOrderForRating = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Rating")
                    }
                }
            }
        }
    }
}

// --- TAB 4: PROFILE TAB ---
@Composable
fun ProfileTab(
    viewModel: NomatoViewModel
) {
    val phone by viewModel.phoneNumber.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val context = LocalContext.current

    var showQuickAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Large user Avatar Placeholder drawing with Canvas
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(NomatoRedLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = userName.firstOrNull()?.toString() ?: "U", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = NomatoRed)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = userName, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Slate900)
        Text(text = "+91 $phone", fontSize = 14.sp, color = Slate500)

        Spacer(modifier = Modifier.height(24.dp))

        // Profile options list
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "YOUR ACCOUNT OPERATIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate500, modifier = Modifier.padding(vertical = 6.dp))

            // Info rows
            ProfileMenuRow(
                title = "Saved Locations",
                subtitle = "${savedAddresses.size} address entries configured",
                icon = Icons.Default.LocationOn,
                onClick = { Toast.makeText(context, "Location parameters managed on Deliver home page.", Toast.LENGTH_LONG).show() }
            )

            ProfileMenuRow(
                title = "Help & Live Chat Support",
                subtitle = "Query refund, cancel and delivery status",
                icon = Icons.Outlined.HelpOutline,
                onClick = { showQuickAboutDialog = true }
            )

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = SoftGrey)
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.PowerSettingsNew, contentDescription = "power", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Secure Log Out")
                }
            }
        }
    }

    if (showQuickAboutDialog) {
        Dialog(onDismissRequest = { showQuickAboutDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Nomato Support Hub 💬", fontWeight = FontWeight.Black, fontSize = 18.sp, color = NomatoRed)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Need help with an order? Standard Indian operations are backed by active live assistants. Since this is a prototype, feel free to review all features, place simulated orders, and enjoy tracking drivers movement instantly. For comments, ping our build support lines.",
                        fontSize = 13.sp,
                        color = Slate700,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showQuickAboutDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Connect with Assistant")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Slate100, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Slate700)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                Text(text = subtitle, fontSize = 12.sp, color = Slate500)
            }
        }
    }
}

data class CuisineData(val name: String, val colorHex: Long, val emoji: String)
