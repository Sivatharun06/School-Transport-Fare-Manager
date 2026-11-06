
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val HomeRoyalPurple = Color(0xFF32105F)
private val HomeDeepPurple = Color(0xFF241044)
private val HomePurple = Color(0xFF55258A)
private val HomeGold = Color(0xFFFFD45A)
private val HomeDarkGold = Color(0xFF9A6B13)
private val HomeCream = Color(0xFFFFF8E8)
private val HomeWhite = Color(0xFFFFFFFF)

// GREEN PALETTE - PAID
private val HomePaidGreen = Color(0xFF16833B)
private val HomePaidDarkGreen = Color(0xFF075B28)
private val HomePaidLightGreen = Color(0xFFE8F8E9)

// YELLOW PALETTE - BALANCE
private val HomeBalanceYellow = Color(0xFFFFC928)
private val HomeBalanceDark = Color(0xFF8A4B08)
private val HomeBalanceLight = Color(0xFFFFF5D5)

// HOME BACKGROUND: 10% SOFT ROYAL PINK + 10% SOFT ROYAL PURPLE
private val HomeSoftRoyalPink =
    lerp(Color.White, Color(0xFFE8AFC5), 0.10f)

private val HomeSoftRoyalPurple =
    lerp(Color.White, Color(0xFF8D6BB5), 0.10f)

@Composable
fun HomeScreen(
    store: TransportStore,
    onOpenFarePlans: () -> Unit,
    onOpenTrips: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val today = remember { Calendar.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser

    val profileName = currentUser?.displayName
        ?.takeIf { it.isNotBlank() }
        ?: currentUser?.email?.substringBefore("@")
            ?.takeIf { it.isNotBlank() }
        ?: "there"

    var selectedMonth by remember {
        mutableIntStateOf(today.get(Calendar.MONTH))
    }

    var selectedYear by remember {
        mutableIntStateOf(today.get(Calendar.YEAR))
    }

    var showMonthPicker by remember { mutableStateOf(false) }
    var showChildDialog by remember { mutableStateOf(false) }
    var showTripDialog by remember { mutableStateOf(false) }

    val monthNames = remember {
        listOf(
            "January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"
        )
    }

    val monthKey = remember(selectedMonth, selectedYear) {
        String.format(
            Locale.US,
            "%04d-%02d",
            selectedYear,
            selectedMonth + 1
        )
    }

    val monthName = remember(selectedMonth, selectedYear) {
        SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(
            Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, 1)
            }.time
        )
    }

    LaunchedEffect(Unit) {
        store.loadAll()
    }

    val summary = store.monthlySummary(monthKey)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        HomeSoftRoyalPink,
                        HomeSoftRoyalPurple
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // PREMIUM HEADER - EXISTING THEME UNCHANGED

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(25.dp),
                    ambientColor = HomeDeepPurple.copy(alpha = 0.25f),
                    spotColor = HomeDeepPurple.copy(alpha = 0.35f)
                ),
            shape = RoundedCornerShape(25.dp),
            color = HomeRoyalPurple
        ) {
            Row(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                HomeDeepPurple,
                                HomeRoyalPurple,
                                HomePurple
                            )
                        )
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 21.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "School Transport",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = HomeWhite
                    )

                    Text(
                        text = "Fare Manager",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HomeGold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .shadow(5.dp, CircleShape)
                        .clip(CircleShape)
                        .background(HomeCream)
                        .border(2.dp, HomeGold, CircleShape)
                        .clickable { onOpenProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Open Profile",
                        modifier = Modifier.size(39.dp),
                        tint = HomeRoyalPurple
                    )
                }
            }
        }

        // WELCOME MESSAGE - REDUCED SIZE

        Text(
            text = "Welcome, $profileName to your Dashboard",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = HomeDeepPurple
        )

        // MONTH SELECTOR - WHITE TAB

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = HomeGold.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(22.dp)
                ),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Box(
                    modifier = Modifier
                        .size(43.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(HomeWhite)
                        .clickable {
                            val calendar = Calendar.getInstance().apply {
                                set(selectedYear, selectedMonth, 1)
                                add(Calendar.MONTH, -1)
                            }
                            selectedMonth = calendar.get(Calendar.MONTH)
                            selectedYear = calendar.get(Calendar.YEAR)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous month",
                        tint = HomeRoyalPurple,
                        modifier = Modifier.size(29.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showMonthPicker = true }
                        .padding(horizontal = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Choose month",
                        tint = HomeRoyalPurple,
                        modifier = Modifier.size(27.dp)
                    )

                    Text(
                        text = monthName,
                        color = HomeRoyalPurple,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    modifier = Modifier
                        .size(43.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(HomeWhite)
                        .clickable {
                            val calendar = Calendar.getInstance().apply {
                                set(selectedYear, selectedMonth, 1)
                                add(Calendar.MONTH, 1)
                            }
                            selectedMonth = calendar.get(Calendar.MONTH)
                            selectedYear = calendar.get(Calendar.YEAR)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next month",
                        tint = HomeRoyalPurple,
                        modifier = Modifier.size(29.dp)
                    )
                }
            }
        }

        // MONTH PICKER - 3 x 4 GRID

        if (showMonthPicker) {
            AlertDialog(
                onDismissRequest = { showMonthPicker = false },
                containerColor = HomeDeepPurple,
                shape = RoundedCornerShape(25.dp),
                title = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            "Select Month",
                            color = HomeWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            selectedYear.toString(),
                            color = HomeGold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { selectedYear-- }
                            ) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Previous year",
                                    tint = HomeGold
                                )
                                Text("Year", color = HomeGold)
                            }

                            TextButton(
                                onClick = { selectedYear++ }
                            ) {
                                Text("Year", color = HomeGold)
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Next year",
                                    tint = HomeGold
                                )
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(9.dp),
                            userScrollEnabled = false
                        ) {
                            items(monthNames) { month ->
                                val monthIndex = monthNames.indexOf(month)
                                val isSelected = monthIndex == selectedMonth

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) HomeGold
                                            else HomePurple
                                        )
                                        .border(
                                            1.dp,
                                            HomeGold.copy(
                                                alpha = if (isSelected) 1f else 0.55f
                                            ),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedMonth = monthIndex
                                            showMonthPicker = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = month.take(3),
                                        color = if (isSelected) {
                                            HomeDeepPurple
                                        } else {
                                            HomeWhite
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showMonthPicker = false }
                    ) {
                        Text("Close", color = HomeGold)
                    }
                }
            )
        }

        // MONTHLY TOTAL - OPPOSITE GRADIENT + GOLD BORDER
        // BUS IMAGE CAN BE ADDED LATER

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = HomeGold,
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                HomePurple,
                                HomeRoyalPurple,
                                HomeDeepPurple
                            )
                        )
                    )
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "MONTHLY TOTAL",
                    color = HomeWhite,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Rs. ${"%.2f".format(Locale.US, summary.totalFare)}",
                    color = HomeWhite,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Total transportation fees of this month",
                    color = HomeCream,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // PAID AND BALANCE CARDS

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // PAID - GREEN PALETTE

            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.5.dp,
                        color = HomePaidGreen,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HomePaidLightGreen
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .background(
                                    HomePaidGreen,
                                    CircleShape
                                )
                        )

                        Text(
                            "PAID",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = HomePaidDarkGreen
                        )
                    }

                    Text(
                        "Rs. ${"%.2f".format(Locale.US, summary.paidAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = HomePaidDarkGreen
                    )

                    Text(
                        "Total paid fees",
                        style = MaterialTheme.typography.labelSmall,
                        color = HomePaidDarkGreen
                    )
                }
            }

            // BALANCE - YELLOW PALETTE

            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.5.dp,
                        color = HomeBalanceYellow,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HomeBalanceLight
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .background(
                                    HomeBalanceYellow,
                                    CircleShape
                                )
                        )

                        Text(
                            "BALANCE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = HomeBalanceDark
                        )
                    }

                    Text(
                        "Rs. ${"%.2f".format(Locale.US, summary.balance)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = HomeBalanceDark
                    )

                    Text(
                        "Remaining balance",
                        style = MaterialTheme.typography.labelSmall,
                        color = HomeBalanceDark
                    )
                }
            }
        }

        if (summary.overpaidAmount > 0) {
            Text(
                "Overpaid: Rs. ${
                    "%.2f".format(Locale.US, summary.overpaidAmount)
                }",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        // NEW TRIP - FULLY ROUNDED PURPLE BUTTON

        Button(
            onClick = { showTripDialog = true },
            enabled = store.children.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = HomeRoyalPurple,
                contentColor = HomeWhite,
                disabledContainerColor = HomeRoyalPurple.copy(alpha = 0.4f),
                disabledContentColor = HomeWhite.copy(alpha = 0.8f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp,
                pressedElevation = 2.dp
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                "New Trip",
                fontWeight = FontWeight.Bold
            )
        }

        // CHILDREN - HEADER PURPLE + GOLD PALETTE

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = HomeGold,
                    shape = RoundedCornerShape(18.dp)
                ),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                HomeDeepPurple,
                                HomeRoyalPurple,
                                HomePurple
                            )
                        )
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Children",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = HomeWhite
                        )

                        Text(
                            "${store.children.size} registered",
                            style = MaterialTheme.typography.bodySmall,
                            color = HomeGold
                        )
                    }

                    FilledTonalButton(
                        onClick = { showChildDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = HomeGold,
                            contentColor = HomeDeepPurple
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Add Child")
                    }
                }

                if (store.children.isEmpty()) {
                    Text(
                        "No children added yet.",
                        color = HomeWhite
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        store.children
                            .sortedBy { it.name.lowercase() }
                            .forEach { child ->
                                Column(
                                    modifier = Modifier.width(76.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(62.dp)
                                            .clip(CircleShape)
                                            .background(HomeCream)
                                            .border(
                                                2.dp,
                                                HomeGold,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            child.name.trim()
                                                .firstOrNull()
                                                ?.uppercaseChar()
                                                ?.toString() ?: "?",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = HomeRoyalPurple
                                        )
                                    }

                                    Text(
                                        child.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        color = HomeWhite
                                    )

                                    Text(
                                        if (child.isActive) "Active" else "Inactive",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (child.isActive) {
                                            HomeGold
                                        } else {
                                            HomeCream
                                        }
                                    )
                                }
                            }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Active children",
                        color = HomeWhite
                    )
                    Text(
                        store.children.count { it.isActive }.toString(),
                        fontWeight = FontWeight.Bold,
                        color = HomeGold
                    )
                }
            }
        }

        // PAYMENT CONFIGURATIONS

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Payment Configurations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Create and manage different morning and return fare plans."
                )

                Text("Available plans: ${store.farePlans.size}")

                Button(
                    onClick = onOpenFarePlans,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Manage Payment Configurations")
                }
            }
        }

        // QUICK INFORMATION

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Quick Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recorded trips")
                    Text(
                        store.trips.size.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Payment records")
                    Text(
                        store.payments.size.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onOpenTrips,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View All Trips")
                }
            }
        }

        store.errorMessage?.let { message ->
            Text(
                message,
                color = MaterialTheme.colorScheme.error
            )
        }

        store.successMessage?.let { message ->
            Text(
                message,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // ADD CHILD POPUP

    if (showChildDialog) {
        ChildDialog(
            store = store,
            child = null,
            onDismiss = { showChildDialog = false },
            onSave = { child ->
                store.saveChild(child)
                showChildDialog = false
            }
        )
    }

    // NEW TRIP POPUP

    if (showTripDialog) {
        TripFormDialog(
            children = store.children,
            existingTrip = null,
            onDismiss = { showTripDialog = false },
            onSave = { trip ->
                store.saveTrip(trip)
                showTripDialog = false
            }
        )
    }
}
