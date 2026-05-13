package com.ats.epodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ats.epodapp.jetpack_compose.models.DeliveryStatus
import com.ats.epodapp.jetpack_compose.models.MockDeliveries
import com.ats.epodapp.jetpack_compose.screens.*
import com.ats.epodapp.jetpack_compose.theme.LogisticsDriverTheme
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        auth = FirebaseAuth.getInstance()
        
        setContent {
            LogisticsDriverTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val currentUser = auth.currentUser
                    
                    val startDestination = if (currentUser != null) "dashboard" else "login"

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("dashboard") {
                            DriverDashboardScreen(
                                onViewDeliveriesClick = {
                                    navController.navigate("delivery_list")
                                },
                                onLogoutClick = {
                                    auth.signOut()
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("delivery_list") {
                            DeliveryListScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onDeliveryClick = { deliveryId ->
                                    navController.navigate("delivery_details/$deliveryId")
                                },
                                onViewPodClick = {
                                    navController.navigate("pod_data")
                                }
                            )
                        }
                        composable("delivery_details/{deliveryId}") { backStackEntry ->
                            val deliveryId = backStackEntry.arguments?.getString("deliveryId") ?: ""
                            DeliveryDetailsScreen(
                                deliveryId = deliveryId,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onUpdateStatusClick = { id ->
                                    // Handle update status
                                },
                                onCaptureProofClick = { id ->
                                    MockDeliveries.updateDeliveryStatus(id, DeliveryStatus.DELIVERED)
                                }
                            )
                        }
                        composable("pod_data") {
                            ProofOfDeliveryDataScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
