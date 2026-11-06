
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(onLogout: () -> Unit) {

    val store = remember { TransportStore() }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showFarePlans by remember { mutableStateOf(false) }
    var showTrips by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }

    val titles = listOf(
        "Home",
        "Children",
        "Calendar",
        "Payments",
        "Settings"
    )

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.DirectionsBus,
        Icons.Default.CalendarMonth,
        Icons.Default.Payments,
        Icons.Default.Settings
    )

    val isSubPage = showFarePlans || showTrips || showProfile
    val isMainPage = !isSubPage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        if (isMainPage) {
            PremiumAppHeader(
                onProfileClick = {
                    showProfile = true
                }
            )
        }

        if (isSubPage) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    )
            ) {
                PremiumSubPageHeader(
                    title = when {
                        showFarePlans -> "Fare Plans"
                        showTrips -> "Trips"
                        showProfile -> "Profile"
                        else -> ""
                    },
                    onBackClick = {
                        showFarePlans = false
                        showTrips = false
                        showProfile = false
                    }
                )
            }
        }

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
                            onLogout = onLogout,
                            onOpenFarePlans = {
                                showFarePlans = true
                            }
                        )
                    }
                }
            }
        }

        if (!isSubPage) {
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
                        }
                    )
                }
            }
        }
    }
}
