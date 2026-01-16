package com.example.rumahistimewa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rumahistimewa.ui.login.LoginScreen
import com.example.rumahistimewa.ui.login.LoginViewModel
import com.example.rumahistimewa.ui.register.RegisterScreen
import com.example.rumahistimewa.ui.register.RegisterViewModel

@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    // ViewModel (Simple instantiation for now)
    val loginViewModel = LoginViewModel()
    val registerViewModel = RegisterViewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        
        // =======================
        // SPLASH
        // =======================
        composable("splash") {
            com.example.rumahistimewa.ui.splash.SplashScreen(
                onTimeout = {
                    navController.navigate("home_customer") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // =======================
        // LOGIN
        // =======================
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onSuccess = { role ->
                    val destination = com.example.rumahistimewa.util.UserSession.afterLoginDestination
                    if (destination != null) {
                        navController.navigate(destination) {
                            popUpTo("login") { inclusive = true }
                        }
                        com.example.rumahistimewa.util.UserSession.afterLoginDestination = null
                    } else {
                        when (role) {
                            "customer" -> navController.navigate("home_customer") {
                                popUpTo("login") { inclusive = true }
                            }
                            "owner" -> navController.navigate("home_owner") {
                                popUpTo("login") { inclusive = true }
                            }
                            "admin" -> navController.navigate("home_admin") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        // =======================
        // REGISTER
        // =======================
        composable("register") {
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // =======================
        // CUSTOMER
        // =======================
        composable("home_customer") {
            com.example.rumahistimewa.ui.customer.CustomerMainScreen(
                onVillaClick = { villaId ->
                    navController.navigate("detail_villa/$villaId")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home_customer") { inclusive = true }
                    }
                },
                onNavigateToProfileDetail = { route ->
                    navController.navigate(route)
                },
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }
        
        // Profile Details
        composable("profile_edit") {
            com.example.rumahistimewa.ui.profile.details.EditProfileScreen(onBackClick = { navController.popBackStack() })
        }
        composable("profile_password") {
            com.example.rumahistimewa.ui.profile.details.ChangePasswordScreen(onBackClick = { navController.popBackStack() })
        }
        composable("profile_transactions") {
            com.example.rumahistimewa.ui.profile.transaction.TransactionHistoryScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { id ->
                    navController.navigate("detail_transaction/$id")
                }
            )
        }
        
        composable("detail_transaction/{transactionId}") { backStackEntry ->
             val transactionId = backStackEntry.arguments?.getString("transactionId")
             if (transactionId != null) {
                 com.example.rumahistimewa.ui.profile.transaction.TransactionDetailScreen(
                     transactionId = transactionId,
                     onBackClick = { navController.popBackStack() }
                 )
             }
        }

        composable("profile_help") {
            com.example.rumahistimewa.ui.profile.help.HelpCenterScreen(onBackClick = { navController.popBackStack() })
        }
        composable("profile_contact") {
            com.example.rumahistimewa.ui.profile.details.ContactUsScreen(onBackClick = { navController.popBackStack() })
        }
        composable("profile_terms") {
            com.example.rumahistimewa.ui.profile.details.TermsScreen(onBackClick = { navController.popBackStack() })
        }
        composable("profile_privacy") {
            com.example.rumahistimewa.ui.profile.details.PrivacyScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable(
            route = "detail_villa/{villaId}",
            arguments = listOf(androidx.navigation.navArgument("villaId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val villaId = backStackEntry.arguments?.getString("villaId")
            com.example.rumahistimewa.ui.detail.DetailVillaScreen(
                villaId = villaId,
                onBackClick = { navController.popBackStack() },
                onBookClick = {

                    if (com.example.rumahistimewa.util.UserSession.isLoggedIn.value) {
                         navController.navigate("booking_flow/$villaId")
                    } else {
                        com.example.rumahistimewa.util.UserSession.afterLoginDestination = "booking_flow/$villaId"
                        navController.navigate("login")
                    }
                }

            )
        }
        
        // Booking Flow with Villa ID
        composable(
            route = "booking_flow/{villaId}",
            arguments = listOf(androidx.navigation.navArgument("villaId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
             val villaId = backStackEntry.arguments?.getString("villaId")
             com.example.rumahistimewa.ui.booking.BookingScreen(
                 villaId = villaId,
                 onBookingSuccess = {
                     navController.navigate("home_customer") {
                         popUpTo("home_customer") { inclusive = true }
                     }
                 }
             )
        }
        
        // Wishlist
        composable("wishlist") {
            com.example.rumahistimewa.ui.wishlist.WishlistScreen(
                onVillaClick = { villaId ->
                    navController.navigate("detail_villa/$villaId")
                }
            )
        }

        // My Bookings
        composable("my_booking") {
             com.example.rumahistimewa.ui.mybooking.MyBookingScreen(
                 onBackClick = { navController.popBackStack() },
                 onBookingClick = { bookingId ->
                     navController.navigate("detail_booking/$bookingId")
                 }
             )
        }

        composable(
            route = "detail_booking/{bookingId}",
            arguments = listOf(androidx.navigation.navArgument("bookingId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getInt("bookingId") ?: return@composable
            com.example.rumahistimewa.ui.mybooking.DetailBookingScreen(
                bookingId = bookingId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // =======================
        // OWNER
        // =======================
        composable("villa_submission") {
            com.example.rumahistimewa.ui.profile.submission.VillaSubmissionScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("villa_add") },
                onVillaClick = { villaId ->
                    navController.navigate("detail_villa/$villaId")
                }
            )
        }
        composable("villa_add") {
            com.example.rumahistimewa.ui.profile.submission.VillaAddScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitSuccess = { navController.popBackStack() }
            )
        }
        composable("owner_bookings") {
            com.example.rumahistimewa.ui.profile.owner.OwnerBookingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("income_revenue") {
            com.example.rumahistimewa.ui.profile.owner.IncomeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // =======================
        // OWNER
        // =======================
        composable("home_owner") {
            com.example.rumahistimewa.ui.owner.OwnerDashboardScreen()
        }

        // =======================
        // ADMIN
        // =======================
        composable("home_admin") {
            com.example.rumahistimewa.ui.admin.AdminDashboardScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home_admin") { inclusive = true }
                    }
                }
            )
        }
        
        composable("admin_users") {
            com.example.rumahistimewa.ui.admin.UserManagementScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home_admin") { inclusive = true }
                    }
                }
            )
        }
        composable("admin_villas") {
            com.example.rumahistimewa.ui.admin.VillaManagementScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home_admin") { inclusive = true }
                    }
                }
            )
        }
        composable("admin_villa_applications") {
            com.example.rumahistimewa.ui.admin.VillaApplicationScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home_admin") { inclusive = true }
                    }
                }
            )
        }
        // Placeholder for Form
        composable("admin_villa_form") {
             com.example.rumahistimewa.ui.admin.AdminVillaFormScreen(
                 villaId = null,
                 onNavigate = { route -> navController.navigate(route) },
                 onLogout = {
                     navController.navigate("login") {
                        popUpTo("home_admin") { inclusive = true }
                    }
                 },
                 onBack = { navController.popBackStack() }
             )
        }
        composable("admin_villa_form/{id}") { backStackEntry ->
             val id = backStackEntry.arguments?.getString("id")
             com.example.rumahistimewa.ui.admin.AdminVillaFormScreen(
                 villaId = id,
                 onNavigate = { route -> navController.navigate(route) },
                 onLogout = {
                     navController.navigate("login") {
                        popUpTo("home_admin") { inclusive = true }
                    }
                 },
                 onBack = { navController.popBackStack() }
             )
        }

        composable("admin_villa_detail/{id}") { backStackEntry ->
             val id = backStackEntry.arguments?.getString("id")
             if (id != null) {
                 com.example.rumahistimewa.ui.admin.AdminVillaDetailScreen(
                     villaId = id,
                     onBackClick = { navController.popBackStack() }
                 )
             }
        }

        composable("admin_transactions") {
            com.example.rumahistimewa.ui.admin.TransactionListScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home_admin") { inclusive = true }
                    }
                }
            )
        }
        composable("admin_transaction_detail/{orderId}") { backStackEntry ->
             val orderId = backStackEntry.arguments?.getString("orderId")
             if (orderId != null) {
                 com.example.rumahistimewa.ui.admin.AdminTransactionDetailScreen(
                     orderId = orderId,
                     onBackClick = { navController.popBackStack() }
                 )
             }
        }
        composable("admin_revenue") {
            com.example.rumahistimewa.ui.admin.RevenueReportScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home_admin") { inclusive = true }
                    }
                }
            )
        }
    }
}
