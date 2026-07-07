package com.treinamento.startingcontrolunit.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Badge
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.treinamento.startingcontrolunit.model.Jogador

@Composable
fun ItemJogador(jogador: Jogador, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(text = "${jogador.numeroCamisa} - ${jogador.nome}") },
        supportingContent = { Text(text = jogador.posicao) },
        trailingContent = {
            if (jogador.gols > 0) {
                Badge { Text(text = "${jogador.gols} gols") }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}
