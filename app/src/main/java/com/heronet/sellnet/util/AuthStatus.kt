package com.heronet.sellnet.util

import com.heronet.sellnet.model.AuthData

sealed class AuthStatus(val authData: AuthData? = null) {
    class Authenticated(authData: AuthData): AuthStatus(authData)
    class Unauthenticated: AuthStatus(null)
}