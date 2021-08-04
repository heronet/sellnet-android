package com.heronet.sellnet.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.heronet.sellnet.R
import com.heronet.sellnet.model.Product
import com.heronet.sellnet.util.DateParser
import com.heronet.sellnet.util.Resource
import com.heronet.sellnet.viewmodel.ProductsViewModel

@Composable
fun ProductDetailScreen(
    productsViewModel: ProductsViewModel,
    productId: String
) {
    val product = produceState<Resource<Product>>(initialValue = Resource.Loading()) {
        value = productsViewModel.getProduct(productId)
    }.value

    Surface(color = MaterialTheme.colors.background) {
        when(product) {
            is Resource.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = product.message!!)
                }
            }
            is Resource.Success -> {
                val scrollState = rememberScrollState()
                Scaffold(
                    floatingActionButton = {
                        val context = LocalContext.current
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Call") },
                            onClick = {
                                makeCall(context = context, product.data!!.supplier.phone)
                            },
                            icon = {
                                Icon(imageVector = Icons.Default.Call, contentDescription = null)
                            },
                        )
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(innerPadding)
                        .verticalScroll(scrollState)) {
                        Details(product = product.data!!)
                        Contact(product = product.data)
                    }
                }
            }
        }
    }
}

@Composable
fun Images(photos: List<Product.Photo>, modifier: Modifier = Modifier) {
    val photo = remember {
        mutableStateOf(photos[0])
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val painter = rememberCoilPainter(request = photo.value.imageUrl)
            Image(
                painter = painter,
                contentDescription = photo.value.publicId,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            when (painter.loadState) {
                is ImageLoadState.Loading -> {
                    // Display a circular progress indicator whilst loading
                    Image(
                        painter = rememberCoilPainter(request = R.drawable.placeholder),
                        contentDescription = photo.value.publicId,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                is ImageLoadState.Error -> {
                    // Display some content if the request fails
                }
                else -> {}
            }
        }
        Spacer(modifier = Modifier.padding(4.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(photos) { localPhoto ->
                Box(modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)) {
                    val painter = rememberCoilPainter(request = localPhoto.imageUrl)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                            .shadow(8.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { photo.value = localPhoto },
                        contentScale = ContentScale.Crop
                    )
                    when (painter.loadState) {
                        is ImageLoadState.Loading -> {
                            Image(
                                painter = rememberCoilPainter(request = R.drawable.placeholder),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(80.dp)
                                    .shadow(4.dp, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { photo.value = localPhoto },
                                contentScale = ContentScale.Crop
                            )
                        }
                        is ImageLoadState.Error -> {
                            // Display some content if the request fails
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun Details(product: Product) {
    Spacer(modifier = Modifier.padding(4.dp))
    Text(text = product.name, fontSize = 24.sp)
    Text(text = "Posted on ${DateParser.getFormattedDate(product.createdAt)}", fontSize = 14.sp, style = TextStyle(color = Color.LightGray))
    Spacer(modifier = Modifier.padding(4.dp))
    Images(product.photos, modifier = Modifier.fillMaxWidth())
    Divider(modifier = Modifier.padding(vertical = 8.dp))
    Text(text = "${product.price} TK", fontSize = 24.sp, style = TextStyle(color = MaterialTheme.colors.primary))
    Text(text = "${product.city}, ${product.division}", fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
    Text(text = "Description", fontSize = 24.sp, modifier = Modifier.padding(vertical = 4.dp))
    Divider()
    Text(text = product.description, modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
fun Contact(product: Product) {
    Text(text = "Seller Information", fontSize = 24.sp, modifier = Modifier.padding(vertical = 4.dp))
    Divider()
    SelectionContainer {
        Column {
            Text(text = product.supplier.name, modifier = Modifier.padding(vertical = 4.dp))
            Text(text = "Phone: ${product.supplier.phone}")
            Text(text = "Email: ${product.supplier.email}", modifier = Modifier.padding(vertical = 4.dp))
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
        }
    }

}

fun makeCall(context: Context, number: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Can't call number", Toast.LENGTH_SHORT).show()
    }
}