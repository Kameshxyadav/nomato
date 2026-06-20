package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NomatoGold
import com.example.ui.theme.NomatoRed
import com.example.ui.theme.NomatoRedLight
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(0) }
    
    val onboardingPages = listOf(
        OnboardingData(
            title = "Craving Indian Spices?",
            description = "Explore top rated traditional Biryani houses, sizzling tandoori hot pots, crispy masala tiffin dosas and creamy curry curations.",
            icon = Icons.Default.Dining,
            colorScheme = listOf(NomatoRed, NomatoGold)
        ),
        OnboardingData(
            title = "Hygiene Certified Kitchens",
            description = "We partner only with premium restaurants meeting gold-standard safety checks. Fresh gourmet cooking prepared exactly with care.",
            icon = Icons.Default.Fastfood,
            colorScheme = listOf(NomatoGold, Color(0xFF2ECC71))
        ),
        OnboardingData(
            title = "Superfast Electric Delivery",
            description = "Eco-friendly EV champions deliver meals sizzle-sealed within 25 minutes. Live-map dynamic route tracking at your fingertips.",
            icon = Icons.Default.Moped,
            colorScheme = listOf(NomatoRed, Color(0xFF3498DB))
        )
    )

    val page = onboardingPages[currentPage]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFFFF6F7))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Skip Button
        TextButton(
            onClick = { onFinished() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .testTag("skip_button")
        ) {
            Text(
                text = "Skip",
                color = NomatoRed,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        // Animated Content Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            // Graphics Vector Canvas Container
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(Color.White, CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Draw decorative ambient circles
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.linearGradient(
                            listOf(page.colorScheme[0].copy(alpha = 0.15f), Color.Transparent)
                        ),
                        radius = size.width / 2f,
                        center = center
                    )
                    drawCircle(
                        color = page.colorScheme[1].copy(alpha = 0.3f),
                        radius = size.width / 2.3f,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                Icon(
                    imageVector = page.icon,
                    contentDescription = page.title,
                    tint = page.colorScheme[0],
                    modifier = Modifier.size(110.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                onboardingPages.forEachIndexed { index, _ ->
                    val isSelected = index == currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .background(
                                color = if (isSelected) NomatoRed else Slate500.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Screen Text Heading
            Text(
                text = page.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Slate900,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle Description
            Text(
                text = page.description,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Slate500,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Button Block
            Button(
                onClick = {
                    if (currentPage < onboardingPages.size - 1) {
                        currentPage++
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("onboarding_next_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NomatoRed
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (currentPage == onboardingPages.size - 1) "Get Started" else "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next icon",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

data class OnboardingData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val colorScheme: List<Color>
)
