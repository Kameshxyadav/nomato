package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object NomatoRepository {
    private val repositoryScope = CoroutineScope(Dispatchers.Default)
    private var trackingJob: Job? = null

    // Restaurants
    val restaurants = listOf(
        Restaurant(
            id = "r1",
            name = "Meghana Foods Express",
            cuisines = listOf("Biryani", "Andhra", "South Indian"),
            rating = 4.4,
            ratingCount = "10K+ ratings",
            deliveryTimeMins = 25,
            distanceKm = 3.2,
            costForTwo = 400.0,
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?q=80&w=600&auto=format&fit=crop",
            isVegOnly = false,
            offers = listOf("50% OFF up to ₹100"),
            locationName = "Indiranagar, Bengaluru",
            gradientColors = listOf(0xFFF39C12, 0xFFE67E22)
        ),
        Restaurant(
            id = "r2",
            name = "Rameshwaram Cafe",
            cuisines = listOf("South Indian", "Breakfast", "Snacks"),
            rating = 4.6,
            ratingCount = "5K+ ratings",
            deliveryTimeMins = 35,
            distanceKm = 5.1,
            costForTwo = 250.0,
            imageUrl = "https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?q=80&w=600&auto=format&fit=crop",
            isVegOnly = true,
            offers = listOf("Flat ₹50 OFF"),
            locationName = "Koramangala, Bengaluru",
            gradientColors = listOf(0xFF2ECC71, 0xFF27AE60)
        ),
        Restaurant(
            id = "r3",
            name = "Empire Restaurant",
            cuisines = listOf("North Indian", "Kebabs", "Mughlai"),
            rating = 4.2,
            ratingCount = "20K+ ratings",
            deliveryTimeMins = 40,
            distanceKm = 6.4,
            costForTwo = 600.0,
            imageUrl = "https://images.unsplash.com/photo-1544025162-8111142154ea?q=80&w=600&auto=format&fit=crop",
            isVegOnly = false,
            offers = emptyList(),
            locationName = "Lalbagh Road, Bengaluru",
            gradientColors = listOf(0xFFE74C3C, 0xFFC0392B)
        ),
        Restaurant(
            id = "r4",
            name = "Corner House Ice Creams",
            cuisines = listOf("Desserts", "Ice Cream", "Beverages"),
            rating = 4.8,
            ratingCount = "15K+ ratings",
            deliveryTimeMins = 15,
            distanceKm = 2.0,
            costForTwo = 300.0,
            imageUrl = "https://images.unsplash.com/photo-1563805042-7684c8e9e1cb?q=80&w=600&auto=format&fit=crop",
            isVegOnly = true,
            offers = listOf("10% OFF on ₹400"),
            locationName = "Commercial Street, Bengaluru",
            gradientColors = listOf(0xFF9B59B6, 0xFF8E44AD)
        ),
        Restaurant(
            id = "r5",
            name = "Leon's Burgers & Wings",
            cuisines = listOf("American", "Burgers", "Fast Food"),
            rating = 4.3,
            ratingCount = "12K+ ratings",
            deliveryTimeMins = 28,
            costForTwo = 500.0,
            offers = listOf("30% OFF with FIRST50", "Free Fries above ₹350"),
            isVegOnly = false,
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=600&auto=format&fit=crop",
            locationName = "HSR Layout, Bengaluru",
            distanceKm = 3.9,
            gradientColors = listOf(0xFF1ABC9C, 0xFF16A085)
        ),
        Restaurant(
            id = "r6",
            name = "Truffles Bistro",
            cuisines = listOf("Continental", "Cafe", "Desserts"),
            rating = 4.5,
            ratingCount = "18K+ ratings",
            deliveryTimeMins = 35,
            costForTwo = 600.0,
            offers = listOf("Nomato Gold Flat 15% OFF"),
            isVegOnly = false,
            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=600&auto=format&fit=crop",
            locationName = "St. Marks Road, Bengaluru",
            distanceKm = 4.1,
            gradientColors = listOf(0xFF34495E, 0xFF2C3E50)
        )
    )

    // Menu Items per Restaurant
    val menuItems = mapOf(
        "r1" to listOf(
            MenuItem("m11", "Special Chicken Biryani", "Aromatic long grain basmati rice layered with juicy masala chicken, and eggs, slow cooked with spices.", 310.0, false, "Biryani", true, 4.7, listOf(
                CustomisationGroup("Extra Accompaniments", 0, 2, listOf(
                    CustomisationOption("opt1", "Raita Extra", 20.0),
                    CustomisationOption("opt2", "Salat Extra", 15.0),
                    CustomisationOption("opt3", "Double Salan", 0.0)
                ))
            ), listOf(0xFFF39C12, 0xFFD35400)),
            MenuItem("m12", "Paneer Biryani", "Fragrant basmati rice cooked with fresh tandoori paneer tikka, mint, and spices.", 270.0, true, "Biryani", false, 4.4, emptyList(), listOf(0xFFF1C40F, 0xFFF39C12)),
            MenuItem("m13", "Chicken Fry Biryani", "Golden crispy fried chicken pieces layered on a bed of signature spiced biryani rice.", 330.0, false, "Biryani", true, 4.6, emptyList(), listOf(0xFFE67E22, 0xFFD35400)),
            MenuItem("m14", "Andhra Chicken Curry", "Super hot and spicy stew of chicken cooked in traditional Guntur-style curry masala. Recommended with roti.", 280.0, false, "Curries", false, 4.3, emptyList(), listOf(0xFFD35400, 0xFFC0392B)),
            MenuItem("m15", "Tandoori Roti", "Fresh whole wheat flour oven-baked flatbread.", 30.0, true, "Breads", false, 4.5, emptyList(), listOf(0xFFD35400, 0xFFBDC3C7))
        ),
        "r2" to listOf(
            MenuItem("m21", "Paneer Butter Masala", "Cottage cheese triangles simmered in rich creamy tomato cashew gravy with real butter glaze.", 260.0, true, "Curries", true, 4.7, listOf(
                CustomisationGroup("Preparation Preference", 0, 1, listOf(
                    CustomisationOption("p1", "Less Spicy", 0.0),
                    CustomisationOption("p2", "Extra Spicy", 0.0),
                    CustomisationOption("p3", "No Butter Cream", 0.0)
                ))
            ), listOf(0xFF2ECC71, 0xFF27AE60)),
            MenuItem("m22", "Dal Makhani Rasoi", "Black lentils slow simmered overnight with tomatoes, butter and rich dairy cream.", 220.0, true, "Curries", true, 4.5, emptyList(), listOf(0xFF1ABC9C, 0xFF16A085)),
            MenuItem("m23", "Butter Naan", "Super soft tandoor baked refined flour bread smothered in luxury salted butter.", 50.0, true, "Breads", false, 4.6, emptyList(), listOf(0xFF27AE60, 0xFF16A085)),
            MenuItem("m24", "Garlic Butter Naan", "Oven fresh bread flavored with roasted garlic granules, fresh coriander and salted butter.", 65.0, true, "Breads", false, 4.8, emptyList(), listOf(0xFF3498DB, 0xFF2980B9)),
            MenuItem("m25", "Kadhai Paneer Tikka", "Paneer cubes tossed with vibrant bell peppers, whole spices, coriander and spicy tomatoes.", 280.0, true, "Starters", false, 4.3, emptyList(), listOf(0xFF2ECC71, 0xFF3498DB))
        ),
        "r3" to listOf(
            MenuItem("m31", "Signature Masala Dosa", "Iconic thick golden crispy rice crepe layered with signature spiced chili chutney, potato mash.", 110.0, true, "Breakfast", true, 4.9, listOf(
                CustomisationGroup("Extra Ghee", 0, 1, listOf(
                    CustomisationOption("g1", "Double Ghee Layer", 25.0)
                ))
            ), listOf(0xFFE74C3C, 0xFFC0392B)),
            MenuItem("m32", "Rava Idli (2 Pcs)", "Original MTR steamed semolina cakes flavored with cashew, ginger and served with ghee.", 90.0, true, "Breakfast", true, 4.8, emptyList(), listOf(0xFFE67E22, 0xFFD35400)),
            MenuItem("m33", "Rava Masala Dosa", "Crispy lacy crepe made of cream-of-wheat filled with aromatic potato masala.", 130.0, true, "Breakfast", false, 4.6, emptyList(), listOf(0xFFF1C40F, 0xFFF39C12)),
            MenuItem("m34", "Filter Coffee (Authentic)", "Traditional drip brewed chicory coffee aerated with whole milk in brass utensil.", 45.0, true, "Beverages", true, 4.9, emptyList(), listOf(0xFF9E9D24, 0xFF827717))
        ),
        "r4" to listOf(
            MenuItem("m41", "Spiced Saffron Gulab Jamun", "Soft round milk cake balls completely steeped in cardamom and saffron infused sugar syrup.", 90.0, true, "Sweets", true, 4.8, emptyList(), listOf(0xFF9B59B6, 0xFF8E44AD)),
            MenuItem("m42", "Kaju Katli Box (250g)", "Classic premium cashew diamond fudge with elegant gold leaf wrapper.", 280.0, true, "Sweets", false, 4.7, emptyList(), listOf(0xFFFEAC5E, 0xFFC779D0)),
            MenuItem("m43", "Dahi Puri Chat", "Crisp flour hollow spheres filled with spicy potatoes, cold sweetened yogurt, sweet & sour chutneys.", 80.0, true, "Sweets", true, 4.5, emptyList(), listOf(0xFF1ABC9C, 0xFF16A085))
        ),
        "r5" to listOf(
            MenuItem("m51", "Healthy Salad", "Fresh garden vegetables with vinaigrette.", 150.0, true, "Healthy", false, 4.5, emptyList(), listOf(0xFF1ABC9C, 0xFF16A085)),
            MenuItem("m52", "Fruit Bowl", "Seasonal fresh fruits.", 120.0, true, "Healthy", false, 4.2, emptyList(), listOf(0xFF2ECC71, 0xFF27AE60))
        ),
        "r6" to listOf(
            MenuItem("m61", "Pasta Alfredo", "Creamy cheese pasta.", 250.0, true, "Italian", false, 4.7, emptyList(), listOf(0xFF34495E, 0xFF2C3E50)),
            MenuItem("m62", "Margherita Pizza", "Classic cheese and tomato pizza.", 300.0, true, "Italian", true, 4.8, emptyList(), listOf(0xFFE74C3C, 0xFFC0392B))
        )
    )

    // Coupons
    val availableCoupons = listOf(
        Coupon("FIRST50", "50% OFF up to ₹100 on your first order!", discountPercent = 50.0, maxDiscount = 100.0),
        Coupon("NOMATO20", "Save 20% on any premium meal above ₹300!", discountPercent = 20.0, minOrderValue = 300.0, maxDiscount = 150.0),
        Coupon("SAVE100", "Flat ₹100 OFF on orders greater than ₹600!", flatDiscount = 100.0, minOrderValue = 600.0),
        Coupon("SUPERVEG", "30% OFF exclusively on Vegetarian restaurants!", discountPercent = 30.0, maxDiscount = 90.0)
    )

    // Saved Addresses
    private val _savedAddresses = MutableStateFlow(listOf(
        SavedAddress("a1", "Home", "Flat 405, Block C", "Prestige Shantiniketan, Whitefield", "Near ITPL Metro", "Bengaluru", isDefault = true),
        SavedAddress("a2", "Work", "Tower B, Level 6, Global Tech Park", "Indiranagar 100ft Road", "Next to Metro Station", "Bengaluru", isDefault = false)
    ))
    val savedAddresses: StateFlow<List<SavedAddress>> = _savedAddresses.asStateFlow()

    private val _selectedAddress = MutableStateFlow<SavedAddress?>(_savedAddresses.value.firstOrNull())
    val selectedAddress: StateFlow<SavedAddress?> = _selectedAddress.asStateFlow()

    // Favorites
    private val _favorites = MutableStateFlow<Set<String>>(setOf("r1", "r3"))
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    // Cart
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartRestaurant = MutableStateFlow<Restaurant?>(null)
    val cartRestaurant: StateFlow<Restaurant?> = _cartRestaurant.asStateFlow()

    // Active Order / Simulation State
    private val _liveOrder = MutableStateFlow<LiveOrderState?>(null)
    val liveOrder: StateFlow<LiveOrderState?> = _liveOrder.asStateFlow()

    // Past Orders
    private val _pastOrders = MutableStateFlow<List<PastOrder>>(listOf(
        PastOrder(
            orderId = "NOM-482910",
            restaurant = restaurants[2],
            items = listOf(
                CartItem(menuItems["r3"]!![0], 2),
                CartItem(menuItems["r3"]!![3], 1)
            ),
            totalPrice = 285.50,
            dateString = "June 14, 2026",
            isDelivered = true,
            userRating = 5,
            userReview = "Traditional and fantastic filter coffee! Delivered sizzling hot."
        )
    ))
    val pastOrders: StateFlow<List<PastOrder>> = _pastOrders.asStateFlow()

    // Current Active Courier Names
    private val riderNames = listOf("Aarav Sharma", "Vikram Rathore", "Kunal Patel", "Rajesh Kumar", "Amit Mishra")
    private val riderVehicles = listOf("Royal Enfield (Electric)", "Ather 450X (Green)", "TVS iQube (Blue)", "Bajaj Pulsar (Red)")

    // --- Actions ---

    fun toggleFavorite(restaurantId: String) {
        _favorites.update { current ->
            if (current.contains(restaurantId)) current - restaurantId else current + restaurantId
        }
    }

    fun selectAddress(addressId: String) {
        _savedAddresses.value.find { it.id == addressId }?.let { addr ->
            _selectedAddress.value = addr
        }
    }

    fun addNewAddress(address: SavedAddress) {
        _savedAddresses.update { it + address }
        if (address.isDefault || _selectedAddress.value == null) {
            _selectedAddress.value = address
        }
    }

    fun removeAddress(addressId: String) {
        _savedAddresses.update { current -> current.filter { it.id != addressId } }
        if (_selectedAddress.value?.id == addressId) {
            _selectedAddress.value = _savedAddresses.value.firstOrNull()
        }
    }

    fun addToCart(restaurant: Restaurant, menuItem: MenuItem, customizations: List<CustomisationOption> = emptyList()) {
        if (_cartRestaurant.value != null && _cartRestaurant.value?.id != restaurant.id) {
            // Re-clearing cart for new restaurant
            _cartItems.value = emptyList()
        }
        _cartRestaurant.value = restaurant
        
        _cartItems.update { items ->
            val match = items.find { it.menuItem.id == menuItem.id && it.selectedCustomisations == customizations }
            if (match != null) {
                items.map {
                    if (it.menuItem.id == menuItem.id && it.selectedCustomisations == customizations) {
                        it.copy(quantity = it.quantity + 1)
                    } else it
                }
            } else {
                items + CartItem(menuItem, 1, customizations)
            }
        }
    }

    fun removeFromCart(menuItemId: String, customizations: List<CustomisationOption> = emptyList()) {
        _cartItems.update { items ->
            items.mapNotNull {
                if (it.menuItem.id == menuItemId && it.selectedCustomisations == customizations) {
                    if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else null
                } else it
            }
        }
        if (_cartItems.value.isEmpty()) {
            _cartRestaurant.value = null
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _cartRestaurant.value = null
    }

    fun submitRating(orderId: String, rating: Int, review: String) {
        _pastOrders.update { list ->
            list.map {
                if (it.orderId == orderId) {
                    it.copy(userRating = rating, userReview = review)
                } else it
            }
        }
    }

    // --- Order Simulation Engine ---

    fun placeOrder(tipAmount: Double, couponCode: String?, orderTotal: Double) {
        val currentRestaurant = _cartRestaurant.value ?: return
        val items = _cartItems.value
        if (items.isEmpty()) return

        // Clear Cart
        clearCart()

        // Generate Live Order
        val randomRider = riderNames[Random.nextInt(riderNames.size)]
        val randomVehicle = riderVehicles[Random.nextInt(riderVehicles.size)]
        val randomColor = String.format("#%06X", Random.nextInt(0xFFFFFF))
        
        val newOrder = LiveOrderState(
            orderId = "NOM-${100000 + Random.nextInt(900000)}",
            restaurant = currentRestaurant,
            items = items,
            status = OrderStatus.CONFIRMED,
            riderName = randomRider,
            riderRating = 4.7 + Random.nextDouble(0.3),
            riderVehicle = randomVehicle,
            riderColor = randomColor,
            etaMinutes = currentRestaurant.deliveryTimeMins,
            riderLatOffset = 0.0f,
            riderLngOffset = 0.0f,
            distanceKm = currentRestaurant.distanceKm
        )

        _liveOrder.value = newOrder

        // Start movement tracking sequencecoroutine
        trackingJob?.cancel()
        trackingJob = repositoryScope.launch {
            // 1. CONFIRMED
            delay(5000)
            _liveOrder.update { it?.copy(status = OrderStatus.PREPARING, etaMinutes = maxOf(2, (it.etaMinutes * 0.95).toInt())) }

            // 2. PREPARING
            delay(8000)
            _liveOrder.update { it?.copy(status = OrderStatus.RIDER_ASSIGNED, etaMinutes = maxOf(2, (it.etaMinutes * 0.9).toInt())) }

            // 3. RIDER_ASSIGNED
            delay(6000)
            _liveOrder.update { it?.copy(status = OrderStatus.ON_THE_WAY, etaMinutes = maxOf(2, (it.etaMinutes * 0.8).toInt())) }

            // 4. ON THE WAY (Smooth Rider Movement simulation across coordinates ticks)
            val ticks = 30
            val tickDelay = 1000L
            val routeFactor = 0.8f
            for (i in 1..ticks) {
                delay(tickDelay)
                val progression = (i.toFloat() / ticks.toFloat()) * routeFactor
                _liveOrder.update { order ->
                    if (order != null) {
                        val currentEta = maxOf(2, order.restaurant.deliveryTimeMins - (order.restaurant.deliveryTimeMins * progressToFactor(progression)).toInt())
                        // Add organic slight wiggle to make tracking path routing look extremely convincing
                        val noiseLat = Random.nextFloat() * 0.04f - 0.02f
                        val noiseLng = Random.nextFloat() * 0.04f - 0.02f
                        order.copy(
                            riderLatOffset = progression + noiseLat,
                            riderLngOffset = progression * 0.5f + noiseLng, // slightly curved path
                            etaMinutes = currentEta
                        )
                    } else null
                }
            }

            // 5. ARRIVING_SOON
            _liveOrder.update { it?.copy(status = OrderStatus.ARRIVING_SOON, etaMinutes = 2, riderLatOffset = 0.95f, riderLngOffset = 0.48f) }
            delay(8000)

            // 6. DELIVERED
            _liveOrder.update { it?.copy(status = OrderStatus.DELIVERED, etaMinutes = 0, riderLatOffset = 1.0f, riderLngOffset = 0.5f) }
            val completedOrder = _liveOrder.value
            delay(3000)

            // Move completed order to past orders history
            if (completedOrder != null) {
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                val pastOrder = PastOrder(
                    orderId = completedOrder.orderId,
                    restaurant = completedOrder.restaurant,
                    items = completedOrder.items,
                    totalPrice = orderTotal,
                    dateString = dateFormat.format(Date()),
                    isDelivered = true
                )
                _pastOrders.update { listOf(pastOrder) + it }
            }
            _liveOrder.value = null // clear active order screens
        }
    }

    private fun progressToFactor(progression: Float): Double {
        return progression.toDouble()
    }
}
