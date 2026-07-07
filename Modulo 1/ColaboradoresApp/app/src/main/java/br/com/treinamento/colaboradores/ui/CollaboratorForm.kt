package br.com.treinamento.colaboradores.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.treinamento.colaboradores.model.Collaborator
import br.com.treinamento.colaboradores.model.Level
import br.com.treinamento.colaboradores.ui.theme.CadastrarEditarButtonColor
import br.com.treinamento.colaboradores.ui.theme.RemoverButtonColor

@Composable
fun CollaboratorForm(
    selected: Collaborator?,
    onSubmit: (name: String, email: String, level: Level) -> Unit,
    onUpdate: (name: String, email: String, level: Level) -> Unit,
    onRemove: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var level by remember { mutableStateOf(Level.ADMINISTRATIVO) }

    LaunchedEffect(selected) {
        name = selected?.name ?: ""
        email = selected?.email ?: ""
        level = selected?.level ?: Level.ADMINISTRATIVO
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
        )

        LevelDropdown(
            selectedLevel = level,
            onLevelChange = { level = it },
            modifier = Modifier.fillMaxWidth(),
        )

        val canSubmit = name.isNotBlank() && email.isNotBlank()

        if (selected == null) {
            Button(
                onClick = {
                    onSubmit(name, email, level)
                    name = ""
                    email = ""
                    level = Level.ADMINISTRATIVO
                },
                enabled = canSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = CadastrarEditarButtonColor),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Cadastrar")
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = { onUpdate(name, email, level) },
                    enabled = canSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = CadastrarEditarButtonColor),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Editar")
                }

                Button(
                    onClick = onRemove,
                    colors = ButtonDefaults.buttonColors(containerColor = RemoverButtonColor),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Remover")
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}
