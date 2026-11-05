
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val ChildAvatarColors = listOf(
    Color(0xFFECE2FA),
    Color(0xFFFFF0C2),
    Color(0xFFDDF2E5),
    Color(0xFFFFE4E1)
)

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
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // HEADER

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Children",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${store.children.size} registered children",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    editingChild = null
                    showDialog = true
                },
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(Modifier.width(5.dp))
                Text("Add Child")
            }
        }

        // EMPTY STATE

        if (store.children.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(66.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        "No children added yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Add a child to start managing transport fares.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            editingChild = null
                            showDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(5.dp))
                        Text("Add First Child")
                    }
                }
            }
        }

        // CHILD CARDS

        store.children.sortedBy { it.name.lowercase() }.forEachIndexed { index, child ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // CHILD PROFILE

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = ChildAvatarColors[
                                index % ChildAvatarColors.size
                            ]
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = child.name.trim()
                                        .firstOrNull()
                                        ?.uppercaseChar()
                                        ?.toString() ?: "?",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                child.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Age: ${child.age}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(4.dp))

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (child.isActive) {
                                    Color(0xFFE1F3E7)
                                } else {
                                    Color(0xFFFFE5E5)
                                }
                            ) {
                                Text(
                                    text = if (child.isActive) "Active" else "Inactive",
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 3.dp
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (child.isActive) {
                                        Color(0xFF236B43)
                                    } else {
                                        Color(0xFFB3261E)
                                    }
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                editingChild = child
                                showDialog = true
                            }
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit child",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = {
                                deleteChild = child
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
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // ROUTE

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )

                        Spacer(Modifier.width(7.dp))

                        Column {
                            Text(
                                "Transport Route",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    child.fromLocation.ifBlank { "Not set" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )

                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = "To",
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .size(15.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    child.toLocation.ifBlank { "Not set" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // PAYMENT CONFIGURATION

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )

                        Spacer(Modifier.width(7.dp))

                        Column {
                            Text(
                                "Payment Configuration",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                child.farePlanName.ifBlank { "Not assigned" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // FARE DETAILS

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.55f
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Morning",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                "Rs. ${"%.0f".format(child.morningFare)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "Return",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                "Rs. ${"%.0f".format(child.returnFare)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }

    // ADD / EDIT DIALOG

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

    // DELETE CONFIRMATION

    deleteChild?.let { child ->
        AlertDialog(
            onDismissRequest = { deleteChild = null },
            title = {
                Text("Delete Child?")
            },
            text = {
                Text(
                    "Delete ${child.name}? Existing trip and payment history will remain."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        store.deleteChild(child.id)
                        deleteChild = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deleteChild = null }
                ) {
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
    var name by remember(child?.id) {
        mutableStateOf(child?.name ?: "")
    }

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

    var expanded by remember {
        mutableStateOf(false)
    }

    val selectedPlan = store.farePlans.firstOrNull {
        it.id == selectedPlanId
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                if (child == null) "Add Child" else "Edit Child",
                fontWeight = FontWeight.Bold
            )
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
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { value ->
                        age = value.filter { it.isDigit() }.take(2)
                    },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = fromLocation,
                    onValueChange = { fromLocation = it },
                    label = { Text("From Place (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = toLocation,
                    onValueChange = { toLocation = it },
                    label = { Text("To Place (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Text(
                    "Payment Configuration",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
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
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Selected Fares",
                                fontWeight = FontWeight.Bold
                            )

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
                },
                shape = RoundedCornerShape(12.dp)
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
