
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private val FarePurple = Color(0xFF6033AD)
private val FareDeepPurple = Color(0xFF382064)
private val FareSoftPurple = Color(0xFFF0E9FC)
private val FareGold = Color(0xFFFFC928)
private val FareBackground = Color(0xFFF8F6FC)

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
            .background(FareBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Payment Configurations",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FareDeepPurple
                )
                Text(
                    "${store.farePlans.size} payment methods",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    store.clearMessages()
                    editingPlan = null
                    showDialog = true
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FarePurple
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))

        store.errorMessage?.let {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEAEA)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    it,
                    modifier = Modifier.padding(12.dp),
                    color = Color(0xFFB3261E)
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        store.successMessage?.let {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE5F5E9)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    it,
                    modifier = Modifier.padding(12.dp),
                    color = Color(0xFF236B43)
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        if (store.isLoading && store.farePlans.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FarePurple)
            }
        } else if (store.farePlans.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Payments,
                            contentDescription = null,
                            tint = FarePurple,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No payment configurations",
                            fontWeight = FontWeight.Bold,
                            color = FareDeepPurple
                        )
                        Text(
                            "Add your first payment configuration.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
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
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(
                                            FareSoftPurple,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = FarePurple
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(Modifier.weight(1f)) {
                                    Text(
                                        plan.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FareDeepPurple
                                    )
                                    Text(
                                        "$assignedCount children assigned",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        store.clearMessages()
                                        editingPlan = plan
                                        showDialog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = FarePurple
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        store.clearMessages()
                                        deletingPlan = plan
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFD94848)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0xFFEDE8F4))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FareAmountCard(
                                    title = "Morning Trip",
                                    amount = plan.morningFare
                                )
                                FareAmountCard(
                                    title = "Return Trip",
                                    amount = plan.returnFare
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        FareSoftPurple,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(13.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Daily Total",
                                    color = FareDeepPurple,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Rs. ${plan.morningFare + plan.returnFare}",
                                    color = FarePurple,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
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

        val assignedCount = store.children.count {
            it.farePlanId == plan.id
        }

        AlertDialog(
            onDismissRequest = {
                deletingPlan = null
            },
            shape = RoundedCornerShape(22.dp),
            title = {
                Text(
                    "Delete Payment Configuration?",
                    fontWeight = FontWeight.Bold,
                    color = FareDeepPurple
                )
            },
            text = {
                if (assignedCount > 0) {
                    Text(
                        "This configuration is assigned to $assignedCount children. " +
                            "Change their payment configuration before deleting this plan."
                    )
                } else {
                    Text("Delete '${plan.name}'? This action cannot be undone.")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (assignedCount == 0) {
                            store.deleteFarePlan(plan.id)
                        }
                        deletingPlan = null
                    },
                    enabled = assignedCount == 0
                ) {
                    Text(
                        "Delete",
                        color = if (assignedCount == 0) {
                            Color(0xFFD94848)
                        } else {
                            Color.Gray
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deletingPlan = null }
                ) {
                    Text("Cancel", color = FarePurple)
                }
            }
        )
    }
}

@Composable
private fun FareAmountCard(
    title: String,
    amount: Double
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .background(
                Color(0xFFFFF8E5),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            title,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(5.dp))
        Text(
            "Rs. $amount",
            color = FareDeepPurple,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                if (existingPlan == null) {
                    "Add Payment Configuration"
                } else {
                    "Edit Payment Configuration"
                },
                fontWeight = FontWeight.Bold,
                color = FareDeepPurple
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 430.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        error = ""
                    },
                    label = { Text("Payment Method Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = morningFare,
                    onValueChange = {
                        morningFare = it.filter { char ->
                            char.isDigit() || char == '.'
                        }
                        error = ""
                    },
                    label = { Text("Morning Trip Amount (Rs.)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = returnFare,
                    onValueChange = {
                        returnFare = it.filter { char ->
                            char.isDigit() || char == '.'
                        }
                        error = ""
                    },
                    label = { Text("Return Trip Amount (Rs.)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
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

                        morning == null || returning == null ||
                            !morning.isFinite() || !returning.isFinite() ->
                            error = "Enter valid fare amounts"

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
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FarePurple
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = FarePurple)
            }
        }
    )
}
