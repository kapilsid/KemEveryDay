package uk.edbetter.kemdaily.data

import java.time.LocalDate

/**
 * Plausibility limits for a Daily Champions entry. firestore.rules enforces the same numbers, so keep
 * the two in step: a Molecule Board find scores at most 130 (7 atoms, 7 elements, full rarity and
 * streak), a repeat at most 30, and Science Uncovered has 3 mysteries a day: 100 points plus a 25 perfect bonus each, plus 50 per life left at the end, which stays within 175 per solved mystery.
 */
object ChampionLimits {
    const val BOARD_MOVES = 20
    const val MAX_FIRST_FIND = 130
    const val MAX_REPEAT_FIND = 30
    const val MAX_MYSTERY = 175
    const val MAX_MYSTERIES = 3

    /** Repeats only score once something has been found, so an empty board scores nothing. */
    fun maxMoleculeScore(uniqueDiscoveries: Int): Int =
        if (uniqueDiscoveries == 0) 0 else uniqueDiscoveries * MAX_FIRST_FIND + (BOARD_MOVES - uniqueDiscoveries) * MAX_REPEAT_FIND

    fun isPlausible(submission: ScienceScoreSubmission): Boolean = with(submission) {
        uniqueDiscoveries in 0..BOARD_MOVES &&
            moleculeScore in 0..maxMoleculeScore(uniqueDiscoveries) &&
            mysteriesSolved in 0..MAX_MYSTERIES &&
            scienceScore in 0..(mysteriesSolved * MAX_MYSTERY)
    }
}

/** Boundary between the Android UI and the Firebase Daily Champions leaderboard. */
interface ScienceLeaderboardGateway {
    suspend fun submit(submission: ScienceScoreSubmission): SubmissionResult
    suspend fun rankings(date: LocalDate, limit: Int = 100): List<PublicScienceRank>
}

data class ScienceScoreSubmission(
    val date: LocalDate,
    val moleculeScore: Int,
    val scienceScore: Int,
    val uniqueDiscoveries: Int,
    val mysteriesSolved: Int,
    val contentVersion: Int,
) {
    val score: Int get() = moleculeScore + scienceScore
}

data class PublicScienceRank(
    val rank: Int,
    val playerId: String,
    val displayName: String,
    val score: Int,
    val moleculeScore: Int,
    val scienceScore: Int,
    val uniqueDiscoveries: Int,
    val mysteriesSolved: Int,
    val verified: Boolean,
)

sealed interface SubmissionResult {
    data class Accepted(val rank: Int?) : SubmissionResult
    data object AlreadySubmitted : SubmissionResult
    data object Offline : SubmissionResult
    data class Rejected(val reason: String) : SubmissionResult
}
