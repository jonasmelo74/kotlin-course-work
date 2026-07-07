# Design — App "Central da Partida"

Data: 2026-07-06
Projeto: `StartingControlUnit` (Módulo 2)

## Contexto e objetivo

Implementar, dentro do projeto Android existente `StartingControlUnit`, um app de acompanhamento
de partidas de futebol com dados 100% mockados em memória (sem API, sem persistência). O app não
usa MVVM: toda lógica de estado fica nos Composables (`remember`/`mutableStateOf`/`rememberSaveable`).
Navegação via Navigation Compose com rotas type-safe (`@Serializable` + `kotlinx.serialization`).

Fonte original do requisito: prompt do usuário em `prompt_app_futebol_compose.md`.

## Decisões confirmadas com o usuário

- **Times**: clubes reais do Brasileirão (Flamengo, Palmeiras, São Paulo, Corinthians, Grêmio,
  Internacional, Atlético-MG, Fluminense) — 8 times.
- **Jogadores**: elencos **fictícios** (nomes inventados) dentro desses clubes reais, para evitar
  dados desatualizados/incorretos sobre atletas reais.
- **Escopo do elenco**: elenco completo (11 titulares + 5 reservas) para **todos os 8 times** da
  tabela de classificação, não só os times com partida na Home.
- **Volume**: 8 times, 8 partidas, ~128 jogadores no total.
- **Rotas de Escalação**: duas rotas distintas (não uma só reaproveitada com parâmetro opcional):
  - `RotaEscalacao(matchId, timeCasa: Time, timeVisitante: Time)` — vinda de `TelaDetalhePartida`,
    mostra Tabs + HorizontalPager alternando os dois times da partida.
  - `RotaEscalacaoTime(teamName: String)` — vinda de `TelaClassificacao`, mostra elenco único do
    time (sem tabs).
- **Parâmetro complexo serializável**: `RotaEscalacao` carrega os objetos `Time` (data class
  `@Serializable`) diretamente, além do `matchId: Int` primitivo — evita nova busca no mock e
  cumpre o requisito do prompt de passar "um parâmetro mais complexo" de forma natural.
- **Dependências novas**: `androidx.navigation:navigation-compose` e
  `kotlinx.serialization` (plugin `org.jetbrains.kotlin.plugin.serialization` +
  `kotlinx-serialization-json`), adicionadas ao `libs.versions.toml` e aos `build.gradle.kts`.

## Modelos de dados (`model/`)

```kotlin
@Serializable
data class Time(
    val nome: String,
    val escudoEmoji: String,
    val cidade: String
)

data class Jogador(
    val id: Int,
    val nome: String,
    val posicao: String,       // "Goleiro", "Zagueiro", "Meio-campo", "Atacante"
    val numeroCamisa: Int,
    val idade: Int,
    val timeNome: String,      // vínculo com Time.nome
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

`Time` precisa ser `@Serializable` pois trafega como argumento de rota Navigation Compose.
`Jogador`, `Partida`, `ClassificacaoTime` não precisam ser `@Serializable` (não trafegam por rota,
só são consultados no mock por id/nome).

## Dados mockados (`data/MockData.kt`)

- `listOf<Time>` com os 8 clubes.
- `listOf<Partida>` com 8 jogos cruzando esses times, mix de status (`AO_VIVO`, `ENCERRADA`,
  `AGENDADA`), placares coerentes com o status (null quando `AGENDADA`).
- `listOf<Jogador>` com 11 titulares + 5 reservas fictícios por time (~128 jogadores), posições e
  estatísticas plausíveis.
- `listOf<ClassificacaoTime>` com os 8 times, pontos/V/E/D coerentes com os resultados das
  partidas `ENCERRADA` (não precisa fechar matematicamente 100%, só ser plausível).
- Funções de busca auxiliares: `buscarPartidaPorId(id)`, `buscarJogadorPorId(id)`,
  `buscarJogadoresPorTime(teamName)`.

## Navegação (`navigation/`)

`navigation/Rotas.kt`:

```kotlin
@Serializable data object RotaListaPartidas
@Serializable data class RotaDetalhePartida(val matchId: Int)
@Serializable data class RotaEscalacao(val matchId: Int, val timeCasa: Time, val timeVisitante: Time)
@Serializable data class RotaEscalacaoTime(val teamName: String)
@Serializable data class RotaPerfilJogador(val playerId: Int)
@Serializable data object RotaClassificacao
```

`navigation/NavGraph.kt` define o `NavHost` com as 6 composable routes acima, chamado a partir de
`MainActivity` (substitui o conteúdo atual de demonstração "Hello Android").

Fluxos de navegação:

- `RotaListaPartidas` → clique num card → `RotaDetalhePartida(matchId)`
- `RotaDetalhePartida` → AssistChip "Escalação" → `RotaEscalacao(matchId, timeCasa, timeVisitante)`
- `RotaDetalhePartida` → botão "Classificação" → `RotaClassificacao`
- `RotaEscalacao` → clique num jogador → `RotaPerfilJogador(playerId)`
- `RotaClassificacao` → clique numa linha/time → `RotaEscalacaoTime(teamName)`
- `RotaEscalacaoTime` → clique num jogador → `RotaPerfilJogador(playerId)`

## Telas (`ui/screens/`)

| Tela | Rota(s) | Conteúdo |
|---|---|---|
| `TelaListaPartidas` | `RotaListaPartidas` | `Scaffold` + `TopAppBar("Central da Partida")`; `LazyColumn` de `CardPartida` (8 itens) |
| `TelaDetalhePartida` | `RotaDetalhePartida` | Busca `Partida` por `matchId`; nomes dos times, placar, data/hora, estádio, árbitro; `AssistChip` → Escalação; botão → Classificação |
| `TelaEscalacao` | `RotaEscalacao` | `TabRow` + `HorizontalPager` alternando os 2 times da partida; cada aba lista titulares/reservas via `ItemJogador` |
| `TelaEscalacaoTime` (mesma composable, modo single-team) | `RotaEscalacaoTime` | Lista única (sem tabs) do elenco do `teamName` recebido |
| `TelaPerfilJogador` | `RotaPerfilJogador` | Busca `Jogador` por `playerId`; nome/posição/número/idade; `LinearProgressIndicator`/`Badge` para gols, assistências, cartões |
| `TelaClassificacao` | `RotaClassificacao` | `Scaffold` + `TopAppBar` com voltar; `LazyColumn` com cabeçalho fixo (`stickyHeader`) e uma `LinhaClassificacao` clicável por time |

Nota de implementação: `TelaEscalacao` e a exibição "single-team" da `RotaEscalacaoTime` podem
compartilhar a mesma composable de tela com um parâmetro interno opcional para o segundo time,
já que a lógica de exibição de lista de jogadores é idêntica — evita duplicar o corpo da tela
mesmo com rotas distintas.

## Componentes reutilizáveis (`ui/components/`)

- `CardPartida(partida: Partida, onClick: () -> Unit)` — state-hoisted, sem estado próprio.
- `ItemJogador(jogador: Jogador, onClick: () -> Unit)`
- `LinhaClassificacao(item: ClassificacaoTime, onClick: () -> Unit)`

## Pacotes finais

```
com.treinamento.startingcontrolunit/
  model/          (Time, Jogador, Partida, ClassificacaoTime, StatusPartida)
  data/           (MockData.kt)
  navigation/     (Rotas.kt, NavGraph.kt)
  ui/screens/     (TelaListaPartidas, TelaDetalhePartida, TelaEscalacao, TelaPerfilJogador, TelaClassificacao)
  ui/components/  (CardPartida, ItemJogador, LinhaClassificacao)
  ui/theme/       (já existente, sem mudanças)
  MainActivity.kt (modificado: passa a hospedar o NavHost)
```

## Arquivos afetados

**Modificados:**
- `MainActivity.kt` — remove o `Greeting` de demo, passa a chamar o `NavHost`.
- `gradle/libs.versions.toml` — adiciona versões/libs de navigation-compose e kotlinx-serialization.
- `build.gradle.kts` (raiz) — adiciona plugin de serialization (apply false).
- `app/build.gradle.kts` — aplica plugin de serialization e adiciona as novas dependências.

**Criados:**
- `model/Time.kt`, `model/Jogador.kt`, `model/Partida.kt`, `model/ClassificacaoTime.kt` (ou um único `model/Modelos.kt`, decidido no plano)
- `data/MockData.kt`
- `navigation/Rotas.kt`, `navigation/NavGraph.kt`
- `ui/screens/TelaListaPartidas.kt`, `TelaDetalhePartida.kt`, `TelaEscalacao.kt`, `TelaPerfilJogador.kt`, `TelaClassificacao.kt`
- `ui/components/CardPartida.kt`, `ItemJogador.kt`, `LinhaClassificacao.kt`

## Fora de escopo

- Persistência (Room/DataStore/SharedPreferences/arquivos).
- Chamadas de rede/API.
- ViewModel/Repository/UseCase (arquitetura MVVM).
- Testes automatizados (não pedidos no prompt original).

## Comentários no código

Comentários em português nos pontos-chave de navegação e passagem de parâmetros (por que a rota
usa aquele tipo de parâmetro, onde a busca no mock acontece), conforme pedido no prompt.
