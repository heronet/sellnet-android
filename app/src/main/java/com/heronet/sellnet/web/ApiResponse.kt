package com.heronet.sellnet.web

data class ApiResponse<T>(
    var data: T,
    val size: Int
)
