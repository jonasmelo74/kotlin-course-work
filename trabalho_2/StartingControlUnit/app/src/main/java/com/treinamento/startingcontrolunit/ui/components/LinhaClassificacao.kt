package com.treinamento.startingcontrolunit.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.model.ClassificacaoTime

@Composable
fun LinhaClassificacao(item: ClassificacaoTime, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${item.posicao}º", modifier = Modifier.width(32.dp))
        Text(text = item.time.nome, modifier = Modifier.width(150.dp))
        Text(text = "${item.pontos} pts", modifier = Modifier.width(60.dp))
        Text(text = "${item.vitorias}V ${item.empates}E ${item.derrotas}D")
    }
}
