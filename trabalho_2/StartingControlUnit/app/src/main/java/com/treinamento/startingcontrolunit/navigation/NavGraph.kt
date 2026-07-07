package com.treinamento.startingcontrolunit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.treinamento.startingcontrolunit.data.times
import com.treinamento.startingcontrolunit.model.Time
import com.treinamento.startingcontrolunit.ui.screens.TelaClassificacao
import com.treinamento.startingcontrolunit.ui.screens.TelaDetalhePartida
import com.treinamento.startingcontrolunit.ui.screens.TelaEscalacao
import com.treinamento.startingcontrolunit.ui.screens.TelaListaPartidas
import com.treinamento.startingcontrolunit.ui.screens.TelaPerfilJogador
import kotlin.reflect.typeOf

// NavHost central do app: cada rota é uma classe/objeto @Serializable (ver Rotas.kt),
// então a navegação passa parâmetros type-safe em vez de Strings/Bundles soltos.
// As telas não conhecem o NavHostController - só recebem lambdas de callback.
@Composable
fun NavGraph() {
    val controladorNavegacao: NavHostController = rememberNavController()

    NavHost(navController = controladorNavegacao, startDestination = RotaListaPartidas) {
        composable<RotaListaPartidas> {
            TelaListaPartidas(
                aoClicarPartida = { matchId ->
                    controladorNavegacao.navigate(RotaDetalhePartida(matchId))
                }
            )
        }

        composable<RotaDetalhePartida> { backStackEntry ->
            val rota: RotaDetalhePartida = backStackEntry.toRoute()
            TelaDetalhePartida(
                matchId = rota.matchId,
                aoVoltar = { controladorNavegacao.popBackStack() },
                aoClicarEscalacao = { partida ->
                    controladorNavegacao.navigate(
                        RotaEscalacao(
                            matchId = partida.id,
                            timeCasa = partida.timeCasa,
                            timeVisitante = partida.timeVisitante
                        )
                    )
                },
                aoClicarClassificacao = { controladorNavegacao.navigate(RotaClassificacao) }
            )
        }

        composable<RotaEscalacao>(
            typeMap = mapOf(typeOf<Time>() to TimeNavType)
        ) { backStackEntry ->
            val rota: RotaEscalacao = backStackEntry.toRoute()
            TelaEscalacao(
                timeCasa = rota.timeCasa,
                timeVisitante = rota.timeVisitante,
                aoClicarJogador = { playerId ->
                    controladorNavegacao.navigate(RotaPerfilJogador(playerId))
                },
                aoVoltar = { controladorNavegacao.popBackStack() }
            )
        }

        composable<RotaEscalacaoTime> { backStackEntry ->
            val rota: RotaEscalacaoTime = backStackEntry.toRoute()
            val time = times.first { it.nome == rota.teamName }
            TelaEscalacao(
                timeCasa = time,
                timeVisitante = null,
                aoClicarJogador = { playerId ->
                    controladorNavegacao.navigate(RotaPerfilJogador(playerId))
                },
                aoVoltar = { controladorNavegacao.popBackStack() }
            )
        }

        composable<RotaPerfilJogador> { backStackEntry ->
            val rota: RotaPerfilJogador = backStackEntry.toRoute()
            TelaPerfilJogador(
                playerId = rota.playerId,
                aoVoltar = { controladorNavegacao.popBackStack() }
            )
        }

        composable<RotaClassificacao> {
            TelaClassificacao(
                aoVoltar = { controladorNavegacao.popBackStack() },
                aoClicarTime = { teamName ->
                    controladorNavegacao.navigate(RotaEscalacaoTime(teamName))
                }
            )
        }
    }
}
