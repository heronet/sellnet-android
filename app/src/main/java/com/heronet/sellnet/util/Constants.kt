package com.heronet.sellnet.util

import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val API_URL = "https://sellnetx-ngmbdouzbq-uc.a.run.app/api/"
//    const val API_URL = "http://127.0.0.1:5000/api/" // Local Server
    const val PAGE_SIZE = 10

    val TOKEN by lazy { stringPreferencesKey("token") }
    val ID by lazy { stringPreferencesKey("id") }
    val NAME by lazy { stringPreferencesKey("name") }
    val ROLE by lazy { stringPreferencesKey("role") }

    val categories by lazy {
        listOf(
            "All",
            "Antiques",
            "Bikes",
            "Cars",
            "Clothes",
            "Computer Parts",
            "Computers",
            "Foods",
            "Groceries",
            "Games",
            "Phones",
            "Pets",
            "Real Estates",
            "Software",
            "Ships",
            "Vans",
            "Trucks"
        )
    }
}