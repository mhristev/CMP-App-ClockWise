package com.clockwise.features.company.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.websocket.Frame
import androidx.compose.ui.graphics.Color
import com.clockwise.features.company.domain.Company

@Composable
fun CompanyScreen(
    viewModel: CompanyViewModel,
    onCompanyClick: (String) -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Frame.Text("Companies") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.companies) { company ->
                            CompanyItem(
                                company = company,
                                onClick = { onCompanyClick(company.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CompanyItem(
    company: Company,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = company.name,
                style = MaterialTheme.typography.h4
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = company.description,
                style = MaterialTheme.typography.h4
            )
        }
    }
}