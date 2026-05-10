package com.example.internshipminiproject.Screen.product

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.internshipminiproject.R
import com.example.internshipminiproject.data.model.CartItem
import com.example.internshipminiproject.viewmodel.CartViewModel
import com.example.internshipminiproject.viewmodel.ProductViewModel
import com.example.internshipminiproject.viewmodel.WishlistViewModel
import kotlin.math.roundToInt

@Composable
fun ProductDetailsScreen(
    productId: Int,
    navController: NavController,
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val product by productViewModel.singleProduct.observeAsState()
    val wishlistViewModel: WishlistViewModel = hiltViewModel()
    val wishlist by wishlistViewModel.wishlist.observeAsState()

    LaunchedEffect(Unit) {
        wishlistViewModel.fetchWishlist()
    }

    LaunchedEffect(productId) {
        productViewModel.fetchProductById(productId)
    }

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


    product?.let { product ->
        var selectedSize by remember { mutableStateOf("S") }
        var selectedColor by remember { mutableStateOf("Orange") }
        var count by remember { mutableStateOf(if (product.minimumOrderQuantity > 5) 4 else product.minimumOrderQuantity) }
        var isFavorite by rememberSaveable { mutableStateOf(false) }

        Scaffold(
            bottomBar = {
                Button(
                    onClick = {
                        cartViewModel.addToCart(
                            CartItem(
                                productId = product.id.toString(),
                                quantity = count,
                                price = (product.price * 90).roundToInt(),
                                selectedSize = if (product.tags.contains("clothing")) selectedSize else "",
                                selectedColor = if (categoryColorList.contains(product.category)) selectedColor else ""
                            )
                        )
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 50.dp, top = 10.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "%.2f".format(product.price * count * 90),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            "Add to Bag",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft, contentDescription = "Back",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(6.dp),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(6.dp)
                    ) {
                        IconButton(onClick = {
                            wishlistViewModel.toggleWishlistItem(product.id.toString())
                        }) {
                            val isWishlisted = wishlist?.contains(product.id.toString()) == true
                            Icon(
                                imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) Color.Red else MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LazyRowImageList(product.images)
                }
                Spacer(modifier = Modifier.height(15.dp))

                Text(product.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "${(product.price * 90).roundToInt()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(20.dp))

                if (product.tags.contains("clothing")) {
                    SizePickerScreen(selectedSize) { size -> selectedSize = size }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (categoryColorList.contains(product.category)) {
                    ColorPickerScreen(selectedColor) { color -> selectedColor = color }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                CountComponent(
                    count,
                    if (product.minimumOrderQuantity > 5) 4 else product.minimumOrderQuantity
                ) { count = it }
                Spacer(modifier = Modifier.height(20.dp))

                Text(product.description, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(15.dp))

                Text("Shipping & Returns", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(product.shippingInformation + " and " + product.returnPolicy)
                Spacer(modifier = Modifier.height(15.dp))

                Text("Reviews", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text("${product.rating} Ratings", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}


@Composable
fun LazyRowImageList(imageResList: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 10.dp),
        horizontalArrangement = if (imageResList.size > 1) Arrangement.spacedBy(10.dp) else Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        items(imageResList) { image ->
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp, 250.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizePickerScreen(selectedSize: String, onSizeSelected: (String) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val sizeOptions = listOf("S", "M", "L", "XL", "2XL")
    var currentSelection by remember { mutableStateOf(selectedSize) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { showBottomSheet = true },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Size", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondary)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        selectedSize,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Size",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        IconButton(onClick = { showBottomSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    sizeOptions.forEach { size ->
                        val isSelected = selectedSize == size

                        OutlinedButton(
                            onClick = {
                                currentSelection = size
                                onSizeSelected(size)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            colors = ButtonDefaults.buttonColors(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    size,
                                    fontSize = 18.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                )

                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerScreen(selectedColor: String, onColorSelected: (String) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentColor by remember { mutableStateOf(selectedColor) }

    val colorOptions = mapOf(
        "Orange" to Color(0xFFFF9800),
        "Black" to Color(0xFF000000),
        "Red" to Color(0xFFF44336),
        "Yellow" to Color(0xFFFFEB3B),
        "Blue" to Color(0xFF2196F3)
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { showBottomSheet = true },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Color", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondary)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(colorOptions.getValue(selectedColor))
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Color",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        IconButton(onClick = { showBottomSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    colorOptions.forEach { (name, color) ->
                        val isSelected = selectedColor == name
                        OutlinedButton(
                            onClick = {
                                currentColor = name
                                onColorSelected(name)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            colors = ButtonDefaults.buttonColors(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    name, fontSize = 18.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))

                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(15.dp))
                }
            }
        }
    }
}


@Composable
fun CountComponent(count: Int, minOrder: Int, onCountChange: (Int) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Count:", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondary)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { if (count > minOrder) onCountChange(count - 1) },
                        enabled = count > minOrder,
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .scale(.75f)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.minus_icon),
                            contentDescription = "Minus",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = count.toString(),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Spacer(Modifier.width(10.dp))

                    IconButton(
                        onClick = { onCountChange(count + 1) },
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .scale(.75f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}