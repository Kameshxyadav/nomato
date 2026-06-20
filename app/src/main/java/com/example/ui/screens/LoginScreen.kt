package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NomatoViewModel
import com.example.ui.theme.NomatoRed
import com.example.ui.theme.NomatoRedLight
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900
import com.example.ui.theme.SoftGrey

@Composable
fun LoginScreen(
    viewModel: NomatoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val phone by viewModel.phoneNumber.collectAsState()
    val isOtpSent by viewModel.isOtpSent.collectAsState()
    val otpCountdown by viewModel.otpCountdown.collectAsState()
    val isVerifying by viewModel.isVerifying.collectAsState()

    var phoneInput by remember { mutableStateOf(phone) }
    var otpInput by remember { mutableStateOf("") }
    
    // Validation flags
    val isPhoneValid = phoneInput.length == 10 && phoneInput.firstOrNull() in listOf('6', '7', '8', '9')
    val isOtpValid = otpInput.length == 4

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = Color(0xFFFBFBFD)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Brand Header
            Text(
                text = "Nomato",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = NomatoRed,
                letterSpacing = (-0.5).sp
            )
            
            Text(
                text = "INDIA'S PREMIUM MEAL SERVICE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate500,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Animated Screen Switching
            if (!isOtpSent) {
                // PHONE INPUT STAGE
                Text(
                    text = "Welcome to Feasting,",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "Enter your mobile number to begin",
                    fontSize = 15.sp,
                    color = Slate500
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Mobile Number Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Country Code Prefix
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .background(SoftGrey.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .border(1.dp, SoftGrey, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+91",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Main Input field
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } && input.length <= 10) {
                                phoneInput = input
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("phone_input"),
                        placeholder = { Text("10-digit primary mobile") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "phone icon",
                                tint = Slate500
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NomatoRed,
                            unfocusedBorderColor = SoftGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (isPhoneValid) {
                            viewModel.sendOtp(phoneInput)
                            Toast.makeText(context, "Verification SMS Sent! Use code '2026'", Toast.LENGTH_LONG).show()
                        }
                    },
                    enabled = isPhoneValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("send_otp_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NomatoRed,
                        disabledContainerColor = Slate500.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Send One-Time Password",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPhoneValid) Color.White else Slate500
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "By signing up, you verify agree to our Terms of Service & Local Safety Guidelines.",
                    fontSize = 11.sp,
                    color = Slate500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // OTP VERIFICATION STAGE
                Text(
                    text = "Verify Mobile Number,",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "We sent a 4-digit verification code to +91 $phoneInput",
                    fontSize = 15.sp,
                    color = Slate500
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = otpInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 4) {
                            otpInput = input
                            if (input.length == 4) {
                                viewModel.verifyOtp(input)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("otp_input"),
                    placeholder = { Text("Enter 4-digit code (Use 2026)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "lock icon",
                            tint = Slate500
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NomatoRed,
                        unfocusedBorderColor = SoftGrey,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            otpInput = ""
                            viewModel.sendOtp(phoneInput)
                            Toast.makeText(context, "Resent code successfully!", Toast.LENGTH_SHORT).show()
                        },
                        enabled = otpCountdown == 0
                    ) {
                        Text(
                            text = "Resend Code",
                            color = if (otpCountdown == 0) NomatoRed else Slate500,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (otpCountdown > 0) {
                        Text(
                            text = "Resend in 0:${String.format("%02d", otpCountdown)}",
                            fontSize = 14.sp,
                            color = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (isOtpValid) {
                            val success = viewModel.verifyOtp(otpInput)
                            if (!success) {
                                Toast.makeText(context, "Incorrect code! Please try 2026", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = isOtpValid && !isVerifying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("verify_otp_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NomatoRed,
                        disabledContainerColor = Slate500.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Verify & Proceed",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOtpValid) Color.White else Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Edit Mobile Number",
                        color = NomatoRed,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
