package com.ats.epodapp.jetpack_compose.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.ats.epodapp.jetpack_compose.components.*
import com.ats.epodapp.jetpack_compose.models.DeliveryStatus
import com.ats.epodapp.jetpack_compose.models.MockDeliveries
import com.ats.epodapp.jetpack_compose.theme.AppColors
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DriverDashboardScreen(
    onViewDeliveriesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val database = FirebaseDatabase.getInstance().reference
    
    val deliveries = MockDeliveries.deliveries
    val deliveriesToday = deliveries.size
    val pending = deliveries.count { 
        it.status == DeliveryStatus.PENDING || it.status == DeliveryStatus.OUT_FOR_DELIVERY 
    }
    val completed = deliveries.count { it.status == DeliveryStatus.DELIVERED }
    
    val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    var showComplaintDialog by remember { mutableStateOf(false) }
    val complaintOptions = listOf(
        "Vehicle Breakdown",
        "Flat Tyre",
        "Engine Overheating",
        "Fuel Empty",
        "Accident",
        "Traffic Blockade",
        "Health Issues"
    )

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            val timestamp = System.currentTimeMillis()
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
            
            // 1. Get Location and Save to Database
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val lat = location?.latitude ?: 0.0
                    val lon = location?.longitude ?: 0.0
                    
                    val podEntry = mapOf(
                        "timestamp" to timestamp,
                        "date" to dateStr,
                        "latitude" to lat,
                        "longitude" to lon,
                        "localPath" to tempImageUri.toString()
                    )
                    
                    database.child("scanned_pods").push().setValue(podEntry)
                        .addOnSuccessListener {
                            Toast.makeText(context, "POD saved with location: $lat, $lon", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save to database", Toast.LENGTH_SHORT).show()
                        }
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        
        if (cameraGranted && locationGranted) {
            val uri = createTempImageUri(context)
            tempImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera and Location permissions are required", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AppColors.BackgroundGradientStart,
                        AppColors.BackgroundGradientMid.copy(alpha = 0.3f),
                        AppColors.BackgroundGradientEnd.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top App Bar
            DriverTopAppBar(title = "Driver Dashboard")

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Welcome Section
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Welcome back, John!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = currentDate,
                        fontSize = 16.sp,
                        color = AppColors.TextSecondary
                    )
                }

                // Stats Cards
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Deliveries Today Card - Featured with gradient
                    GradientCard(
                        gradient = Brush.horizontalGradient(
                            colors = listOf(AppColors.GradientBlueStart, AppColors.GradientBlueEnd)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Deliveries Today",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = deliveriesToday.toString(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        shape = MaterialTheme.shapes.medium
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Pending Deliveries Card
                    StatCard(
                        title = "Pending Deliveries",
                        value = pending.toString(),
                        icon = Icons.Default.Schedule,
                        iconBackgroundGradient = Brush.linearGradient(
                            colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                        ),
                        iconTint = AppColors.StatusPending
                    )

                    // Completed Today Card
                    StatCard(
                        title = "Completed Today",
                        value = completed.toString(),
                        icon = Icons.Default.CheckCircle,
                        iconBackgroundGradient = Brush.linearGradient(
                            colors = listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0))
                        ),
                        iconTint = AppColors.StatusDelivered
                    )
                }

                // Action Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 96.dp) // Space for chatbot
                ) {
                    GradientButton(
                        text = "View All Deliveries",
                        onClick = onViewDeliveriesClick,
                        variant = ButtonVariant.PRIMARY
                    )

                    GradientButton(
                        text = "POD Scanner",
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            )
                        },
                        variant = ButtonVariant.SUCCESS,
                        leadingIcon = Icons.Default.QrCodeScanner
                    )
                    
                    GradientButton(
                        text = "Report Transport Complaint",
                        onClick = { showComplaintDialog = true },
                        variant = ButtonVariant.DANGER,
                        leadingIcon = Icons.Default.ReportProblem
                    )

                    GradientButton(
                        text = "Logout",
                        onClick = onLogoutClick,
                        variant = ButtonVariant.SECONDARY
                    )
                }
            }
        }
        
        // AI Chatbot
        AIChatbot(
            context = ChatContext.DRIVER,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        if (showComplaintDialog) {
            AlertDialog(
                onDismissRequest = { showComplaintDialog = false },
                title = { Text("Transport Complaint") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select the reason for journey halt:")
                        complaintOptions.forEach { option ->
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Reported: $option", Toast.LENGTH_LONG).show()
                                    showComplaintDialog = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Background)
                            ) {
                                Text(option, color = AppColors.TextPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showComplaintDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun createTempImageUri(context: Context): Uri {
    val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "POD")
    if (!directory.exists()) directory.mkdirs()
    
    val tempFile = File(
        directory,
        "SCAN_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
    )
    
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBackgroundGradient: Brush,
    iconTint: Color
) {
    GradientCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = AppColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBackgroundGradient, shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
