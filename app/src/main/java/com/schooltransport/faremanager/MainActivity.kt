
package com.schooltransport.faremanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SchoolTransportApp()
            }
        }
    }
}

private val DeepPurple = Color(0xFF241044)
private val Purple = Color(0xFF51247A)
private val Gold = Color(0xFFFFC857)

@Composable
fun SchoolTransportApp() {

    var showLogin by remember {
        mutableStateOf(false)
    }

    var isAuthenticated by remember {
        mutableStateOf(
            FirebaseAuth.getInstance().currentUser != null
        )
    }

    when {
        isAuthenticated -> {
            SuccessScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    isAuthenticated = false
                    showLogin = false
                }
            )
        }

        showLogin -> {
            AuthScreen(
                onAuthenticated = {
                    isAuthenticated = true
                }
            )
        }

        else -> {
            SplashScreen(
                onGetStarted = {
                    showLogin = true
                }
            )
        }
    }
}

@Composable
private fun SplashScreen(
    onGetStarted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepPurple, Purple, DeepPurple)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text("🚌", fontSize = 76.sp)

            Spacer(Modifier.height(20.dp))

            Text(
                "School Transport",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Fare Manager",
                color = Gold,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))

            Box(
                Modifier
                    .width(75.dp)
                    .height(4.dp)
                    .background(Gold, RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "Manage school transport fares easily",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onGetStarted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = DeepPurple
                )
            ) {
                Text(
                    "Get Started",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Version 1.0",
                color = Gold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SuccessScreen(
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepPurple, Purple, DeepPurple)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text("🎉", fontSize = 64.sp)

            Spacer(Modifier.height(20.dp))

            Text(
                "Login Successful!",
                color = Gold,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Welcome to School Transport Fare Manager",
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}
