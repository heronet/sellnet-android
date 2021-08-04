package com.heronet.sellnet

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.heronet.sellnet.ui.screen.MainScreen
import com.heronet.sellnet.ui.theme.SellnetTheme
import com.heronet.sellnet.viewmodel.AuthViewModel
import com.heronet.sellnet.viewmodel.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "authData")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val productsViewModel: ProductsViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SellnetTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(productsViewModel, authViewModel)
                }
            }
        }
    }
}
