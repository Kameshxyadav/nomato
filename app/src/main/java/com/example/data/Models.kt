package com.example.data

import android.os.Parcelable
import androidx.compose.ui.graphics.Color

data class Restaurant(
    val id: String,
    val name: String,
    val cuisines: List<String>,
    val rating: Double,
    val ratingCount: String,
    val deliveryTimeMins: Int,
    val costForTwo: Double,
    val offers: List<String>,
    val isVegOnly: Boolean,
    val imageUrl: String,
    val locationName: String,
    val distanceKm: Double,
    val gradientColors: List<Long> = listOf(0xFFE21A43, 0xFFFFECEF)
)

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val isVeg: Boolean,
    val category: String,
    val isBestseller: Boolean = false,
    val rating: Double? = null,
    val customisations: List<CustomisationGroup> = emptyList(),
    val gradientColors: List<Long> = listOf(0xFFFEAC5E, 0xFFC779D0)
)

data class CustomisationGroup(
    val name: String,
    val minSelect: Int,
    val maxSelect: Int,
    val options: List<CustomisationOption>
)

data class CustomisationOption(
    val id: String,
    val name: String,
    val price: Double,
    val isSelected: Boolean = false
)

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int,
    val selectedCustomisations: List<CustomisationOption> = emptyList()
) {
    val totalUnitPrice: Double
        get() = menuItem.price + selectedCustomisations.sumOf { it.price }
    
    val totalPrice: Double
        get() = totalUnitPrice * quantity
}

data class Coupon(
    val code: String,
    val description: String,
    val discountPercent: Double = 0.0,
    val flatDiscount: Double = 0.0,
    val minOrderValue: Double = 0.0,
    val maxDiscount: Double = Double.MAX_VALUE
)

enum class OrderStatus {
    CONFIRMED,
    PREPARING,
    RIDER_ASSIGNED,
    ON_THE_WAY,
    ARRIVING_SOON,
    DELIVERED
}

data class LiveOrderState(
    val orderId: String,
    val restaurant: Restaurant,
    val items: List<CartItem>,
    val status: OrderStatus,
    val riderName: String,
    val riderRating: Double,
    val riderVehicle: String,
    val riderColor: String,
    val etaMinutes: Int,
    val riderLatOffset: Float, // Simulated lat offset (0f = restaurant, 1f = user)
    val riderLngOffset: Float,
    val distanceKm: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class SavedAddress(
    val id: String,
    val type: String, // Home, Work, Other
    val houseNumber: String,
    val area: String,
    val landmark: String = "",
    val city: String = "Bengaluru",
    val isDefault: Boolean = false
)

data class PastOrder(
    val orderId: String,
    val restaurant: Restaurant,
    val items: List<CartItem>,
    val totalPrice: Double,
    val dateString: String,
    val isDelivered: Boolean = true,
    val userRating: Int? = null,
    val userReview: String? = null
)
