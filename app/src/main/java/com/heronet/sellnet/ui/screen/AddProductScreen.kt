package com.heronet.sellnet.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.accompanist.coil.rememberCoilPainter
import com.heronet.sellnet.ui.navigation.Screen
import com.heronet.sellnet.util.Constants
import com.heronet.sellnet.viewmodel.AuthViewModel
import com.heronet.sellnet.viewmodel.ProductsViewModel

@Composable
fun AddProductScreen(
    productsViewModel: ProductsViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        var name by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var imageUris by remember { mutableStateOf(listOf<Uri?>()) }
        var category by remember { mutableStateOf("") }
        var categoryExpanded by remember { mutableStateOf(false) }
        val isLoading by remember { productsViewModel.isLoading }
        val uploadFinished by remember { productsViewModel.uploadFinished }
        var submitError by remember { mutableStateOf("") }
        var maxPhotosExceededError by remember { mutableStateOf("") }
        val categories = Constants.categories
        val selectorLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) {
                imageUris = it
            }
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(8.dp),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text("Product Title") },
                placeholder = { Text("Add an easily guessable name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                singleLine = true,
                label = { Text("Price") },
                placeholder = { Text("Good prices, good sells") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { value ->
                    if (value.length <= 9) {
                        price = value.filter { it.isDigit() }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Describe your item as good as you can") },
                modifier = Modifier.fillMaxWidth()
            )
            if (category.isNotBlank()) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { /* Do Nothing */ },
                    enabled = false,
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(onClick = { category = cat; categoryExpanded = false }) {
                        Text(text = cat)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { categoryExpanded = !categoryExpanded },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(text = "Select Category")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { selectorLauncher.launch("image/*") },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Add Photos")
                }
            }
            Button(
                onClick = {
                    val token = authViewModel.authStatus.value.authData!!.token
                    if (name.isNotBlank() && price.isNotBlank() && description.isNotBlank() && category.isNotBlank() && imageUris.isNotEmpty() && imageUris.size <= 5) {
                        submitError = ""
                        maxPhotosExceededError = ""
                        productsViewModel.addProduct(
                            name,
                            price,
                            description,
                            category,
                            imageUris,
                            token
                        )
                    } else {
                        submitError = "You must fill the form."
                        if (imageUris.size > 5)
                            maxPhotosExceededError = "You can only add up to 5 photos. Try reelecting them."
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                if (!isLoading)
                    Text(text = "Submit Product")
                else
                    Text(text = "Please Wait...")
            }
            if (submitError.isNotBlank())
                Text(
                    text = submitError,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            if (maxPhotosExceededError.isNotBlank())
                Text(
                    text = maxPhotosExceededError,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

            PhotosPreview(imageUris, modifier = Modifier.fillMaxWidth())
        }
        if (uploadFinished) {
            // Reset upload status for using with subsequent uploads.
            productsViewModel.resetUploadStatus()
            // Go to ProductsScreen
            navController.navigate(Screen.Products.route) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun PhotosPreview(images: List<Uri?>, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(images) { image ->
            Image(
                painter = rememberCoilPainter(request = image),
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .shadow(8.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}