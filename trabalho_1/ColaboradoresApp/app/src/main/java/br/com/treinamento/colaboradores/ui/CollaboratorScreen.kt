package br.com.treinamento.colaboradores.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.treinamento.colaboradores.viewmodel.CollaboratorViewModel

@Composable
fun CollaboratorScreen(
    modifier: Modifier = Modifier,
    viewModel: CollaboratorViewModel = viewModel(),
) {
    Column(modifier = modifier.fillMaxSize()) {
        CollaboratorForm(
            selected = viewModel.selected,
            onSubmit = { name, email, level -> viewModel.add(name, email, level) },
            onUpdate = { name, email, level -> viewModel.updateSelected(name, email, level) },
            onRemove = viewModel::removeSelected,
            onCancel = viewModel::clearSelection,
        )

        CollaboratorList(
            collaborators = viewModel.collaborators,
            onSelect = viewModel::select,
            modifier = Modifier.weight(1f),
        )
    }
}
