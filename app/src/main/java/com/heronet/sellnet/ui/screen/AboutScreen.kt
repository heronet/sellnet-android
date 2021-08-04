package com.heronet.sellnet.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun AboutScreen() {
    Surface {
        SelectionContainer(modifier = Modifier.fillMaxWidth()) {
            val scrollState = rememberScrollState()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            ) {
                Text(text = "Sellnet", style = MaterialTheme.typography.h1)
                Text(text = "Copyright (C) 2021  Siratul Islam", color = MaterialTheme.colors.primary)
                Text(
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    text = "Sellnet is an E-commerce platform made to make life easy for people to buy online.\n" +
                            "You can buy products or list your own for sale. You can also add photos to make them more appealing.\n" +
                            "To top it off, You are completely free to use this product to your heart's extent.\n\n" +
                            "Thank You for using Sellnet."
                )
            }
        }
    }
}