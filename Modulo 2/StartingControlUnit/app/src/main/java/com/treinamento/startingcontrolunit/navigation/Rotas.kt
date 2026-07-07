package com.treinamento.startingcontrolunit.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.treinamento.startingcontrolunit.model.Time
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Tela inicial: lista de partidas. Não recebe parâmetro nenhum.
@Serializable
data object RotaListaPartidas

// Recebe o id da partida (parâmetro primitivo) para buscar os dados no mock.
@Serializable
data class RotaDetalhePartida(val matchId: Int)

// Vem de TelaDetalhePartida: além do matchId (primitivo), carrega os dois objetos Time
// (parâmetro mais complexo, serializável) para montar as abas de titulares/reservas sem
// precisar buscar a partida de novo no mock.
@Serializable
data class RotaEscalacao(
    val matchId: Int,
    val timeCasa: Time,
    val timeVisitante: Time
)

// Navigation Compose só gera NavType automático para tipos primitivos - um tipo
// customizado como Time (usado acima em RotaEscalacao) precisa de um NavType explícito
// que ensine como serializar/desserializar o valor como argumento de rota. Aqui isso é
// feito via JSON, com Uri.encode/decode porque o valor vira parte de um padrão de rota
// no formato de URI (chaves como { } e : quebrariam o padrão sem o encode).
val TimeNavType = object : NavType<Time>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: Time) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(bundle: Bundle, key: String): Time? =
        bundle.getString(key)?.let { Json.decodeFromString<Time>(it) }

    override fun parseValue(value: String): Time =
        Json.decodeFromString<Time>(Uri.decode(value))

    override fun serializeAsValue(value: Time): String =
        Uri.encode(Json.encodeToString(value))
}

// Vem de TelaClassificacao: só o nome do time (parâmetro primitivo), mostra elenco único,
// sem abas.
@Serializable
data class RotaEscalacaoTime(val teamName: String)

// Recebe o id do jogador (parâmetro primitivo) para buscar os dados no mock.
@Serializable
data class RotaPerfilJogador(val playerId: Int)

// Tela de classificação. Não recebe parâmetro nenhum.
@Serializable
data object RotaClassificacao
