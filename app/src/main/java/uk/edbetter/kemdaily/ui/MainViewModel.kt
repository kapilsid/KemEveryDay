package uk.edbetter.kemdaily.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.edbetter.kemdaily.R
import uk.edbetter.kemdaily.LocaleManager
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
import uk.edbetter.kemdaily.data.ChampionLimits
import uk.edbetter.kemdaily.data.ElementGuess
import uk.edbetter.kemdaily.data.FirebaseScienceLeaderboardGateway
import uk.edbetter.kemdaily.data.PlayerNames
import uk.edbetter.kemdaily.data.PublicScienceRank
import uk.edbetter.kemdaily.data.ScienceScoreSubmission
import uk.edbetter.kemdaily.data.ScienceRunSnapshot
import uk.edbetter.kemdaily.data.ScienceLeaderboardEntry
import uk.edbetter.kemdaily.data.SubmissionResult
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

data class SettingsState(val sound: Boolean, val vibration: Boolean, val reducedMotion: Boolean, val languageTag: String = "system")

data class ScienceCloudState(
    val connecting: Boolean = true,
    val online: Boolean = false,
    val playerId: String = "",
    val nickname: String = "",
    val rankings: List<PublicScienceRank> = emptyList(),
    val weeklyRankings: List<PublicScienceRank> = emptyList(),
    val message: String = "",
    val scoreSubmitted: Boolean = false,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LocalRepository(application)
    private val cloudLeaderboard = FirebaseScienceLeaderboardGateway()
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
    private val _elementGuesses = MutableStateFlow(repository.elementGuesses())
    val elementGuesses: StateFlow<Map<LocalDate, ElementGuess>> = _elementGuesses.asStateFlow()
    private val _scienceBestScore = MutableStateFlow(repository.scienceBestScore())
    val scienceBestScore: StateFlow<Int> = _scienceBestScore.asStateFlow()
    private val _scienceLeaderboard = MutableStateFlow(repository.scienceLeaderboard())
    val scienceLeaderboard: StateFlow<List<ScienceLeaderboardEntry>> = _scienceLeaderboard.asStateFlow()
    private val _scienceCloud = MutableStateFlow(
        ScienceCloudState(nickname = repository.playerNickname.orEmpty(), message = text(R.string.champions_status_connecting)),
    )
    val scienceCloud: StateFlow<ScienceCloudState> = _scienceCloud.asStateFlow()
    private var lastScienceSubmissionAttempt: String? = null

    init {
        connectScienceLeaderboard()
    }

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
    fun setLanguage(tag: String) { repository.languageTag = tag; _settings.value = settingsSnapshot() }

    private fun publish() {
        val value = engine?.state ?: return
        _game.value = value
        repository.saveGame(value)
    }

    fun recordElementGuess(symbol: String, correct: Boolean) {
        repository.recordElementGuess(ElementGuess(todayUtc(), symbol, correct))
        _elementGuesses.value = repository.elementGuesses()
    }

    fun refreshDailyHistory() { _dailyHistory.value = repository.dailyHistory() }

    fun recordScienceBest(score: Int) {
        repository.recordScienceBest(score)
        _scienceBestScore.value = repository.scienceBestScore()
    }

    fun loadScienceRun(): ScienceRunSnapshot = repository.scienceRun(todayUtc())

    fun saveScienceRun(run: ScienceRunSnapshot) {
        repository.saveScienceRun(run)
        _scienceLeaderboard.value = repository.scienceLeaderboard()
        if (run.runOver) submitDailyChampions()
    }

    fun refreshScienceLeaderboard() {
        lastScienceSubmissionAttempt = null
        connectScienceLeaderboard()
    }

    fun updatePlayerNickname(value: String) {
        val cleaned = PlayerNames.clean(value)
        if (!PlayerNames.isValid(cleaned)) {
            _scienceCloud.value = _scienceCloud.value.copy(message = text(R.string.champions_status_nickname_requirement))
            return
        }
        viewModelScope.launch {
            _scienceCloud.value = _scienceCloud.value.copy(connecting = true, message = text(R.string.champions_status_saving))
            try {
                val player = cloudLeaderboard.updateDisplayName(cleaned)
                repository.playerNickname = player.displayName
                _scienceCloud.value = _scienceCloud.value.copy(
                    connecting = false,
                    online = true,
                    playerId = player.uid,
                    nickname = player.displayName,
                    message = text(R.string.champions_status_saved),
                )
                loadCloudRankings()
            } catch (_: Exception) {
                _scienceCloud.value = _scienceCloud.value.copy(
                    connecting = false,
                    message = text(R.string.champions_status_save_failed),
                )
            }
        }
    }

    private fun connectScienceLeaderboard() {
        viewModelScope.launch {
            _scienceCloud.value = _scienceCloud.value.copy(connecting = true, message = text(R.string.champions_status_connecting))
            try {
                val player = cloudLeaderboard.connect(repository.playerNickname)
                repository.playerNickname = player.displayName
                _scienceCloud.value = _scienceCloud.value.copy(
                    connecting = false,
                    online = true,
                    playerId = player.uid,
                    nickname = player.displayName,
                    message = text(R.string.champions_status_connected),
                )
                loadCloudRankings()
                submitDailyChampions()
            } catch (_: Exception) {
                _scienceCloud.value = _scienceCloud.value.copy(
                    connecting = false,
                    online = false,
                    message = text(R.string.champions_status_offline_setup),
                )
            }
        }
    }

    private suspend fun loadCloudRankings() {
        try {
            val ranks = cloudLeaderboard.rankings(todayUtc())
            val weeklyRanks = cloudLeaderboard.weeklyRankings(currentWeekStartUtc())
            _scienceCloud.value = _scienceCloud.value.copy(
                rankings = ranks,
                weeklyRankings = weeklyRanks,
                online = true,
            )
        } catch (_: Exception) {
            _scienceCloud.value = _scienceCloud.value.copy(message = text(R.string.champions_status_rankings_unavailable))
        }
    }

    /** Today's partial or complete entry. An unplayed daily game contributes zero until it is finished. */
    fun championEntry(): ScienceScoreSubmission? {
        val today = todayUtc()
        val board = repository.dailyHistory()[today]
        val run = loadScienceRun().takeIf { it.runOver }
        if (board == null && run == null) return null
        return ScienceScoreSubmission(
            date = today,
            moleculeScore = board?.score ?: 0,
            scienceScore = run?.totalScore ?: 0,
            uniqueDiscoveries = board?.uniqueDiscoveries ?: 0,
            mysteriesSolved = run?.solvedCount ?: 0,
            contentVersion = 1,
        )
    }

    private fun submitDailyChampions() {
        val entry = championEntry() ?: return
        if (!_scienceCloud.value.online || !ChampionLimits.isPlausible(entry)) return
        val key = "${entry.date}:${entry.moleculeScore}:${entry.scienceScore}"
        if (lastScienceSubmissionAttempt == key) return
        lastScienceSubmissionAttempt = key
        viewModelScope.launch {
            _scienceCloud.value = _scienceCloud.value.copy(message = text(R.string.champions_status_submitting))
            when (val result = cloudLeaderboard.submit(entry)) {
                is SubmissionResult.Accepted -> {
                    _scienceCloud.value = _scienceCloud.value.copy(
                        scoreSubmitted = true,
                        message = result.rank?.let { text(R.string.champions_status_submitted_rank, it) }
                            ?: text(R.string.champions_status_submitted),
                    )
                    loadCloudRankings()
                }
                SubmissionResult.UpToDate -> {
                    _scienceCloud.value = _scienceCloud.value.copy(scoreSubmitted = true, message = text(R.string.champions_status_already_submitted))
                    loadCloudRankings()
                }
                SubmissionResult.Offline -> {
                    _scienceCloud.value = _scienceCloud.value.copy(message = text(R.string.champions_status_offline_retry))
                }
                is SubmissionResult.Rejected -> {
                    _scienceCloud.value = _scienceCloud.value.copy(message = result.reason)
                }
            }
        }
    }

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
            submitDailyChampions()
        }
    }

    fun todayUtc(): LocalDate = LocalDate.now(ZoneOffset.UTC)

    fun currentWeekStartUtc(): LocalDate =
        todayUtc().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    /** Status text follows the app-selected language instead of the device/application locale. */
    private fun text(id: Int, vararg args: Any): String =
        LocaleManager.wrap(getApplication(), repository.languageTag).getString(id, *args)

    private fun settingsSnapshot() = SettingsState(
        repository.soundEnabled, repository.vibrationEnabled, repository.reducedMotion, repository.languageTag,
    )
}
