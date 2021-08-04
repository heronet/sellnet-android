package com.heronet.sellnet.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.heronet.sellnet.ui.navigation.NavHostContainer
import com.heronet.sellnet.ui.navigation.Screen
import com.heronet.sellnet.util.AuthStatus
import com.heronet.sellnet.viewmodel.AuthViewModel
import com.heronet.sellnet.viewmodel.ProductsViewModel

@Composable
fun MainScreen(productsViewModel: ProductsViewModel, authViewModel: AuthViewModel) {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val navDestinations = remember {
        listOf(
            Screen.Products,
            Screen.UserProducts,
            Screen.Login
        )
    }
    val navIcons = remember {
        mapOf(
            Screen.Products.route to Icons.Filled.ShoppingCart,
            Screen.UserProducts.route to Icons.Filled.MenuBook,
            Screen.Login.route to Icons.Filled.Login,
        )
    }
    val isTokenRefreshing by remember { authViewModel.isRefreshing }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var menuVisible by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { ToolbarText(navBackStackEntry = navBackStackEntry) },
                actions = {
                    IconButton(onClick = { menuVisible = !menuVisible }) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                    DropdownMenu(
                        expanded = menuVisible,
                        onDismissRequest = { menuVisible = false }
                    ) {
                        when(authViewModel.authStatus.value) {
                            is AuthStatus.Authenticated -> {
                                DropdownMenuItem(onClick = { authViewModel.logout(); menuVisible = !menuVisible }) {
                                    Text(text = "Logout")
                                }
                            }
                            else -> {}
                        }
                        DropdownMenuItem(onClick = { navController.navigate("about"); menuVisible = !menuVisible }) {
                            Text(text = "About")
                        }
                    }
                },
                navigationIcon = {
                    ToolbarIcon(
                        navBackStackEntry = navBackStackEntry,
                        onClick = { navController.popBackStack() }
                    )
                }
            )
        },
        content = { innerPadding ->
            when {
                isTokenRefreshing -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                // Let users in even if token refresh fails. No internet will be handled by other Screens.
                else -> {
                    NavHostContainer(
                        navController = navController,
                        productsViewModel = productsViewModel,
                        authViewModel = authViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        },
        bottomBar = {
            if (showBottomNav(navController = navController)) {
                BottomNavigation {
                    val currentDestination = navBackStackEntry?.destination
                    navDestinations.filter {
                        when(authViewModel.authStatus.value) {
                            is AuthStatus.Authenticated -> it.route != Screen.Login.route

                            is AuthStatus.Unauthenticated -> it.route != Screen.UserProducts.route
                        }
                    }.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(
                                imageVector = navIcons[screen.route]!!,
                                contentDescription = null
                            )},
                            label = { Text(text = stringResource(id = screen.resourceId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
@Composable
fun showBottomNav(navController: NavHostController): Boolean {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    return currentDestination?.hierarchy?.any {
        it.route == Screen.Products.route
                || it.route == Screen.UserProducts.route
                || it.route == Screen.Login.route
    } == true
}

@Composable
fun ToolbarIcon(navBackStackEntry: NavBackStackEntry?, onClick: () -> Unit) {
    when(navBackStackEntry?.destination?.route) {
        "${Screen.Products.route}/{productId}" -> {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
        Screen.AddProduct.route -> {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
        Screen.UserProducts.route -> {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
        Screen.Login.route -> {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
        Screen.Register.route -> {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
        Screen.About.route -> {
            IconButton(onClick = onClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
        else -> IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Default.Store, contentDescription = null)
        }
    }
}
@Composable
fun ToolbarText(navBackStackEntry: NavBackStackEntry?) {
    when(navBackStackEntry?.destination?.route) {
        Screen.Products.route -> {
            Text(
                text = stringResource(id = Screen.Products.resourceId),
                fontSize = 20.sp
            )
        }
        "${Screen.Products.route}/{productId}" -> {
            Text(text = stringResource(id = Screen.Products.resourceId), fontSize = 20.sp)
        }
        Screen.AddProduct.route -> {
            Text(text = stringResource(id = Screen.AddProduct.resourceId), fontSize = 20.sp)
        }
        Screen.UserProducts.route -> {
            Text(text = stringResource(id = Screen.UserProducts.resourceId), fontSize = 20.sp)
        }
        Screen.Login.route -> {
            Text(text = stringResource(id = Screen.Login.resourceId), fontSize = 20.sp)
        }
        Screen.Register.route -> {
            Text(text = stringResource(id = Screen.Register.resourceId), fontSize = 20.sp)
        }
        Screen.About.route -> {
            Text(text = stringResource(id = Screen.About.resourceId), fontSize = 20.sp)
        }
        else -> {
            Text(text = "Sellnet", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}