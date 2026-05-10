package com.example.internshipminiproject.Screen.homepage

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.internshipminiproject.data.model.Categories
import com.example.internshipminiproject.data.model.Product
import com.example.internshipminiproject.viewmodel.AuthState
import com.example.internshipminiproject.viewmodel.AuthViewModel
import com.example.internshipminiproject.viewmodel.CategoriesViewModel
import com.example.internshipminiproject.viewmodel.ProductViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ComponentActivity
import com.example.internshipminiproject.viewmodel.UserViewModel
import com.example.internshipminiproject.viewmodel.WishlistViewModel
import kotlin.math.roundToInt


@SuppressLint("RestrictedApi")
@Composable
fun HomepageScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel(),
    categoryViewModel: CategoriesViewModel = viewModel(),
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val wishlistViewModel: WishlistViewModel = hiltViewModel()


    val productResponse by productViewModel.productResponseList.observeAsState()
    val categoryList by categoryViewModel.categoriesList.observeAsState()
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                productViewModel.fetchProducts()
                categoryViewModel.fetchCategories()
                wishlistViewModel.fetchWishlist()
            }

            is AuthState.Unauthenticated -> navController.navigate("SignInScreen")
            else -> Unit
        }
    }

    var topDeals by remember { mutableStateOf<List<Product>>(emptyList()) }
    val randomNumbers = remember {
        (1..120).shuffled().take(10)
    }
    LaunchedEffect(randomNumbers) {
        if (randomNumbers.isNotEmpty()) {
            val fetchedProducts = productViewModel.fetchProductsByIds(randomNumbers)
            topDeals = fetchedProducts
        }
    }

    val context = LocalContext.current
    val activity = context as? ComponentActivity

    var showExitDialog by remember { mutableStateOf(false) }
    BackHandler(enabled = authState.value is AuthState.Authenticated) {
        showExitDialog = true
    }


    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomMenu(navController, "HomepageScreen") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (productResponse == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val products = productResponse?.products.orEmpty()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(20.dp, 0.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Search Button
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SearchButton(navController)
                    }

                    // Categories Section
                    if (!categoryList.isNullOrEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Categories",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "See All",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier
                                            .clickable { navController.navigate("AllCategoriesScreen") }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(
                                        categoryList.orEmpty().take(7),
                                        key = { it.slug }) { category ->
                                        CategoryCard(category, navController)
                                    }
                                }
                            }
                        }
                    }

                    // Top Selling Section
                    if (!topDeals.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Top Selling",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(topDeals) { product ->
                                        ProductCard(product, navController)
                                    }
                                }
                            }
                        }
                    }

                    // Product Items
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "New In",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(products) { product ->
                        ProductCard(product, navController)
                    }
                }
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Exit App") },
                text = { Text("Are you sure you want to exit the app?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            activity?.finish()
                        }
                    ) {
                        Text("Yes", fontSize = 17.sp)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                        }
                    ) {
                        Text("No", fontSize = 17.sp)
                    }
                }
            )
        }

    }
}


@Composable
fun CategoryCard(category: Categories, navController: NavController) {
    val imageUri = mapOf(
        "beauty" to "https://cdn.dummyjson.com/product-images/beauty/powder-canister/thumbnail.webp",
        "fragrances" to "https://cdn.dummyjson.com/product-images/fragrances/chanel-coco-noir-eau-de/thumbnail.webp",
        "furniture" to "https://cdn.dummyjson.com/product-images/furniture/annibale-colombo-bed/thumbnail.webp",
        "groceries" to "https://cdn.dummyjson.com/product-images/groceries/apple/thumbnail.webp",
        "home-decoration" to "https://cdn.dummyjson.com/product-images/home-decoration/decoration-swing/thumbnail.webp",
        "kitchen-accessories" to "https://cdn.dummyjson.com/product-images/kitchen-accessories/bamboo-spatula/thumbnail.webp",
        "laptops" to "https://cdn.dummyjson.com/product-images/laptops/apple-macbook-pro-14-inch-space-grey/thumbnail.webp"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                onClick = {
                    navController.navigate("category/${category.slug}")
                }
            )
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(8.dp) // Inner padding for better spacing
            .size(60.dp, 90.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.White, shape = CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUri.getValue(category.slug),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = category.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun ProductCard(
    product: Product,
    navController: NavController,
) {
    val wishlistViewModel: WishlistViewModel = hiltViewModel()
    val wishlist by wishlistViewModel.wishlist.observeAsState(emptyList())
    val isInWishlist = remember(wishlist) { wishlist.contains(product.id.toString()) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = Modifier
            .width(screenWidth * 0.45f)
            .clickable { navController.navigate("product/${product.id}") }
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondary),
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            Box {
                AsyncImage(
                    model = product.thumbnail,
                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 160.dp, height = 170.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                )

                IconButton(
                    onClick = {
                        wishlistViewModel.toggleWishlistItem(product.id.toString())
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isInWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isInWishlist) Color.Red else Color.Gray
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 5.dp)
        ) {
            Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text("${(product.price * 90).roundToInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

    }
}


@Composable
fun TopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 50.dp, bottom = 10.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            tint = MaterialTheme.colorScheme.onSecondary
        )

        GenderPickerScreen()

        IconButton(
            onClick = { navController.navigate("CartScreen") },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .scale(.75f)
        ) {
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Shopping Cart",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderPickerScreen() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var selectedGender by remember { mutableStateOf("All") }
    val GenderOptions = listOf("All", "Men", "Women", "Kids")

    Box() {
        Button(
            onClick = { showBottomSheet = true },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    selectedGender,
                    fontSize = 18.sp,
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
                            "Gender",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        IconButton(onClick = { showBottomSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    GenderOptions.forEach { gender ->
                        val isSelected = selectedGender == gender

                        OutlinedButton(
                            onClick = {
                                selectedGender = gender
                                showBottomSheet = false
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
                                    gender,
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

@Composable
fun BottomMenu(navController: NavController, selectedItem: String) {
    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.height(2.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Home,
            contentDescription = "Home",
            modifier = Modifier
                .size(30.dp)
                .let {
                    if (selectedItem != "HomepageScreen") {
                        it.clickable { navController.navigate("HomepageScreen") }
                    } else {
                        it
                    }
                },
            tint = if (selectedItem == "HomepageScreen") MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary
        )
        Icon(
            Icons.Default.Notifications,
            contentDescription = "Notifications",
            modifier = Modifier
                .size(30.dp)
                .let {
                    if (selectedItem != "NotificationScreen") {
                        it.clickable { navController.navigate("NotificationScreen") }
                    } else {
                        it
                    }
                },
            tint = if (selectedItem == "NotificationScreen") MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary
        )
        Icon(
            Icons.Default.List,
            contentDescription = "Orders",
            modifier = Modifier
                .size(30.dp)
                .let {
                    if (selectedItem != "OrderScreen") {
                        it.clickable { navController.navigate("OrderScreen") }
                    } else {
                        it
                    }
                },
            tint = if (selectedItem == "OrderScreen") MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary
        )
        Icon(
            Icons.Default.Person,
            contentDescription = "Settings",
            modifier = Modifier
                .size(30.dp)
                .let {
                    if (selectedItem != "SettingsScreen") {
                        it.clickable { navController.navigate("SettingsScreen") }
                    } else {
                        it
                    }
                },
            tint = if (selectedItem == "SettingsScreen") MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun SearchButton(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                navController.navigate("SearchScreen")
            },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Search",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}