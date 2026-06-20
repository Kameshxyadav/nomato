package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.NomatoViewModel
import com.example.ui.theme.NomatoRed
import com.example.ui.theme.NomatoRedLight
import com.example.ui.theme.NomatoVegGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SoftGrey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(
    viewModel: NomatoViewModel,
    onBack: () -> Unit,
    onOrderPlacedSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val restaurant by viewModel.cartRestaurant.collectAsState()
    val selectedCoupon by viewModel.selectedCoupon.collectAsState()
    val tipAmount by viewModel.tipAmount.collectAsState()
    
    val context = LocalContext.current
    var selectedPaymentMethod by remember { mutableStateOf("UPI_GPAY") }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var showAddressSelectionDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (cartItems.isEmpty() || restaurant == null) {
        onBack()
        return
    }

    val subTotal = cartItems.sumOf { it.totalPrice }
    val deliveryFee = restaurant!!.distanceKm * 10
    val packagingCharges = 15.0
    val taxesAndGst = subTotal * 0.05
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
            
            // Top App Bar
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
                Text(text = "Review & Pay Securely", fontSize = 17.sp, fontWeight = FontWeight.Black, color = Slate900)
            }

            // Scrollable Settings List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                
                // 1. Delivery Address Card Segment
                Text(text = "DELIVERING MEALS TO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate500, modifier = Modifier.padding(bottom = 6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().clickable { showAddressSelectionDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Pin", tint = NomatoRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${selectedAddress?.type} Address", fontWeight = FontWeight.Bold, color = Slate900)
                            Text(text = "${selectedAddress?.houseNumber}, ${selectedAddress?.area}", fontSize = 12.sp, color = Slate500)
                            Text(text = "${selectedAddress?.city}", fontSize = 11.sp, color = Slate500)
                        }
                        Text(text = "Change", color = NomatoRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Select Payment Methods Card
                Text(text = "SELECT INDIAN SECURE GATEWAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate500, modifier = Modifier.padding(bottom = 6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        
                        // Payment GPay
                        PaymentMethodRow(
                            id = "UPI_GPAY",
                            label = "Google Pay (Secure UPI)",
                            description = "Link banking parameters instantly",
                            icon = Icons.Default.Smartphone,
                            selected = selectedPaymentMethod == "UPI_GPAY",
                            onSelect = { selectedPaymentMethod = "UPI_GPAY" }
                        )
                        
                        Divider(color = Slate100, modifier = Modifier.padding(vertical = 4.dp))

                        // Payment PhonePe
                        PaymentMethodRow(
                            id = "UPI_PHONEPE",
                            label = "PhonePe (BHIM API)",
                            description = "Settle using unified payments interface",
                            icon = Icons.Default.Smartphone,
                            selected = selectedPaymentMethod == "UPI_PHONEPE",
                            onSelect = { selectedPaymentMethod = "UPI_PHONEPE" }
                        )

                        Divider(color = Slate100, modifier = Modifier.padding(vertical = 4.dp))

                        // Payment Card
                        PaymentMethodRow(
                            id = "CARD",
                            label = "Credit or Debit Card",
                            description = "Visa, Mastercard, RuPay processed",
                            icon = Icons.Default.CreditCard,
                            selected = selectedPaymentMethod == "CARD",
                            onSelect = { selectedPaymentMethod = "CARD" }
                        )

                        Divider(color = Slate100, modifier = Modifier.padding(vertical = 4.dp))

                        // Cash on Delivery
                        PaymentMethodRow(
                            id = "COD",
                            label = "Cash on Delivery",
                            description = "Settle order locally on arrival",
                            icon = Icons.Default.Payments,
                            selected = selectedPaymentMethod == "COD",
                            onSelect = { selectedPaymentMethod = "COD" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Security checklist indicators
                Card(
                    colors = CardDefaults.cardColors(containerColor = NomatoVegGreen.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "safe", tint = NomatoVegGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "SSL Encrypted BHIM API Sandbox Checkout Gateway", fontSize = 11.sp, color = Slate700, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Fixed bottom payment bar
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "GRAND TOTAL PAYABLE", fontSize = 11.sp, color = Slate500)
                            Text(text = "₹${grandTotal.toInt()}", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Slate900)
                        }

                        Button(
                            onClick = {
                                isProcessingPayment = true
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(1800) // Beautiful transaction security lock screens delay simulation
                                    isProcessingPayment = false
                                    viewModel.placeOrder(grandTotal)
                                    Toast.makeText(context, "Order Placed Successfully! Best of feasts 🎉", Toast.LENGTH_LONG).show()
                                    onOrderPlacedSuccess()
                                }
                            },
                            enabled = !isProcessingPayment && selectedAddress != null,
                            colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                            modifier = Modifier
                                .height(52.dp)
                                .width(180.dp)
                                .testTag("pay_total_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isProcessingPayment) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(text = "Confirm ₹${grandTotal.toInt()}", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Address selector popup dialog ---
    if (showAddressSelectionDialog) {
        Dialog(onDismissRequest = { showAddressSelectionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Deliver To Which Address?", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    savedAddresses.forEach { address ->
                        val isSelected = selectedAddress?.id == address.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) NomatoRedLight else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.selectSavedAddress(address.id)
                                    showAddressSelectionDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Pin", tint = if (isSelected) NomatoRed else Slate500)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = address.type, fontWeight = FontWeight.Bold, color = Slate900)
                                Text(text = "${address.houseNumber}, ${address.area}", fontSize = 11.sp, color = Slate500)
                            }
                        }
                        Divider(color = Slate100, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodRow(
    id: String,
    label: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Slate100, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = id, tint = Slate700)
        }
        
        Spacer(modifier = Modifier.width(14.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
            Text(text = description, fontSize = 11.sp, color = Slate500)
        }

        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = NomatoRed)
        )
    }
}
