package com.heronet.sellnet.ui.navigation

import androidx.annotation.StringRes
import com.heronet.sellnet.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Products: Screen("products", R.string.products)
    object AddProduct: Screen("add-product", R.string.addProduct)
    object UserProducts: Screen("user-products", R.string.userProducts)
    object Login: Screen("login", R.string.login)
    object Register: Screen("register", R.string.register)
    object About: Screen("about", R.string.about)
}
