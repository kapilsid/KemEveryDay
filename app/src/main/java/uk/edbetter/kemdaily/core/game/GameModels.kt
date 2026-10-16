package uk.edbetter.kemdaily.core.game

import uk.edbetter.kemdaily.core.chemistry.Substance

const val BOARD_SIZE = 5
const val BOARD_CELLS = BOARD_SIZE * BOARD_SIZE
const val STARTING_MOVES = 20
const val MISSION_TARGET = 10
const val MAX_SELECTION_ATOMS = 7

data class Cell(val row: Int, val column: Int) {
    init { require(row in 0 until BOARD_SIZE && column in 0 until BOARD_SIZE) }
    val index: Int get() = row * BOARD_SIZE + column
    fun adjacentTo(other: Cell): Boolean =
        this != other && kotlin.math.abs(row - other.row) <= 1 && kotlin.math.abs(column - other.column) <= 1
    companion object { fun fromIndex(index: Int) = Cell(index / BOARD_SIZE, index % BOARD_SIZE) }
}

data class AtomTile(val id: Long, val symbol: String)

data class ScoreBreakdown(
    val atoms: Int,
    val elements: Int,
    val rarity: Int,
    val streak: Int,
    val repeatMultiplier: Float,
    val awarded: Int,
)

data class DiscoveryResult(
    val substance: Substance,
    val isFirst: Boolean,
    val score: ScoreBreakdown,
)

enum class GamePhase { PLAYING, MISSION_COMPLETE, FINISHED }
enum class GameMode { STANDARD, DAILY }

data class GameState(
    val mode: GameMode,
    val seed: Long,
    val board: List<AtomTile>,
    val selection: List<Cell> = emptyList(),
    val movesRemaining: Int = STARTING_MOVES,
    val score: Int = 0,
    val uniqueDiscoveries: Set<String> = emptySet(),
    val discoveryHistory: List<String> = emptyList(),
    val consecutiveUnique: Int = 0,
    val missionAchieved: Boolean = false,
    val phase: GamePhase = GamePhase.PLAYING,
    val lastDiscovery: DiscoveryResult? = null,
    val message: String? = null,
    val hintedCells: Set<Cell> = emptySet(),
) {
    init { require(board.size == BOARD_CELLS) }
}

sealed interface SubmitResult {
    data class Accepted(val state: GameState, val discovery: DiscoveryResult) : SubmitResult
    data class PendingReview(val state: GameState, val canonicalKey: String, val formula: String) : SubmitResult
    data class TooShort(val state: GameState) : SubmitResult
}
