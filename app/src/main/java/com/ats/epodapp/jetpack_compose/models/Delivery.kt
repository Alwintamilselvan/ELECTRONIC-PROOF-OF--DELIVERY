package com.ats.epodapp.jetpack_compose.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Date

enum class DeliveryStatus {
    PENDING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED
}

data class Delivery(
    val id: String,
    val packageId: String,
    val customerName: String,
    val address: String,
    val city: String,
    val zipCode: String,
    val contactNumber: String,
    val status: DeliveryStatus,
    val deliveryDate: Date,
    val specialInstructions: String? = null,
    val assignedDriver: String? = null
)

object MockDeliveries {
    var deliveries by mutableStateOf(
        listOf(
            Delivery(
                id = "1",
                packageId = "PKG-TN-001",
                customerName = "Arun Kumar",
                address = "SIPCOT Industrial Park",
                city = "Sriperumbudur",
                zipCode = "602105",
                contactNumber = "+91 98765 43210",
                status = DeliveryStatus.PENDING,
                deliveryDate = Date(),
                specialInstructions = "Deliver to Warehouse A"
            ),
            Delivery(
                id = "2",
                packageId = "PKG-TN-002",
                customerName = "Priya Dharshini",
                address = "Oragadam Industrial Corridor",
                city = "Oragadam",
                zipCode = "603109",
                contactNumber = "+91 98765 43211",
                status = DeliveryStatus.OUT_FOR_DELIVERY,
                deliveryDate = Date(),
                specialInstructions = "Near Apollo Tyres Factory"
            ),
            Delivery(
                id = "3",
                packageId = "PKG-TN-003",
                customerName = "Senthil Nathan",
                address = "Ennore Port Road",
                city = "Ennore",
                zipCode = "600057",
                contactNumber = "+91 98765 43212",
                status = DeliveryStatus.DELIVERED,
                deliveryDate = Date(),
                specialInstructions = null
            ),
            Delivery(
                id = "4",
                packageId = "PKG-TN-004",
                customerName = "Meenakshi Sundaram",
                address = "Kamarajar Port Limited",
                city = "Ennore",
                zipCode = "600120",
                contactNumber = "+91 98765 43213",
                status = DeliveryStatus.PENDING,
                deliveryDate = Date(),
                specialInstructions = "Contact Port Security"
            )
        )
    )

    fun updateDeliveryStatus(id: String, newStatus: DeliveryStatus) {
        deliveries = deliveries.map {
            if (it.id == id) it.copy(status = newStatus) else it
        }
    }
}
