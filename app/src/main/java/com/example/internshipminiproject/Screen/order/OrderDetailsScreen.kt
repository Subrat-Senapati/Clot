package com.example.internshipminiproject.Screen.order

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.internshipminiproject.data.model.Order
import com.example.internshipminiproject.viewmodel.OrderViewModel
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(orderId: String, navController: NavController) {
    val orderViewModel: OrderViewModel = hiltViewModel()
    val context = LocalContext.current
    var order by remember { mutableStateOf<Order?>(null) }

    LaunchedEffect(orderId) {
        orderViewModel.getOrderById(orderId) {
            order = it
            if (it == null) {
                Toast.makeText(context, "Order not found", Toast.LENGTH_SHORT).show()
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Order Details",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(6.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                // Order Id
                Text("Order Id", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(order!!.orderId, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(15.dp))


                // Order Status
                Text("Order Status", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                StatusItem("Order Placed", formatDate(order!!.orderDate), isCompleted = true)
                StatusItem("Order Confirmed", formatDate(order!!.orderDate), isCompleted = true)
                StatusItem("Shipped", "Pending", isCompleted = false)
                StatusItem("Delivered", "Pending", isCompleted = false)

                Spacer(Modifier.height(20.dp))

                // Order Items
                Text("Order Items", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = "Orders",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(Modifier.width(20.dp))
                            Text(
                                "${order!!.productIds.size} item(s)",
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 14.sp,
                            )
                        }
                        Text(
                            "View All",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                navController.navigate("OrderItemScreen/${orderId}")
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Shipping Details
                Text("Shipping Details", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        val address = order!!.address
                        Text(
                            address.name,
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            "${address.street}, ${address.city}, ${address.state} - ${address.zipCode}",
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 12.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            address.phoneNumber,
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Total Amount
                Text("Total: ₹${(order!!.totalAmount * 90).roundToInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}


@Composable
fun StatusItem(title: String, date: String, isCompleted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isCompleted) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCompleted) MaterialTheme.colorScheme.onBackground else Color.Gray
            )
            Text(date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}


fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
