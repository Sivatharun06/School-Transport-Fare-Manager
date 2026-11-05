
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(onLogout: () -> Unit) {

    val store = remember { TransportStore() }

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var showFarePlans by remember {
        mutableStateOf(false)
    }

    var showTrips by remember {
        mutableStateOf(false)
    }

    var showProfile by remember {
        mutableStateOf(false)
    }

    val titles = listOf(
        "Home",
        "Children",
        "Calendar",
        "Payments",
        "Settings"
    )

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.People,
        Icons.Default.CalendarMonth,
        Icons.Default.Payments,
        Icons.Default.Settings
    )

    val currentTitle = when {
        showFarePlans -> "Payment Configurations"
        showTrips -> "Trips"
        showProfile -> "Profile"
        else -> "School Transport"
    }

    val currentSubtitle = when {
        showFarePlans -> "Manage your transport fare plans"
        showTrips -> "Manage your transport trips"
        showProfile -> "Manage your account details"
        else -> "Fare Manager"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // PREMIUM APP HEADER

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (showFarePlans || showTrips || showProfile) {
                    IconButton(
                        onClick = {
                            showFarePlans = false
                            showTrips = false
                            showProfile = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = "School transport",
                            modifier = Modifier.padding(11.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = currentTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = currentSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // MAIN CONTENT

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {

                showFarePlans -> {
                    FarePlansScreen(store)
                }

                showTrips -> {
                    TripsScreen(store)
                }

                showProfile -> {
                    ProfileScreen()
                }

                else -> {
                    when (selectedTab) {

                        0 -> HomeScreen(
                            store = store,
                            onOpenFarePlans = {
                                showFarePlans = true
                            },
                            onOpenTrips = {
                                showTrips = true
                            },
                            onOpenProfile = {
                                showProfile = true
                            }
                        )

                        1 -> ChildrenScreen(store)

                        2 -> CalendarScreen(store)

                        3 -> PaymentsScreen(store)

                        4 -> SettingsScreen(
                            onLogout = {
                                showFarePlans = false
                                showTrips = false
                                showProfile = false
                                selectedTab = 0
                                onLogout()
                            },
                            onOpenFarePlans = {
                                showFarePlans = true
                            }
                        )
                    }
                }
            }
        }

        // BOTTOM NAVIGATION

        if (!showFarePlans && !showTrips && !showProfile) {

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                tonalElevation = 8.dp
            ) {

                titles.forEachIndexed { index, title ->

                    NavigationBarItem(
                        selected = selectedTab == index,

                        onClick = {
                            selectedTab = index
                        },

                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = title
                            )
                        },

                        label = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Medium
                                }
                            )
                        },

                        alwaysShowLabel = true,

                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
