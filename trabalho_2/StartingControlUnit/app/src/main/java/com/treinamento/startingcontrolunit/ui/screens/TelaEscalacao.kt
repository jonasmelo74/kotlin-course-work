package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.data.buscarJogadoresPorTime
import com.treinamento.startingcontrolunit.model.Time
import com.treinamento.startingcontrolunit.ui.components.ItemJogador
import kotlinx.coroutines.launch

// Reaproveitada em dois fluxos de navegação:
// - vindo de RotaEscalacao (TelaDetalhePartida): timeVisitante != null -> mostra Tabs+Pager.
// - vindo de RotaEscalacaoTime (TelaClassificacao): timeVisitante == null -> mostra direto
//   o elenco daquele time, sem tabs.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEscalacao(
    timeCasa: Time,
    timeVisitante: Time?,
    aoClicarJogador: (playerId: Int) -> Unit,
    aoVoltar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escalação") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Text(text = "←")
                    }
                }
            )
        }
    ) { paddingInterno ->
        if (timeVisitante == null) {
            ListaJogadoresTime(
                nomeTime = timeCasa.nome,
                aoClicarJogador = aoClicarJogador,
                modifier = Modifier.padding(paddingInterno)
            )
        } else {
            EscalacaoComTabs(
                timeCasa = timeCasa,
                timeVisitante = timeVisitante,
                aoClicarJogador = aoClicarJogador,
                modifier = Modifier.padding(paddingInterno)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EscalacaoComTabs(
    timeCasa: Time,
    timeVisitante: Time,
    aoClicarJogador: (playerId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listaTimes = listOf(timeCasa, timeVisitante)
    val estadoPager = rememberPagerState(initialPage = 0) { listaTimes.size }
    val escopo = rememberCoroutineScope()

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = estadoPager.currentPage) {
            listaTimes.forEachIndexed { indice, time ->
                Tab(
                    selected = estadoPager.currentPage == indice,
                    onClick = { escopo.launch { estadoPager.animateScrollToPage(indice) } },
                    text = { Text("${time.escudoEmoji} ${time.nome}") }
                )
            }
        }
        HorizontalPager(state = estadoPager, modifier = Modifier.fillMaxSize()) { pagina ->
            ListaJogadoresTime(nomeTime = listaTimes[pagina].nome, aoClicarJogador = aoClicarJogador)
        }
    }
}

@Composable
private fun ListaJogadoresTime(
    nomeTime: String,
    aoClicarJogador: (playerId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val jogadoresDoTime = buscarJogadoresPorTime(nomeTime)
    val titulares = jogadoresDoTime.filter { it.titular }
    val reservas = jogadoresDoTime.filter { !it.titular }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { Text(text = "Titulares", modifier = Modifier.padding(16.dp)) }
        items(titulares, key = { it.id }) { jogador ->
            ItemJogador(jogador = jogador, onClick = { aoClicarJogador(jogador.id) })
        }
        item { Text(text = "Reservas", modifier = Modifier.padding(16.dp)) }
        items(reservas, key = { it.id }) { jogador ->
            ItemJogador(jogador = jogador, onClick = { aoClicarJogador(jogador.id) })
        }
    }
}
