
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // TOP HEADER

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),
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
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when {
                        showFarePlans -> "Payment Configurations"
                        showTrips -> "Trips"
                        showProfile -> "Profile"
                        else -> "School Transport"
                    },
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = when {
                        showFarePlans ->
                            "Manage your transport fare plans"
                        showTrips ->
                            "Manage your transport trips"
                        showProfile ->
                            "Manage your account details"
                        else ->
                            "Fare Manager"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        HorizontalDivider()

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

            NavigationBar {

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
                            Text(title)
                        },

                        alwaysShowLabel = true
                    )
                }
            }
        }
    }
}
