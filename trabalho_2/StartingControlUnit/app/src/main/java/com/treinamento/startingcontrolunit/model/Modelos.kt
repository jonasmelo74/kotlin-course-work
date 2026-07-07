package com.treinamento.startingcontrolunit.model

import kotlinx.serialization.Serializable

// Time precisa ser @Serializable porque trafega como argumento de rota:
// RotaEscalacao carrega os dois Time da partida (ver navigation/Rotas.kt),
// então o Navigation Compose precisa saber serializar/desserializar esse tipo.
@Serializable
data class Time(
    val nome: String,
    val escudoEmoji: String,
    val cidade: String
)

data class Jogador(
    val id: Int,
    val nome: String,
    val posicao: String, // "Goleiro", "Zagueiro", "Meio-campo" ou "Atacante"
    val numeroCamisa: Int,
    val idade: Int,
    val timeNome: String, // vínculo com Time.nome
    val titular: Boolean,
    val gols: Int,
    val assistencias: Int,
    val cartoesAmarelos: Int,
    val cartoesVermelhos: Int
)

enum class StatusPartida { AO_VIVO, ENCERRADA, AGENDADA }

data class Partida(
    val id: Int,
    val timeCasa: Time,
    val timeVisitante: Time,
    val placarCasa: Int?,
    val placarVisitante: Int?,
    val dataHora: String,
    val estadio: String,
    val arbitro: String,
    val status: StatusPartida
)

data class ClassificacaoTime(
    val posicao: Int,
    val time: Time,
    val pontos: Int,
    val vitorias: Int,
    val empates: Int,
    val derrotas: Int
)
