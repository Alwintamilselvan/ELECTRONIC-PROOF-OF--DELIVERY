package com.ats.epodapp.jetpack_compose.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ats.epodapp.jetpack_compose.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val id: String,
    val text: String,
    val sender: MessageSender,
    val timestamp: Date = Date()
)

enum class MessageSender {
    USER,
    BOT
}

enum class ChatContext {
    DRIVER,
    ADMIN
}

@Composable
fun AIChatbot(
    context: ChatContext = ChatContext.DRIVER,
    modifier: Modifier = Modifier,
    onSendMessage: suspend (String) -> String = { message ->
        // Mock response
        delay(1000)
        getMockResponse(message, context)
    }
) {
    var isOpen by remember { mutableStateOf(false) }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "1",
                    text = if (context == ChatContext.DRIVER)
                        "Hi! I'm your AI assistant. I can help you with delivery instructions, navigation tips, or answer questions about your deliveries. How can I help you today?"
                    else
                        "Hello! I'm your AI assistant for logistics management. I can help you analyze delivery data, manage shipments, track driver performance, and answer operational questions. What would you like to know?",
                    sender = MessageSender.BOT
                )
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val gradient = Brush.horizontalGradient(
        colors = if (context == ChatContext.DRIVER) {
            listOf(AppColors.GradientBlueStart, AppColors.GradientBlueEnd)
        } else {
            listOf(AppColors.GradientPurpleEnd, AppColors.AccentPurple)
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 20.dp, end = 20.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Chat Window
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            Surface(
                modifier = Modifier
                    .width(360.dp)
                    .height(500.dp)
                    .shadow(16.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(gradient)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.2f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "AI Assistant",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Always here to help",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { isOpen = false }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Messages
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC))
                            .padding(16.dp),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(messages) { message ->
                            MessageBubble(message = message)
                        }

                        if (isLoading) {
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(gradient, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SmartToy,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color.White,
                                                RoundedCornerShape(
                                                    topStart = 4.dp,
                                                    topEnd = 16.dp,
                                                    bottomEnd = 16.dp,
                                                    bottomStart = 16.dp
                                                )
                                            )
                                            .border(
                                                1.dp,
                                                AppColors.Border,
                                                RoundedCornerShape(
                                                    topStart = 4.dp,
                                                    topEnd = 16.dp,
                                                    bottomEnd = 16.dp,
                                                    bottomStart = 16.dp
                                                )
                                            )
                                            .padding(12.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = AppColors.PrimaryBlue
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Input Area
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                modifier = Modifier.weight(1f),
                                placeholder = {
                                    Text(
                                        "Type your message...",
                                        fontSize = 14.sp,
                                        color = AppColors.TextMuted
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF1F5F9),
                                    unfocusedContainerColor = Color(0xFFF1F5F9),
                                    disabledContainerColor = Color(0xFFF1F5F9),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            )

                            IconButton(
                                onClick = {
                                    if (inputText.isNotBlank() && !isLoading) {
                                        val userMessage = ChatMessage(
                                            id = UUID.randomUUID().toString(),
                                            text = inputText,
                                            sender = MessageSender.USER
                                        )
                                        messages = messages + userMessage
                                        val messageToSend = inputText
                                        inputText = ""
                                        isLoading = true

                                        coroutineScope.launch {
                                            try {
                                                val response = onSendMessage(messageToSend)
                                                val botMessage = ChatMessage(
                                                    id = UUID.randomUUID().toString(),
                                                    text = response,
                                                    sender = MessageSender.BOT
                                                )
                                                messages = messages + botMessage
                                                listState.animateScrollToItem(messages.size - 1)
                                            } catch (e: Exception) {
                                                val errorMessage = ChatMessage(
                                                    id = UUID.randomUUID().toString(),
                                                    text = "Sorry, I encountered an error. Please try again.",
                                                    sender = MessageSender.BOT
                                                )
                                                messages = messages + errorMessage
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(gradient, CircleShape),
                                enabled = inputText.isNotBlank() && !isLoading
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { isOpen = !isOpen },
            modifier = Modifier.size(56.dp),
            containerColor = Color.Transparent,
            contentColor = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isOpen) Icons.Default.Close else Icons.Default.Chat,
                    contentDescription = if (isOpen) "Close chat" else "Open chat",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val timeString = timeFormat.format(message.timestamp)

    if (message.sender == MessageSender.USER) {
        // User message - right aligned
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 250.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        AppColors.GradientBlueStart,
                                        AppColors.GradientBlueEnd
                                    )
                                ),
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = 16.dp,
                                    bottomEnd = 4.dp
                                )
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = Color.White,
                            lineHeight = 20.sp
                        )
                    }
                    Text(
                        text = timeString,
                        fontSize = 12.sp,
                        color = AppColors.TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF64748B), Color(0xFF475569))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    } else {
        // Bot message - left aligned
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AppColors.GradientBlueStart, AppColors.GradientPurpleStart)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier.widthIn(max = 250.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Color.White,
                            RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 16.dp,
                                bottomEnd = 16.dp,
                                bottomStart = 16.dp
                            )
                        )
                        .border(
                            1.dp,
                            AppColors.Border,
                            RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 16.dp,
                                bottomEnd = 16.dp,
                                bottomStart = 16.dp
                            )
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        color = AppColors.TextPrimary,
                        lineHeight = 20.sp
                    )
                }
                Text(
                    text = timeString,
                    fontSize = 12.sp,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

fun getMockResponse(userMessage: String, context: ChatContext): String {
    val lowerMessage = userMessage.lowercase()

    if (context == ChatContext.DRIVER) {
        return when {
            lowerMessage.contains("delivery") || lowerMessage.contains("package") ->
                "I can see your current deliveries in the system. Would you like me to provide navigation assistance, delivery instructions, or help you update a delivery status?"
            lowerMessage.contains("navigation") || lowerMessage.contains("route") ->
                "I can help optimize your route! Based on traffic conditions, I recommend visiting the deliveries in order of proximity. Would you like me to suggest the best route?"
            lowerMessage.contains("help") || lowerMessage.contains("how") ->
                "I'm here to assist with:\n• Navigation and route optimization\n• Delivery instructions and special notes\n• Status updates and proof of delivery\n• Customer contact information\n\nWhat specific help do you need?"
            else ->
                "I understand you're asking about your deliveries. Could you please provide more details so I can assist you better?"
        }
    } else {
        return when {
            lowerMessage.contains("driver") || lowerMessage.contains("performance") ->
                "I can analyze driver performance metrics. Currently, all active drivers are performing well with an average completion rate of 94%. Would you like detailed analytics for a specific driver?"
            lowerMessage.contains("shipment") || lowerMessage.contains("delivery") ->
                "I can help you track shipments, analyze delivery patterns, and identify potential issues. What specific information are you looking for?"
            lowerMessage.contains("analytics") || lowerMessage.contains("report") ->
                "I can generate insights on delivery success rates, common delay patterns, and efficiency metrics. Would you like a summary report?"
            lowerMessage.contains("help") || lowerMessage.contains("how") ->
                "I can assist with:\n• Real-time shipment tracking\n• Driver performance analytics\n• Delivery success rate analysis\n• Route optimization insights\n• Operational reporting\n\nHow can I help you today?"
            else ->
                "I'm here to help with logistics management. Could you please specify what information or assistance you need?"
        }
    }
}
