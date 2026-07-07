package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.treinamento.startingcontrolunit.data.buscarPartidaPorId
import com.treinamento.startingcontrolunit.model.Partida
import com.treinamento.startingcontrolunit.model.StatusPartida

// Recebe só o matchId via navegação type-safe (RotaDetalhePartida) e busca a Partida
// no mock aqui dentro - a tela não guarda estado de negócio, só lê o dado fixo pelo id.
// aoVoltar aciona popBackStack() no NavGraph - a tela não conhece o NavHostController,
// só recebe o callback (mesmo padrão usado em TelaClassificacao/TelaEscalacao).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhePartida(
    matchId: Int,
    aoVoltar: () -> Unit,
    aoClicarEscalacao: (partida: Partida) -> Unit,
    aoClicarClassificacao: () -> Unit
) {
    val partida = buscarPartidaPorId(matchId) ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${partida.timeCasa.nome} x ${partida.timeVisitante.nome}") },
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
                    TextoStatus(partida.status)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimeComEscudo(
                            emoji = partida.timeCasa.escudoEmoji,
                            nome = partida.timeCasa.nome,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = textoPlacarOuHorario(partida),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(80.dp)
                        )

                        TimeComEscudo(
                            emoji = partida.timeVisitante.escudoEmoji,
                            nome = partida.timeVisitante.nome,
                            modifier = Modifier.weight(1f),
                            alinharDireita = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LinhaInfo(rotulo = "Data/hora", valor = partida.dataHora)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    LinhaInfo(rotulo = "Estádio", valor = partida.estadio)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    LinhaInfo(rotulo = "Árbitro", valor = partida.arbitro)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Passamos a própria Partida (não só o id) para o NavGraph montar a
            // RotaEscalacao com matchId + os dois Times já carregados, sem nova busca.
            AssistChip(
                onClick = { aoClicarEscalacao(partida) },
                label = { Text("Ver escalação") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = aoClicarClassificacao,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver classificação")
            }
        }
    }
}

@Composable
private fun TimeComEscudo(
    emoji: String,
    nome: String,
    modifier: Modifier = Modifier,
    alinharDireita: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alinharDireita) Alignment.End else Alignment.Start
    ) {
        Text(text = emoji, fontSize = 28.sp)
        Text(
            text = nome,
            fontWeight = FontWeight.Bold,
            textAlign = if (alinharDireita) TextAlign.End else TextAlign.Start
        )
    }
}

@Composable
private fun LinhaInfo(rotulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = rotulo, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = valor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TextoStatus(status: StatusPartida) {
    val texto = when (status) {
        StatusPartida.AO_VIVO -> "🔴 Ao vivo"
        StatusPartida.ENCERRADA -> "Encerrado"
        StatusPartida.AGENDADA -> "Agendado"
    }
    Text(text = texto, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
}

private fun textoPlacarOuHorario(partida: Partida): String =
    if (partida.placarCasa != null && partida.placarVisitante != null) {
        "${partida.placarCasa} x ${partida.placarVisitante}"
    } else {
        partida.dataHora
    }
