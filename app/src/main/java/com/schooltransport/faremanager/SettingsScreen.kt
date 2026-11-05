
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

private val SettingsPurple = Color(0xFF6033AD)
private val SettingsDeepPurple = Color(0xFF382064)
private val SettingsSoftPurple = Color(0xFFF0E9FC)
private val SettingsBackground = Color(0xFFF8F6FC)

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onOpenFarePlans: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = SettingsDeepPurple
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsSectionHeader(
                    icon = {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = SettingsPurple
                        )
                    },
                    title = "Account Information"
                )

                Text(
                    "Email",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    user?.email ?: "Not available",
                    fontWeight = FontWeight.SemiBold,
                    color = SettingsDeepPurple
                )

                HorizontalDivider(color = Color(0xFFEDE8F4))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(
                                if (user?.isEmailVerified == true) {
                                    Color(0xFF236B43)
                                } else {
                                    Color(0xFFD94848)
                                },
                                CircleShape
                            )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (user?.isEmailVerified == true) {
                            "Email verified"
                        } else {
                            "Email not verified"
                        },
                        color = if (user?.isEmailVerified == true) {
                            Color(0xFF236B43)
                        } else {
                            Color(0xFFD94848)
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsSectionHeader(
                    icon = {
                        Icon(
                            Icons.Default.Payments,
                            contentDescription = null,
                            tint = SettingsPurple
                        )
                    },
                    title = "Payment Configurations"
                )

                Text(
                    "Manage multiple fare plans with different morning and return prices.",
                    color = Color(0xFF66616D),
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = onOpenFarePlans,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SettingsPurple
                    )
                ) {
                    Icon(
                        Icons.Default.Payments,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Open Payment Configurations")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsSectionHeader(
                    icon = {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = SettingsPurple
                        )
                    },
                    title = "Account Security"
                )

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SettingsPurple
                    )
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Reset Password")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsSectionHeader(
                    icon = {
                        Icon(
                            Icons.Default.Apps,
                            contentDescription = null,
                            tint = SettingsPurple
                        )
                    },
                    title = "Application Information"
                )

                Text(
                    "School Transport Fare Manager",
                    color = SettingsDeepPurple,
                    fontWeight = FontWeight.SemiBold
                )
                Text("Version 1.0", color = Color.Gray)
                Text("Cloud database: Firebase Firestore", color = Color.Gray)
            }
        }

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD94848)
            )
        ) {
            Icon(
                Icons.Default.Logout,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Logout",
                fontWeight = FontWeight.Bold
            )
        }

        if (message.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SettingsSoftPurple
                )
            ) {
                Text(
                    message,
                    modifier = Modifier.padding(12.dp),
                    color = SettingsDeepPurple
                )
            }
        }

        Spacer(Modifier.height(12.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            shape = RoundedCornerShape(22.dp),
            title = {
                Text(
                    "Reset Password",
                    fontWeight = FontWeight.Bold,
                    color = SettingsDeepPurple
                )
            },
            text = {
                Text(
                    "Send a password reset email to ${user?.email ?: "your account"}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val email = user?.email
                        if (!email.isNullOrBlank()) {
                            FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(email)
                                .addOnSuccessListener {
                                    message = "Password reset email sent."
                                }
                                .addOnFailureListener {
                                    message = it.message
                                        ?: "Unable to send reset email."
                                }
                        } else {
                            message = "Account email not available."
                        }
                        showResetDialog = false
                    }
                ) {
                    Text("Send Email", color = SettingsPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = SettingsPurple)
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(22.dp),
            title = {
                Text(
                    "Logout?",
                    fontWeight = FontWeight.Bold,
                    color = SettingsDeepPurple
                )
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Logout", color = Color(0xFFD94848))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel", color = SettingsPurple)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    icon: @Composable () -> Unit,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(SettingsSoftPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SettingsDeepPurple
        )
    }
}
