package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LiveOrderState
import com.example.data.OrderStatus
import com.example.ui.NomatoViewModel
import com.example.ui.theme.NomatoGold
import com.example.ui.theme.NomatoRed
import com.example.ui.theme.NomatoRedLight
import com.example.ui.theme.NomatoVegGreen
import com.example.ui.theme.ScreenBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SoftGrey
import kotlin.math.sin

@Composable
fun OrderTrackingScreen(
    viewModel: NomatoViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val liveOrderState by viewModel.liveOrder.collectAsState()
    val context = LocalContext.current

    if (liveOrderState == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = NomatoRed.copy(alpha = 0.3f), modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "No Active Delivery Tracking", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Slate900)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Place a new premium meal order to experience real-time high-fidelity driver GIS simulations.", color = Slate500, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = NomatoRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Go Back Home")
            }
        }
        return
    }

    val order = liveOrderState!!

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(ScreenBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // 1. Top status header bar - Geometric Balance Style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(width = 1.dp, color = Slate100, shape = RoundedCornerShape(0.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Slate100, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate900,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ORDER #NM-${order.orderId}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate500,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (order.status == OrderStatus.DELIVERED) "Delivered" else "Arriving in ${order.etaMinutes} mins",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Slate900,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Help Button
                Row(
                    modifier = Modifier
                        .border(1.dp, SoftGrey, RoundedCornerShape(20.dp))
                        .clickable {
                            Toast.makeText(context, "Contacting Nomato Support...", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Help",
                        tint = Slate700,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Help",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate700
                    )
                }
            }

            // 2. Main Stylized Interactive Map with Dot Grid background and floating pill overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .background(Color(0xFFE8EAEF))
            ) {
                // Interactive Canvas Map
                MapCanvasSimulation(
                    riderLatOffset = order.riderLatOffset,
                    riderLngOffset = order.riderLngOffset,
                    riderColor = order.riderColor,
                    status = order.status
                )

                // Floating Status Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF10B981), CircleShape) // emerald
                        )
                        val stepStatusText = when (order.status) {
                            OrderStatus.CONFIRMED -> "Rider is verifying your package details"
                            OrderStatus.PREPARING -> "Food is sealing at Biryani Hotpots"
                            OrderStatus.RIDER_ASSIGNED -> "Rider is near Indiranagar Metro"
                            OrderStatus.ON_THE_WAY -> "Rider heading to your location"
                            OrderStatus.ARRIVING_SOON -> "Rider is approaching gate!"
                            OrderStatus.DELIVERED -> "Delivered safely!"
                        }
                        Text(
                            text = stepStatusText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                    }
                }

                // Sandbox Proto Note Overlay at bottom corner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Slate700, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Sandbox tracking simulation", fontSize = 9.sp, color = Slate700)
                    }
                }
            }

            // 3. Lower Detail Dashboard panel (Geometric Bottom Sheet with beautiful border and shadows)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp)
                ) {
                    
                    // Rider details Card Row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Avatar with overlapping yellow rating badge
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .background(Slate100, RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsBike,
                                            contentDescription = null,
                                            tint = NomatoRed,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    
                                    // Rating badge overlapping at bottom right corner
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .background(Color(0xFFFACC15), RoundedCornerShape(8.dp))
                                            .border(1.5.dp, Color.White, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = String.format("%.1f", order.riderRating),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = Slate900
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Slate900,
                                                modifier = Modifier.size(8.dp)
                                            )
                                        }
                                    }
                                }

                                Column {
                                    Text(
                                        text = order.riderName,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = Slate900
                                    )
                                    Text(
                                        text = order.riderVehicle,
                                        fontSize = 12.sp,
                                        color = Slate500
                                    )
                                }
                            }

                            // Quick Action Buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color(0xFFECFDF5), RoundedCornerShape(16.dp))
                                        .clickable {
                                            Toast.makeText(context, "Directing secure call to driver...", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "call",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color(0xFFEFF6FF), RoundedCornerShape(16.dp))
                                        .clickable {
                                            Toast.makeText(context, "Opening direct chat helper...", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "chat",
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Timeline updates Segment
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Progress Nodes
                    item {
                        TimelineNode(
                            title = "Food is being prepared",
                            subtitle = "${order.restaurant.name} • Confirmed & prepping",
                            isActive = order.status == OrderStatus.PREPARING,
                            isDone = order.status >= OrderStatus.PREPARING
                        )
                        TimelineNode(
                            title = "Rider is on the way",
                            subtitle = "Heading to your location • ${order.distanceKm} km out",
                            isActive = order.status == OrderStatus.ON_THE_WAY || order.status == OrderStatus.ARRIVING_SOON,
                            isDone = order.status >= OrderStatus.ON_THE_WAY
                        )
                    }

                    // Order Details Snippet
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate100, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .border(1.dp, Slate100, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalMall,
                                        contentDescription = "order icon",
                                        tint = Slate500,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "CURRENT ORDER",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate500,
                                        letterSpacing = 0.5.sp
                                    )
                                    val itemSummary = if (order.items.isNotEmpty()) {
                                        val firstItem = order.items.first()
                                        val quantityPref = "${firstItem.quantity}x "
                                        val nameVal = firstItem.menuItem.name
                                        if (order.items.size > 1) {
                                            "$quantityPref$nameVal + ${order.items.size - 1} items"
                                        } else {
                                            "$quantityPref$nameVal"
                                        }
                                    } else {
                                        "Nomato Special Meal"
                                    }
                                    Text(
                                        text = itemSummary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate900
                                    )
                                }
                            }
                            
                            val grandTotal = order.items.sumOf { it.menuItem.price * it.quantity }
                            val totalText = if (grandTotal > 0) "₹${grandTotal.toInt()}" else "₹429"
                            Text(
                                text = totalText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        }
                    }

                    // Disclaimer centered label
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "DEMO TRACKING SIMULATION ACTIVE",
                            fontSize = 10.sp,
                            color = Slate500,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineNode(
    title: String,
    subtitle: String,
    isActive: Boolean,
    isDone: Boolean,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (isActive) Color(0xFF3B82F6) else if (isDone) Color(0xFF10B981) else Color(0xFFE2E8F0),
                        shape = CircleShape
                    )
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(if (isDone) Color(0xFF10B981) else Color(0xFFF1F5F9))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isActive || isDone) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) Color(0xFF3B82F6) else if (isDone) Slate900 else Slate500
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Slate500
            )
        }
    }
}

// Custom Draw Canvas Map representation with background Procedural Dot Grid
@Composable
fun MapCanvasSimulation(
    riderLatOffset: Float,
    riderLngOffset: Float,
    riderColor: String,
    status: OrderStatus
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Procedural Grid System (Geometric dot system)
        val gridSize = 24.dp.toPx()
        val dotRadius = 1.5.dp.toPx()
        val columns = (width / gridSize).toInt() + 1
        val rows = (height / gridSize).toInt() + 1
        for (i in 0..columns) {
            for (j in 0..rows) {
                drawCircle(
                    color = Color(0xFF94A3B8).copy(alpha = 0.2f),
                    radius = dotRadius,
                    center = Offset(i * gridSize, j * gridSize)
                )
            }
        }

        // Define Points on Map
        // Restaurant Location (Top Left-ish)
        val restX = width * 0.25f
        val restY = height * 0.3f

        // User destination (Bottom Right-ish)
        val userX = width * 0.75f
        val userY = height * 0.7f

        // Draw street background grid systems (stylized abstract map lines)
        val routeEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)

        // Segment roads grid lines
        drawLine(Color(0xFFCBD5E1).copy(alpha = 0.5f), Offset(0f, height * 0.2f), Offset(width, height * 0.2f), strokeWidth = 3f)
        drawLine(Color(0xFFCBD5E1).copy(alpha = 0.5f), Offset(0f, height * 0.5f), Offset(width, height * 0.5f), strokeWidth = 4f)
        drawLine(Color(0xFFCBD5E1).copy(alpha = 0.5f), Offset(0f, height * 0.8f), Offset(width, height * 0.8f), strokeWidth = 3f)
        
        drawLine(Color(0xFFCBD5E1).copy(alpha = 0.5f), Offset(width * 0.3f, 0f), Offset(width * 0.3f, height), strokeWidth = 3f)
        drawLine(Color(0xFFCBD5E1).copy(alpha = 0.5f), Offset(width * 0.7f, 0f), Offset(width * 0.7f, height), strokeWidth = 4f)

        // Draw main courier path route grid (Restaurant to User)
        val corner1X = width * 0.75f
        val corner1Y = height * 0.3f

        // Draw street path lines
        drawLine(Color(0xFFCBD5E1), Offset(restX, restY), Offset(corner1X, corner1Y), strokeWidth = 8f)
        drawLine(Color(0xFFCBD5E1), Offset(corner1X, corner1Y), Offset(userX, userY), strokeWidth = 8f)

        // Draw pulsing dash tracking route indicator
        drawLine(Color(0xFFF87171), Offset(restX, restY), Offset(corner1X, corner1Y), strokeWidth = 4f, pathEffect = routeEffect)
        drawLine(Color(0xFFF87171), Offset(corner1X, corner1Y), Offset(userX, userY), strokeWidth = 4f, pathEffect = routeEffect)

        // 1. Draw Restaurant Beacon Circle (Red)
        drawCircle(color = Color(0xFFEF4444).copy(alpha = 0.1f), radius = 16.dp.toPx(), center = Offset(restX, restY))
        drawCircle(color = Color(0xFFEF4444), radius = 8.dp.toPx(), center = Offset(restX, restY))

        // 2. Draw User Home Beacon (Blue)
        drawCircle(color = Color(0xFF3B82F6).copy(alpha = 0.1f), radius = 16.dp.toPx(), center = Offset(userX, userY))
        drawCircle(color = Color(0xFF3B82F6), radius = 8.dp.toPx(), center = Offset(userX, userY))

        // 3. Draw Rider Marker smoothly moving along offset
        val riderX: Float
        val riderY: Float
        if (status == OrderStatus.CONFIRMED || status == OrderStatus.PREPARING) {
            // Rider is still near restaurant
            riderX = restX
            riderY = restY
        } else if (status == OrderStatus.DELIVERED) {
            // Arrived at user
            riderX = userX
            riderY = userY
        } else {
            // Interpolate progress along path segment
            val totalSeg1 = (corner1X - restX)
            val totalSeg2 = (userY - corner1Y)
            
            // riderLatOffset moves from 0 to 1
            if (riderLatOffset < 0.5f) {
                val localProg = riderLatOffset / 0.5f
                riderX = restX + totalSeg1 * localProg
                riderY = restY
            } else {
                val localProg = (riderLatOffset - 0.5f) / 0.5f
                riderX = corner1X
                riderY = corner1Y + totalSeg2 * localProg
            }
        }

        // Draw Rider pulsing locator (Geometric Rounded Box matching SVG)
        drawCircle(color = Color(0xFF1E293B).copy(alpha = 0.15f), radius = 24.dp.toPx(), center = Offset(riderX, riderY))
        
        val rectSize = 24.dp.toPx()
        val rectTopLeft = Offset(riderX - rectSize / 2, riderY - rectSize / 2)
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = rectTopLeft,
            size = androidx.compose.ui.geometry.Size(rectSize, rectSize),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
        // Draw a pristine tiny custom arrow pointing up inside the box
        val arrowPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(riderX, riderY - 4.dp.toPx())
            lineTo(riderX - 4.dp.toPx(), riderY + 2.dp.toPx())
            lineTo(riderX + 4.dp.toPx(), riderY + 2.dp.toPx())
            close()
        }
        drawPath(path = arrowPath, color = Color.White)
    }
}
