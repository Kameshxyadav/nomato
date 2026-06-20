package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CartItem
import com.example.data.Coupon
import com.example.data.CustomisationOption
import com.example.data.LiveOrderState
import com.example.data.MenuItem
import com.example.data.NomatoRepository
import com.example.data.PastOrder
import com.example.data.Restaurant
import com.example.data.SavedAddress
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class NomatoViewModel : ViewModel() {

    // Auth state
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _isOtpSent = MutableStateFlow(false)
    val isOtpSent: StateFlow<Boolean> = _isOtpSent.asStateFlow()

    private val _otpCountdown = MutableStateFlow(0)
    val otpCountdown: StateFlow<Int> = _otpCountdown.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isVerifying = MutableStateFlow(false)
    val isVerifying: StateFlow<Boolean> = _isVerifying.asStateFlow()

    // Filters & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCuisine = MutableStateFlow<String?>(null)
    val selectedCuisine: StateFlow<String?> = _selectedCuisine.asStateFlow()

    private val _vegFilter = MutableStateFlow(false)
    val vegFilter: StateFlow<Boolean> = _vegFilter.asStateFlow()

    private val _sortType = MutableStateFlow("RATING") // RATING, ETA, PRICE_LOW, PRICE_HIGH
    val sortType: StateFlow<String> = _sortType.asStateFlow()

    // Selected items
    private val _selectedRestaurant = MutableStateFlow<Restaurant?>(null)
    val selectedRestaurant: StateFlow<Restaurant?> = _selectedRestaurant.asStateFlow()

    private val _selectedCoupon = MutableStateFlow<Coupon?>(null)
    val selectedCoupon: StateFlow<Coupon?> = _selectedCoupon.asStateFlow()

    private val _tipAmount = MutableStateFlow(30.0) // Default tip 30 rupees
    val tipAmount: StateFlow<Double> = _tipAmount.asStateFlow()

    // Screen Loading/Skeleton Simulation
    private val _isScreenLoading = MutableStateFlow(false)
    val isScreenLoading: StateFlow<Boolean> = _isScreenLoading.asStateFlow()

    // User Profile Information
    private val _userName = MutableStateFlow("Rajesh Patel")
    val userName: StateFlow<String> = _userName.asStateFlow()
    
    // Notifications list
    private val _notifications = MutableStateFlow(listOf(
        NotificationItem("n1", "Welcome to Nomato!", "Order delicious hot food with super-fast delivery & big discounts.", "9:00 AM", false),
        NotificationItem("n2", "NOMATO25 Special Offer 🌶️", "Get 25% discount on top rated biryani hubs near you.", "Yesterday", true)
    ))
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Onboarding
    private val _hasFinishedOnboarding = MutableStateFlow(false)
    val hasFinishedOnboarding: StateFlow<Boolean> = _hasFinishedOnboarding.asStateFlow()

    // Flows from repository
    val savedAddresses: StateFlow<List<SavedAddress>> = NomatoRepository.savedAddresses
    val selectedAddress: StateFlow<SavedAddress?> = NomatoRepository.selectedAddress
    val favorites: StateFlow<Set<String>> = NomatoRepository.favorites
    val cartItems: StateFlow<List<CartItem>> = NomatoRepository.cartItems
    val cartRestaurant: StateFlow<Restaurant?> = NomatoRepository.cartRestaurant
    val liveOrder: StateFlow<LiveOrderState?> = NomatoRepository.liveOrder
    val pastOrders: StateFlow<List<PastOrder>> = NomatoRepository.pastOrders

    // Combine filters to restaurant list
    val filteredRestaurants: StateFlow<List<Restaurant>> = combine(
        MutableStateFlow(NomatoRepository.restaurants),
        _searchQuery,
        _selectedCuisine,
        _vegFilter,
        _sortType
    ) { list, query, cuisine, vegOnly, sort ->
        var result = list
        if (query.isNotEmpty()) {
            result = result.filter { it.name.contains(query, ignoreCase = true) || it.cuisines.any { c -> c.contains(query, ignoreCase = true) } }
        }
        if (cuisine != null) {
            result = result.filter { it.cuisines.contains(cuisine) }
        }
        if (vegOnly) {
            result = result.filter { it.isVegOnly }
        }

        when (sort) {
            "RATING" -> result.sortedByDescending { it.rating }
            "ETA" -> result.sortedBy { it.deliveryTimeMins }
            "PRICE_LOW" -> result.sortedBy { it.costForTwo }
            "PRICE_HIGH" -> result.sortedByDescending { it.costForTwo }
            else -> result
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NomatoRepository.restaurants)

    private var countdownJob: Job? = null

    // OTP Timer
    fun sendOtp(phone: String) {
        if (phone.length < 10) return
        _phoneNumber.value = phone
        _isOtpSent.value = true
        _otpCountdown.value = 30
        
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_otpCountdown.value > 0) {
                delay(1000)
                _otpCountdown.update { it - 1 }
            }
        }
        
        // Add welcome notification
        addNotification("OTP Received! 📱", "Use OTP 2026 to log in to your Nomato Account.", "Just now")
    }

    fun verifyOtp(otp: String): Boolean {
        _otpCode.value = otp
        if (otp == "2026" || otp.length == 4) { // Let standard 4 digits pass for prototype simplicity
            _isVerifying.value = true
            viewModelScope.launch {
                delay(1200) // Beautiful authentic progress delay
                _isVerifying.value = false
                _isLoggedIn.value = true
            }
            return true
        }
        return false
    }

    fun logout() {
        _isLoggedIn.value = false
        _isOtpSent.value = false
        _phoneNumber.value = ""
        _otpCode.value = ""
    }

    fun finishOnboarding() {
        _hasFinishedOnboarding.value = true
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCuisine(cuisine: String?) {
        if (_selectedCuisine.value == cuisine) {
            _selectedCuisine.value = null
        } else {
            _selectedCuisine.value = cuisine
        }
    }

    fun toggleVegFilter() {
        _vegFilter.value = !_vegFilter.value
    }

    fun setSortType(sort: String) {
        _sortType.value = sort
    }

    fun selectRestaurant(restaurant: Restaurant?) {
        _selectedRestaurant.value = restaurant
        // Reset local coupon when switching restaurant detail screen
        _selectedCoupon.value = null
    }

    fun applyCoupon(coupon: Coupon?) {
        _selectedCoupon.value = coupon
    }

    fun setTipAmount(amount: Double) {
        _tipAmount.value = amount
    }

    fun toggleFavorite(restaurantId: String) {
        NomatoRepository.toggleFavorite(restaurantId)
    }

    fun selectSavedAddress(addressId: String) {
        NomatoRepository.selectAddress(addressId)
    }

    fun addSavedAddress(type: String, houseNum: String, sector: String) {
        val randomId = "a-${Random.nextInt(10000)}"
        val newAddr = SavedAddress(randomId, type, houseNum, sector, city = "Bengaluru")
        NomatoRepository.addNewAddress(newAddr)
    }

    fun removeSavedAddress(addressId: String) {
        NomatoRepository.removeAddress(addressId)
    }

    fun addToCart(item: MenuItem, customizations: List<CustomisationOption> = emptyList()) {
        val rest = _selectedRestaurant.value ?: return
        NomatoRepository.addToCart(rest, item, customizations)
    }

    fun removeFromCart(itemId: String, customizations: List<CustomisationOption> = emptyList()) {
        NomatoRepository.removeFromCart(itemId, customizations)
    }

    fun clearCart() {
        NomatoRepository.clearCart()
        _selectedCoupon.value = null
    }

    fun placeOrder(orderTotal: Double) {
        val couponCode = _selectedCoupon.value?.code
        NomatoRepository.placeOrder(_tipAmount.value, couponCode, orderTotal)
        _selectedCoupon.value = null
        
        // Add dispatch notifications in background
        viewModelScope.launch {
            addNotification("Order Confirmed! 🎉", "Your sizzling meal is being confirmed by the chef.", "1 min")
            delay(10000)
            addNotification("Kitchen is Preparing! 🍳", "Our master chefs are carefully cooking your gourmet dishes.", "Just now")
            delay(12000)
            addNotification("Rider Assigned! 🏍️", "Your delivery champion is at the restaurant picking up your food.", "Just now")
        }
    }

    private fun addNotification(title: String, body: String, time: String) {
        _notifications.update { current ->
            val newItem = NotificationItem("n-${Random.nextInt(10001)}", title, body, time, false)
            listOf(newItem) + current
        }
    }

    fun markNotificationsAsRead() {
        _notifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
    }

    fun simulateNotificationTrigger() {
        // Simulates receiving a dynamic order notification update
        addNotification(
            "Feast Alert! 🍗",
            "Savor spicy, freshly-baked butter naans! Check active deals for up to 50% discount.",
            "Just now"
        )
    }

    fun submitRating(orderId: String, rating: Int, review: String) {
        NomatoRepository.submitRating(orderId, rating, review)
    }

    fun triggerQuickLoading() {
        _isScreenLoading.value = true
        viewModelScope.launch {
            delay(400)
            _isScreenLoading.value = false
        }
    }
}

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean
)
