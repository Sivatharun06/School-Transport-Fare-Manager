
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

@Composable
fun FarePlansScreen(store: TransportStore) {

    var showDialog by remember { mutableStateOf(false) }
    var editingPlan by remember { mutableStateOf<FarePlan?>(null) }
    var deletingPlan by remember { mutableStateOf<FarePlan?>(null) }

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Payment Configurations",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${store.farePlans.size} payment methods",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    editingPlan = null
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
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

        if (store.isLoading && store.farePlans.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (store.farePlans.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No payment methods yet. Add your first method.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(
                    store.farePlans,
                    key = { it.id }
                ) { plan ->

                    val assignedCount = store.children.count {
                        it.farePlanId == plan.id
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        plan.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "$assignedCount children assigned",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        editingPlan = plan
                                        showDialog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        deletingPlan = plan
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            HorizontalDivider()

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Morning Trip")
                                Text(
                                    "Rs. ${plan.morningFare}",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Return Trip")
                                Text(
                                    "Rs. ${plan.returnFare}",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                "Daily total: Rs. ${plan.morningFare + plan.returnFare}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        FarePlanDialog(
            existingPlan = editingPlan,
            onDismiss = {
                showDialog = false
                editingPlan = null
            },
            onSave = { plan ->
                store.saveFarePlan(plan)
                showDialog = false
                editingPlan = null
            }
        )
    }

    deletingPlan?.let { plan ->
        AlertDialog(
            onDismissRequest = { deletingPlan = null },
            title = { Text("Delete Payment Method?") },
            text = {
                Text(
                    "Delete '${plan.name}'? Plans assigned to children cannot be deleted."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        store.deleteFarePlan(plan.id)
                        deletingPlan = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deletingPlan = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FarePlanDialog(
    existingPlan: FarePlan?,
    onDismiss: () -> Unit,
    onSave: (FarePlan) -> Unit
) {
    var name by remember(existingPlan) {
        mutableStateOf(existingPlan?.name ?: "")
    }

    var morningFare by remember(existingPlan) {
        mutableStateOf(existingPlan?.morningFare?.toString() ?: "400")
    }

    var returnFare by remember(existingPlan) {
        mutableStateOf(existingPlan?.returnFare?.toString() ?: "400")
    }

    var error by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (existingPlan == null)
                    "Add Payment Method"
                else
                    "Edit Payment Method"
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Payment Method Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = morningFare,
                    onValueChange = { morningFare = it },
                    label = { Text("Morning Trip Amount (Rs.)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = returnFare,
                    onValueChange = { returnFare = it },
                    label = { Text("Return Trip Amount (Rs.)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.isNotBlank()) {
                    Text(
                        error,
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

                    when {
                        name.isBlank() ->
                            error = "Enter a payment method name"

                        morning == null || returning == null ->
                            error = "Enter valid amounts"

                        morning < 0 || returning < 0 ->
                            error = "Amounts cannot be negative"

                        else -> {
                            onSave(
                                FarePlan(
                                    id = existingPlan?.id ?: "",
                                    name = name.trim(),
                                    morningFare = morning,
                                    returnFare = returning,
                                    createdAt = existingPlan?.createdAt
                                        ?: System.currentTimeMillis()
                                )
                            )
                        }
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
