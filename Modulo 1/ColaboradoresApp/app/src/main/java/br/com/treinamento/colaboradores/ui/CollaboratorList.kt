package br.com.treinamento.colaboradores.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.treinamento.colaboradores.model.Collaborator

@Composable
fun CollaboratorList(
    collaborators: List<Collaborator>,
    onSelect: (Collaborator) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(collaborators, key = { it.id }) { collaborator ->
            CollaboratorCard(
                collaborator = collaborator,
                onSelect = { onSelect(collaborator) },
            )
        }
    }
}
