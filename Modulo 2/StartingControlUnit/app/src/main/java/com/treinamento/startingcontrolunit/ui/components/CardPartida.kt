package com.treinamento.startingcontrolunit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.model.Partida
import com.treinamento.startingcontrolunit.model.StatusPartida

@Composable
fun CardPartida(partida: Partida, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${partida.timeCasa.escudoEmoji} ${partida.timeCasa.nome}",
                    fontWeight = FontWeight.Bold
                )
                Text(text = textoPlacarOuHorario(partida))
                Text(
                    text = "${partida.timeVisitante.nome} ${partida.timeVisitante.escudoEmoji}",
                    fontWeight = FontWeight.Bold
                )
            }
            Text(text = textoStatus(partida.status), modifier = Modifier.padding(top = 8.dp))
        }
    }
}

private fun textoPlacarOuHorario(partida: Partida): String =
    if (partida.placarCasa != null && partida.placarVisitante != null) {
        "${partida.placarCasa} x ${partida.placarVisitante}"
    } else {
        partida.dataHora
    }

private fun textoStatus(status: StatusPartida): String = when (status) {
    StatusPartida.AO_VIVO -> "🔴 Ao vivo"
    StatusPartida.ENCERRADA -> "Encerrado"
    StatusPartida.AGENDADA -> "Agendado"
}
