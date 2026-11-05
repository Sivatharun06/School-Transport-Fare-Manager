
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

private val ProfilePurple = Color(0xFF6033AD)
private val ProfileDeepPurple = Color(0xFF382064)
private val ProfileSoftPurple = Color(0xFFF0E9FC)
private val ProfileBackground = Color(0xFFF8F6FC)

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
            .background(ProfileBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "My Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ProfileDeepPurple
            )
            Text(
                "View and manage your account information.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ProfileSectionHeader(
                    icon = {
                        Icon(
                            Icons.Default.Badge,
                            contentDescription = null,
                            tint = ProfilePurple
                        )
                    },
                    title = "Account Information"
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileIcon(Icons.Default.Badge)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "User ID",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            user?.uid ?: "Not available",
                            style = MaterialTheme.typography.bodySmall,
                            color = ProfileDeepPurple
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFEDE8F4))

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
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    shape = RoundedCornerShape(12.dp)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProfilePurple
                    )
                ) {
                    if (isSavingName) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Save Name", fontWeight = FontWeight.SemiBold)
                    }
                }

                HorizontalDivider(color = Color(0xFFEDE8F4))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileIcon(Icons.Default.Email)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Email Address",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            user?.email ?: "Not available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ProfileDeepPurple
                        )
                    }
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
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileSectionHeader(
                    icon = {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = ProfilePurple
                        )
                    },
                    title = "Change Password"
                )

                Text(
                    "Enter your current password to verify your identity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
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
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProfilePurple
                    )
                ) {
                    if (isChangingPassword) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Update Password", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (message.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (messageIsError) {
                        Color(0xFFFFEAEA)
                    } else {
                        Color(0xFFE5F5E9)
                    }
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(12.dp),
                    color = if (messageIsError) {
                        Color(0xFFB3261E)
                    } else {
                        Color(0xFF236B43)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ProfileIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(ProfileSoftPurple, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = ProfilePurple
        )
    }
}

@Composable
private fun ProfileSectionHeader(
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
                .background(ProfileSoftPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ProfileDeepPurple
        )
    }
}
