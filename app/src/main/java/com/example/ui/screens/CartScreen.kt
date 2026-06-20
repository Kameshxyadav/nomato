package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CartItem
import com.example.data.Coupon
import com.example.data.NomatoRepository
import com.example.ui.NomatoViewModel
import com.example.ui.theme.NomatoGold
import com.example.ui.theme.NomatoRed
import com.example.ui.theme.NomatoRedLight
import com.example.ui.theme.NomatoVegGreen
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SoftGrey

@Composable
fun CartScreen(
    viewModel: NomatoViewModel,
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val r by viewModel.cartRestaurant.collectAsState()
    val selectedCoupon by viewModel.selectedCoupon.collectAsState()
    val tipAmount by viewModel.tipAmount.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val context = LocalContext.current

    var showCouponModal by remember { mutableStateOf(false) }
    var couponInput by remember { mutableStateOf("") }

    if (cartItems.isEmpty() || r == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = NomatoRed.copy(alpha = 0.3f), modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Your Cart is Empty", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Slate900)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Add some delicious food from local premium hubs near you to fill this bag.", color = Slate500, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { onBack() },
                colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Browse Restaurants")
            }
        }
        return
    }

    val restaurant = r!!
    val subTotal = cartItems.sumOf { it.totalPrice }

    // Bill Calculations
    val deliveryFee = restaurant.distanceKm * 10.0 // Rs 10 per km
    val packagingCharges = 15.0
    val taxesAndGst = subTotal * 0.05 // 5% GST combined

    var couponDiscount = 0.0
    selectedCoupon?.let { coupon ->
        if (subTotal >= coupon.minOrderValue) {
            couponDiscount = if (coupon.discountPercent > 0) {
                val calDiscount = subTotal * (coupon.discountPercent / 100.0)
                minOf(calDiscount, coupon.maxDiscount)
            } else {
                coupon.flatDiscount
            }
        }
    }

    val grandTotal = maxOf(0.0, subTotal + deliveryFee + packagingCharges + taxesAndGst + tipAmount - couponDiscount)

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color(0xFFFAFAFC))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // 1. Top Bar Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate900)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Your Order Bag", fontSize = 17.sp, fontWeight = FontWeight.Black, color = Slate900)
                    Text(text = "From ${restaurant.name}", fontSize = 11.sp, color = Slate500)
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { viewModel.clearCart() }) {
                    Text("Clear Cart ✕", color = NomatoRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                // 2. Items List Row summary
                items(cartItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .border(
                                        1.dp,
                                        if (item.menuItem.isVeg) NomatoVegGreen else Color(0xFFC0392B),
                                        RoundedCornerShape(2.dp)
                                    )
                                    .padding(2.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize().background(if (item.menuItem.isVeg) NomatoVegGreen else Color(0xFFC0392B), CircleShape))
                            }
                            
                            Spacer(modifier = Modifier.width(10.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.menuItem.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                                if (item.selectedCustomisations.isNotEmpty()) {
                                    Text(
                                        text = "Customized: ${item.selectedCustomisations.joinToString { it.name }}",
                                        fontSize = 10.sp,
                                        color = Slate500
                                    )
                                }
                                Text(text = "₹${(item.totalUnitPrice).toInt()}", fontSize = 12.sp, color = Slate500)
                            }

                            // Add & Remove indicators
                            Row(
                                modifier = Modifier
                                    .width(76.dp)
                                    .height(30.dp)
                                    .border(1.dp, NomatoRed, RoundedCornerShape(8.dp)),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { viewModel.removeFromCart(item.menuItem.id, item.selectedCustomisations) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Rem", tint = NomatoRed, modifier = Modifier.size(12.dp))
                                }
                                Text(text = "${item.quantity}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NomatoRed)
                                IconButton(
                                    onClick = { viewModel.addToCart(item.menuItem, item.selectedCustomisations) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = NomatoRed, modifier = Modifier.size(12.dp))
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = "₹${item.totalPrice.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate900,
                                fontSize = 14.sp,
                                modifier = Modifier.width(50.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }

                // 3. Coupons Section row click
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        onClick = { showCouponModal = true },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().testTag("apply_coupon_trigger")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = "promo", tint = NomatoRed)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedCoupon != null) "Coupon API applied! (${selectedCoupon?.code})" else "Use Promo Coupons",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = if (selectedCoupon != null) "Saves ₹${couponDiscount.toInt()}" else "FIRST50, NOMATO20, SAVE100",
                                    fontSize = 11.sp,
                                    color = if (selectedCoupon != null) NomatoVegGreen else Slate500
                                )
                            }
                            Text(
                                text = if (selectedCoupon != null) "Change ✕" else "Select Code →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NomatoRed
                            )
                        }
                    }
                }

                // 4. Delivery Driver Tip Option
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Welcome Delivery Champions Tip", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                            Text(text = "Clean EV drivers ensure hot fast delivery in monsoon storms. 100% of tips are transferred.", fontSize = 11.sp, color = Slate500)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf(20.0, 30.0, 50.0).forEach { amount ->
                                    val isSelected = tipAmount == amount
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 4.dp)
                                            .background(
                                                color = if (isSelected) NomatoRedLight else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) NomatoRed else SoftGrey,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.setTipAmount(amount) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "₹${amount.toInt()}",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) NomatoRed else Slate700,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                        .background(
                                            color = if (!listOf(20.0, 30.0, 50.0).contains(tipAmount)) NomatoRedLight else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (!listOf(20.0, 30.0, 50.0).contains(tipAmount)) NomatoRed else SoftGrey,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.setTipAmount(0.0) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No Tip",
                                        fontWeight = FontWeight.Bold,
                                        color = if (tipAmount == 0.0) NomatoRed else Slate700,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. Total Price Detailed items breaks
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Gourmet Bill Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            BillRow(label = "Item Subtotal", value = subTotal)
                            BillRow(label = "Restaurant Packaging Charges", value = packagingCharges)
                            BillRow(label = "Monsoon Electric Delivery Partner Fee", value = deliveryFee)
                            BillRow(label = "Country CGST / SGST Taxes (5%)", value = taxesAndGst)
                            
                            if (selectedCoupon != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Coupon ${selectedCoupon?.code} Discount", color = NomatoVegGreen, fontSize = 13.sp)
                                    Text(text = "- ₹${couponDiscount.toInt()}", color = NomatoVegGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (tipAmount > 0) {
                                BillRow(label = "Delivery Champion Tip Contribution", value = tipAmount)
                            }

                            Divider(color = SoftGrey, modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Grand Order Total", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Slate900)
                                Text(text = "₹${grandTotal.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = NomatoRed)
                            }
                        }
                    }
                }
            }

            // 6. Bottom Checkout trigger buttons row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "PAY USING SAVED SETS", fontSize = 11.sp, color = Slate500)
                    Text(text = "₹${grandTotal.toInt()}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Slate900)
                }

                Button(
                    onClick = { onCheckout() },
                    colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .width(180.dp)
                        .testTag("checkout_payment_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Pay Securely", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }

    // --- COUPON SELECTOR MODAL DIALOGS ---
    if (showCouponModal) {
        Dialog(onDismissRequest = { showCouponModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Select / Enter Promo Coupon", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Slate900)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Manual TextField coupon enter
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = couponInput,
                            onValueChange = { couponInput = it.uppercase() },
                            placeholder = { Text("Code: e.g. FIRST50") },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NomatoRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val found = NomatoRepository.availableCoupons.find { it.code.equals(couponInput, ignoreCase = true) }
                                if (found != null) {
                                    if (subTotal >= found.minOrderValue) {
                                        viewModel.applyCoupon(found)
                                        showCouponModal = false
                                        Toast.makeText(context, "Applied ${found.code} code!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Spend ₹${found.minOrderValue.toInt()} more first!", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Invalid active code entered!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Apply", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(color = SoftGrey)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "AVAILABLE OFFERS LIST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate500)
                    Spacer(modifier = Modifier.height(8.dp))

                    NomatoRepository.availableCoupons.forEach { coup ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (subTotal >= coup.minOrderValue) {
                                        viewModel.applyCoupon(coup)
                                        showCouponModal = false
                                        Toast.makeText(context, "Applied ${coup.code} code!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Add ₹${(coup.minOrderValue - subTotal).toInt()} more value first!", Toast.LENGTH_LONG).show()
                                    }
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .background(NomatoRedLight, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(text = coup.code, fontWeight = FontWeight.Bold, color = NomatoRed, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = coup.description, fontSize = 12.sp, color = Slate700)
                                if (coup.minOrderValue > 0) {
                                    Text(text = "Min Spend: ₹${coup.minOrderValue.toInt()}", fontSize = 10.sp, color = Slate500)
                                }
                            }
                            Icon(imageVector = Icons.Default.LocalOffer, contentDescription = "coupon", tint = NomatoRed)
                        }
                        Divider(color = SoftGrey)
                    }
                }
            }
        }
    }
}

@Composable
fun BillRow(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Slate700, fontSize = 13.sp)
        Text(text = "₹${value.toInt()}", color = Slate900, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
