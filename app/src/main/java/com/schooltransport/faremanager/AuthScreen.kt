
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

private val AuthPurple = Color(0xFF5120A0)
private val AuthDark = Color(0xFF29105E)
private val AuthGold = Color(0xFFFFC928)
private val AuthLavender = Color(0xFFF4EDFF)

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit
) {
    var isRegister by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val auth = remember { FirebaseAuth.getInstance() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF9E9),
                        AuthLavender,
                        Color.White,
                        Color(0xFFF3E7FF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = AuthPurple.copy(alpha = 0.12f),
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier.size(104.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚌", fontSize = 62.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "School Transport",
                fontSize = 29.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthDark,
                textAlign = TextAlign.Center
            )

            Text(
                "Fare Manager",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthPurple,
                textAlign = TextAlign.Center
            )

            Text(
                "Safe Rides  •  Happy Kids  •  Smart Fares",
                fontSize = 13.sp,
                color = Color(0xFF77669A),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.85f),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        if (isRegister) "Create Account" else "Welcome Back!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AuthDark
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        if (isRegister)
                            "Register to manage your school transport fares."
                        else
                            "Login to your account and manage your school transport fare details.",
                        fontSize = 14.sp,
                        color = Color(0xFF81739D)
                    )

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AuthPurple,
                            unfocusedBorderColor = Color(0xFFE3D9F3),
                            focusedLabelColor = AuthPurple
                        )
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password")
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AuthPurple,
                            unfocusedBorderColor = Color(0xFFE3D9F3),
                            focusedLabelColor = AuthPurple
                        )
                    )

                    if (!isRegister) {
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = AuthPurple
                                )
                            )

                            Text(
                                "Remember Me",
                                color = AuthDark,
                                fontSize = 13.sp
                            )

                            Spacer(Modifier.weight(1f))

                            TextButton(
                                onClick = {
                                    message = ""
                                    if (email.isBlank()) {
                                        message = "Enter your email first."
                                    } else {
                                        loading = true
                                        auth.sendPasswordResetEmail(email.trim())
                                            .addOnCompleteListener {
                                                loading = false
                                                message = if (it.isSuccessful)
                                                    "Password reset email sent."
                                                else
                                                    it.exception?.localizedMessage
                                                        ?: "Could not send reset email."
                                            }
                                    }
                                },
                                enabled = !loading
                            ) {
                                Text(
                                    "Forgot Password?",
                                    color = AuthPurple,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (message.isNotBlank()) {
                        Text(
                            message,
                            color = if (
                                message.contains("sent", ignoreCase = true)
                            ) Color(0xFF287A42)
                            else Color(0xFFB3261E),
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            message = ""

                            if (email.isBlank() || password.isBlank()) {
                                message = "Please enter email and password."
                            } else {
                                loading = true

                                val task = if (isRegister) {
                                    auth.createUserWithEmailAndPassword(
                                        email.trim(),
                                        password
                                    )
                                } else {
                                    auth.signInWithEmailAndPassword(
                                        email.trim(),
                                        password
                                    )
                                }

                                task.addOnCompleteListener { result ->
                                    loading = false

                                    if (result.isSuccessful) {
                                        onAuthenticated()
                                    } else {
                                        message = result.exception?.localizedMessage
                                            ?: "Authentication failed."
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        enabled = !loading,
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuthPurple
                        )
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                if (isRegister) "Create Account" else "➜  Login",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    HorizontalDivider(color = Color(0xFFE6DDF1))

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isRegister) "Already have an account?"
                            else "Don't have an account?",
                            color = Color(0xFF77669A),
                            fontSize = 13.sp
                        )

                        TextButton(
                            onClick = {
                                isRegister = !isRegister
                                message = ""
                            }
                        ) {
                            Text(
                                if (isRegister) "Login Here"
                                else "Register Here →",
                                color = AuthPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "Version 1.0",
                color = AuthPurple.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

