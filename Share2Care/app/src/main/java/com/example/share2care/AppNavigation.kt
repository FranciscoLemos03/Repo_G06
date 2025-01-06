package com.example.share2care

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.share2care.pages.AgregadoDetailsPage
import com.example.share2care.pages.AgregadoManagementPage
import com.example.share2care.pages.AnnounceDetailsPage
import com.example.share2care.pages.AnnounceManagementPage
import com.example.share2care.pages.BeneficiarioProfilePage
import com.example.share2care.pages.BeneficiariosManagementPage
import com.example.share2care.pages.CheckInPage
import com.example.share2care.pages.CheckOutPage
import com.example.share2care.pages.CreateAgregadoPage
import com.example.share2care.pages.CreateAnnouncePage
import com.example.share2care.pages.CreateBeneficiarioPage
import com.example.share2care.pages.CreateTicketPage
import com.example.share2care.pages.EditAgregadoPage
import com.example.share2care.pages.EditBeneficiarioPage
import com.example.share2care.pages.HomePage
import com.example.share2care.pages.InitialPage
import com.example.share2care.pages.LoginPage
import com.example.share2care.pages.RegisterPage
import com.example.share2care.pages.EditLojaSocial
import com.example.share2care.pages.HomePageAnonymous
import com.example.share2care.pages.InitialBeneficiarioPage
import com.example.share2care.pages.TicketsPage
import com.example.share2care.ui.components.EditAnuncioPage


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
        composable(
            "announceDetails/{announceId}",
            arguments = listOf(navArgument("announceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val announceId = backStackEntry.arguments?.getString("announceId")
            AnnounceDetailsPage(announceId = announceId ?: "",navController)
        }
        composable(
            "editAnuncio/{announceId}",
            arguments = listOf(navArgument("announceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val announceId = backStackEntry.arguments?.getString("announceId")
            announceId?.let {
                EditAnuncioPage(navController = navController, authViewModel = authViewModel, announceId = announceId)
            }
        }
        composable(
            "createTicket/{announceId}",
            arguments = listOf(navArgument("announceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val announceId = backStackEntry.arguments?.getString("announceId")
            announceId?.let {
                CreateTicketPage(navController = navController, authViewModel = authViewModel, announceId = announceId)
            }
        }
        composable("ticketsPage") {
            TicketsPage(navController,authViewModel)
        }
        composable("initialbeneficiario"){
            InitialBeneficiarioPage(navController,authViewModel)
        }
        composable("agregadomanagement"){
            AgregadoManagementPage(navController,authViewModel)
        }
        composable("createagregado"){
            CreateAgregadoPage(navController,authViewModel)
        }
        composable("beneficiariomanagement"){
            BeneficiariosManagementPage(navController,authViewModel)
        }
        composable(
            "agregado/{agregadoID}",
            arguments = listOf(navArgument("agregadoID") { type = NavType.StringType })
        ) { backStackEntry ->
            val agregadoID = backStackEntry.arguments?.getString("agregadoID")
            agregadoID?.let {
                AgregadoDetailsPage(agregadoID, navController, authViewModel)
            }
        }
        composable(
            "createbeneficiario/{agregadoID}",
            arguments = listOf(navArgument("agregadoID") { type = NavType.StringType })
        ) { backStackEntry ->
            val agregadoID = backStackEntry.arguments?.getString("agregadoID")
            agregadoID?.let {
                CreateBeneficiarioPage(agregadoID, navController, authViewModel)
            }
        }
        composable(
            "updateagregado/{agregadoID}",
            arguments = listOf(navArgument("agregadoID") { type = NavType.StringType })
        ) { backStackEntry ->
            val agregadoID = backStackEntry.arguments?.getString("agregadoID")
            agregadoID?.let {
                EditAgregadoPage(agregadoID, navController, authViewModel)
            }
        }
        composable(
            "beneficiario/{beneficiarioID}",
            arguments = listOf(navArgument("beneficiarioID") { type = NavType.StringType })
        ) { backStackEntry ->
            val beneficiarioID = backStackEntry.arguments?.getString("beneficiarioID")
            beneficiarioID?.let {
                BeneficiarioProfilePage(beneficiarioID, navController, authViewModel)
            }
        }
        composable(
            "editbeneficiario/{beneficiarioID}",
            arguments = listOf(navArgument("beneficiarioID") { type = NavType.StringType })
        ) { backStackEntry ->
            val beneficiarioID = backStackEntry.arguments?.getString("beneficiarioID")
            beneficiarioID?.let {
                EditBeneficiarioPage(beneficiarioID, navController, authViewModel)
            }
        }
        composable("checkin") {
            CheckInPage(navController,authViewModel)
        }
        composable("checkout") {
            CheckOutPage(navController,authViewModel)
        }
    })
}