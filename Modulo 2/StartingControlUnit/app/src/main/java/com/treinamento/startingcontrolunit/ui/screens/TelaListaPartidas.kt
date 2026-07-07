package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.treinamento.startingcontrolunit.data.partidas
import com.treinamento.startingcontrolunit.ui.components.CardPartida

// Tela inicial (Home): lista as partidas mockadas. A tela não conhece rotas -
// ao clicar num card, só avisa "cliquei no matchId X" via callback; quem decide
// para onde navegar é o NavGraph (navigation/NavGraph.kt).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaPartidas(aoClicarPartida: (matchId: Int) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Central da Partida") })
        }
    ) { paddingInterno ->
        LazyColumn(modifier = Modifier.padding(paddingInterno)) {
            items(partidas, key = { it.id }) { partida ->
                CardPartida(partida = partida, onClick = { aoClicarPartida(partida.id) })
            }
        }
    }
}
