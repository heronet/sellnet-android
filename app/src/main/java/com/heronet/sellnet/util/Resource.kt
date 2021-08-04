package com.heronet.sellnet.util

sealed class Resource<T>(val data: T? = null, val message: String? = null, val messages: List<String>? = null) {
    class Loading<T>(): Resource<T>(null, null)
    class Success<T>(data: T, message: String? = null): Resource<T>(data, message)
    class Error<T>(message: String, messages: List<String>? = null): Resource<T>(message = message, messages = messages)
}
