package com.example.share2care

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.share2care.pages.AnnounceManagementPage
import com.example.share2care.pages.CreateAnnouncePage
import com.example.share2care.pages.HomePage
import com.example.share2care.pages.InitialPage
import com.example.share2care.pages.LoginPage
import com.example.share2care.pages.RegisterPage
import com.example.share2care.pages.EditLojaSocial
import com.example.share2care.pages.HomePageAnonymous


@Composable
fun AppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "initial", builder = {
        composable("initial"){
            InitialPage(navController,authViewModel)
        }
        composable("login"){
            LoginPage(navController,authViewModel)
        }
        composable("register"){
            RegisterPage(navController,authViewModel)
        }
        composable("home"){
            HomePage(navController,authViewModel)
        }
        composable("editLojaSocial"){
            EditLojaSocial(navController,authViewModel)
        }
        composable("homeAnonymous"){
            HomePageAnonymous(navController,authViewModel)
        }
        composable("announceManagement") {
            AnnounceManagementPage(navController,authViewModel)
        }
        composable("announceCreation") {
            CreateAnnouncePage(navController,authViewModel)
        }
    })
}