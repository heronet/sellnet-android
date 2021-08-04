package com.heronet.sellnet.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.heronet.sellnet.R
import com.heronet.sellnet.model.Product
import com.heronet.sellnet.util.DateParser
import com.heronet.sellnet.viewmodel.UserProductsViewModel

@Composable
fun UserProductsScreen(
    navController: NavHostController,
    productsViewModel: UserProductsViewModel = hiltViewModel()
) {
    var products by remember { productsViewModel.products }
    val isLoading by remember { productsViewModel.isLoading }
    val isDeleting by remember { productsViewModel.isDeleting }
    val isDeleteSuccessful by remember { productsViewModel.isDeleteSuccessful }
    val loadError by remember { productsViewModel.errorMessage }
    val productsCount by remember { productsViewModel.productsCount }
    var deletableProduct by remember { mutableStateOf("") }

    Surface {
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
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Oops. It looks like you haven't added any product yet.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h4,
                    color = Color.LightGray
                )
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                itemsIndexed(
                    items = products,
                    key = { _: Int, item: Product -> item.id }) { index, product: Product ->
                    if (!isLoading) {
                        if ((products.size < productsCount) && (index == products.size - 1)) {
                            productsViewModel.getProducts()
                        }
                    }
                    ProductCard(
                        product = product,
                        onDelete = {id -> deletableProduct = id; productsViewModel.deleteProduct(id)},
                        isDeleting = isDeleting && product.id == deletableProduct,
                        modifier = Modifier.clickable { navController.navigate("products/${product.id}") })
                }
            }
        }
        if (isDeleteSuccessful) {
            productsViewModel.resetDeleteStatus()
            products = products.filter { it.id != deletableProduct }
            deletableProduct = ""
            Toast.makeText(LocalContext.current, "Product Deleted Successfully", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ProductCard(product: Product, onDelete: (String) -> Unit, isDeleting: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = 8.dp,
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
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
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.h6,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${product.price} TK",
                        style = TextStyle(color = MaterialTheme.colors.primaryVariant)
                    )
                    Text(text = DateParser.getFormattedDate(product.createdAt))
                }
                Button(onClick = { onDelete(product.id) }, enabled = !isDeleting) {
                    if (!isDeleting) Text(text = "Delete") else Text(text = "Please Wait...")
                }
            }
        }
    }
}