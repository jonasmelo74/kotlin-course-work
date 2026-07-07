# Central da Partida — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the 5-screen "Central da Partida" football-match-tracking app inside the existing `StartingControlUnit` Android project, using only Composable-local state (no MVVM), fully static mock data, and type-safe Navigation Compose routes.

**Architecture:** Pure Jetpack Compose UI over static in-memory mock data (`data/MockData.kt`). No ViewModel/Repository/UseCase — each screen composable reads mock data directly and receives navigation callbacks as lambdas from the `NavGraph`, which owns the single `NavHostController`. Routes are `@Serializable` classes/objects (`navigation/Rotas.kt`) passed via `androidx.navigation.compose.composable<T>` + `toRoute()`.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose (BOM 2026.02.01), Material 3, Navigation Compose 2.9.8, kotlinx.serialization 1.9.0.

**Reference spec:** `docs/superpowers/specs/2026-07-06-central-da-partida-design.md`

**No automated tests in this plan** (explicitly out of scope per the spec — course prompt only asked for a working static-data app). Verification per task is: project compiles (`./gradlew compileDebugKotlin`), confirmed by running the command and reading its output.

---

### Task 1: Add Navigation Compose + kotlinx.serialization dependencies

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts`
- Modify: `app/build.gradle.kts`

- [ ] **Step 1: Add version entries to `gradle/libs.versions.toml`**

In the `[versions]` block, add two lines right after `composeBom = "2026.02.01"`:

```toml
navigationCompose = "2.9.8"
kotlinxSerializationJson = "1.9.0"
```

In the `[libraries]` block, add two lines right after `androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }`:

```toml
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }
```

In the `[plugins]` block, add one line right after `kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }`:

```toml
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

- [ ] **Step 2: Register the serialization plugin in the root `build.gradle.kts`**

Replace the whole file content with:

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
```

- [ ] **Step 3: Apply the plugin and add dependencies in `app/build.gradle.kts`**

Replace the `plugins { ... }` block at the top with:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}
```

Then, inside the `dependencies { ... }` block, add these two lines right after `implementation(libs.androidx.compose.material3)`:

```kotlin
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
```

- [ ] **Step 4: Verify the project still syncs/compiles with the new dependencies**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL` (no source changes yet, this only proves the new dependencies resolve).

- [ ] **Step 5: Commit is not applicable** — this project is not a git repository (confirmed during brainstorming: `git rev-parse --is-inside-work-tree` fails in `StartingControlUnit`). Skip git steps for every task in this plan; just move to the next task.

---

### Task 2: Create the model classes

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/model/Modelos.kt`

- [ ] **Step 1: Write the file**

```kotlin
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
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

---

### Task 3: Create the mock data

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/data/MockData.kt`

This is the single "arquivo de dados mockados" required by the prompt. It defines 8 real Brasileirão clubs, 8 matches across them, a plausible standings table, and a full fictional squad (11 starters + 5 reserves) for every club — 128 players total. Player stats are derived once from small position templates instead of typing 128 near-identical literal blocks by hand; every value is still fixed/static (computed once from hardcoded tables, no I/O, no randomness, no API).

- [ ] **Step 1: Write the file**

```kotlin
package com.treinamento.startingcontrolunit.data

import com.treinamento.startingcontrolunit.model.ClassificacaoTime
import com.treinamento.startingcontrolunit.model.Jogador
import com.treinamento.startingcontrolunit.model.Partida
import com.treinamento.startingcontrolunit.model.StatusPartida
import com.treinamento.startingcontrolunit.model.Time

// ---------- Times ----------

val times = listOf(
    Time("Flamengo", "🔴⚫", "Rio de Janeiro"),
    Time("Palmeiras", "🟢⚪", "São Paulo"),
    Time("São Paulo", "🔴⚪⚫", "São Paulo"),
    Time("Corinthians", "🐘", "São Paulo"),
    Time("Grêmio", "🔵⚫⚪", "Porto Alegre"),
    Time("Internacional", "🔴⚪", "Porto Alegre"),
    Time("Atlético-MG", "🐓", "Belo Horizonte"),
    Time("Fluminense", "🟢🟤⚪", "Rio de Janeiro")
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
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Sanity-check the generated data size**

Run a quick Kotlin check via a throwaway `println` is not needed — instead confirm by reading the file: 8 teams × 16 templates = 128 entries in `jogadores`, IDs 1..128 with no gaps (indiceTime * 16 + indicePosicao + 1 ranges 1..128 across indiceTime 0..7 and indicePosicao 0..15). This is a static/structural guarantee from the loop bounds, not something that needs a runtime test.

---

### Task 4: Create the navigation routes

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/navigation/Rotas.kt`

- [ ] **Step 1: Write the file**

```kotlin
package com.treinamento.startingcontrolunit.navigation

import com.treinamento.startingcontrolunit.model.Time
import kotlinx.serialization.Serializable

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
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

---

### Task 5: Create the reusable UI components

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/ui/components/CardPartida.kt`
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/ui/components/ItemJogador.kt`
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/ui/components/LinhaClassificacao.kt`

All three use **state hoisting**: they only take data + an `onClick` callback, no internal business state.

- [ ] **Step 1: Write `CardPartida.kt`**

```kotlin
package com.treinamento.startingcontrolunit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.model.Partida
import com.treinamento.startingcontrolunit.model.StatusPartida

@Composable
fun CardPartida(partida: Partida, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${partida.timeCasa.escudoEmoji} ${partida.timeCasa.nome}",
                    fontWeight = FontWeight.Bold
                )
                Text(text = textoPlacarOuHorario(partida))
                Text(
                    text = "${partida.timeVisitante.nome} ${partida.timeVisitante.escudoEmoji}",
                    fontWeight = FontWeight.Bold
                )
            }
            Text(text = textoStatus(partida.status), modifier = Modifier.padding(top = 8.dp))
        }
    }
}

private fun textoPlacarOuHorario(partida: Partida): String =
    if (partida.placarCasa != null && partida.placarVisitante != null) {
        "${partida.placarCasa} x ${partida.placarVisitante}"
    } else {
        partida.dataHora
    }

private fun textoStatus(status: StatusPartida): String = when (status) {
    StatusPartida.AO_VIVO -> "🔴 Ao vivo"
    StatusPartida.ENCERRADA -> "Encerrado"
    StatusPartida.AGENDADA -> "Agendado"
}
```

- [ ] **Step 2: Write `ItemJogador.kt`**

```kotlin
package com.treinamento.startingcontrolunit.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Badge
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.treinamento.startingcontrolunit.model.Jogador

@Composable
fun ItemJogador(jogador: Jogador, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(text = "${jogador.numeroCamisa} - ${jogador.nome}") },
        supportingContent = { Text(text = jogador.posicao) },
        trailingContent = {
            if (jogador.gols > 0) {
                Badge { Text(text = "${jogador.gols} gols") }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}
```

- [ ] **Step 3: Write `LinhaClassificacao.kt`**

```kotlin
package com.treinamento.startingcontrolunit.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.model.ClassificacaoTime

@Composable
fun LinhaClassificacao(item: ClassificacaoTime, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${item.posicao}º", modifier = Modifier.width(32.dp))
        Text(text = "${item.time.escudoEmoji} ${item.time.nome}", modifier = Modifier.width(150.dp))
        Text(text = "${item.pontos} pts", modifier = Modifier.width(60.dp))
        Text(text = "${item.vitorias}V ${item.empates}E ${item.derrotas}D")
    }
}
```

- [ ] **Step 4: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

---

### Task 6: Create TelaListaPartidas (Home)

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/ui/screens/TelaListaPartidas.kt`

- [ ] **Step 1: Write the file**

```kotlin
package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.treinamento.startingcontrolunit.data.partidas
import com.treinamento.startingcontrolunit.ui.components.CardPartida

// Tela inicial (Home): lista as partidas mockadas. A tela não conhece rotas -
// ao clicar num card, só avisa "cliquei no matchId X" via callback; quem decide
// para onde navegar é o NavGraph (navigation/NavGraph.kt).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaListaPartidas(aoClicarPartida: (matchId: Int) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Central da Partida") })
        }
    ) { paddingInterno ->
        LazyColumn(modifier = Modifier.padding(paddingInterno)) {
            items(partidas, key = { it.id }) { partida ->
                CardPartida(partida = partida, onClick = { aoClicarPartida(partida.id) })
            }
        }
    }
}
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

---

### Task 7: Create TelaDetalhePartida

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/ui/screens/TelaDetalhePartida.kt`

- [ ] **Step 1: Write the file**

```kotlin
package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.data.buscarPartidaPorId
import com.treinamento.startingcontrolunit.model.Partida

// Recebe só o matchId via navegação type-safe (RotaDetalhePartida) e busca a Partida
// no mock aqui dentro - a tela não guarda estado de negócio, só lê o dado fixo pelo id.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalhePartida(
    matchId: Int,
    aoClicarEscalacao: (partida: Partida) -> Unit,
    aoClicarClassificacao: () -> Unit
) {
    val partida = buscarPartidaPorId(matchId) ?: return

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("${partida.timeCasa.nome} x ${partida.timeVisitante.nome}") })
        }
    ) { paddingInterno ->
        Column(modifier = Modifier.padding(paddingInterno).padding(16.dp)) {
            Text(text = "${partida.timeCasa.escudoEmoji} ${partida.timeCasa.nome}")
            Text(text = "${partida.timeVisitante.escudoEmoji} ${partida.timeVisitante.nome}")
            if (partida.placarCasa != null && partida.placarVisitante != null) {
                Text(text = "Placar: ${partida.placarCasa} x ${partida.placarVisitante}")
            }
            Text(text = "Data/hora: ${partida.dataHora}")
            Text(text = "Estádio: ${partida.estadio}")
            Text(text = "Árbitro: ${partida.arbitro}")

            Spacer(modifier = Modifier.height(16.dp))

            // Passamos a própria Partida (não só o id) para o NavGraph montar a
            // RotaEscalacao com matchId + os dois Times já carregados, sem nova busca.
            AssistChip(onClick = { aoClicarEscalacao(partida) }, label = { Text("Ver escalação") })

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = aoClicarClassificacao) {
                Text("Ver classificação")
            }
        }
    }
}
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

---

### Task 8: Create TelaEscalacao

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/ui/screens/TelaEscalacao.kt`

This single composable is reused for both entry points: `RotaEscalacao` (two teams, tabs + pager) and `RotaEscalacaoTime` (one team, no tabs) — the `NavGraph` (Task 11) decides which one to call based on the route.

- [ ] **Step 1: Write the file**

```kotlin
package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.data.buscarJogadoresPorTime
import com.treinamento.startingcontrolunit.model.Time
import com.treinamento.startingcontrolunit.ui.components.ItemJogador
import kotlinx.coroutines.launch

// Reaproveitada em dois fluxos de navegação:
// - vindo de RotaEscalacao (TelaDetalhePartida): timeVisitante != null -> mostra Tabs+Pager.
// - vindo de RotaEscalacaoTime (TelaClassificacao): timeVisitante == null -> mostra direto
//   o elenco daquele time, sem tabs.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEscalacao(
    timeCasa: Time,
    timeVisitante: Time?,
    aoClicarJogador: (playerId: Int) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Escalação") }) }
    ) { paddingInterno ->
        if (timeVisitante == null) {
            ListaJogadoresTime(
                nomeTime = timeCasa.nome,
                aoClicarJogador = aoClicarJogador,
                modifier = Modifier.padding(paddingInterno)
            )
        } else {
            EscalacaoComTabs(
                timeCasa = timeCasa,
                timeVisitante = timeVisitante,
                aoClicarJogador = aoClicarJogador,
                modifier = Modifier.padding(paddingInterno)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EscalacaoComTabs(
    timeCasa: Time,
    timeVisitante: Time,
    aoClicarJogador: (playerId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listaTimes = listOf(timeCasa, timeVisitante)
    val estadoPager = rememberPagerState(initialPage = 0) { listaTimes.size }
    val escopo = rememberCoroutineScope()

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = estadoPager.currentPage) {
            listaTimes.forEachIndexed { indice, time ->
                Tab(
                    selected = estadoPager.currentPage == indice,
                    onClick = { escopo.launch { estadoPager.animateScrollToPage(indice) } },
                    text = { Text("${time.escudoEmoji} ${time.nome}") }
                )
            }
        }
        HorizontalPager(state = estadoPager, modifier = Modifier.fillMaxSize()) { pagina ->
            ListaJogadoresTime(nomeTime = listaTimes[pagina].nome, aoClicarJogador = aoClicarJogador)
        }
    }
}

@Composable
private fun ListaJogadoresTime(
    nomeTime: String,
    aoClicarJogador: (playerId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val jogadoresDoTime = buscarJogadoresPorTime(nomeTime)
    val titulares = jogadoresDoTime.filter { it.titular }
    val reservas = jogadoresDoTime.filter { !it.titular }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { Text(text = "Titulares", modifier = Modifier.padding(16.dp)) }
        items(titulares, key = { it.id }) { jogador ->
            ItemJogador(jogador = jogador, onClick = { aoClicarJogador(jogador.id) })
        }
        item { Text(text = "Reservas", modifier = Modifier.padding(16.dp)) }
        items(reservas, key = { it.id }) { jogador ->
            ItemJogador(jogador = jogador, onClick = { aoClicarJogador(jogador.id) })
        }
    }
}
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

---

### Task 9: Create TelaPerfilJogador

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/ui/screens/TelaPerfilJogador.kt`

- [ ] **Step 1: Write the file**

```kotlin
package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.data.buscarJogadorPorId

// Recebe só o playerId via navegação type-safe (RotaPerfilJogador) e busca o Jogador
// no mock aqui dentro.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfilJogador(playerId: Int) {
    val jogador = buscarJogadorPorId(playerId) ?: return

    Scaffold(
        topBar = { TopAppBar(title = { Text(jogador.nome) }) }
    ) { paddingInterno ->
        Column(modifier = Modifier.padding(paddingInterno).padding(16.dp)) {
            Text(text = "Posição: ${jogador.posicao}")
            Text(text = "Camisa: ${jogador.numeroCamisa}")
            Text(text = "Idade: ${jogador.idade} anos")
            Text(text = "Time: ${jogador.timeNome}")

            Text(text = "Gols", modifier = Modifier.padding(top = 16.dp))
            LinearProgressIndicator(
                progress = { jogador.gols.coerceAtMost(20) / 20f },
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = "Assistências", modifier = Modifier.padding(top = 8.dp))
            LinearProgressIndicator(
                progress = { jogador.assistencias.coerceAtMost(20) / 20f },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Cartões amarelos: ${jogador.cartoesAmarelos} · Cartões vermelhos: ${jogador.cartoesVermelhos}",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

---

### Task 10: Create TelaClassificacao

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/ui/screens/TelaClassificacao.kt`

- [ ] **Step 1: Write the file**

```kotlin
package com.treinamento.startingcontrolunit.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.stickyHeader
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.treinamento.startingcontrolunit.data.classificacao
import com.treinamento.startingcontrolunit.ui.components.LinhaClassificacao

// Botão de voltar como Text("←") em vez de um ícone do Material Icons, para não precisar
// adicionar mais uma dependência (androidx.compose.material:material-icons-core) só por isso.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TelaClassificacao(aoVoltar: () -> Unit, aoClicarTime: (teamName: String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Classificação") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Text(text = "←")
                    }
                }
            )
        }
    ) { paddingInterno ->
        LazyColumn(modifier = Modifier.padding(paddingInterno)) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Pos", modifier = Modifier.width(32.dp), fontWeight = FontWeight.Bold)
                    Text(text = "Time", modifier = Modifier.width(150.dp), fontWeight = FontWeight.Bold)
                    Text(text = "Pts", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold)
                    Text(text = "V/E/D", fontWeight = FontWeight.Bold)
                }
            }
            items(classificacao, key = { it.posicao }) { item ->
                LinhaClassificacao(item = item, onClick = { aoClicarTime(item.time.nome) })
            }
        }
    }
}
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

---

### Task 11: Create the NavGraph

**Files:**
- Create: `app/src/main/java/com/treinamento/startingcontrolunit/navigation/NavGraph.kt`

- [ ] **Step 1: Write the file**

```kotlin
package com.treinamento.startingcontrolunit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.treinamento.startingcontrolunit.data.times
import com.treinamento.startingcontrolunit.ui.screens.TelaClassificacao
import com.treinamento.startingcontrolunit.ui.screens.TelaDetalhePartida
import com.treinamento.startingcontrolunit.ui.screens.TelaEscalacao
import com.treinamento.startingcontrolunit.ui.screens.TelaListaPartidas
import com.treinamento.startingcontrolunit.ui.screens.TelaPerfilJogador

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

        composable<RotaEscalacao> { backStackEntry ->
            val rota: RotaEscalacao = backStackEntry.toRoute()
            TelaEscalacao(
                timeCasa = rota.timeCasa,
                timeVisitante = rota.timeVisitante,
                aoClicarJogador = { playerId ->
                    controladorNavegacao.navigate(RotaPerfilJogador(playerId))
                }
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
                }
            )
        }

        composable<RotaPerfilJogador> { backStackEntry ->
            val rota: RotaPerfilJogador = backStackEntry.toRoute()
            TelaPerfilJogador(playerId = rota.playerId)
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
```

- [ ] **Step 2: Verify it compiles**

Run: `./gradlew compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`

---

### Task 12: Wire NavGraph into MainActivity

**Files:**
- Modify: `app/src/main/java/com/treinamento/startingcontrolunit/MainActivity.kt`

- [ ] **Step 1: Replace the whole file content**

The current file hosts a demo `Greeting` composable inside a bare `Scaffold`. Replace the entire content with:

```kotlin
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
```

This removes the demo `Greeting`/`GreetingPreview` composables — the app's real content is now the 5-screen navigation graph.

- [ ] **Step 2: Verify full debug build**

Run: `./gradlew assembleDebug --console=plain`
Expected: `BUILD SUCCESSFUL` — this compiles Kotlin, merges resources, and produces the debug APK, exercising the whole app including the newly-added navigation/serialization dependencies end-to-end.

---

### Task 13: Manual smoke test on a device/emulator

**Files:** none (verification only)

- [ ] **Step 1: Install and launch the app**

Run: `./gradlew installDebug --console=plain` (requires a running emulator or connected device)
Expected: `BUILD SUCCESSFUL`, app installs as "Central da Partida".

- [ ] **Step 2: Walk the full navigation chain manually**

On the device/emulator: open the app → tap a match card on the Home list → confirm `TelaDetalhePartida` shows the right teams/score/stadium/referee for that `matchId` → tap "Ver escalação" → confirm `TelaEscalacao` shows two tabs (home/away team) with titulares/reservas lists, tap a player → confirm `TelaPerfilJogador` shows that exact player's stats → go back twice to `TelaDetalhePartida` → tap "Ver classificação" → confirm `TelaClassificacao` shows the 8-team table with sticky header → tap a team row → confirm it opens `TelaEscalacao` for that single team with no tabs → tap a player → confirm the right profile shows again.

Expected: every step navigates correctly and shows data consistent with `MockData.kt` (no crashes, no "partida/jogador não encontrado" blank screens).

- [ ] **Step 3: Report findings**

If any step fails (wrong data shown, crash, or blank screen from a null lookup), note the exact tap sequence and go back to the relevant task above to fix it before considering the plan complete.

---

## Plan self-review notes

- **Spec coverage:** all 5 screens (Tasks 6–10), model (Task 2), mock data incl. full 8-team squads (Task 3), routes with a primitive param (`matchId`/`teamName`) and a complex serializable param (`Time` in `RotaEscalacao`) (Task 4), NavGraph (Task 11), package layout `model/data/navigation/ui/screens/ui/components` (all tasks), Portuguese comments on navigation/parameter-passing points (Tasks 2–11), Gradle dependency additions (Task 1) are all covered.
- **Not included on purpose:** automated tests, Room/DataStore, ViewModel — explicitly out of scope per the design spec.
- **Deliverable #6** ("indicação de quais arquivos existentes foram alterados e quais foram criados") is satisfied by this plan's Files sections; a final summary restating this list should be given to the user after Task 13.
