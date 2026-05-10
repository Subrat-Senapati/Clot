package com.example.internshipminiproject.Screen.cart

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.internshipminiproject.R

@Composable
fun OrderPlacedScreen(navController: NavController) {
    BackHandler {
        navController.navigate("HomepageScreen") {
            popUpTo("OrderPlacedScreen") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.purple)),
    ) {
        // First half - Image
        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxWidth()
                .background(colorResource(R.color.purple)),
            contentAlignment = Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.order_placed),
                contentDescription = "Order Placed image",
                modifier = Modifier.size(300.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Second half - Box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .padding(20.dp, 50.dp),
            contentAlignment = Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Order Placed",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Successfully",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(15.dp))
                Text(
                    text = "You will receive  an email confirmation ",
                    color = Color.LightGray,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(50.dp))
                Button(
                    onClick = {
                        navController.navigate("OrderScreen") {
                            popUpTo("CheckoutScreen") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "See Order Details",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}