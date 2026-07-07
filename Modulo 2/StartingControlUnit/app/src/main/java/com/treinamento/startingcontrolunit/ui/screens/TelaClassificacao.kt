package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.data.classificacao
import com.treinamento.startingcontrolunit.ui.components.LinhaClassificacao

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TelaClassificacao(aoVoltar: () -> Unit, aoClicarTime: (teamName: String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Classificação") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Text(text = "←")
                    }
                }
            )
        }
    ) { paddingInterno ->
        LazyColumn(modifier = Modifier.padding(paddingInterno)) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Pos", modifier = Modifier.width(32.dp), fontWeight = FontWeight.Bold)
                    Text(text = "Time", modifier = Modifier.width(150.dp), fontWeight = FontWeight.Bold)
                    Text(text = "Pts", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold)
                    Text(text = "V/E/D", fontWeight = FontWeight.Bold)
                }
            }
            items(classificacao, key = { it.posicao }) { item ->
                LinhaClassificacao(item = item, onClick = { aoClicarTime(item.time.nome) })
            }
        }
    }
}
