package br.com.treinamento.colaboradores.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.treinamento.colaboradores.ui.theme.AdministrativoCardColor
import br.com.treinamento.colaboradores.ui.theme.FinanceiroCardColor
import br.com.treinamento.colaboradores.ui.theme.GerenciaCardColor
import br.com.treinamento.colaboradores.ui.theme.SuporteCardColor

enum class Level(val label: String, val icon: ImageVector, val cardColor: Color) {
    ADMINISTRATIVO("Administrativo", Icons.Default.Business, AdministrativoCardColor),
    FINANCEIRO("Financeiro", Icons.Default.AttachMoney, FinanceiroCardColor),
    GERENCIA("Gerência", Icons.Default.SupervisorAccount, GerenciaCardColor),
    SUPORTE("Suporte", Icons.Default.Build, SuporteCardColor),
}
