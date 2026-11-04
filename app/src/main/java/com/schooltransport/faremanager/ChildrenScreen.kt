
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.foundation.verticalScroll

@Composable
fun ChildrenScreen(store: TransportStore) {

    var showDialog by remember { mutableStateOf(false) }
    var editingChild by remember {
        mutableStateOf<TransportChild?>(null)
    }

    LaunchedEffect(Unit) {
        store.loadAll()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Children",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${store.children.size} children registered",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalButton(
                onClick = {
                    editingChild = null
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(Modifier.width(4.dp))
                Text("Add")
            }
        }

        Spacer(Modifier.height(12.dp))

        store.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        store.successMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.primary)
        }

        if (store.isLoading && store.children.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (store.children.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No children added yet.\nTap Add to register a child.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(
                    items = store.children,
                    key = { it.id }
                ) { child ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        child.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Parent: ${child.parentName}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        editingChild = child
                                        showDialog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit child"
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        store.deleteChild(child.id)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete child",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Text("Phone: ${child.phone}")
                            Text("Route: ${child.fromLocation} → ${child.toLocation}")
                            Text(
                                "Morning: Rs. ${child.morningFare.toInt()}  |  Return: Rs. ${child.returnFare.toInt()}",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ChildFormDialog(
            existingChild = editingChild,
            onDismiss = {
                showDialog = false
                editingChild = null
            },
            onSave = { child ->
                store.saveChild(child)
                showDialog = false
                editingChild = null
            }
        )
    }
}

@Composable
private fun ChildFormDialog(
    existingChild: TransportChild?,
    onDismiss: () -> Unit,
    onSave: (TransportChild) -> Unit
) {
    var name by remember(existingChild) {
        mutableStateOf(existingChild?.name ?: "")
    }
    var parentName by remember(existingChild) {
        mutableStateOf(existingChild?.parentName ?: "")
    }
    var phone by remember(existingChild) {
        mutableStateOf(existingChild?.phone ?: "")
    }
    var fromLocation by remember(existingChild) {
        mutableStateOf(existingChild?.fromLocation ?: "")
    }
    var toLocation by remember(existingChild) {
        mutableStateOf(existingChild?.toLocation ?: "")
    }
    var morningFare by remember(existingChild) {
        mutableStateOf(existingChild?.morningFare?.toString() ?: "400")
    }
    var returnFare by remember(existingChild) {
        mutableStateOf(existingChild?.returnFare?.toString() ?: "400")
    }

    var validationError by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existingChild == null) "Add Child" else "Edit Child")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.foundation.rememberScrollState().let { scrollState ->
                    Column(
                        modifier = Modifier.verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Child name *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = parentName,
                            onValueChange = { parentName = it },
                            label = { Text("Parent name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone number") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = fromLocation,
                            onValueChange = { fromLocation = it },
                            label = { Text("Pickup location") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = toLocation,
                            onValueChange = { toLocation = it },
                            label = { Text("School / Destination") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = morningFare,
                            onValueChange = { morningFare = it },
                            label = { Text("Morning fare (Rs.)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = returnFare,
                            onValueChange = { returnFare = it },
                            label = { Text("Return fare (Rs.)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (validationError.isNotBlank()) {
                    Text(
                        validationError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val morning = morningFare.toDoubleOrNull()
                    val returning = returnFare.toDoubleOrNull()

                    if (name.isBlank()) {
                        validationError = "Enter the child's name"
                    } else if (morning == null || returning == null ||
                        morning < 0 || returning < 0
                    ) {
                        validationError = "Enter valid fare amounts"
                    } else {
                        onSave(
                            TransportChild(
                                id = existingChild?.id ?: "",
                                name = name.trim(),
                                parentName = parentName.trim(),
                                phone = phone.trim(),
                                fromLocation = fromLocation.trim(),
                                toLocation = toLocation.trim(),
                                morningFare = morning,
                                returnFare = returning,
                                isActive = existingChild?.isActive ?: true,
                                createdAt = existingChild?.createdAt
                                    ?: System.currentTimeMillis()
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
