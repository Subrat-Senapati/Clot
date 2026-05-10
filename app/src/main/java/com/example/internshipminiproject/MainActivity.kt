package com.example.internshipminiproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.internshipminiproject.Screen.auth.ConformationEmailScreen
import com.example.internshipminiproject.Screen.auth.CreateAccountScreen
import com.example.internshipminiproject.Screen.auth.ForgotPasswordScreen
import com.example.internshipminiproject.Screen.auth.OnboardingScreen
import com.example.internshipminiproject.Screen.auth.SignInPasswordScreen
import com.example.internshipminiproject.Screen.auth.SignInScreen
import com.example.internshipminiproject.Screen.cart.CartScreen
import com.example.internshipminiproject.Screen.cart.CheckoutScreen
import com.example.internshipminiproject.Screen.cart.OrderPlacedScreen
import com.example.internshipminiproject.Screen.homepage.AllCategoriesScreen
import com.example.internshipminiproject.Screen.homepage.CategoryProductsScreen
import com.example.internshipminiproject.Screen.homepage.HomepageScreen
import com.example.internshipminiproject.Screen.notification.NotificationScreen
import com.example.internshipminiproject.Screen.order.OrderDetailsScreen
import com.example.internshipminiproject.Screen.order.OrderItemScreen
import com.example.internshipminiproject.Screen.order.OrderScreen
import com.example.internshipminiproject.Screen.product.ProductDetailsScreen
import com.example.internshipminiproject.Screen.search.SearchScreen
import com.example.internshipminiproject.Screen.settings.AddAddressScreen
import com.example.internshipminiproject.Screen.settings.AddressScreen
import com.example.internshipminiproject.Screen.settings.EditAddressScreen
import com.example.internshipminiproject.Screen.settings.EditProfile
import com.example.internshipminiproject.Screen.settings.SettingsScreen
import com.example.internshipminiproject.Screen.settings.WishlistScreen
import com.example.internshipminiproject.ui.theme.InternshipMiniProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            InternshipMiniProjectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "SignInScreen",
                        builder = {
                            composable("SignInScreen") {
                                SignInScreen(navController)
                            }
                            composable("SignInPasswordScreen" + "/{email}") { backStackEntry ->
                                val email = backStackEntry.arguments?.getString("email") ?: ""
                                SignInPasswordScreen(email, navController)
                            }
                            composable("CreateAccountScreen") {
                                CreateAccountScreen(navController)
                            }
                            composable("ForgotPasswordScreen") {
                                ForgotPasswordScreen(navController)
                            }
                            composable("ConformationEmailScreen") {
                                ConformationEmailScreen(navController)
                            }
                            composable("OnboardingScreen" + "/{id}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("id") ?: ""
                                OnboardingScreen(id, navController)
                            }

                            composable("HomepageScreen") {
                                HomepageScreen(navController)
                            }
                            composable("NotificationScreen") {
                                NotificationScreen(navController)
                            }
                            composable("OrderScreen") {
                                OrderScreen(navController)
                            }
                            composable("SettingsScreen") {
                                SettingsScreen(navController)
                            }

                            composable("CartScreen") {
                                CartScreen(navController)
                            }
                            composable("CheckoutScreen") {
                                CheckoutScreen(navController)
                            }
                            composable("OrderPlacedScreen") {
                                OrderPlacedScreen(navController)
                            }

                            composable("SearchScreen") {
                                SearchScreen(navController)
                            }

                            composable("AllCategoriesScreen") {
                                AllCategoriesScreen(navController)
                            }
                            composable("category/{slug}") { backStackEntry ->
                                val slug =
                                    backStackEntry.arguments?.getString("slug") ?: return@composable
                                CategoryProductsScreen(slug = slug, navController = navController)
                            }

                            composable("product/{productId}") { backStackEntry ->
                                val productId =
                                    backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                                productId?.let {
                                    ProductDetailsScreen(
                                        productId = it,
                                        navController = navController
                                    )
                                }
                            }

                            composable("EditProfile") {
                                EditProfile(navController)
                            }
                            composable("AddressScreen") {
                                AddressScreen(navController)
                            }
                            composable("AddAddressScreen") {
                                AddAddressScreen(navController)
                            }
                            composable("EditAddressScreen/{addressId}") { backStackEntry ->
                                val addressId = backStackEntry.arguments?.getString("addressId")
                                    ?: return@composable

                                EditAddressScreen(
                                    navController = navController,
                                    addressId = addressId
                                )
                            }
                            composable("WishlistScreen") {
                                WishlistScreen(navController)
                            }
                            composable("OrderDetailsScreen/{orderId}") { backStackEntry ->
                                val orderId = backStackEntry.arguments?.getString("orderId")
                                    ?: return@composable

                                OrderDetailsScreen(orderId, navController)
                            }

                            composable("OrderItemScreen/{orderId}") {backStackEntry ->
                                val orderId = backStackEntry.arguments?.getString("orderId")
                                    ?: return@composable
                                OrderItemScreen(orderId, navController)
                            }
                        })
                }
            }
        }
    }
}
