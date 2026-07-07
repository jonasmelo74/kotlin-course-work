package com.treinamento.startingcontrolunit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.treinamento.startingcontrolunit.navigation.NavGraph
import com.treinamento.startingcontrolunit.ui.theme.StartingControlUnitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StartingControlUnitTheme {
                NavGraph()
            }
        }
    }
}
