package br.com.treinamento.colaboradores.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.com.treinamento.colaboradores.model.Collaborator
import br.com.treinamento.colaboradores.model.Level

class CollaboratorViewModel : ViewModel() {

    val collaborators = mutableStateListOf<Collaborator>()

    var selected by mutableStateOf<Collaborator?>(null)
        private set

    private var nextId = 1

    fun add(name: String, email: String, level: Level) {
        collaborators.add(Collaborator(nextId++, name, email, level))
    }

    fun select(collaborator: Collaborator) {
        selected = collaborator
    }

    fun updateSelected(name: String, email: String, level: Level) {
        val current = selected ?: return
        val index = collaborators.indexOfFirst { it.id == current.id }
        if (index != -1) {
            collaborators[index] = current.copy(name = name, email = email, level = level)
        }
        selected = null
    }

    fun removeSelected() {
        val current = selected ?: return
        collaborators.removeAll { it.id == current.id }
        selected = null
    }

    fun clearSelection() {
        selected = null
    }
}
