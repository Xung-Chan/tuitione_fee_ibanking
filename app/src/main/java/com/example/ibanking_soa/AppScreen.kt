package com.example.ibanking_soa

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ibanking_soa.event.HistoryEffect
import com.example.ibanking_soa.event.LoginEffect
import com.example.ibanking_soa.event.PaymentDetailEffect
import com.example.ibanking_soa.event.TuitionFeeEffect
import com.example.ibanking_soa.ui.LoginScreen
import com.example.ibanking_soa.ui.PaymentHistoryDetails
import com.example.ibanking_soa.ui.PaymentHistory
import com.example.ibanking_soa.ui.PaymentDetails
import com.example.ibanking_soa.ui.PaymentSuccessful
import com.example.ibanking_soa.ui.TuitionFeeScreen
import com.example.ibanking_soa.uiState.SnackBarUS
import com.example.ibanking_soa.utils.GradientSnackBar
import com.example.ibanking_soa.utils.SnackBarType
import com.example.ibanking_soa.viewModel.AppViewModel
import com.example.ibanking_soa.viewModel.HistoryDetailVM
import com.example.ibanking_soa.viewModel.HistoryVM
import com.example.ibanking_soa.viewModel.LoginVM
import com.example.ibanking_soa.viewModel.PaymentDetailVM
import com.example.ibanking_soa.viewModel.PaymentResultVM
import com.example.ibanking_soa.viewModel.TuitionFeeVM
import kotlinx.coroutines.delay

enum class Screens {
    Login, TuitionFee, PaymentDetails,
    HistoryList, HistoryDetails,
    PaymentSuccessful
}

@Composable
fun AppScreen(
    appViewModel: AppViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    var snackBarState by remember {
        mutableStateOf<SnackBarUS?>(null)
    }

    LaunchedEffect(snackBarState) {
        if (snackBarState != null) {
            delay(3000L)
            snackBarState = null
        }
    }


    Box {
        NavHost(
            navController = navController,
            startDestination = Screens.Login.name
        ) {
            composable(route = Screens.Login.name) {
                val loginVM = hiltViewModel<LoginVM>()
                val uiState by loginVM.uiState.collectAsState()
                LaunchedEffect(Unit) {
                    loginVM.uiEffect.collect { effect ->
                        when (effect) {

                            LoginEffect.LoginSuccess -> {
                                snackBarState = SnackBarUS(
                                    message = "Đăng nhập thành công",
                                    type = SnackBarType.SUCCESS
                                )
                                navController.navigate(Screens.TuitionFee.name) {
                                    popUpTo(Screens.Login.name) { inclusive = true }
                                }
                            }

                            is LoginEffect.ShowSnackBar -> {
                                snackBarState = SnackBarUS(
                                    message = effect.message,
                                    type = effect.type
                                )
                            }

                        }
                    }
                }
                LoginScreen(
                    uiState = uiState,
                    onEvent = loginVM::onEvent,
                )
            }

            composable(route = Screens.TuitionFee.name) {
                val tuitionFeeVM = hiltViewModel<TuitionFeeVM>()
                val uiState by tuitionFeeVM.uiState.collectAsState()
                LaunchedEffect(Unit) {
                    tuitionFeeVM.uiEffect.collect { effect ->
                        when (effect) {
                            is TuitionFeeEffect.ShowSnackBar -> {
                                snackBarState = SnackBarUS(
                                    message = effect.message,
                                    type = effect.type
                                )
                            }

                            TuitionFeeEffect.NavigateHistory -> {
                                navController.navigate(Screens.HistoryList.name)
                            }

                            TuitionFeeEffect.NavigateToConfirmPayment -> {
                                navController.navigate(Screens.PaymentDetails.name)
                            }

                            TuitionFeeEffect.NavigateToLogin -> {
                                navController.navigate(Screens.Login.name) {
                                    popUpTo(Screens.TuitionFee.name) { inclusive = true }
                                }
                            }
                        }
                    }
                }
                TuitionFeeScreen(
                    uiState = uiState,
                    onEvent = tuitionFeeVM::onEvent,
                )
            }

            composable(route = Screens.PaymentDetails.name) {
                val paymentDetailVM = hiltViewModel<PaymentDetailVM>()
                val uiState by paymentDetailVM.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    paymentDetailVM.uiEffect.collect { effect ->
                        when (effect) {
                            PaymentDetailEffect.PaymentSuccess -> {
                                navController.navigate(Screens.PaymentSuccessful.name) {
                                    popUpTo(Screens.TuitionFee.name) { inclusive = false }
                                }
                            }

                            is PaymentDetailEffect.ShowSnackBar -> {
                                snackBarState = SnackBarUS(
                                    message = effect.message,
                                    type = effect.type
                                )
                            }
                        }
                    }
                }

                PaymentDetails(
                    uiState = uiState,
                    onEvent = paymentDetailVM::onEvent,
                    onBackClick = {
                        navController.popBackStack()
                    },
                )
            }
            composable(route = Screens.PaymentSuccessful.name) {
                val paymentResultVM = hiltViewModel<PaymentResultVM>()
                val payment by paymentResultVM.uiState.collectAsState()
                PaymentSuccessful(
                    payment = payment,
                    onBackClick = {
                        navController.navigate(Screens.TuitionFee.name) {
                            popUpTo(Screens.TuitionFee.name) { inclusive = false }
                        }
                    }
                )
            }

            composable(route = Screens.HistoryList.name) {
                val historyVM = hiltViewModel<HistoryVM>()
                val uiState by historyVM.uiState.collectAsState()
                LaunchedEffect(Unit) {
                    historyVM.uiEffect.collect { effect ->

                        when (effect) {
                            HistoryEffect.NavigateToPaymentDetail -> {
                                navController.navigate(Screens.HistoryDetails.name)
                            }

                            is HistoryEffect.ShowSnackBar -> {
                                snackBarState = SnackBarUS(
                                    message = effect.message,
                                    type = effect.type
                                )
                            }
                        }
                    }
                }
                PaymentHistory(
                    uiState = uiState,
                    onEvent = historyVM::onEvent,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = Screens.HistoryDetails.name) {
                val historyDetailVM = hiltViewModel<HistoryDetailVM>()
                val payment by historyDetailVM.uiState.collectAsState()
                PaymentHistoryDetails(
                    payment = payment,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
        snackBarState?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                GradientSnackBar(
                    message = snackBarState!!.message,
                    type = snackBarState!!.type,
                    onAction = {
                        snackBarState = null
                    },
                )
            }
        }
    }
}