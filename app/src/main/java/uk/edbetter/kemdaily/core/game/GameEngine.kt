package uk.edbetter.kemdaily.core.game

import kotlin.math.roundToInt
import uk.edbetter.kemdaily.core.chemistry.Composition
import uk.edbetter.kemdaily.core.chemistry.Recognition
import uk.edbetter.kemdaily.core.chemistry.Substance
import uk.edbetter.kemdaily.core.chemistry.SubstanceCatalog

class GameEngine(seed: Long, mode: GameMode = GameMode.STANDARD) {
    private val generator = BoardGenerator(DeterministicRandom(seed))
    var state: GameState = GameState(mode = mode, seed = seed, board = generator.create())
        private set

    fun restore(saved: GameState): GameState {
        require(saved.seed == state.seed && saved.mode == state.mode)
        state = saved
        return state
    }

    fun select(cell: Cell): GameState {
        if (state.phase != GamePhase.PLAYING && state.phase != GamePhase.MISSION_COMPLETE) return state
        val current = state.selection
        state = when {
            current.isEmpty() -> state.copy(selection = listOf(cell), message = null, hintedCells = emptySet())
            cell in current -> state.copy(
                selection = current.take(current.indexOf(cell)),
                message = null,
                hintedCells = emptySet(),
            )
            !cell.adjacentTo(current.last()) -> state.copy(message = "Choose a neighbouring tile")
            current.size >= MAX_SELECTION_ATOMS -> state.copy(message = "A discovery can use up to $MAX_SELECTION_ATOMS atoms")
            else -> state.copy(selection = current + cell, message = null, hintedCells = emptySet())
        }
        return state
    }

    fun clear(): GameState {
        state = state.copy(selection = emptyList(), message = null, hintedCells = emptySet())
        return state
    }

    fun hint(): GameState {
        val path = PathFinder.firstKnownPath(state.board, state.uniqueDiscoveries)
            ?: PathFinder.firstKnownPath(state.board)
        state = if (path == null) shuffle().copy(message = "Board refreshed — try this new layout")
        else state.copy(hintedCells = path.toSet(), message = "A possible discovery is glowing")
        return state
    }

    fun shuffle(): GameState {
        state = state.copy(
            board = generator.shuffled(state.board),
            selection = emptyList(),
            hintedCells = emptySet(),
            message = "Board shuffled",
        )
        return state
    }

    fun continueAfterMission(): GameState {
        if (state.phase == GamePhase.MISSION_COMPLETE) {
            state = state.copy(
                phase = if (state.movesRemaining > 0) GamePhase.PLAYING else GamePhase.FINISHED,
                message = null,
                lastDiscovery = null,
            )
        }
        return state
    }

    fun dismissDiscovery(): GameState {
        state = state.copy(lastDiscovery = null, message = null)
        return state
    }

    fun finish(): GameState {
        state = state.copy(phase = GamePhase.FINISHED, selection = emptyList())
        return state
    }

    fun submit(): SubmitResult {
        if (state.movesRemaining <= 0 || state.phase == GamePhase.FINISHED) {
            state = state.copy(phase = GamePhase.FINISHED, message = "No moves remaining")
            return SubmitResult.TooShort(state)
        }
        if (state.selection.size < 2) {
            state = state.copy(message = "Connect at least two atoms")
            return SubmitResult.TooShort(state)
        }
        val symbols = state.selection.map { state.board[it.index].symbol }
        val composition = Composition.fromSymbols(symbols)
        return when (val recognition = SubstanceCatalog.recognize(composition)) {
            is Recognition.NotYetRecognized -> {
                state = state.copy(
                    selection = emptyList(), consecutiveUnique = 0,
                    message = "${composition.formula} is not yet recognised — saved for review",
                )
                SubmitResult.PendingReview(state, composition.key, composition.formula)
            }
            is Recognition.Recognized -> accept(recognition.substance)
            is Recognition.Ambiguous -> accept(recognition.substances.first())
        }
    }

    private fun accept(substance: Substance): SubmitResult.Accepted {
        val first = substance.id !in state.uniqueDiscoveries
        val breakdown = score(substance, first, state.consecutiveUnique)
        var board = applyGravity(state.board, state.selection, generator)
        if (PathFinder.firstKnownPath(board) == null) board = generator.shuffled(board)
        val unique = if (first) state.uniqueDiscoveries + substance.id else state.uniqueDiscoveries
        val moves = state.movesRemaining - 1
        val achieved = state.missionAchieved || unique.size >= MISSION_TARGET
        val phase = when {
            achieved && !state.missionAchieved -> GamePhase.MISSION_COMPLETE
            moves == 0 -> GamePhase.FINISHED
            else -> state.phase
        }
        val discovery = DiscoveryResult(substance, first, breakdown)
        state = state.copy(
            board = board,
            selection = emptyList(),
            hintedCells = emptySet(),
            movesRemaining = moves,
            score = state.score + breakdown.awarded,
            uniqueDiscoveries = unique,
            discoveryHistory = state.discoveryHistory + substance.id,
            consecutiveUnique = if (first) state.consecutiveUnique + 1 else 0,
            missionAchieved = achieved,
            phase = phase,
            lastDiscovery = discovery,
            message = if (first) "New discovery!" else "Repeat discovery · 25% score",
        )
        return SubmitResult.Accepted(state, discovery)
    }

    companion object {
        fun score(substance: Substance, first: Boolean, previousStreak: Int): ScoreBreakdown {
            val composition = substance.composition
            val rarity = composition.atoms.keys.sumOf { AtomConfig.rarity[it] ?: 0 }.coerceAtMost(40)
            val base = 5 + 5 * composition.atomCount + 5 * composition.elementCount + rarity
            val streak = if (first) (5 * previousStreak).coerceAtMost(15) else 0
            val awarded = if (first) base + streak else (base * .25f).roundToInt().coerceAtLeast(2)
            return ScoreBreakdown(composition.atomCount, composition.elementCount, rarity, streak, if (first) 1f else .25f, awarded)
        }

        fun applyGravity(
            board: List<AtomTile>,
            selected: List<Cell>,
            generator: BoardGenerator,
        ): List<AtomTile> {
            val removed = selected.map { it.index }.toSet()
            val result = MutableList<AtomTile?>(BOARD_CELLS) { null }
            for (column in 0 until BOARD_SIZE) {
                val survivors = (BOARD_SIZE - 1 downTo 0)
                    .map { row -> row * BOARD_SIZE + column }
                    .filterNot { it in removed }
                    .map { board[it] }
                survivors.forEachIndexed { offset, tile ->
                    val row = BOARD_SIZE - 1 - offset
                    result[row * BOARD_SIZE + column] = tile
                }
            }
            return generator.refill(result)
        }
    }
}
