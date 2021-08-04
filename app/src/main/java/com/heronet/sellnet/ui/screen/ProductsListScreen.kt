package com.heronet.sellnet.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.heronet.sellnet.R
import com.heronet.sellnet.model.Product
import com.heronet.sellnet.util.DateParser
import com.heronet.sellnet.viewmodel.AuthViewModel
import com.heronet.sellnet.viewmodel.ProductsViewModel


@Composable
fun ProductsListScreen(
    productsViewModel: ProductsViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val products by remember { productsViewModel.products }
    val isLoading by remember { productsViewModel.isLoading }
    val loadError by remember { productsViewModel.errorMessage }
    val productsCount by remember { productsViewModel.productsCount }
    val categories = remember { productsViewModel.categories }
    val sortOrders = remember { productsViewModel.sortOrders }
    var filterVisible by remember { mutableStateOf(false) }
    val location by remember { authViewModel.locations }
    val isLocationLoading by remember { authViewModel.isLocationsLoading }

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            content = {
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    val focusManager = LocalFocusManager.current
                    OutlinedTextField(
                        value = productsViewModel.name,
                        onValueChange = { productsViewModel.name = it },
                        label = { Text(text = "Search") },
                        placeholder = { Text(text = "What are you looking for?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                productsViewModel.resetProducts()
                                productsViewModel.getProducts()
                            }
                        )
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(categories, key = { category -> category }) { category ->
                            Button(
                                onClick = {
                                    productsViewModel.resetProducts()
                                    productsViewModel.selectedCategory = category

                                    productsViewModel.getProducts()
                                },
                                enabled = productsViewModel.selectedCategory != category
                            ) {
                                Text(text = category)
                            }
                        }
                    }
                    if (isLoading && products.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (!isLoading && products.isEmpty()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Filter(result = "$productsCount ${if (productsCount > 1) "results" else "result" } found") {
                                filterVisible = !filterVisible
                                authViewModel.getLocations()
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Oops. It looks like there is no product listed yet.",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.h4,
                                    color = Color.LightGray
                                )
                            }
                        }
                    } else if (loadError.isNotBlank()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = loadError)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            item {
                                Filter(result = "$productsCount ${if (productsCount > 1) "results" else "result" } found") {
                                    filterVisible = !filterVisible
                                    authViewModel.getLocations()
                                }
                            }
                            itemsIndexed(
                                items = products,
                                key = { _: Int, item: Product -> item.id }) { index, product: Product ->
                                if (!isLoading) {
                                    if ((products.size < productsCount) && (index == products.size - 1)) {
                                        productsViewModel.getProducts()
                                    }
                                }
                                ItemCard(
                                    product = product,
                                    modifier = Modifier
                                        .clickable { navController.navigate("products/${product.id}") }
                                )
                            }
                        }
                    }
                    if (filterVisible) {
                        Dialog(onDismissRequest = { filterVisible = false }) {
                            if (isLocationLoading) {
                                Column(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(100.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colors.background,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(16.dp)
                                        .width(300.dp),
                                ) {
                                    Text(text = "Filter")
                                    OutlinedTextField(
                                        value = productsViewModel.selectedCategory,
                                        onValueChange = { productsViewModel.selectedCategory = it },
                                        label = { Text(text = "Category") },
                                        modifier = Modifier.fillMaxWidth(),
                                        readOnly = true
                                    )
                                    OutlinedTextField(
                                        value = productsViewModel.sortBy,
                                        onValueChange = { productsViewModel.sortBy = it },
                                        label = { Text(text = "Sort By") },
                                        modifier = Modifier.fillMaxWidth(),
                                        readOnly = true
                                    )
                                    OutlinedTextField(
                                        value = productsViewModel.city,
                                        onValueChange = { productsViewModel.city = it },
                                        label = { Text(text = "City") },
                                        modifier = Modifier.fillMaxWidth(),
                                        readOnly = true
                                    )
                                    OutlinedTextField(
                                        value = productsViewModel.division,
                                        onValueChange = { productsViewModel.division = it },
                                        label = { Text(text = "Division") },
                                        modifier = Modifier.fillMaxWidth(),
                                        readOnly = true
                                    )

                                    var categoryExpanded by remember { mutableStateOf(false) }
                                    var cityExpanded by remember { mutableStateOf(false) }
                                    var divisionExpanded by remember { mutableStateOf(false) }
                                    var sortByExpanded by remember { mutableStateOf(false) }

                                    DropdownMenu(
                                        expanded = categoryExpanded,
                                        onDismissRequest = { categoryExpanded = false }) {
                                        categories.forEach { category ->
                                            DropdownMenuItem(onClick = {
                                                productsViewModel.selectedCategory = category; categoryExpanded = false
                                            }) {
                                                Text(text = category)
                                            }
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = cityExpanded,
                                        onDismissRequest = { cityExpanded = false }) {
                                        location?.cities!!.forEach { ct ->
                                            DropdownMenuItem(onClick = {
                                                productsViewModel.city = ct; cityExpanded = false
                                            }) {
                                                Text(text = ct)
                                            }
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = divisionExpanded,
                                        onDismissRequest = { divisionExpanded = false }) {
                                        location?.divisions!!.forEach { dv ->
                                            DropdownMenuItem(onClick = {
                                                productsViewModel.division = dv; divisionExpanded = false
                                            }) {
                                                Text(text = dv)
                                            }
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = sortByExpanded,
                                        onDismissRequest = { sortByExpanded = false }) {
                                        sortOrders.forEach { so ->
                                            DropdownMenuItem(onClick = {
                                                productsViewModel.sortBy = so; sortByExpanded = false
                                            }) {
                                                Text(text = so)
                                            }
                                        }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Button(onClick = {
                                            categoryExpanded = !categoryExpanded
                                        }, modifier = Modifier.fillMaxWidth(0.5f)) {
                                            Text(text = "Category")
                                        }
                                        Button(
                                            onClick = { sortByExpanded = !sortByExpanded },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(text = "Sort By")
                                        }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Button(
                                            onClick = { cityExpanded = !cityExpanded },
                                            modifier = Modifier.fillMaxWidth(0.5f)
                                        ) {
                                            Text(text = "City")
                                        }
                                        Button(onClick = {
                                            divisionExpanded = !divisionExpanded
                                        }, modifier = Modifier.fillMaxWidth()) {
                                            Text(text = "Division")
                                        }
                                    }
                                    Button(
                                        onClick = {
                                            productsViewModel.resetProducts()
                                            productsViewModel.getProducts()
                                            filterVisible = false
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp)
                                    ) {
                                        Text(text = "Filter")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            productsViewModel.resetFilters()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "Reset Filters")
                                    }
                                }
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("add-product") {
                            launchSingleTop = true
                        }
                    },
                    text = { Text(text = "Add") },
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") }
                )
            }
        )
    }
}

@Composable
fun Filter(result: String, onFilterClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .background(
                MaterialTheme.colors.primary,
                RoundedCornerShape(topStart = 6.dp, bottomEnd = 6.dp)
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(MaterialTheme.colors.primary)
        ) {
            Text(
                text = result,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            OutlinedButton(
                onClick = onFilterClick,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = "Filter")
            }
        }
    }
}

@Composable
fun ItemCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    val date = DateParser.getFormattedDate(product.createdAt)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = 8.dp,
    ) {
        Row {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(130.dp)
            ) {
                val painter = rememberCoilPainter(
                    request = product.thumbnail.imageUrl,
                    fadeIn = true
                )
                Image(
                    painter = painter,
                    contentDescription = product.thumbnail.publicId,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                when (painter.loadState) {
                    is ImageLoadState.Loading -> {
                        Image(
                            painter = rememberCoilPainter(request = R.drawable.placeholder),
                            contentDescription = product.thumbnail.publicId,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is ImageLoadState.Error -> {
                        // Display some content if the request fails
                    }
                    else -> {
                    }
                }
            }

            Column(Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    style = MaterialTheme.typography.h6,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = product.category, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text(
                    text = "${product.price} TK",
                    style = TextStyle(color = MaterialTheme.colors.primaryVariant)
                )
                Text(text = "${product.city}, ${product.division}")
                Text(text = date)
            }
        }
    }
}
