@file:OptIn(ExperimentalMaterialApi::class)

package com.clockwise.features.shiftexchange.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clockwise.features.shiftexchange.presentation.components.AvailableShiftCard
import com.clockwise.features.shiftexchange.presentation.components.MyPostedShiftCard
import com.clockwise.features.shiftexchange.presentation.components.PostShiftDialog
import com.clockwise.features.shiftexchange.presentation.components.ShiftRequestDialog
import com.clockwise.features.shiftexchange.presentation.components.ViewRequestsDialog

@Composable
fun ShiftExchangeScreen(
    state: ShiftExchangeState,
    onAction: (ShiftExchangeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = state.selectedTab == ShiftExchangeTab.AVAILABLE_SHIFTS,
                onClick = { onAction(ShiftExchangeAction.SelectTab(ShiftExchangeTab.AVAILABLE_SHIFTS)) },
                text = { Text("Available Shifts") }
            )
            Tab(
                selected = state.selectedTab == ShiftExchangeTab.MY_POSTED_EXCHANGES,
                onClick = { onAction(ShiftExchangeAction.SelectTab(ShiftExchangeTab.MY_POSTED_EXCHANGES)) },
                text = { Text("My Posted Exchanges") }
            )
        }
        
        // Error message
        if (state.errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { onAction(ShiftExchangeAction.ClearError) }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
        
        // Content based on selected tab
        when (state.selectedTab) {
            ShiftExchangeTab.AVAILABLE_SHIFTS -> {
                AvailableShiftsContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.weight(1f)
                )
            }
            ShiftExchangeTab.MY_POSTED_EXCHANGES -> {
                MyPostedExchangesContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
    
    // Dialogs
    if (state.showPostShiftDialog) {
        PostShiftDialog(
            userShifts = state.userShifts,
            selectedShift = state.selectedShiftToPost,
            onShiftSelected = { onAction(ShiftExchangeAction.SelectShiftToPost(it)) },
            onPostShift = { onAction(ShiftExchangeAction.PostShiftToMarketplace(it)) },
            onDismiss = { onAction(ShiftExchangeAction.HidePostShiftDialog) }
        )
    }
    
    if (state.showRequestDialog && state.selectedExchangeShift != null) {
        ShiftRequestDialog(
            exchangeShift = state.selectedExchangeShift,
            userShifts = state.userShifts,
            onSubmitRequest = { requestType, swapShiftId, swapShiftPosition, swapShiftStartTime, swapShiftEndTime ->
                onAction(ShiftExchangeAction.SubmitShiftRequest(
                    exchangeShiftId = state.selectedExchangeShift.id,
                    requestType = requestType,
                    swapShiftId = swapShiftId,
                    swapShiftPosition = swapShiftPosition,
                    swapShiftStartTime = swapShiftStartTime,
                    swapShiftEndTime = swapShiftEndTime,
                    requesterUserFirstName = null, // Will be populated in ViewModel from UserService
                    requesterUserLastName = null   // Will be populated in ViewModel from UserService
                ))
            },
            onDismiss = { onAction(ShiftExchangeAction.HideRequestDialog) }
        )
    }
    
    if (state.showRequestsDialog && state.selectedExchangeShiftForRequests != null) {
        ViewRequestsDialog(
            exchangeShift = state.selectedExchangeShiftForRequests,
            requests = state.myShiftRequests[state.selectedExchangeShiftForRequests.id] ?: emptyList(),
            onAcceptRequest = { requestId ->
                onAction(ShiftExchangeAction.AcceptRequest(
                    state.selectedExchangeShiftForRequests.id,
                    requestId
                ))
            },
            onDismiss = { onAction(ShiftExchangeAction.HideRequestsDialog) }
        )
    }
}

@Composable
private fun AvailableShiftsContent(
    state: ShiftExchangeState,
    onAction: (ShiftExchangeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    println("DEBUG: AvailableShiftsContent - isLoading: ${state.isLoadingAvailableShifts}, posted shifts count: ${state.availableShifts.size}")
    if (state.availableShifts.isNotEmpty()) {
        state.availableShifts.forEach { exchangeShift ->
            println("DEBUG: AvailableShiftsContent - ExchangeShift: ${exchangeShift.id}, ${exchangeShift.position}, posted by ${exchangeShift.posterName}")
        }
    }

    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoadingAvailableShifts,
        onRefresh = {
            onAction(ShiftExchangeAction.LoadAvailableShifts)
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (state.isLoadingAvailableShifts && state.availableShifts.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.availableShifts.isEmpty() && !state.isLoadingAvailableShifts) {
            Text(
                text = "No shifts available for exchange",
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.availableShifts) { exchangeShift ->
                    val isOwnShift = state.currentUserId != null && 
                                   exchangeShift.posterUserId == state.currentUserId
                    val isLoading = state.cancellingExchangeShiftIds.contains(exchangeShift.id)
                    
                    AvailableShiftCard(
                        exchangeShift = exchangeShift,
                        isOwnShift = isOwnShift,
                        isLoading = isLoading,
                        onRequestShift = { 
                            onAction(ShiftExchangeAction.ShowRequestDialog(exchangeShift))
                        },
                        onCancelShift = if (isOwnShift) {
                            { onAction(ShiftExchangeAction.CancelExchangeShift(exchangeShift.id)) }
                        } else null
                    )
                }
            }
        }

        // Pull to refresh indicator
        PullRefreshIndicator(
            refreshing = state.isLoadingAvailableShifts,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = MaterialTheme.colors.primary
        )
        
        // Floating Action Button for posting shifts
        FloatingActionButton(
            onClick = { onAction(ShiftExchangeAction.ShowPostShiftDialog) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Post shift for exchange",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun MyPostedExchangesContent(
    state: ShiftExchangeState,
    onAction: (ShiftExchangeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoadingMyPostedShifts,
        onRefresh = {
            onAction(ShiftExchangeAction.LoadMyPostedShifts)
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (state.isLoadingMyPostedShifts && state.myPostedShifts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.myPostedShifts.isEmpty() && !state.isLoadingMyPostedShifts) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "You haven't posted any shifts for exchange",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Switch to the Available Shifts tab to post your first shift",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.myPostedShifts) { exchangeShift ->
                    MyPostedShiftCard(
                        exchangeShift = exchangeShift,
                        requests = state.myShiftRequests[exchangeShift.id] ?: emptyList(),
                        onViewRequests = { 
                            onAction(ShiftExchangeAction.ShowRequestsDialog(exchangeShift))
                        },
                        onCancelShift = {
                            onAction(ShiftExchangeAction.CancelExchangeShift(exchangeShift.id))
                        }
                    )
                }
            }
        }

        // Pull to refresh indicator
        PullRefreshIndicator(
            refreshing = state.isLoadingMyPostedShifts,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = MaterialTheme.colors.primary
        )
    }
}