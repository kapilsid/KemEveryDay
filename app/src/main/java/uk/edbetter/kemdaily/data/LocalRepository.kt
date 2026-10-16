package uk.edbetter.kemdaily.data

import android.content.Context
import uk.edbetter.kemdaily.core.game.AtomTile
import uk.edbetter.kemdaily.core.game.Cell
import uk.edbetter.kemdaily.core.game.DailyHistory
import uk.edbetter.kemdaily.core.game.DailyRecord
import uk.edbetter.kemdaily.core.game.GameMode
import uk.edbetter.kemdaily.core.game.GamePhase
import uk.edbetter.kemdaily.core.game.GameState
import java.time.LocalDate

data class PendingRecord(
    val canonicalKey: String,
    val formula: String,
    val firstSeenAt: Long,
    val lastSeenAt: Long,
    val count: Int,
)

data class LeaderboardEntry(
    val id: String,
    val mode: GameMode,
    val seed: Long,
    val score: Int,
    val uniqueDiscoveries: Int,
    val movesRemaining: Int,
    val completedAt: Long,
)

data class ScienceRunSnapshot(
    val date: LocalDate,
    val mysteryOffset: Int = 0,
    val totalScore: Int = 0,
    val lives: Int = 3,
    val solvedCount: Int = 0,
    val mysteryNumber: Int = 1,
    val visibleClues: Int = 1,
    val guessedLetters: Set<Char> = emptySet(),
    val points: Int = 100,
    val wrongAnswers: Int = 0,
    val solved: Boolean = false,
    val revealed: Boolean = false,
    val runOver: Boolean = false,
)

data class ScienceLeaderboardEntry(
    val date: LocalDate,
    val score: Int,
    val mysteriesSolved: Int,
    val completedAt: Long,
)

/**
 * Device-local MVP persistence. The interface is deliberately storage-agnostic so this can be
 * replaced by Room without coupling the chemistry or game engines to Android.
 */
class LocalRepository(context: Context) {
    private val prefs = context.getSharedPreferences("kemistri_everyday", Context.MODE_PRIVATE)

    init {
        // V1 briefly stored a global collection. Discoveries now belong only to their game.
        val obsoleteKeys = prefs.all.keys.filter { it.startsWith("discovery_") }
        if (obsoleteKeys.isNotEmpty()) {
            prefs.edit().also { editor -> obsoleteKeys.forEach(editor::remove) }.apply()
        }
        // The stored streak counter was never maintained; streaks are now derived from daily history.
        if (prefs.contains("daily_streak")) prefs.edit().remove("daily_streak").apply()
    }

    fun recordPending(key: String, formula: String) {
        val now = System.currentTimeMillis()
        val storageKey = "pending_${key.replace('|', '_')}"
        val existing = prefs.getString(storageKey, null)?.split(',')
        val first = existing?.get(1)?.toLongOrNull() ?: now
        val count = (existing?.get(3)?.toIntOrNull() ?: 0) + 1
        prefs.edit().putString(storageKey, "$formula,$first,$now,$count,$key").apply()
    }

    fun pending(): List<PendingRecord> = prefs.all.entries.mapNotNull { (storageKey, value) ->
        if (!storageKey.startsWith("pending_")) return@mapNotNull null
        val parts = (value as? String)?.split(',') ?: return@mapNotNull null
        if (parts.size < 5) return@mapNotNull null
        PendingRecord(parts.drop(4).joinToString(","), parts[0], parts[1].toLong(), parts[2].toLong(), parts[3].toInt())
    }.sortedByDescending { it.lastSeenAt }

    fun deletePending(record: PendingRecord) {
        prefs.edit().remove("pending_${record.canonicalKey.replace('|', '_')}").apply()
    }

    /**
     * Keeps one best result per seed. Daily attempts therefore improve today's row rather than
     * flooding the board, while standard games retain their independently generated boards.
     */
    fun recordLeaderboard(state: GameState) {
        if (state.phase != GamePhase.FINISHED || state.mode != GameMode.DAILY) return
        val id = "${state.mode.name}_${state.seed}"
        val candidate = LeaderboardEntry(
            id = id,
            mode = state.mode,
            seed = state.seed,
            score = state.score,
            uniqueDiscoveries = state.uniqueDiscoveries.size,
            movesRemaining = state.movesRemaining,
            completedAt = System.currentTimeMillis(),
        )
        val entries = leaderboard().associateBy { it.id }.toMutableMap()
        val previous = entries[id]
        if (previous == null || candidate.score > previous.score ||
            (candidate.score == previous.score && candidate.uniqueDiscoveries > previous.uniqueDiscoveries)
        ) {
            entries[id] = candidate
            val encoded = entries.values
                .sortedByDescending { it.score }
                .take(100)
                .joinToString("\n") { entry ->
                    listOf(
                        entry.id, entry.mode.name, entry.seed, entry.score,
                        entry.uniqueDiscoveries, entry.movesRemaining, entry.completedAt,
                    ).joinToString(",")
                }
            prefs.edit().putString("leaderboard_v1", encoded).apply()
        }
    }

    fun leaderboard(): List<LeaderboardEntry> = prefs.getString("leaderboard_v1", null)
        .orEmpty()
        .lineSequence()
        .filter(String::isNotBlank)
        .mapNotNull { encoded ->
            val fields = encoded.split(',')
            if (fields.size != 7) return@mapNotNull null
            runCatching {
                LeaderboardEntry(
                    id = fields[0],
                    mode = GameMode.valueOf(fields[1]),
                    seed = fields[2].toLong(),
                    score = fields[3].toInt(),
                    uniqueDiscoveries = fields[4].toInt(),
                    movesRemaining = fields[5].toInt(),
                    completedAt = fields[6].toLong(),
                )
            }.getOrNull()
        }
        .sortedWith(compareByDescending<LeaderboardEntry> { it.score }.thenByDescending { it.uniqueDiscoveries })
        .toList()

    fun saveGame(state: GameState) {
        val encoded = listOf(
            state.mode.name,
            state.seed.toString(),
            state.board.joinToString(",") { "${it.id}:${it.symbol}" },
            state.selection.joinToString(",") { it.index.toString() },
            state.movesRemaining.toString(),
            state.score.toString(),
            state.uniqueDiscoveries.joinToString(","),
            state.discoveryHistory.joinToString(","),
            state.consecutiveUnique.toString(),
            state.missionAchieved.toString(),
            state.phase.name,
        ).joinToString(";")
        prefs.edit().putString("active_${state.mode.name}", encoded).apply()
    }

    fun loadGame(mode: GameMode): GameState? = runCatching {
        val parts = prefs.getString("active_${mode.name}", null)!!.split(';')
        val board = parts[2].split(',').map { encoded ->
            val separator = encoded.indexOf(':')
            AtomTile(encoded.substring(0, separator).toLong(), encoded.substring(separator + 1))
        }
        GameState(
            mode = GameMode.valueOf(parts[0]),
            seed = parts[1].toLong(),
            board = board,
            selection = parts[3].takeIf(String::isNotEmpty)?.split(',')?.map { Cell.fromIndex(it.toInt()) }.orEmpty(),
            movesRemaining = parts[4].toInt(),
            score = parts[5].toInt(),
            uniqueDiscoveries = parts[6].takeIf(String::isNotEmpty)?.split(',')?.toSet().orEmpty(),
            discoveryHistory = parts[7].takeIf(String::isNotEmpty)?.split(',').orEmpty(),
            consecutiveUnique = parts[8].toInt(),
            missionAchieved = parts[9].toBoolean(),
            phase = GamePhase.valueOf(parts[10]),
        )
    }.getOrNull()

    fun clearGame(mode: GameMode) { prefs.edit().remove("active_${mode.name}").apply() }

    /** Best Daily Board result per UTC board date; streaks are derived from these dates. */
    fun dailyHistory(): Map<LocalDate, DailyRecord> = DailyHistory.decode(prefs.getString("daily_history_v1", null))

    fun recordDaily(record: DailyRecord) {
        val history = dailyHistory()
        val updated = DailyHistory.merge(history, record)
        if (updated != history) prefs.edit().putString("daily_history_v1", DailyHistory.encode(updated)).apply()
    }

    fun scienceBestScore(): Int = prefs.getInt("science_uncovered_best", 0)

    fun recordScienceBest(score: Int) {
        if (score > scienceBestScore()) prefs.edit().putInt("science_uncovered_best", score).apply()
    }

    fun scienceRun(date: LocalDate): ScienceRunSnapshot {
        val encoded = prefs.getString("science_run_v1", null)
        val restored = encoded?.split(';')?.takeIf { it.size == 13 }?.let { fields ->
            runCatching {
                ScienceRunSnapshot(
                    date = LocalDate.parse(fields[0]),
                    mysteryOffset = fields[1].toInt(),
                    totalScore = fields[2].toInt(),
                    lives = fields[3].toInt(),
                    solvedCount = fields[4].toInt(),
                    mysteryNumber = fields[5].toInt(),
                    visibleClues = fields[6].toInt(),
                    guessedLetters = fields[7].toSet(),
                    points = fields[8].toInt(),
                    wrongAnswers = fields[9].toInt(),
                    solved = fields[10].toBooleanStrict(),
                    revealed = fields[11].toBooleanStrict(),
                    runOver = fields[12].toBooleanStrict(),
                )
            }.getOrNull()
        }
        if (restored?.date == date) return restored
        return ScienceRunSnapshot(date).also(::saveScienceRun)
    }

    fun saveScienceRun(run: ScienceRunSnapshot) {
        val encoded = listOf(
            run.date,
            run.mysteryOffset,
            run.totalScore,
            run.lives,
            run.solvedCount,
            run.mysteryNumber,
            run.visibleClues,
            run.guessedLetters.sorted().joinToString(""),
            run.points,
            run.wrongAnswers,
            run.solved,
            run.revealed,
            run.runOver,
        ).joinToString(";")
        prefs.edit().putString("science_run_v1", encoded).apply()
        if (run.runOver) recordScienceLeaderboard(run)
    }

    fun scienceLeaderboard(): List<ScienceLeaderboardEntry> = prefs.getString("science_leaderboard_v1", null)
        .orEmpty()
        .lineSequence()
        .filter(String::isNotBlank)
        .mapNotNull { row ->
            val fields = row.split(',')
            if (fields.size != 4) return@mapNotNull null
            runCatching {
                ScienceLeaderboardEntry(
                    date = LocalDate.parse(fields[0]),
                    score = fields[1].toInt(),
                    mysteriesSolved = fields[2].toInt(),
                    completedAt = fields[3].toLong(),
                )
            }.getOrNull()
        }
        .sortedWith(compareByDescending<ScienceLeaderboardEntry> { it.score }.thenByDescending { it.mysteriesSolved })
        .toList()

    private fun recordScienceLeaderboard(run: ScienceRunSnapshot) {
        val byDate = scienceLeaderboard().associateBy { it.date }.toMutableMap()
        val previous = byDate[run.date]
        if (previous == null || run.totalScore > previous.score) {
            byDate[run.date] = ScienceLeaderboardEntry(
                date = run.date,
                score = run.totalScore,
                mysteriesSolved = run.solvedCount,
                completedAt = System.currentTimeMillis(),
            )
            val encoded = byDate.values.sortedByDescending { it.date }.take(100).joinToString("\n") {
                "${it.date},${it.score},${it.mysteriesSolved},${it.completedAt}"
            }
            prefs.edit().putString("science_leaderboard_v1", encoded).apply()
        }
    }

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound", true)
        set(value) { prefs.edit().putBoolean("sound", value).apply() }
    var vibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration", true)
        set(value) { prefs.edit().putBoolean("vibration", value).apply() }
    var reducedMotion: Boolean
        get() = prefs.getBoolean("reduced_motion", false)
        set(value) { prefs.edit().putBoolean("reduced_motion", value).apply() }

    var playerNickname: String?
        get() = prefs.getString("player_nickname", null)
        set(value) { prefs.edit().putString("player_nickname", value).apply() }
}
