package br.com.treinamento.colaboradores.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.treinamento.colaboradores.model.Collaborator

@Composable
fun CollaboratorCard(
    collaborator: Collaborator,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = collaborator.level.cardColor),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                imageVector = collaborator.level.icon,
                contentDescription = collaborator.level.label,
                modifier = Modifier.size(40.dp),
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(collaborator.name)
                Text(collaborator.email)
                Text(collaborator.level.label)
            }

            Button(onClick = onSelect) {
                Text("Selecionar")
            }
        }
    }
}
