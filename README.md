# LogiTrack - Electronic Proof of Delivery (ePOD) App

LogiTrack is a modern, Jetpack Compose-based Android application designed for delivery drivers to manage their shipments, capture proof of delivery with geo-tagging, and maintain real-time communication with management.

## 🚀 Technical Stack

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
*   **Backend**: 
    *   **Firebase Authentication**: Secure driver sign-in.
    *   **Firebase Realtime Database**: Real-time status updates and POD metadata tracking.
    *   **Firebase Storage**: Secure storage for Proof of Delivery (POD) images.
*   **Location Services**: Google Play Services Location for precise geo-tagging and tracking.
*   **Maps**: [OSMDroid](https://github.com/osmdroid/osmdroid) for open-source map integration.
*   **AI Integration**: Custom AI Chatbot assistant for drivers.
*   **Image Loading**: [Coil](https://coil-kt.github.io/coil/) for asynchronous image processing.
*   **Navigation**: Jetpack Navigation Compose.

## ✨ Key Features

*   **Driver Dashboard**: Real-time statistics including today's total deliveries, pending tasks, and completion progress.
*   **Tamil Nadu Logistics Focus**: Pre-configured with major industrial hubs like Sriperumbudur, Oragadam, and Ennore Port.
*   **Functional POD Scanner**: Dedicated dashboard tool to scan/capture delivery documents with automated GPS and timestamp logging.
*   **Geo-Tagged Proof**: Capture delivery photos that are automatically tagged with coordinates and synced to the cloud.
*   **Transport Complaint System**: Quick-report feature for drivers to notify management about vehicle breakdowns, traffic, or health issues.
*   **AI Assistant**: In-app chatbot providing instant help with navigation and delivery instructions.
*   **POD History**: A dedicated view to track and verify all successfully delivered items with their captured proof.




