
package com.schooltransport.faremanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
private val LightGold = Color(0xFFFFE6A6)

@Composable
fun SchoolTransportApp() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DeepPurple
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DeepPurple,
                            Purple,
                            DeepPurple
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "🚌",
                    fontSize = 76.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "School Transport",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Fare Manager",
                    color = Gold,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .width(75.dp)
                        .height(4.dp)
                        .background(
                            LightGold,
                            RoundedCornerShape(10.dp)
                        )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Manage school transport fares easily",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Version 1.0",
                    color = LightGold.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
