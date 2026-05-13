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
import androidx.compose.runtime.saveable.rememberSaveable
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DeliveryDetailsScreen(
    deliveryId: String,
    onBackClick: () -> Unit,
    onUpdateStatusClick: (String) -> Unit,
    onCaptureProofClick: (String) -> Unit
) {
    val context = LocalContext.current
    var tempImageUriString by rememberSaveable { mutableStateOf<String?>(null) }

    // Launcher for Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Toast.makeText(context, "Proof of Delivery captured!", Toast.LENGTH_SHORT).show()
            onCaptureProofClick(deliveryId)
        } else {
            Toast.makeText(context, "Camera capture cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher for Permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        
        if (cameraGranted) {
            try {
                val uri = createTempImageUri(context)
                tempImageUriString = uri.toString()
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Error creating file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    // Find delivery by ID
    val delivery = MockDeliveries.deliveries.find { it.id == deliveryId }
        ?: return // Handle not found

    val dateFormat = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(delivery.deliveryDate)

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
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            DriverTopAppBar(
                title = "Delivery Details",
                onBackClick = onBackClick
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Status Card
                GradientCard(
                    gradient = when (delivery.status) {
                        DeliveryStatus.DELIVERED -> Brush.horizontalGradient(
                            colors = listOf(AppColors.GradientGreenStart, AppColors.GradientGreenEnd)
                        )
                        DeliveryStatus.OUT_FOR_DELIVERY -> Brush.horizontalGradient(
                            colors = listOf(AppColors.GradientBlueStart, AppColors.GradientBlueEnd)
                        )
                        DeliveryStatus.FAILED -> Brush.horizontalGradient(
                            colors = listOf(AppColors.StatusFailed, AppColors.StatusFailedLight)
                        )
                        else -> Brush.horizontalGradient(
                            colors = listOf(AppColors.StatusPending, AppColors.StatusPendingLight)
                        )
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (delivery.status) {
                                DeliveryStatus.DELIVERED -> Icons.Default.CheckCircle
                                DeliveryStatus.OUT_FOR_DELIVERY -> Icons.Default.LocalShipping
                                DeliveryStatus.FAILED -> Icons.Default.Cancel
                                else -> Icons.Default.Schedule
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = when (delivery.status) {
                                DeliveryStatus.DELIVERED -> "Delivered"
                                DeliveryStatus.OUT_FOR_DELIVERY -> "Out for Delivery"
                                DeliveryStatus.FAILED -> "Failed"
                                else -> "Pending"
                            },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                // Package Info Card
                InfoCard(
                    title = "Package Information",
                    items = listOf(
                        InfoItem(Icons.Default.Inventory, "Package ID", delivery.packageId),
                        InfoItem(Icons.Default.CalendarToday, "Delivery Date", formattedDate)
                    )
                )

                // Customer Info Card
                InfoCard(
                    title = "Customer Information",
                    items = listOf(
                        InfoItem(Icons.Default.Person, "Customer Name", delivery.customerName),
                        InfoItem(Icons.Default.Phone, "Contact Number", delivery.contactNumber),
                        InfoItem(
                            Icons.Default.LocationOn, 
                            "Delivery Address", 
                            "${delivery.address}, ${delivery.city}, ${delivery.zipCode}"
                        )
                    )
                )

                // Action Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 96.dp) // Space for chatbot
                ) {
                    if (delivery.status != DeliveryStatus.DELIVERED) {
                        GradientButton(
                            text = "Update Status",
                            onClick = { 
                                // Make Update Status functional by setting it to OUT_FOR_DELIVERY first
                                onUpdateStatusClick(delivery.id) 
                                if (delivery.status == DeliveryStatus.PENDING) {
                                    MockDeliveries.updateDeliveryStatus(delivery.id, DeliveryStatus.OUT_FOR_DELIVERY)
                                    Toast.makeText(context, "Status updated to Out for Delivery", Toast.LENGTH_SHORT).show()
                                }
                            },
                            variant = ButtonVariant.PRIMARY
                        )
                    }

                    if (delivery.status == DeliveryStatus.OUT_FOR_DELIVERY) {
                        GradientButton(
                            text = "Capture Proof of Delivery",
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(Manifest.permission.CAMERA)
                                )
                            },
                            variant = ButtonVariant.SUCCESS,
                            leadingIcon = Icons.Default.CameraAlt
                        )
                    }

                    // Navigate Button
                    GradientButton(
                        text = "Navigate to Address",
                        onClick = { /* Open maps */ },
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
    }
}

private fun createTempImageUri(context: Context): Uri {
    val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "POD")
    if (!directory.exists()) directory.mkdirs()
    
    val tempFile = File(
        directory,
        "POD_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}

@Composable
fun InfoCard(
    title: String,
    items: List<InfoItem>
) {
    GradientCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )

            HorizontalDivider(color = AppColors.Border, thickness = 1.dp)

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items.forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.label,
                                fontSize = 14.sp,
                                color = AppColors.TextSecondary
                            )
                            Text(
                                text = item.value,
                                fontSize = 16.sp,
                                color = AppColors.TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

data class InfoItem(
    val icon: ImageVector,
    val label: String,
    val value: String
)
