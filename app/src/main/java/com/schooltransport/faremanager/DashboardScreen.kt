
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DashPurple = Color(0xFF51247A)
private val DashDark = Color(0xFF241044)
private val DashGold = Color(0xFFFFC857)
private val DashBg = Color(0xFFF7F3FC)

@Composable
fun DashboardScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val titles = listOf(
        "Home", "Children", "Calendar", "Payments", "Settings"
    )

    Scaffold(
        containerColor = DashBg,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val icons = listOf(
                    Icons.Default.Home,
                    Icons.Default.People,
                    Icons.Default.CalendarMonth,
                    Icons.Default.AccountBalanceWallet,
                    Icons.Default.Settings
                )

                titles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(icons[index], contentDescription = title)
                        },
                        label = { Text(title, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DashPurple,
                            selectedTextColor = DashPurple,
                            indicatorColor = Color(0xFFEBDDFA),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(DashDark, DashPurple)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "School Transport",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Fare Manager",
                        color = DashGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    Icons.Default.DirectionsBus,
                    contentDescription = "Bus",
                    tint = DashGold,
                    modifier = Modifier.size(38.dp)
                )
            }

            when (selectedTab) {
                0 -> HomeTab()
                1 -> PlaceholderTab(
                    "Children",
                    "Manage student profiles and transport details.",
                    Icons.Default.People
                )
                2 -> PlaceholderTab(
                    "Calendar",
                    "View daily Morning and Return attendance.",
                    Icons.Default.CalendarMonth
                )
                3 -> PlaceholderTab(
                    "Payments",
                    "Track monthly fares, payments and balances.",
                    Icons.Default.AccountBalanceWallet
                )
                4 -> SettingsTab(onLogout)
            }
        }
    }
}

@Composable
private fun HomeTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Dashboard",
            color = DashDark,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Today's Overview",
            color = Color.Gray,
            fontSize = 14.sp
        )

        SummaryCard("Total Fare", "Rs. 0", Icons.Default.Payments)
        SummaryCard("Paid Amount", "Rs. 0", Icons.Default.CheckCircle)
        SummaryCard("Balance", "Rs. 0", Icons.Default.AccountBalanceWallet)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    "Transport Summary",
                    fontWeight = FontWeight.Bold,
                    color = DashDark,
                    fontSize = 17.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "No trips recorded yet.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF0E5FF)
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = DashPurple,
                    modifier = Modifier.padding(12.dp).size(25.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column {
                Text(title, color = Color.Gray, fontSize = 13.sp)
                Text(
                    amount,
                    color = DashDark,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PlaceholderTab(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = DashPurple,
            modifier = Modifier.size(65.dp)
        )
        Spacer(Modifier.height(18.dp))
        Text(
            title,
            color = DashDark,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            description,
            color = Color.Gray
        )
        Spacer(Modifier.height(18.dp))
        Text(
            "Coming soon",
            color = DashPurple,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsTab(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Settings",
            color = DashDark,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        SettingsItem("Profile & Account", Icons.Default.Person)
        SettingsItem("Change Password", Icons.Default.Lock)
        SettingsItem("Privacy & Security", Icons.Default.Security)
        SettingsItem("App Information", Icons.Default.Info)

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB3261E)
            )
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }

        Text(
            "School Transport Fare Manager • Version 1.0",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = DashPurple)
            Spacer(Modifier.width(14.dp))
            Text(
                title,
                color = DashDark,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.weight(1f))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
