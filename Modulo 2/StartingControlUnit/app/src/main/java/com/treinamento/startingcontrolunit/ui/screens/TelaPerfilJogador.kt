package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.treinamento.startingcontrolunit.data.buscarJogadorPorId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfilJogador(playerId: Int, aoVoltar: () -> Unit) {
    val jogador = buscarJogadorPorId(playerId) ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(jogador.nome) },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Text(text = "←")
                    }
                }
            )
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .padding(paddingInterno)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = jogador.nome, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    LinhaInfoJogador(rotulo = "Posição", valor = jogador.posicao)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    LinhaInfoJogador(rotulo = "Camisa", valor = "${jogador.numeroCamisa}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    LinhaInfoJogador(rotulo = "Idade", valor = "${jogador.idade} anos")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    LinhaInfoJogador(rotulo = "Time", valor = jogador.timeNome)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Estatísticas", fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Gols: ${jogador.gols}")
                    LinearProgressIndicator(
                        progress = { jogador.gols.coerceAtMost(20) / 20f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Assistências: ${jogador.assistencias}")
                    LinearProgressIndicator(
                        progress = { jogador.assistencias.coerceAtMost(20) / 20f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Cartões amarelos: ${jogador.cartoesAmarelos} · Cartões vermelhos: ${jogador.cartoesVermelhos}"
                    )
                }
            }
        }
    }
}

@Composable
private fun LinhaInfoJogador(rotulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = rotulo, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = valor, fontWeight = FontWeight.Medium)
    }
}
