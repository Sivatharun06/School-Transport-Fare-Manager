
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
    onLogout: () -> Unit
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    var showResetDialog by remember {
        mutableStateOf(false)
    }

    var resetMessage by remember {
        mutableStateOf("")
    }

    var resetError by remember {
        mutableStateOf(false)
    }

    var sendingReset by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        // ACCOUNT PROFILE

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(14.dp))

                Column {
                    Text(
                        "Account Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        user?.email ?: "No email available",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        if (user?.isEmailVerified == true)
                            "Email verified"
                        else
                            "Email not verified",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (user?.isEmailVerified == true)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            "Account & Security",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        SettingsItem(
            icon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            title = "Reset Password",
            subtitle = "Receive a password reset email",
            onClick = {
                resetMessage = ""
                showResetDialog = true
            }
        )

        SettingsItem(
            icon = {
                Icon(Icons.Default.Refresh, contentDescription = null)
            },
            title = "Refresh Account",
            subtitle = "Reload your account information",
            onClick = {
                auth.currentUser?.reload()
            }
        )

        Spacer(Modifier.height(18.dp))

        Text(
            "Application",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        SettingsItem(
            icon = {
                Icon(Icons.Default.Info, contentDescription = null)
            },
            title = "App Information",
            subtitle = "School Transport Fare Manager · Version 1.0",
            onClick = {
                resetMessage = "School Transport Fare Manager\nVersion 1.0"
                resetError = false
                showResetDialog = true
            }
        )

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                showLogoutDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                Icons.Default.Logout,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Your transport data is stored securely in your Firebase account.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // LOGOUT CONFIRMATION

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text("Logout?")
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        auth.signOut()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // PASSWORD RESET / APP INFORMATION

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!sendingReset) showResetDialog = false
            },
            title = {
                Text(
                    if (resetMessage.isNotBlank())
                        "Information"
                    else
                        "Reset Password"
                )
            },
            text = {
                Column {
                    if (resetMessage.isNotBlank()) {
                        Text(resetMessage)
                    } else {
                        Text(
                            "A password reset link will be sent to:\n\n${user?.email ?: "No email available"}"
                        )
                    }
                }
            },
            confirmButton = {
                if (resetMessage.isNotBlank()) {
                    TextButton(
                        onClick = {
                            showResetDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                } else {
                    Button(
                        enabled = !sendingReset && !user?.email.isNullOrBlank(),
                        onClick = {
                            val email = user?.email

                            if (!email.isNullOrBlank()) {
                                sendingReset = true

                                auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener { task ->
                                        sendingReset = false

                                        if (task.isSuccessful) {
                                            resetMessage =
                                                "Password reset email sent to $email"
                                            resetError = false
                                        } else {
                                            resetMessage =
                                                task.exception?.message
                                                    ?: "Unable to send reset email"
                                            resetError = true
                                        }
                                    }
                            }
                        }
                    ) {
                        if (sendingReset) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Send Email")
                        }
                    }
                }
            },
            dismissButton = {
                if (resetMessage.isBlank()) {
                    TextButton(
                        enabled = !sendingReset,
                        onClick = {
                            showResetDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
private fun SettingsItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
