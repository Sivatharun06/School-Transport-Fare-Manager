
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun ProfileScreen() {

    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser

    var name by remember(user?.uid) {
        mutableStateOf(user?.displayName.orEmpty())
    }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isSavingName by remember { mutableStateOf(false) }
    var isChangingPassword by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }
    var messageIsError by remember { mutableStateOf(false) }

    fun showMessage(text: String, isError: Boolean = false) {
        message = text
        messageIsError = isError
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "My Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            "View and manage your account information.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // ACCOUNT DETAILS

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    "Account Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Badge,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.width(10.dp))

                    Column {
                        Text(
                            "User ID",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            user?.uid ?: "Not available",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        message = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSavingName,
                    singleLine = true,
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null
                        )
                    }
                )

                Button(
                    onClick = {
                        val cleanName = name.trim()

                        if (cleanName.isBlank()) {
                            showMessage("Please enter your name.", true)
                        } else if (user == null) {
                            showMessage("User account not available.", true)
                        } else {
                            isSavingName = true
                            message = ""

                            val request =
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(cleanName)
                                    .build()

                            user.updateProfile(request)
                                .addOnCompleteListener { result ->
                                    isSavingName = false

                                    if (result.isSuccessful) {
                                        name = cleanName
                                        showMessage("Name updated successfully.")
                                    } else {
                                        showMessage(
                                            result.exception?.localizedMessage
                                                ?: "Unable to update name.",
                                            true
                                        )
                                    }
                                }
                        }
                    },
                    enabled = !isSavingName && !isChangingPassword,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSavingName) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Name")
                    }
                }

                HorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.width(10.dp))

                    Column {
                        Text(
                            "Email Address",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            user?.email ?: "Not available",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // CHANGE PASSWORD

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "Change Password",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Enter your current password to verify your identity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        message = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isChangingPassword,
                    singleLine = true,
                    label = { Text("Current Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        message = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isChangingPassword,
                    singleLine = true,
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        message = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isChangingPassword,
                    singleLine = true,
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )

                Button(
                    onClick = {
                        val email = user?.email

                        when {
                            user == null || email.isNullOrBlank() -> {
                                showMessage("Account email not available.", true)
                            }

                            currentPassword.isBlank() -> {
                                showMessage("Enter your current password.", true)
                            }

                            newPassword.length < 6 -> {
                                showMessage(
                                    "New password must contain at least 6 characters.",
                                    true
                                )
                            }

                            newPassword != confirmPassword -> {
                                showMessage("New passwords do not match.", true)
                            }

                            currentPassword == newPassword -> {
                                showMessage(
                                    "New password must be different from current password.",
                                    true
                                )
                            }

                            else -> {
                                isChangingPassword = true
                                message = ""

                                val credential =
                                    EmailAuthProvider.getCredential(
                                        email,
                                        currentPassword
                                    )

                                user.reauthenticate(credential)
                                    .addOnCompleteListener { reauthResult ->

                                        if (!reauthResult.isSuccessful) {
                                            isChangingPassword = false
                                            showMessage(
                                                reauthResult.exception?.localizedMessage
                                                    ?: "Current password verification failed.",
                                                true
                                            )
                                        } else {
                                            user.updatePassword(newPassword)
                                                .addOnCompleteListener { updateResult ->
                                                    isChangingPassword = false

                                                    if (updateResult.isSuccessful) {
                                                        currentPassword = ""
                                                        newPassword = ""
                                                        confirmPassword = ""

                                                        showMessage(
                                                            "Password changed successfully."
                                                        )
                                                    } else {
                                                        showMessage(
                                                            updateResult.exception?.localizedMessage
                                                                ?: "Unable to change password.",
                                                            true
                                                        )
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                    },
                    enabled = !isChangingPassword && !isSavingName,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isChangingPassword) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Update Password")
                    }
                }
            }
        }

        if (message.isNotBlank()) {
            Text(
                text = message,
                color = if (messageIsError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
