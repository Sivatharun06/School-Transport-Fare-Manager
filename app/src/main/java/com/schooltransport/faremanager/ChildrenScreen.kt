
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChildrenScreen(store: TransportStore) {
    var showDialog by remember { mutableStateOf(false) }
    var editingChild by remember { mutableStateOf<TransportChild?>(null) }
    var deleteChild by remember { mutableStateOf<TransportChild?>(null) }

    LaunchedEffect(Unit) {
        store.loadAll()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Children", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = {
                editingChild = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add Child")
            }
        }

        Text(
            "Registered children: ${store.children.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        if (store.children.isEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("No children added yet.")
                    Spacer(Modifier.height(6.dp))
                    Text("Add a child to start managing transport fares.")
                }
            }
        }

        store.children.sortedBy { it.name.lowercase() }.forEach { child ->
            Card(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                child.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Age: ${child.age}")
                        }

                        Row {
                            IconButton(onClick = {
                                editingChild = child
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                deleteChild = child
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }

                    if (child.fromLocation.isNotBlank() ||
                        child.toLocation.isNotBlank()
                    ) {
                        Text(
                            "Route: ${child.fromLocation.ifBlank { "Not set" }} → " +
                                child.toLocation.ifBlank { "Not set" }
                        )
                    }

                    Text(
                        "Payment Configuration: " +
                            child.farePlanName.ifBlank { "Not assigned" }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Morning: Rs. ${child.morningFare}")
                        Text("Return: Rs. ${child.returnFare}")
                    }

                    Text(
                        if (child.isActive) "Active" else "Inactive",
                        color = if (child.isActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        ChildDialog(
            store = store,
            child = editingChild,
            onDismiss = { showDialog = false },
            onSave = { child ->
                store.saveChild(child)
                showDialog = false
            }
        )
    }

    deleteChild?.let { child ->
        AlertDialog(
            onDismissRequest = { deleteChild = null },
            title = { Text("Delete Child?") },
            text = {
                Text("Delete ${child.name}? Existing trip and payment history will remain.")
            },
            confirmButton = {
                TextButton(onClick = {
                    store.deleteChild(child.id)
                    deleteChild = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteChild = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDialog(
    store: TransportStore,
    child: TransportChild?,
    onDismiss: () -> Unit,
    onSave: (TransportChild) -> Unit
) {
    var name by remember(child?.id) { mutableStateOf(child?.name ?: "") }
    var age by remember(child?.id) {
        mutableStateOf(if (child == null) "" else child.age.toString())
    }
    var fromLocation by remember(child?.id) {
        mutableStateOf(child?.fromLocation ?: "")
    }
    var toLocation by remember(child?.id) {
        mutableStateOf(child?.toLocation ?: "")
    }

    var selectedPlanId by remember(child?.id) {
        mutableStateOf(child?.farePlanId ?: "")
    }

    var expanded by remember { mutableStateOf(false) }

    val selectedPlan = store.farePlans.firstOrNull {
        it.id == selectedPlanId
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (child == null) "Add Child" else "Edit Child")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Child Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { value ->
                        age = value.filter { it.isDigit() }.take(2)
                    },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = fromLocation,
                    onValueChange = { fromLocation = it },
                    label = { Text("From Place (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = toLocation,
                    onValueChange = { toLocation = it },
                    label = { Text("To Place (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    "Payment Configuration",
                    style = MaterialTheme.typography.titleSmall
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedPlan?.name
                            ?: if (selectedPlanId.isBlank()) {
                                "Select Payment Configuration"
                            } else {
                                "Previously assigned plan"
                            },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fare Plan") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        store.farePlans.forEach { plan ->
                            DropdownMenuItem(
                                text = { Text(plan.name) },
                                onClick = {
                                    selectedPlanId = plan.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (store.farePlans.isEmpty()) {
                    Text(
                        "No configurations available. Create one from Payment Configurations in Home or Settings.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                selectedPlan?.let { plan ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Selected fares")
                            Text("Morning: Rs. ${plan.morningFare}")
                            Text("Return: Rs. ${plan.returnFare}")
                        }
                    }
                }

                if (child != null && selectedPlanId.isBlank()) {
                    Text(
                        "No new plan selected. Existing fare values will be kept.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = name.isNotBlank() &&
                    age.toIntOrNull() != null &&
                    (selectedPlan != null || child != null),
                onClick = {
                    val updatedChild = (child ?: TransportChild()).copy(
                        name = name.trim(),
                        age = age.toInt(),
                        fromLocation = fromLocation.trim(),
                        toLocation = toLocation.trim(),
                        farePlanId = selectedPlan?.id
                            ?: child?.farePlanId.orEmpty(),
                        farePlanName = selectedPlan?.name
                            ?: child?.farePlanName.orEmpty(),
                        morningFare = selectedPlan?.morningFare
                            ?: child?.morningFare ?: 400.0,
                        returnFare = selectedPlan?.returnFare
                            ?: child?.returnFare ?: 400.0
                    )
                    onSave(updatedChild)
                }
            ) {
                Text("Save Child")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
