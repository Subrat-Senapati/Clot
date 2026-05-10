package com.example.internshipminiproject.Screen.cart

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.internshipminiproject.R
import com.example.internshipminiproject.data.model.CartItem
import com.example.internshipminiproject.data.model.CartSummary
import com.example.internshipminiproject.viewmodel.CartViewModel
import com.example.internshipminiproject.viewmodel.UserViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel = hiltViewModel()) {
    val cartItems by cartViewModel.cartItems.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        cartViewModel.fetchCartItems()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            cartItems == null -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            cartItems!!.isEmpty() -> {
                EmptyCartScreen(navController)
            }

            else -> {
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
                                        "Cart",
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
                    },
                    bottomBar = {
                        cartItems?.let {
                            CartBottomBar(cartItems = it) {
                                Toast.makeText(context, "Proceeding to checkout", Toast.LENGTH_SHORT).show()
                                navController.navigate("CheckoutScreen")
                            }
                        }
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(20.dp)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                "Remove All",
                                modifier = Modifier.clickable {
                                    cartViewModel.clearCart()
                                    Toast.makeText(context, "Cart is Cleared", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        LazyColumn(
                        ) {
                            items(cartItems!!) { item ->
                                CartItemCard(item, navController)
                                Spacer(Modifier.height(5.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CartItemCard(
    item: CartItem,
    navController: NavController,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    var cartSummary by remember { mutableStateOf<CartSummary?>(null) }
    var minOrder by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val categoryColorList = listOf(
        "womens-shoes",
        "womens-dresses",
        "womens-bags",
        "tops",
        "smartphones",
        "mens-shoes",
        "mens-shirts"
    )

    LaunchedEffect(item.productId) {
        cartViewModel.fetchCartSummaryById(
            productId = item.productId,
            onSuccess = { product ->
                cartSummary = product
                minOrder = product.minimumOrderQuantity.coerceAtMost(4)
            },
            onFailure = {
                Toast.makeText(context, "Failed to load product", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                navController.navigate("product/${item.productId}")
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cartSummary?.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = cartSummary?.title ?: "Loading...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    cartSummary?.tags?.let { tags ->
                        if ("clothing" in tags) {
                            Text(
                                text = "Size: ${item.selectedSize}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                    }

                    if (categoryColorList.contains(cartSummary?.category)) {
                        Text(
                            text = "Color: ${item.selectedColor}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Text(
                    text = "₹ ${((cartSummary?.price?.toDouble() ?: 0.0) * 90).toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (item.quantity > minOrder) {
//                                item.quantity -= 1
                                cartViewModel.updateQuantity(item, item.quantity - 1)
                            } else {
                                cartViewModel.removeFromCart(item)
                                Toast.makeText(context, "Removed from Cart", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .scale(.75f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.minus_icon),
                            contentDescription = "Decrease Quantity",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = item.quantity.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    IconButton(
                        onClick = {
//                            item.quantity += 1
                            cartViewModel.updateQuantity(item, item.quantity + 1)
                        },
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .scale(.75f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Quantity",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CartBottomBar(
    cartItems: List<CartItem>,
    modifier: Modifier = Modifier,
    onCheckoutClick: () -> Unit
) {
    val subtotal = remember(cartItems) {
        cartItems.fold(0.0) { acc, item ->
            acc + (item.price * 90) * item.quantity
        }
    }
    val deliveryFee = 10.0
    val tax = 0.0
    val total = subtotal + deliveryFee + tax

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 50.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", color = MaterialTheme.colorScheme.onBackground)
                Text("₹%.2f".format(subtotal), color = MaterialTheme.colorScheme.onBackground)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Shipping Cost", color = MaterialTheme.colorScheme.onBackground)
                Text("₹%.2f".format(deliveryFee), color = MaterialTheme.colorScheme.onBackground)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tax", color = MaterialTheme.colorScheme.onBackground)
                Text("₹%.2f".format(tax), color = MaterialTheme.colorScheme.onBackground)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onBackground)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    text = "₹%.2f".format(total),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text("Checkout", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}


@Composable
fun EmptyCartScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 50.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .scale(.75f)
        ) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Keyboard Arrow Left button",
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(R.drawable.empty_cart),
                    contentDescription = "Link Send",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color.Transparent)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your Cart is Empty",
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        navController.navigate("AllCategoriesScreen")
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Explore Categories",
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}