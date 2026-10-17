package uk.edbetter.kemdaily.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

data class FirebasePlayer(val uid: String, val displayName: String)

/** Firebase implementation of the combined Daily Champions leaderboard. */
class FirebaseScienceLeaderboardGateway(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) : ScienceLeaderboardGateway {
    private var player: FirebasePlayer? = null

    suspend fun connect(preferredName: String?): FirebasePlayer {
        val user = auth.currentUser ?: auth.signInAnonymously().await().user
            ?: error("Firebase did not return a player identity")
        val profile = firestore.collection(PLAYERS).document(user.uid).get().await()
        val storedName = profile.getString("displayName")?.takeIf(PlayerNames::isValid)
        val displayName = storedName ?: preferredName?.takeIf(PlayerNames::isValid)
            ?: PlayerNames.generated(user.uid)
        if (storedName == null) writeProfile(user.uid, displayName)
        return FirebasePlayer(user.uid, displayName).also { player = it }
    }

    suspend fun updateDisplayName(displayName: String): FirebasePlayer {
        require(PlayerNames.isValid(displayName)) { PlayerNames.requirement }
        val current = player ?: connect(null)
        val cleaned = PlayerNames.clean(displayName)
        writeProfile(current.uid, cleaned)
        return FirebasePlayer(current.uid, cleaned).also { player = it }
    }

    override suspend fun submit(submission: ScienceScoreSubmission): SubmissionResult {
        val current = player ?: connect(null)
        val scoreRef = firestore.collection(SCORES).document(submission.date.toString())
            .collection(PLAYERS).document(current.uid)
        val weekStart = submission.date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weeklyRef = firestore.collection(WEEKLY_SCORES).document(weekStart.toString())
            .collection(PLAYERS).document(current.uid)
        return try {
            val updated = firestore.runTransaction { transaction ->
                val existing = transaction.get(scoreRef)
                val previousScore = existing.getLong("score")?.toInt() ?: 0
                if (existing.exists() && previousScore >= submission.score) return@runTransaction false

                val previousMolecule = existing.getLong("moleculeScore")?.toInt() ?: 0
                val previousScience = existing.getLong("scienceScore")?.toInt() ?: 0
                val previousUnique = existing.getLong("uniqueDiscoveries")?.toInt() ?: 0
                val previousSolved = existing.getLong("mysteriesSolved")?.toInt() ?: 0
                require(submission.moleculeScore >= previousMolecule && submission.scienceScore >= previousScience)

                val weekly = transaction.get(weeklyRef)
                val daysPlayed = (weekly.getLong("daysPlayed")?.toInt() ?: 0) + if (existing.exists()) 0 else 1
                val weeklyMolecule = (weekly.getLong("moleculeScore")?.toInt() ?: 0) + submission.moleculeScore - previousMolecule
                val weeklyScience = (weekly.getLong("scienceScore")?.toInt() ?: 0) + submission.scienceScore - previousScience
                val weeklyUnique = (weekly.getLong("uniqueDiscoveries")?.toInt() ?: 0) + submission.uniqueDiscoveries - previousUnique
                val weeklySolved = (weekly.getLong("mysteriesSolved")?.toInt() ?: 0) + submission.mysteriesSolved - previousSolved
                val dailyContentVersion = maxOf(existing.getLong("contentVersion")?.toInt() ?: 1, submission.contentVersion)
                val weeklyContentVersion = maxOf(weekly.getLong("contentVersion")?.toInt() ?: 1, submission.contentVersion)

                transaction.set(scoreRef, mapOf(
                    "displayName" to current.displayName,
                    "score" to submission.score,
                    "moleculeScore" to submission.moleculeScore,
                    "scienceScore" to submission.scienceScore,
                    "uniqueDiscoveries" to submission.uniqueDiscoveries,
                    "mysteriesSolved" to submission.mysteriesSolved,
                    "contentVersion" to dailyContentVersion,
                    "submittedAt" to FieldValue.serverTimestamp(),
                    "verified" to false,
                ))
                transaction.set(weeklyRef, mapOf(
                    "displayName" to current.displayName,
                    "score" to weeklyMolecule + weeklyScience,
                    "moleculeScore" to weeklyMolecule,
                    "scienceScore" to weeklyScience,
                    "daysPlayed" to daysPlayed,
                    "uniqueDiscoveries" to weeklyUnique,
                    "mysteriesSolved" to weeklySolved,
                    "contentVersion" to weeklyContentVersion,
                    "updatedAt" to FieldValue.serverTimestamp(),
                    "verified" to false,
                ))
                true
            }.await()
            if (updated) SubmissionResult.Accepted(null) else SubmissionResult.UpToDate
        } catch (_: Exception) {
            // A concurrent submission may already have stored the same or a better total.
            try {
                val storedScore = scoreRef.get().await().getLong("score") ?: -1L
                if (storedScore >= submission.score) SubmissionResult.UpToDate else SubmissionResult.Offline
            } catch (_: Exception) {
                SubmissionResult.Offline
            }
        }
    }

    override suspend fun rankings(date: LocalDate, limit: Int): List<PublicScienceRank> {
        if (auth.currentUser == null) connect(null)
        return loadRankings(SCORES, date, limit)
    }

    override suspend fun weeklyRankings(weekStart: LocalDate, limit: Int): List<PublicScienceRank> {
        if (auth.currentUser == null) connect(null)
        return loadRankings(WEEKLY_SCORES, weekStart, limit)
    }

    private suspend fun loadRankings(collection: String, periodStart: LocalDate, limit: Int): List<PublicScienceRank> {
        val documents = firestore.collection(collection).document(periodStart.toString()).collection(PLAYERS)
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get().await().documents
        return documents.mapNotNull { document ->
            val score = document.getLong("score")?.toInt() ?: return@mapNotNull null
            PublicScienceRank(
                rank = 0,
                playerId = document.id,
                displayName = document.getString("displayName") ?: "Mystery Scientist",
                score = score,
                moleculeScore = document.getLong("moleculeScore")?.toInt() ?: 0,
                scienceScore = document.getLong("scienceScore")?.toInt() ?: 0,
                uniqueDiscoveries = document.getLong("uniqueDiscoveries")?.toInt() ?: 0,
                mysteriesSolved = document.getLong("mysteriesSolved")?.toInt() ?: 0,
                verified = document.getBoolean("verified") ?: false,
            )
        }.sortedWith(
            compareByDescending<PublicScienceRank> { it.score }
                .thenByDescending { it.mysteriesSolved }
                .thenBy { it.displayName.lowercase(Locale.ROOT) },
        ).mapIndexed { index, rank -> rank.copy(rank = index + 1) }
    }

    private suspend fun writeProfile(uid: String, displayName: String) {
        firestore.collection(PLAYERS).document(uid).set(
            mapOf(
                "displayName" to displayName,
                "normalizedName" to displayName.lowercase(Locale.ROOT),
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
        ).await()
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { value -> if (continuation.isActive) continuation.resume(value) }
        addOnFailureListener { error -> if (continuation.isActive) continuation.resumeWithException(error) }
        addOnCanceledListener { continuation.cancel() }
    }

    private companion object {
        const val PLAYERS = "players"
        const val SCORES = "daily_champions"
        const val WEEKLY_SCORES = "weekly_champions"
    }
}

object PlayerNames {
    const val requirement = "Use 3–16 letters, numbers or spaces"
    private val allowed = Regex("[A-Za-z0-9 ]{3,16}")
    private val blocked = setOf("admin", "administrator", "moderator", "firebase", "edbetter", "kemistri")
    private val adjectives = listOf("Curious", "Bright", "Clever", "Cosmic", "Brave", "Keen", "Swift", "Sunny")
    private val nouns = listOf("Atom", "Beaker", "Flask", "Photon", "Nova", "Quark", "Comet", "Orbit")

    fun clean(value: String): String = value.trim().replace(Regex(" +"), " ")

    fun isValid(value: String): Boolean {
        val cleaned = clean(value)
        return allowed.matches(cleaned) && cleaned.lowercase(Locale.ROOT) !in blocked
    }

    fun generated(uid: String): String {
        val hash = uid.hashCode().toUInt().toLong()
        val adjective = adjectives[(hash % adjectives.size).toInt()]
        val noun = nouns[((hash / adjectives.size) % nouns.size).toInt()]
        val number = (hash % 900 + 100).toInt()
        return "$adjective$noun$number".take(16)
    }
}
