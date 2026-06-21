package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CustomisationOption
import com.example.data.MenuItem
import com.example.data.NomatoRepository
import com.example.ui.NomatoViewModel
import com.example.ui.theme.NomatoGold
import com.example.ui.theme.NomatoRed
import com.example.ui.theme.NomatoRedLight
import com.example.ui.theme.NomatoVegGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SoftGrey

@Composable
fun RestaurantDetailScreen(
    viewModel: NomatoViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val restaurant by viewModel.selectedRestaurant.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val context = LocalContext.current

    val r = restaurant ?: return // Safety exit

    val isFavorite = favorites.contains(r.id)
    val menuList = NomatoRepository.menuItems[r.id] ?: emptyList()

    // Customize dialog triggers state
    var selectedItemForCustomise by remember { mutableStateOf<MenuItem?>(null) }
    
    // Group menu list by categories (e.g. Biryani, Curries)
    val groupedMenu = menuList.groupBy { it.category }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color(0xFFFAFAFC))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 72.dp) // Spacing for floating checkout buttons if any
        ) {
            // 1. Hero banner drawing
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(r.gradientColors[0].toInt()), Color(r.gradientColors[1].toInt()))
                            )
                        )
                ) {
                    // Back button overlay
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate900)
                    }

                    // Favorite and Share button overlays
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleFavorite(r.id) },
                            modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Fav",
                                tint = NomatoRed
                            )
                        }
                    }

                    // Centered decorative symbol drawing
                    Icon(
                        imageVector = Icons.Default.Dining,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(100.dp),
                        tint = Color.White.copy(alpha = 0.15f)
                    )
                }
            }

            // 2. Info Cards Floating overlap overlay
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = (-24).dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (r.isVegOnly) {
                                Box(
                                    modifier = Modifier
                                        .border(1.dp, NomatoVegGreen, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("VEG ONLY", color = NomatoVegGreen, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = "OPEN NOW",
                                color = NomatoVegGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = r.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Slate900
                        )

                        Text(
                            text = r.cuisines.joinToString(" • "),
                            fontSize = 13.sp,
                            color = Slate500
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = SoftGrey)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ratings Columns
                            Column(horizontalAlignment = Alignment.Start) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${r.rating}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Slate900)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = NomatoGold, modifier = Modifier.size(16.dp))
                                }
                                Text(text = r.ratingCount, fontSize = 10.sp, color = Slate500)
                            }
                            
                            // Delivery details
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${r.deliveryTimeMins} MINS", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Slate900)
                                Text(text = "Fast Delivery ⏱", fontSize = 10.sp, color = Slate500)
                            }

                            // Price point details
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "₹${r.costForTwo.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Slate900)
                                Text(text = "Cost for two", fontSize = 10.sp, color = Slate500)
                            }
                        }
                    }
                }
            }

            // 3. Offers listed carousel
            if (r.offers.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "Coupons and active offers",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            r.offers.forEach { offer ->
                                Box(
                                    modifier = Modifier
                                        .background(NomatoRedLight, RoundedCornerShape(8.dp))
                                        .border(0.5.dp, NomatoRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = offer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NomatoRed
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            }

            // 4. Menu lists sections grouped by Category
            groupedMenu.forEach { (categoryName, dishes) ->
                item {
                    Text(
                        text = categoryName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Slate900,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
                    )
                }

                items(dishes) { item ->
                    val cartMatch = cartItems.filter { it.menuItem.id == item.id }
                    val itemQty = cartMatch.sumOf { it.quantity }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Text Block
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Veg Indicator box drawing
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .border(
                                                1.dp,
                                                if (item.isVeg) NomatoVegGreen else Color(0xFFC0392B),
                                                RoundedCornerShape(3.dp)
                                            )
                                            .padding(3.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    color = if (item.isVeg) NomatoVegGreen else Color(0xFFC0392B),
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (item.isBestseller) {
                                        Box(
                                            modifier = Modifier
                                                .background(NomatoGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("★ BESTSELLER", color = NomatoGold, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                                Text(text = "₹${item.price.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Slate900)
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.description,
                                    fontSize = 11.sp,
                                    color = Slate500,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Right custom food placeholder / Add buttons
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Abstract meal square indicator in lieu of generated photo
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(item.gradientColors[0].toInt()), Color(item.gradientColors[1].toInt()))
                                            ),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (item.isVeg) "🥬" else "🍗",
                                        fontSize = 24.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Interactive cart ADD/Remover buttons block
                                if (itemQty > 0) {
                                    Row(
                                        modifier = Modifier
                                            .width(76.dp)
                                            .height(30.dp)
                                            .background(Color.White, RoundedCornerShape(8.dp))
                                            .border(1.dp, NomatoRed, RoundedCornerShape(8.dp)),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.removeFromCart(item.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Rem", tint = NomatoRed, modifier = Modifier.size(12.dp))
                                        }
                                        Text(text = "$itemQty", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NomatoRed)
                                        IconButton(
                                            onClick = { viewModel.addToCart(item) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = NomatoRed, modifier = Modifier.size(12.dp))
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            if (item.customisations.isNotEmpty()) {
                                                selectedItemForCustomise = item
                                            } else {
                                                viewModel.addToCart(item)
                                                Toast.makeText(context, "${item.name} added!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        modifier = Modifier
                                            .width(76.dp)
                                            .height(30.dp)
                                            .border(1.dp, NomatoRed, RoundedCornerShape(8.dp))
                                            .testTag("add_item_${item.id}"),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = "ADD",
                                            color = NomatoRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    if (item.customisations.isNotEmpty()) {
                                        Text("customisable", fontSize = 8.sp, color = Slate500)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- CUSTOMISATIONS DIALOG MODAL ---
    if (selectedItemForCustomise != null) {
        val dish = selectedItemForCustomise!!
        
        // Local remember store of customization option selection
        val selectedOptionsMap = remember { mutableStateMapOf<String, Boolean>() }

        Dialog(onDismissRequest = { selectedItemForCustomise = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Customise Speciality", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Slate900)
                    Text(text = dish.name, fontSize = 14.sp, color = NomatoRed, fontWeight = FontWeight.Bold)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = SoftGrey)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Render groups
                    dish.customisations.forEach { group ->
                        Text(text = group.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                        Text(
                            text = if (group.maxSelect == 1) "Select any 1 option" else "Select up to ${group.maxSelect} options",
                            fontSize = 10.sp,
                            color = Slate500
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        group.options.forEach { option ->
                            val isSelected = selectedOptionsMap[option.id] ?: false
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (group.maxSelect == 1) {
                                            // Reset other values in same group
                                            group.options.forEach { optionsIter ->
                                                selectedOptionsMap[optionsIter.id] = false
                                            }
                                            selectedOptionsMap[option.id] = true
                                        } else {
                                            val currentCount = group.options.count { selectedOptionsMap[it.id] == true }
                                            if (isSelected) {
                                                selectedOptionsMap[option.id] = false
                                            } else if (currentCount < group.maxSelect) {
                                                selectedOptionsMap[option.id] = true
                                            } else {
                                                Toast.makeText(context, "Max customization choices matching reached!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (group.maxSelect == 1) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = {
                                                group.options.forEach { o -> selectedOptionsMap[o.id] = false }
                                                selectedOptionsMap[option.id] = true
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = NomatoRed)
                                        )
                                    } else {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { checked ->
                                                val currentCount = group.options.count { selectedOptionsMap[it.id] == true }
                                                if (!checked) {
                                                    selectedOptionsMap[option.id] = false
                                                } else if (currentCount < group.maxSelect) {
                                                    selectedOptionsMap[option.id] = true
                                                }
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = NomatoRed)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = option.name, fontSize = 13.sp, color = Slate700)
                                }
                                if (option.price > 0) {
                                    Text(text = "+ ₹${option.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = SoftGrey)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val activeList = dish.customisations.flatMap { g ->
                                g.options.filter { selectedOptionsMap[it.id] == true }
                            }
                            viewModel.addToCart(dish, activeList)
                            selectedItemForCustomise = null
                            Toast.makeText(context, "Added Customized Dish!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add Customised Recipe to Cart")
                    }
                }
            }
        }
    }
}
