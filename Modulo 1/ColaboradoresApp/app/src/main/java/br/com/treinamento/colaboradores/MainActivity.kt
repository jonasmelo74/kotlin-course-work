package br.com.treinamento.colaboradores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import br.com.treinamento.colaboradores.ui.CollaboratorScreen
import br.com.treinamento.colaboradores.ui.theme.ColaboradoresAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColaboradoresAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CollaboratorScreen()
                }
            }
        }
    }
}
