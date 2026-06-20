package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.NomatoViewModel
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainHubScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.RestaurantDetailScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        val viewModel: NomatoViewModel = viewModel()
        
        val isLoggedIn by viewModel.isLoggedIn.collectAsState()
        val hasFinishedOnboarding by viewModel.hasFinishedOnboarding.collectAsState()

        Surface(modifier = Modifier.fillMaxSize()) {
          NavHost(
            navController = navController,
            startDestination = "onboarding"
          ) {
            // 1. Onboarding Screen Composable
            composable("onboarding") {
              OnboardingScreen(
                onFinished = {
                  viewModel.finishOnboarding()
                  navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                  }
                }
              )
            }

            // 2. Login SMS OTP Screen
            composable("login") {
              if (isLoggedIn) {
                navController.navigate("main") {
                  popUpTo("login") { inclusive = true }
                }
              } else {
                LoginScreen(viewModel = viewModel)
              }
            }

            // 3. Main Delivery Hub Screen showing Home, Favorites, History, Profile
            composable("main") {
              if (!isLoggedIn) {
                navController.navigate("login") {
                  popUpTo("main") { inclusive = true }
                }
              } else {
                MainHubScreen(
                  viewModel = viewModel,
                  onRestaurantSelected = {
                    navController.navigate("restaurant_detail")
                  },
                  onCartSelected = {
                    navController.navigate("cart")
                  },
                  onTrackingSelected = {
                    navController.navigate("tracking")
                  }
                )
              }
            }

            // 4. Detailed Restaurant Cuisines Screen
            composable("restaurant_detail") {
              RestaurantDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
              )
            }

            // 5. Checkout Cart Items review Screen
            composable("cart") {
              CartScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate("checkout") }
              )
            }

            // 6. UPI / COD Payments Screen
            composable("checkout") {
              CheckoutScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOrderPlacedSuccess = {
                  navController.navigate("tracking") {
                    popUpTo("cart") { inclusive = true }
                  }
                }
              )
            }

            // 7. Live Driver Simulation routing Tracking Screen
            composable("tracking") {
              OrderTrackingScreen(
                viewModel = viewModel,
                onBack = { navController.navigate("main") {
                  popUpTo("tracking") { inclusive = true }
                }}
              )
            }
          }
        }
      }
    }
  }
}
