package uk.edbetter.kemdaily.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.edbetter.kemdaily.core.game.Cell
import uk.edbetter.kemdaily.core.game.DailyRecord
import uk.edbetter.kemdaily.core.game.DailySeed
import uk.edbetter.kemdaily.core.game.GameEngine
import uk.edbetter.kemdaily.core.game.GameMode
import uk.edbetter.kemdaily.core.game.GamePhase
import uk.edbetter.kemdaily.core.game.GameState
import uk.edbetter.kemdaily.core.game.SubmitResult
import uk.edbetter.kemdaily.data.LocalRepository
import uk.edbetter.kemdaily.data.LeaderboardEntry
import uk.edbetter.kemdaily.data.PendingRecord
import java.time.LocalDate
import java.time.ZoneOffset

data class SettingsState(val sound: Boolean, val vibration: Boolean, val reducedMotion: Boolean)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LocalRepository(application)
    private var engine: GameEngine? = null
    private val _game = MutableStateFlow<GameState?>(null)
    val game: StateFlow<GameState?> = _game.asStateFlow()
    private val _pending = MutableStateFlow(repository.pending())
    val pending: StateFlow<List<PendingRecord>> = _pending.asStateFlow()
    private val _leaderboard = MutableStateFlow(repository.leaderboard())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()
    private val _settings = MutableStateFlow(settingsSnapshot())
    val settings: StateFlow<SettingsState> = _settings.asStateFlow()
    private val _dailyHistory = MutableStateFlow(repository.dailyHistory())
    val dailyHistory: StateFlow<Map<LocalDate, DailyRecord>> = _dailyHistory.asStateFlow()

    /** The UTC board date of the Daily game currently loaded, so a midnight rollover mid-game still credits that board. */
    private var activeDailyDate: LocalDate? = null

    fun start(mode: GameMode, fresh: Boolean = false) {
        val today = todayUtc()
        val todaySeed = DailySeed.forDate(today.toString())
        // A saved Daily game from an earlier date is stale: the board has rolled over to today's seed.
        val saved = if (fresh) null else repository.loadGame(mode)
            ?.takeUnless { mode == GameMode.DAILY && it.seed != todaySeed }
        val seed = saved?.seed ?: if (mode == GameMode.DAILY) todaySeed else System.nanoTime()
        activeDailyDate = if (mode == GameMode.DAILY) today else null
        engine = GameEngine(seed, mode).also { saved?.let(it::restore) }
        publish()
        recordFinishedGame()
    }

    fun select(cell: Cell) { engine?.select(cell); publish() }
    fun clearSelection() { engine?.clear(); publish() }
    fun hint() { engine?.hint(); publish() }
    fun shuffle() { engine?.shuffle(); publish() }
    fun continueMission() { engine?.continueAfterMission(); publish(); recordFinishedGame() }
    fun dismissDiscovery() { engine?.dismissDiscovery(); publish() }
    fun finish() { engine?.finish(); publish(); recordFinishedGame() }

    fun submit() {
        when (val result = engine?.submit()) {
            is SubmitResult.Accepted -> Unit
            is SubmitResult.PendingReview -> {
                repository.recordPending(result.canonicalKey, result.formula)
                _pending.value = repository.pending()
            }
            else -> Unit
        }
        publish()
        recordFinishedGame()
    }

    fun deletePending(record: PendingRecord) {
        repository.deletePending(record)
        _pending.value = repository.pending()
    }

    fun setSound(value: Boolean) { repository.soundEnabled = value; _settings.value = settingsSnapshot() }
    fun setVibration(value: Boolean) { repository.vibrationEnabled = value; _settings.value = settingsSnapshot() }
    fun setReducedMotion(value: Boolean) { repository.reducedMotion = value; _settings.value = settingsSnapshot() }

    private fun publish() {
        val value = engine?.state ?: return
        _game.value = value
        repository.saveGame(value)
    }

    fun refreshDailyHistory() { _dailyHistory.value = repository.dailyHistory() }

    private fun recordFinishedGame() {
        val state = engine?.state ?: return
        if (state.phase == GamePhase.FINISHED) {
            repository.recordLeaderboard(state)
            _leaderboard.value = repository.leaderboard()
        }
        // Reaching the mission or ending the hunt both count as playing that day's board.
        val date = activeDailyDate
        if (state.mode == GameMode.DAILY && date != null && state.phase != GamePhase.PLAYING) {
            repository.recordDaily(
                DailyRecord(
                    date = date,
                    score = state.score,
                    uniqueDiscoveries = state.uniqueDiscoveries.size,
                    missionAchieved = state.missionAchieved,
                    completedAt = System.currentTimeMillis(),
                ),
            )
            refreshDailyHistory()
        }
    }

    fun todayUtc(): LocalDate = LocalDate.now(ZoneOffset.UTC)

    private fun settingsSnapshot() = SettingsState(
        repository.soundEnabled, repository.vibrationEnabled, repository.reducedMotion,
    )
}
