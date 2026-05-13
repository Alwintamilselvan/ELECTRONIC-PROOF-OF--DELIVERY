package com.ats.epodapp.jetpack_compose.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.ats.epodapp.jetpack_compose.components.*
import com.ats.epodapp.jetpack_compose.models.Delivery
import com.ats.epodapp.jetpack_compose.models.DeliveryStatus
import com.ats.epodapp.jetpack_compose.models.MockDeliveries
import com.ats.epodapp.jetpack_compose.theme.AppColors
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProofOfDeliveryDataScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val deliveredItems = MockDeliveries.deliveries.filter { it.status == DeliveryStatus.DELIVERED }
    
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedImageUri = tempImageUri
            Toast.makeText(context, "POD photo captured successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            val uri = createTempImageUri(context)
            tempImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
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
        Column(modifier = Modifier.fillMaxSize()) {
            DriverTopAppBar(title = "Proof of Delivery Data", onBackClick = onBackClick)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Delivered Shipments",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }

                if (deliveredItems.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No delivered items found", color = AppColors.TextMuted)
                        }
                    }
                } else {
                    items(deliveredItems) { delivery ->
                        PODItemCard(delivery = delivery)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Camera Access Model",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    
                    GradientCard(modifier = Modifier.padding(top = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            if (capturedImageUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(capturedImageUri),
                                    contentDescription = "Captured POD",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            GradientButton(
                                text = "Test POD Camera",
                                onClick = {
                                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                                },
                                leadingIcon = Icons.Default.CameraAlt
                            )
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun PODItemCard(delivery: Delivery) {
    GradientCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = delivery.customerName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = "${delivery.address}, ${delivery.city}",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary
                )
                Text(
                    text = "ID: ${delivery.packageId}",
                    fontSize = 12.sp,
                    color = AppColors.TextMuted
                )
            }
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AppColors.StatusDelivered,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun createTempImageUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "POD_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}_",
        ".jpg",
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
