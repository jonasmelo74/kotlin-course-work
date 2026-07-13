package com.treinamento.startingcontrolunit.data

import com.treinamento.startingcontrolunit.model.ClassificacaoTime
import com.treinamento.startingcontrolunit.model.Jogador
import com.treinamento.startingcontrolunit.model.Partida
import com.treinamento.startingcontrolunit.model.StatusPartida
import com.treinamento.startingcontrolunit.model.Time

// ---------- Times ----------

val times = listOf(
    Time("Flamengo", "", "Rio de Janeiro"),
    Time("Palmeiras", "", "São Paulo"),
    Time("São Paulo", "", "São Paulo"),
    Time("Corinthians", "", "São Paulo"),
    Time("Grêmio", "", "Porto Alegre"),
    Time("Internacional", "", "Porto Alegre"),
    Time("Atlético-MG", "", "Belo Horizonte"),
    Time("Fluminense", "", "Rio de Janeiro")
)

private fun timePorNome(nome: String): Time = times.first { it.nome == nome }

// ---------- Partidas ----------
// Mix de status para exercitar os três estados na Home (Ao vivo / Encerrado / Agendado).

val partidas = listOf(
    Partida(1, timePorNome("Flamengo"), timePorNome("Palmeiras"), 2, 1, "05/07 16:00", "Maracanã", "Carlos Mendes", StatusPartida.ENCERRADA),
    Partida(2, timePorNome("Corinthians"), timePorNome("São Paulo"), 1, 1, "06/07 18:30", "Neo Química Arena", "Ricardo Alves", StatusPartida.AO_VIVO),
    Partida(3, timePorNome("Grêmio"), timePorNome("Internacional"), null, null, "07/07 20:00", "Arena do Grêmio", "Fernando Souza", StatusPartida.AGENDADA),
    Partida(4, timePorNome("Atlético-MG"), timePorNome("Fluminense"), 3, 0, "05/07 19:00", "Arena MRV", "Paulo Ribeiro", StatusPartida.ENCERRADA),
    Partida(5, timePorNome("Palmeiras"), timePorNome("Corinthians"), null, null, "10/07 16:00", "Allianz Parque", "Carlos Mendes", StatusPartida.AGENDADA),
    Partida(6, timePorNome("São Paulo"), timePorNome("Flamengo"), 0, 2, "06/07 21:00", "MorumBIS", "Ricardo Alves", StatusPartida.AO_VIVO),
    Partida(7, timePorNome("Internacional"), timePorNome("Atlético-MG"), 1, 1, "04/07 17:00", "Beira-Rio", "Fernando Souza", StatusPartida.ENCERRADA),
    Partida(8, timePorNome("Fluminense"), timePorNome("Grêmio"), null, null, "12/07 16:00", "Maracanã", "Paulo Ribeiro", StatusPartida.AGENDADA)
)

fun buscarPartidaPorId(id: Int): Partida? = partidas.firstOrNull { it.id == id }

// ---------- Classificação ----------
// Pontos/V/E/D fixos e plausíveis (cada time soma 14 jogos), não precisam bater
// matematicamente com as 8 partidas acima (representam só uma rodada da temporada).

val classificacao = listOf(
    ClassificacaoTime(1, timePorNome("Palmeiras"), 30, 9, 3, 2),
    ClassificacaoTime(2, timePorNome("Flamengo"), 28, 8, 4, 2),
    ClassificacaoTime(3, timePorNome("Grêmio"), 25, 7, 4, 3),
    ClassificacaoTime(4, timePorNome("Corinthians"), 23, 6, 5, 3),
    ClassificacaoTime(5, timePorNome("São Paulo"), 21, 6, 3, 5),
    ClassificacaoTime(6, timePorNome("Internacional"), 19, 5, 4, 5),
    ClassificacaoTime(7, timePorNome("Atlético-MG"), 17, 4, 5, 5),
    ClassificacaoTime(8, timePorNome("Fluminense"), 12, 3, 3, 8)
)

// ---------- Jogadores ----------
// Elenco 100% fictício (nomes inventados) dentro de cada clube real: 11 titulares + 5
// reservas por time = 128 jogadores. Cada um dos 16 "moldes" de posição abaixo é aplicado
// aos 8 times, variando nome/idade/estatísticas de forma determinística pelos índices de
// time e posição — continua sendo dado 100% estático, sem API nem persistência.

private data class MoldePosicao(val posicao: String, val numero: Int, val titular: Boolean)

private val moldes = listOf(
    MoldePosicao("Goleiro", 1, true),
    MoldePosicao("Zagueiro", 2, true),
    MoldePosicao("Zagueiro", 3, true),
    MoldePosicao("Zagueiro", 4, true),
    MoldePosicao("Zagueiro", 5, true),
    MoldePosicao("Meio-campo", 6, true),
    MoldePosicao("Meio-campo", 7, true),
    MoldePosicao("Meio-campo", 8, true),
    MoldePosicao("Meio-campo", 10, true),
    MoldePosicao("Atacante", 9, true),
    MoldePosicao("Atacante", 11, true),
    MoldePosicao("Goleiro", 12, false),
    MoldePosicao("Zagueiro", 13, false),
    MoldePosicao("Zagueiro", 14, false),
    MoldePosicao("Meio-campo", 15, false),
    MoldePosicao("Atacante", 16, false)
)

private val primeirosNomes = listOf(
    "Gabriel", "Lucas", "Matheus", "Rafael", "Bruno", "Diego", "Thiago", "Vinícius",
    "Rodrigo", "Eduardo", "Felipe", "Marcelo", "André", "Gustavo", "Leonardo", "Renato"
)

private val sobrenomePorTime = mapOf(
    "Flamengo" to "Farias",
    "Palmeiras" to "Nogueira",
    "São Paulo" to "Barbosa",
    "Corinthians" to "Cardoso",
    "Grêmio" to "Teixeira",
    "Internacional" to "Moraes",
    "Atlético-MG" to "Azevedo",
    "Fluminense" to "Correia"
)

val jogadores: List<Jogador> = times.flatMapIndexed { indiceTime, time ->
    moldes.mapIndexed { indicePosicao, molde ->
        val gols = when (molde.posicao) {
            "Goleiro" -> 0
            "Zagueiro" -> (indicePosicao + indiceTime) % 4
            "Meio-campo" -> 2 + ((indicePosicao + indiceTime) % 6)
            else -> 5 + ((indicePosicao + indiceTime) % 8) // Atacante
        }
        val assistencias = when (molde.posicao) {
            "Goleiro" -> 0
            "Zagueiro" -> (indicePosicao + indiceTime) % 3
            "Meio-campo" -> 3 + ((indicePosicao + indiceTime) % 6)
            else -> 2 + ((indicePosicao + indiceTime) % 5) // Atacante
        }
        Jogador(
            id = indiceTime * 16 + indicePosicao + 1,
            nome = "${primeirosNomes[indicePosicao]} ${sobrenomePorTime.getValue(time.nome)}",
            posicao = molde.posicao,
            numeroCamisa = molde.numero,
            idade = 20 + ((indicePosicao + indiceTime) % 15),
            timeNome = time.nome,
            titular = molde.titular,
            gols = gols,
            assistencias = assistencias,
            cartoesAmarelos = (indicePosicao + indiceTime * 2) % 7,
            cartoesVermelhos = if ((indicePosicao + indiceTime) % 9 == 0) 1 else 0
        )
    }
}

fun buscarJogadorPorId(id: Int): Jogador? = jogadores.firstOrNull { it.id == id }

fun buscarJogadoresPorTime(nomeTime: String): List<Jogador> =
    jogadores.filter { it.timeNome == nomeTime }
