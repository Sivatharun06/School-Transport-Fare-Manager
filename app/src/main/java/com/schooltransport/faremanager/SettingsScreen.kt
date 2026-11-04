
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

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
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Account Information",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Email: ${user?.email ?: "Not available"}")

                Text(
                    if (user?.isEmailVerified == true) {
                        "Email verified"
                    } else {
                        "Email not verified"
                    },
                    color = if (user?.isEmailVerified == true) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Payments,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Payment Configurations",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    "Manage multiple fare plans with different morning and return prices."
                )

                Text("Total plans: Manage them from this section.")

                Button(
                    onClick = onOpenFarePlans,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Payment Configurations")
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Account Security",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset Password")
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Application Information",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("School Transport Fare Manager")
                Text("Version 1.0")
                Text("Cloud database: Firebase Firestore")
            }
        }

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Logout")
        }

        if (message.isNotBlank()) {
            Text(
                message,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Password") },
            text = {
                Text("Send a password reset email to ${user?.email ?: "your account"}?")
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
                                    message = it.message ?: "Unable to send reset email."
                                }
                        } else {
                            message = "Account email not available."
                        }
                        showResetDialog = false
                    }
                ) {
                    Text("Send Email")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout?") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
