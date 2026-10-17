package uk.edbetter.kemdaily.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.edbetter.kemdaily.BuildConfig
import uk.edbetter.kemdaily.R
import uk.edbetter.kemdaily.AppLanguage
import uk.edbetter.kemdaily.ads.AdMobInterstitial
import uk.edbetter.kemdaily.core.chemistry.ElementCatalog
import uk.edbetter.kemdaily.core.chemistry.Composition
import uk.edbetter.kemdaily.core.chemistry.Recognition
import uk.edbetter.kemdaily.core.chemistry.Substance
import uk.edbetter.kemdaily.core.chemistry.SubstanceCatalog
import uk.edbetter.kemdaily.core.game.BOARD_SIZE
import uk.edbetter.kemdaily.core.game.CalendarDay
import uk.edbetter.kemdaily.core.game.Cell
import uk.edbetter.kemdaily.core.game.DailyHistory
import uk.edbetter.kemdaily.core.game.DailyRecord
import uk.edbetter.kemdaily.core.game.GameMode
import uk.edbetter.kemdaily.core.game.GamePhase
import uk.edbetter.kemdaily.core.game.GameState
import uk.edbetter.kemdaily.core.game.GameEngine
import uk.edbetter.kemdaily.core.game.MISSION_TARGET
import uk.edbetter.kemdaily.data.DailyElement
import uk.edbetter.kemdaily.data.ElementOfDay
import uk.edbetter.kemdaily.data.ElementFactsTable
import uk.edbetter.kemdaily.data.EverydayFacts
import uk.edbetter.kemdaily.data.MysteryCategory
import uk.edbetter.kemdaily.data.ScienceMysteries
import uk.edbetter.kemdaily.data.ScienceLeaderboardEntry
import uk.edbetter.kemdaily.data.ScienceRunSnapshot
import uk.edbetter.kemdaily.data.TablePosition
import uk.edbetter.kemdaily.ui.theme.Coral
import uk.edbetter.kemdaily.ui.theme.Gold
import uk.edbetter.kemdaily.ui.theme.Green
import uk.edbetter.kemdaily.ui.theme.Navy
import uk.edbetter.kemdaily.ui.theme.Sky
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private object Routes {
    const val Home = "home"
    const val Daily = "daily"
    const val DailyHistory = "daily_history"
    const val Science = "science"
    const val Fact = "fact"
    const val Element = "element"
    const val Leaderboard = "leaderboard"
    const val Review = "review"
    const val Settings = "settings"
}

private data class LocalDailyTotal(
    val date: LocalDate,
    val science: ScienceLeaderboardEntry?,
    val molecule: DailyRecord?,
) {
    val score: Int get() = (science?.score ?: 0) + (molecule?.score ?: 0)
}

@Composable
fun KemistriApp(viewModel: MainViewModel = viewModel()) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.Home) {
        composable(Routes.Home) { HomeScreen(nav, viewModel) }
        composable(Routes.Daily) {
            androidx.compose.runtime.LaunchedEffect(Unit) { viewModel.start(GameMode.DAILY) }
            HuntScreen(nav, viewModel, daily = true)
        }
        composable(Routes.DailyHistory) { DailyHistoryScreen(nav, viewModel) }
        composable(Routes.Science) { ScienceUncoveredScreen(nav, viewModel) }
        composable(Routes.Fact) { FactScreen(nav) }
        composable(Routes.Element) { ElementOfDayScreen(nav, viewModel) }
        composable(Routes.Leaderboard) { LeaderboardScreen(nav, viewModel) }
        composable(Routes.Review) { ReviewScreen(nav, viewModel) }
        composable(Routes.Settings) { SettingsScreen(nav, viewModel) }
    }
}

@Composable
private fun HomeScreen(nav: NavHostController, viewModel: MainViewModel) {
    val history by viewModel.dailyHistory.collectAsStateWithLifecycle()
    val scienceRuns by viewModel.scienceLeaderboard.collectAsStateWithLifecycle()
    val elementGuesses by viewModel.elementGuesses.collectAsStateWithLifecycle()
    val streakDates = history.keys + scienceRuns.map { it.date } + elementGuesses.values.filter { it.correct }.map { it.date }
    val streak = DailyHistory.streaks(streakDates, viewModel.todayUtc()).current
    val cloud by viewModel.scienceCloud.collectAsStateWithLifecycle()
    Scaffold(containerColor = Color(0xFFE8FAFF)) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(R.drawable.kemistri_everyday_brand),
                        contentDescription = stringResource(R.string.home_logo_description),
                        modifier = Modifier.size(76.dp).clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(stringResource(R.string.app_name), color = Navy, fontWeight = FontWeight.Black, fontSize = 24.sp)
                        Text(
                            stringResource(R.string.home_tagline),
                            color = Navy.copy(alpha = .72f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (cloud.nickname.isNotBlank()) {
                            // The public leaderboard name; tapping opens Daily Champions, where it can be changed.
                            val openChampionsLabel = stringResource(R.string.home_open_champions)
                            Row(
                                Modifier.padding(top = 6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable(onClickLabel = openChampionsLabel) { nav.navigate(Routes.Leaderboard) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(5.dp))
                                Text(stringResource(R.string.home_playing_as, cloud.nickname), color = Navy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            item {
                val dailySubtitle = if (streak > 0) {
                    pluralStringResource(R.plurals.home_daily_subtitle_streak, streak, streak)
                } else {
                    stringResource(R.string.home_daily_subtitle)
                }
                FeatureCard(stringResource(R.string.home_daily_title), dailySubtitle, Icons.Default.CalendarMonth, Green) {
                    nav.navigate(Routes.Daily)
                }
            }
            item {
                FeatureCard(stringResource(R.string.home_science_title), stringResource(R.string.home_science_subtitle), Icons.Default.AutoAwesome, Sky) {
                    nav.navigate(Routes.Science)
                }
            }
            item {
                FeatureCard(stringResource(R.string.home_everyday_title), stringResource(R.string.home_everyday_subtitle), Icons.Default.Lightbulb, Gold) {
                    nav.navigate(Routes.Fact)
                }
            }
            item {
                FeatureCard(stringResource(R.string.home_element_title), stringResource(R.string.home_element_subtitle), Icons.Default.Science, Coral) {
                    nav.navigate(Routes.Element)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SmallNavCard(stringResource(R.string.home_nav_streak), Icons.Default.LocalFireDepartment, Modifier.weight(1f)) { nav.navigate(Routes.DailyHistory) }
                    SmallNavCard(stringResource(R.string.home_nav_leaders), Icons.Default.EmojiEvents, Modifier.weight(1f)) { nav.navigate(Routes.Leaderboard) }
                    SmallNavCard(stringResource(R.string.home_nav_settings), Icons.Default.Settings, Modifier.weight(1f)) { nav.navigate(Routes.Settings) }
                }
            }
            item {
                Text(
                    stringResource(R.string.common_copyright),
                    modifier = Modifier.fillMaxWidth().padding(10.dp), textAlign = TextAlign.Center,
                    color = Navy.copy(alpha = .55f), fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(title: String, subtitle: String, icon: ImageVector, color: Color, action: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 92.dp).clickable(onClick = action),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = .13f)),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(Modifier.fillMaxWidth().heightIn(min = 92.dp).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(54.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(31.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(subtitle, color = Navy.copy(alpha = .7f), fontSize = 13.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = color)
        }
    }
}

@Composable
private fun SmallNavCard(title: String, icon: ImageVector, modifier: Modifier, action: () -> Unit) {
    Card(modifier.height(78.dp).clickable(onClick = action), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = Navy)
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HuntScreen(nav: NavHostController, viewModel: MainViewModel, daily: Boolean = false) {
    val leave = rememberLeaveWithAd(nav)
    val state by viewModel.game.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showRules by remember { mutableStateOf(false) }
    var showDiscoveries by remember { mutableStateOf(false) }
    var revealDiscovery by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(state?.lastDiscovery, settings.reducedMotion) {
        revealDiscovery = false
        state?.lastDiscovery?.let { discovery ->
            if (!settings.reducedMotion) delay(460)
            if (discovery.isFirst) {
                revealDiscovery = true
            } else {
                Toast.makeText(
                    context,
                    context.resources.getQuantityString(
                        R.plurals.hunt_already_found_toast,
                        discovery.score.awarded,
                        discovery.substance.formula.withSubscripts(),
                        discovery.score.awarded,
                    ),
                    Toast.LENGTH_SHORT,
                ).show()
                viewModel.dismissDiscovery()
            }
        }
    }
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(if (daily) R.string.home_daily_title else R.string.hunt_title_standard),
                back = { leave() },
                actions = {
                    if (daily) {
                        IconButton({ nav.navigate(Routes.DailyHistory) }) { Icon(Icons.Default.CalendarMonth, stringResource(R.string.hunt_daily_history)) }
                    }
                    IconButton({ showDiscoveries = true }) {
                        Box {
                            Icon(Icons.Default.EmojiEvents, stringResource(R.string.hunt_session_discoveries))
                            val found = state?.uniqueDiscoveries?.size ?: 0
                            if (found > 0) {
                                Box(
                                    Modifier.align(Alignment.TopEnd).size(16.dp).background(Coral, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) { Text(found.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                            }
                        }
                    }
                    IconButton({ showRules = true }) { Icon(Icons.Default.Info, stringResource(R.string.hunt_game_rules)) }
                },
            )
        },
        containerColor = Color(0xFFF4FCFF),
    ) { padding ->
        state?.let { game ->
            Column(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 14.dp).navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(8.dp))
                if (daily) {
                    Text(
                        stringResource(
                            R.string.hunt_shared_puzzle,
                            viewModel.todayUtc().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())),
                        ),
                        color = Coral, fontWeight = FontWeight.Bold, fontSize = 12.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                }
                StatsRow(game, onFoundClick = { showDiscoveries = true })
                Spacer(Modifier.height(5.dp))
                Text(stringResource(R.string.hunt_mission_goal), fontSize = 11.sp, color = Navy.copy(alpha = .65f))
                LinearProgressIndicator(
                    progress = { (game.uniqueDiscoveries.size / MISSION_TARGET.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = Green,
                    trackColor = Color(0xFFDDE9EC),
                )
                Spacer(Modifier.height(8.dp))
                AtomBoard(game, viewModel::select, settings.reducedMotion)
                Spacer(Modifier.height(7.dp))
                SelectionTray(game)
                RecentDiscoveries(game)
                AnimatedVisibility(game.message != null) {
                    Text(localizedGameMessage(game.message.orEmpty()), color = if (game.message?.contains("not yet") == true) Coral else Green,
                        fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp), textAlign = TextAlign.Center)
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    OutlinedButton(viewModel::hint, Modifier.weight(1f), contentPadding = PaddingValues(8.dp)) {
                        Icon(Icons.Default.Lightbulb, null); Spacer(Modifier.width(5.dp)); Text(stringResource(R.string.hunt_hint))
                    }
                    OutlinedButton(viewModel::shuffle, Modifier.weight(1f), contentPadding = PaddingValues(8.dp)) {
                        Icon(Icons.Default.Shuffle, null); Spacer(Modifier.width(5.dp)); Text(stringResource(R.string.hunt_shuffle))
                    }
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    OutlinedButton(viewModel::clearSelection, Modifier.weight(.8f)) { Text(stringResource(R.string.hunt_clear), color = Coral) }
                    Button(viewModel::submit, Modifier.weight(1.35f), colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                        Icon(Icons.Default.AutoAwesome, null); Spacer(Modifier.width(6.dp)); Text(stringResource(R.string.hunt_discover))
                    }
                }
            }
            if (game.phase == GamePhase.MISSION_COMPLETE) MissionDialog(game, viewModel, nav)
            else if (game.phase == GamePhase.FINISHED) ResultDialog(game, viewModel, nav)
            else if (game.lastDiscovery?.isFirst == true && revealDiscovery) DiscoveryDialog(game, viewModel)
            if (showRules) RulesDialog(onDismiss = { showRules = false })
            if (showDiscoveries) {
                SessionDiscoveriesDialog(
                    game = game,
                    onDismiss = { showDiscoveries = false },
                )
            }
        }
    }
}

@Composable
private fun StatsRow(game: GameState, onFoundClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        StatPill(stringResource(R.string.hunt_stat_moves), game.movesRemaining.toString(), Modifier.weight(1f))
        StatPill(stringResource(R.string.hunt_stat_score), game.score.toString(), Modifier.weight(1f))
        StatPill(stringResource(R.string.hunt_stat_found), "${game.uniqueDiscoveries.size}/$MISSION_TARGET", Modifier.weight(1f).clickable(onClick = onFoundClick))
    }
}

@Composable
private fun StatPill(label: String, value: String, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(13.dp)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, color = Navy.copy(alpha = .65f))
            Text(value, fontSize = 19.sp, fontWeight = FontWeight.Black)
        }
    }
}

private val atomColors = mapOf(
    "H" to Color(0xFFBFE4FF), "O" to Color(0xFFFFBECB), "C" to Color(0xFFFFE08A),
    "N" to Color(0xFFD4C8FF), "Na" to Color(0xFFBFC8FF), "Cl" to Color(0xFFBDE9B6),
    "K" to Color(0xFFFFC4DE), "S" to Color(0xFFFFE49A), "Li" to Color(0xFFDCCBFA),
    "F" to Color(0xFFB7ECE7), "Ca" to Color(0xFFBBDFF6), "Mg" to Color(0xFFFFD0AD),
)

@Composable
private fun AtomBoard(game: GameState, onSelect: (Cell) -> Unit, reducedMotion: Boolean) {
    Column(
        Modifier.fillMaxWidth().aspectRatio(1f).background(Color.White, RoundedCornerShape(20.dp)).padding(7.dp)
            .pointerInput(game.board) {
                var lastDragCell: Cell? = null
                fun cellAt(x: Float, y: Float): Cell? {
                    if (x !in 0f..size.width.toFloat() || y !in 0f..size.height.toFloat()) return null
                    val column = (x / (size.width / BOARD_SIZE.toFloat())).toInt().coerceIn(0, BOARD_SIZE - 1)
                    val row = (y / (size.height / BOARD_SIZE.toFloat())).toInt().coerceIn(0, BOARD_SIZE - 1)
                    return Cell(row, column)
                }
                detectDragGestures(
                    onDragStart = { offset ->
                        cellAt(offset.x, offset.y)?.let { cell ->
                            lastDragCell = cell
                            onSelect(cell)
                        }
                    },
                    onDrag = { change, _ ->
                        cellAt(change.position.x, change.position.y)?.let { cell ->
                            if (cell != lastDragCell) {
                                lastDragCell = cell
                                onSelect(cell)
                            }
                        }
                    },
                    onDragEnd = { lastDragCell = null },
                    onDragCancel = { lastDragCell = null },
                )
            },
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        repeat(BOARD_SIZE) { row ->
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(BOARD_SIZE) { column ->
                    val cell = Cell(row, column)
                    val tile = game.board[cell.index]
                    val number = game.selection.indexOf(cell).takeIf { it >= 0 }?.plus(1)
                    val selected = number != null
                    val hinted = cell in game.hintedCells
                    val tileDescription = buildString {
                        append(stringResource(R.string.hunt_tile_description, ElementCatalog.names[tile.symbol] ?: tile.symbol, row + 1, column + 1))
                        if (number != null) append(stringResource(R.string.hunt_tile_selected, number))
                        if (hinted) append(stringResource(R.string.hunt_tile_hinted))
                    }
                    Box(
                        Modifier.weight(1f).fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .then(if (hinted) Modifier.border(3.dp, Gold, RoundedCornerShape(12.dp)) else Modifier)
                            .clickable { onSelect(cell) }
                            .semantics {
                                contentDescription = tileDescription
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (reducedMotion) {
                            AtomTileFace(tile.symbol, selected)
                        } else {
                            AnimatedContent(
                                targetState = tile,
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                                transitionSpec = {
                                    (slideInVertically(
                                        animationSpec = tween(durationMillis = 380, delayMillis = row * 28),
                                        initialOffsetY = { -it },
                                    ) + fadeIn(tween(220, delayMillis = row * 28))) togetherWith
                                        (slideOutVertically(
                                            animationSpec = tween(170),
                                            targetOffsetY = { it },
                                        ) + fadeOut(tween(140)))
                                },
                                label = "atom drop",
                            ) { animatedTile ->
                                AtomTileFace(animatedTile.symbol, selected)
                            }
                        }
                        number?.let {
                            Box(Modifier.align(Alignment.TopEnd).padding(3.dp).size(17.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                                Text(it.toString(), color = Green, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AtomTileFace(symbol: String, selected: Boolean) {
    Box(
        Modifier.fillMaxSize().background(if (selected) Green else atomColors[symbol] ?: Color.LightGray),
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, fontSize = 19.sp, fontWeight = FontWeight.Black, color = if (selected) Color.White else Navy)
    }
}

@Composable
private fun SelectionTray(game: GameState) {
    val symbols = game.selection.map { game.board[it.index].symbol }
    val recognized = if (symbols.size >= 2) {
        when (val result = SubstanceCatalog.recognize(Composition.fromSymbols(symbols))) {
            is Recognition.Recognized -> result.substance
            is Recognition.Ambiguous -> result.substances.firstOrNull()
            is Recognition.NotYetRecognized -> null
        }
    } else null
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF7FF)), shape = RoundedCornerShape(13.dp)) {
        if (symbols.isEmpty()) {
            Text(
                stringResource(R.string.hunt_select_neighbours),
                Modifier.fillMaxWidth().padding(10.dp),
                textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Navy,
            )
        } else {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(symbols.joinToString(" + "), fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("→", color = Navy.copy(alpha = .55f), fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp))
                if (recognized == null) {
                    Text(stringResource(R.string.hunt_ready_to_review), color = Navy.copy(alpha = .65f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    val first = recognized.id !in game.uniqueDiscoveries
                    val points = GameEngine.score(recognized, first, game.consecutiveUnique).awarded
                    Column(horizontalAlignment = Alignment.End) {
                        Text(recognized.formula.withSubscripts(), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text(recognized.displayName, fontSize = 10.sp, color = Navy.copy(alpha = .65f))
                        Text(pluralStringResource(R.plurals.common_points_awarded, points, points), fontSize = 10.sp, color = Green, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentDiscoveries(game: GameState) {
    val recent = game.discoveryHistory.asReversed().distinct().take(5).mapNotNull { id ->
        SubstanceCatalog.all.firstOrNull { it.id == id }
    }
    AnimatedVisibility(recent.isNotEmpty()) {
        Row(
            Modifier.fillMaxWidth().padding(top = 5.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.hunt_stat_found), fontSize = 10.sp, color = Navy.copy(alpha = .55f), fontWeight = FontWeight.Bold)
            recent.forEach { substance ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(9.dp)) {
                    Text(substance.formula.withSubscripts(), Modifier.padding(horizontal = 9.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MissionDialog(game: GameState, viewModel: MainViewModel, nav: NavHostController) {
    AlertDialog(
        onDismissRequest = {}, icon = { Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(48.dp)) },
        title = { Text(stringResource(R.string.hunt_mission_complete_title), fontWeight = FontWeight.Black) },
        text = {
            Text(
                stringResource(
                    R.string.hunt_mission_complete_text,
                    pluralStringResource(R.plurals.hunt_found_substances, game.uniqueDiscoveries.size, game.uniqueDiscoveries.size),
                    pluralStringResource(R.plurals.hunt_moves_remaining, game.movesRemaining, game.movesRemaining),
                    game.score,
                ),
            )
        },
        confirmButton = { Button(viewModel::continueMission) { Text(stringResource(R.string.hunt_keep_discovering)) } },
        dismissButton = {
            val activity = LocalContext.current.findActivity()
            OutlinedButton({
                viewModel.finish()
                showAdThen(activity) { nav.popBackStack() }
            }) { Text(stringResource(R.string.hunt_finish)) }
        },
    )
}

@Composable
private fun DiscoveryDialog(game: GameState, viewModel: MainViewModel) {
    val discovery = game.lastDiscovery ?: return
    val item = discovery.substance
    val score = discovery.score
    Dialog(
        onDismissRequest = viewModel::dismissDiscovery,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            Modifier.fillMaxWidth().padding(horizontal = 22.dp).widthIn(max = 430.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.fillMaxWidth().background(Color(0xFFE9FAFF)).padding(top = 22.dp, bottom = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🍃", fontSize = 34.sp, modifier = Modifier.align(Alignment.TopStart).padding(start = 20.dp))
                    Text("✨", fontSize = 28.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(if (discovery.isFirst) R.string.hunt_discovery_great else R.string.hunt_discovery_again),
                            color = Navy, fontWeight = FontWeight.Black, fontSize = 24.sp,
                        )
                        Spacer(Modifier.height(12.dp))
                        CompositionGraphic(item)
                        Spacer(Modifier.height(10.dp))
                        Text(item.formula.withSubscripts(), color = Navy, fontWeight = FontWeight.Black, fontSize = 34.sp)
                        Text(item.displayName, color = Navy, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(item.category.label, color = Navy.copy(alpha = .58f), fontSize = 11.sp)
                    }
                }
                Card(
                    modifier = Modifier.padding(top = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE34E)),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        pluralStringResource(R.plurals.common_points_awarded, score.awarded, score.awarded),
                        Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                        color = Navy, fontWeight = FontWeight.Black, fontSize = 17.sp,
                    )
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F9FB)),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Row(Modifier.padding(13.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Science, null, tint = Navy.copy(alpha = .7f), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(9.dp))
                        Text(item.fact, color = Navy, fontSize = 12.sp, lineHeight = 17.sp)
                    }
                }
                Text(
                    listOfNotNull(
                        pluralStringResource(R.plurals.hunt_score_atoms, score.atoms, score.atoms),
                        pluralStringResource(R.plurals.hunt_score_elements, score.elements, score.elements),
                        stringResource(R.string.hunt_score_rarity, score.rarity),
                        if (score.streak > 0) stringResource(R.string.hunt_score_streak, score.streak) else null,
                    ).joinToString(" · "),
                    color = Navy.copy(alpha = .55f), fontSize = 10.sp,
                )
                Button(
                    viewModel::dismissDiscovery,
                    modifier = Modifier.fillMaxWidth().padding(18.dp).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                ) { Text(stringResource(R.string.common_continue), fontWeight = FontWeight.Black, fontSize = 16.sp) }
            }
        }
    }
}

@Composable
private fun CompositionGraphic(substance: Substance) {
    val atoms = substance.composition.atoms.entries.flatMap { (symbol, count) -> List(count) { symbol } }.take(7)
    Row(horizontalArrangement = Arrangement.spacedBy((-5).dp), verticalAlignment = Alignment.CenterVertically) {
        atoms.forEachIndexed { index, symbol ->
            if (index > 0) Box(Modifier.width(14.dp).height(4.dp).background(Color(0xFFBBC2C8)))
            Box(
                Modifier.size(if (index == atoms.lastIndex / 2) 48.dp else 42.dp)
                    .background(atomColors[symbol] ?: Sky, CircleShape)
                    .border(2.dp, Color.White.copy(alpha = .85f), CircleShape),
                contentAlignment = Alignment.Center,
            ) { Text(symbol, color = Navy, fontWeight = FontWeight.Black, fontSize = 13.sp) }
        }
    }
}

@Composable
private fun RulesDialog(onDismiss: () -> Unit) {
    val rules = listOf(
        stringResource(R.string.hunt_rule_1),
        stringResource(R.string.hunt_rule_2),
        stringResource(R.string.hunt_rule_3),
        stringResource(R.string.hunt_rule_4),
        stringResource(R.string.hunt_rule_5),
        stringResource(R.string.hunt_rule_6),
        stringResource(R.string.hunt_rule_7),
        stringResource(R.string.hunt_rule_8),
        stringResource(R.string.hunt_rule_9),
        stringResource(R.string.hunt_rule_10),
    )
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier.fillMaxWidth().padding(18.dp).heightIn(max = 760.dp).widthIn(max = 520.dp),
            shape = RoundedCornerShape(24.dp),
        ) {
            LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = Sky, modifier = Modifier.size(30.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(stringResource(R.string.hunt_rules_title), fontWeight = FontWeight.Black, fontSize = 21.sp)
                            Text(stringResource(R.string.hunt_rules_subtitle), color = Navy.copy(alpha = .62f), fontSize = 12.sp)
                        }
                    }
                }
                items(rules.size) { index ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(Modifier.size(22.dp).background(Coral.copy(alpha = .16f), CircleShape), contentAlignment = Alignment.Center) {
                            Text((index + 1).toString(), color = Coral, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                        Spacer(Modifier.width(9.dp))
                        Text(rules[index], fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }
                item { HorizontalDivider(Modifier.padding(vertical = 5.dp)) }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFBF3))) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.hunt_scoring), fontWeight = FontWeight.Black, fontSize = 17.sp)
                            Text(stringResource(R.string.hunt_scoring_formula), color = Green, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text(
                                stringResource(R.string.hunt_scoring_legend),
                                color = Navy.copy(alpha = .68f), textAlign = TextAlign.Center, fontSize = 11.sp,
                            )
                        }
                    }
                }
                item {
                    Text(
                        stringResource(R.string.hunt_formula_note),
                        color = Navy.copy(alpha = .65f), textAlign = TextAlign.Center, fontSize = 11.sp,
                    )
                }
                item { Button(onDismiss, Modifier.fillMaxWidth()) { Text(stringResource(R.string.common_got_it)) } }
            }
        }
    }
}

@Composable
private fun SessionDiscoveriesDialog(game: GameState, onDismiss: () -> Unit) {
    val counts = game.discoveryHistory.groupingBy { it }.eachCount()
    val discoveries = game.discoveryHistory.distinct().mapNotNull { id ->
        SubstanceCatalog.all.firstOrNull { it.id == id }
    }
    val missionDiscoveries = discoveries.take(MISSION_TARGET)
    val extraDiscoveries = discoveries.drop(MISSION_TARGET)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier.fillMaxWidth().padding(18.dp).heightIn(max = 720.dp).widthIn(max = 500.dp),
            shape = RoundedCornerShape(24.dp),
        ) {
            LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(34.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.hunt_discoveries_title), fontWeight = FontWeight.Black, fontSize = 22.sp)
                            Text(
                                listOf(
                                    pluralStringResource(R.plurals.hunt_discoveries_mission, missionDiscoveries.size, missionDiscoveries.size),
                                    pluralStringResource(R.plurals.hunt_discoveries_extra, extraDiscoveries.size, extraDiscoveries.size),
                                    pluralStringResource(R.plurals.hunt_discoveries_total, game.discoveryHistory.size, game.discoveryHistory.size),
                                ).joinToString(" · "),
                                color = Navy.copy(alpha = .62f),
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
                if (discoveries.isEmpty()) {
                    item {
                        Text(stringResource(R.string.hunt_discoveries_empty), Modifier.padding(vertical = 24.dp), textAlign = TextAlign.Center)
                    }
                } else {
                    item { Text(stringResource(R.string.hunt_mission_discoveries), color = Green, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp) }
                    items(missionDiscoveries, key = { "mission_${it.id}" }) { substance ->
                        DiscoverySummaryRow(substance, counts[substance.id] ?: 1)
                    }
                    if (extraDiscoveries.isNotEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.hunt_extra_discoveries),
                                modifier = Modifier.padding(top = 8.dp),
                                color = Coral,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                            )
                        }
                        items(extraDiscoveries, key = { "extra_${it.id}" }) { substance ->
                            DiscoverySummaryRow(substance, counts[substance.id] ?: 1)
                        }
                    }
                }
                item { HorizontalDivider(Modifier.padding(vertical = 3.dp)) }
                item {
                    Button(onDismiss, Modifier.fillMaxWidth()) { Text(stringResource(R.string.hunt_back_to_hunt)) }
                }
            }
        }
    }
}

@Composable
private fun DiscoverySummaryRow(substance: Substance, timesFound: Int) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(48.dp).background(atomColors[substance.composition.atoms.keys.first()] ?: Sky, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(substance.formula.withSubscripts(), color = Navy, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(substance.displayName, fontWeight = FontWeight.Bold)
            Text(substance.category.label, color = Navy.copy(alpha = .58f), fontSize = 11.sp)
        }
        Text("×$timesFound", color = Green, fontWeight = FontWeight.Black)
    }
}

private fun String.withSubscripts(): String = buildString {
    this@withSubscripts.forEach { character ->
        append(
            when (character) {
                '0' -> '₀'; '1' -> '₁'; '2' -> '₂'; '3' -> '₃'; '4' -> '₄'
                '5' -> '₅'; '6' -> '₆'; '7' -> '₇'; '8' -> '₈'; '9' -> '₉'
                else -> character
            }
        )
    }
}

@Composable
private fun ResultDialog(game: GameState, viewModel: MainViewModel, nav: NavHostController) {
    AlertDialog(
        onDismissRequest = {}, icon = { Icon(Icons.Default.Star, null, tint = Gold) },
        title = { Text(stringResource(if (game.missionAchieved) R.string.hunt_result_brilliant else R.string.hunt_result_complete)) },
        text = { Text(pluralStringResource(R.plurals.hunt_result_summary, game.uniqueDiscoveries.size, game.score, game.uniqueDiscoveries.size)) },
        confirmButton = {
            val activity = LocalContext.current.findActivity()
            Button({ showAdThen(activity) { viewModel.start(game.mode, fresh = true) } }) { Text(stringResource(R.string.hunt_play_again)) }
        },
        dismissButton = {
            val activity = LocalContext.current.findActivity()
            OutlinedButton({ showAdThen(activity) { nav.popBackStack() } }) { Text(stringResource(R.string.common_home)) }
        },
    )
}

/**
 * Leaving a feature (back arrow, system back or its finish button) returns Home through an interstitial.
 * Ads never interrupt play, and AdMobInterstitial allows at most one every three minutes.
 */
@Composable
private fun rememberLeaveWithAd(nav: NavHostController): () -> Unit {
    val activity = LocalContext.current.findActivity()
    val leave: () -> Unit = remember(nav, activity) { { showAdThen(activity) { nav.popBackStack() } } }
    BackHandler(onBack = leave)
    return leave
}

/** Ads appear only when a finished board is left, so a hunt is never interrupted. */
private fun showAdThen(activity: Activity?, next: () -> Unit) {
    if (activity == null) next() else AdMobInterstitial.show(activity, next)
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
private fun ScienceUncoveredScreen(nav: NavHostController, viewModel: MainViewModel) {
    val leave = rememberLeaveWithAd(nav)
    val context = LocalContext.current
    val savedBest by viewModel.scienceBestScore.collectAsStateWithLifecycle()
    val restoredRun = remember { viewModel.loadScienceRun() }
    val todaysMysteries = remember { ScienceMysteries.forDay(restoredRun.date) }
    var mysteryOffset by remember { mutableStateOf(restoredRun.mysteryOffset) }
    var totalScore by remember { mutableStateOf(restoredRun.totalScore) }
    var lives by remember { mutableStateOf(restoredRun.lives) }
    var solvedCount by remember { mutableStateOf(restoredRun.solvedCount) }
    var mysteryNumber by remember { mutableStateOf(restoredRun.mysteryNumber) }
    var runOver by remember { mutableStateOf(restoredRun.runOver) }
    val mystery = todaysMysteries[mysteryOffset.coerceIn(0, todaysMysteries.lastIndex)]
    val lastOfToday = mysteryOffset >= todaysMysteries.lastIndex
    var visibleClues by remember(mysteryOffset) { mutableStateOf(if (mysteryOffset == restoredRun.mysteryOffset) restoredRun.visibleClues else 1) }
    var guessedLetters by remember(mysteryOffset) { mutableStateOf(if (mysteryOffset == restoredRun.mysteryOffset) restoredRun.guessedLetters else emptySet()) }
    var points by remember(mysteryOffset) { mutableStateOf(if (mysteryOffset == restoredRun.mysteryOffset) restoredRun.points else 100) }
    var wrongAnswers by remember(mysteryOffset) { mutableStateOf(if (mysteryOffset == restoredRun.mysteryOffset) restoredRun.wrongAnswers else 0) }
    var solved by remember(mysteryOffset) { mutableStateOf(mysteryOffset == restoredRun.mysteryOffset && restoredRun.solved) }
    var revealed by remember(mysteryOffset) { mutableStateOf(mysteryOffset == restoredRun.mysteryOffset && restoredRun.revealed) }
    var message by remember(mysteryOffset) { mutableStateOf<String?>(null) }
    val complete = solved || revealed
    val answerLength = mystery.normalizedAnswer.length
    fun saveRun(
        storedVisibleClues: Int = visibleClues,
        storedGuessedLetters: Set<Char> = guessedLetters,
        storedPoints: Int = points,
        storedWrongAnswers: Int = wrongAnswers,
        storedSolved: Boolean = solved,
        storedRevealed: Boolean = revealed,
        storedRunOver: Boolean = runOver,
    ) {
        viewModel.saveScienceRun(
            ScienceRunSnapshot(
                date = restoredRun.date,
                mysteryOffset = mysteryOffset,
                totalScore = totalScore,
                lives = lives,
                solvedCount = solvedCount,
                mysteryNumber = mysteryNumber,
                visibleClues = storedVisibleClues,
                guessedLetters = storedGuessedLetters,
                points = storedPoints,
                wrongAnswers = storedWrongAnswers,
                solved = storedSolved,
                revealed = storedRevealed,
                runOver = storedRunOver,
            ),
        )
    }
    val displayAnswer = buildString {
        mystery.answer.uppercase().forEach { character ->
            if (character.isLetter()) {
                append(if (character.uppercaseChar() in guessedLetters) character.uppercaseChar() else '＿')
                append(' ')
            } else if (character.isWhitespace()) {
                append(if (answerLength > 14) "\n" else "  ")
            }
        }
    }.trim()

    Scaffold(topBar = { AppBar(stringResource(R.string.home_science_title), leave) }, containerColor = Color(0xFFF4F7FB)) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Navy),
                    shape = RoundedCornerShape(22.dp),
                ) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                stringResource(R.string.science_run_score, totalScore),
                                modifier = Modifier.weight(1f),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                            )
                            Text("${"♥".repeat(lives)}${"♡".repeat(3 - lives)}", color = Coral, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        }
                        Text(
                            stringResource(R.string.science_run_status, mysteryOffset + 1, todaysMysteries.size, savedBest),
                            color = Color.White.copy(alpha = .62f),
                            fontSize = 10.sp,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(mystery.category.localizedLabel().uppercase(), color = Gold, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.3.sp)
                                Text(mystery.category.localizedPrompt(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 25.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(points.toString(), color = Gold, fontWeight = FontWeight.Black, fontSize = 28.sp)
                                Text(stringResource(R.string.science_possible_points), color = Color.White.copy(alpha = .62f), fontSize = 10.sp)
                            }
                        }
                        LinearProgressIndicator(
                            progress = { points / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = if (points >= 55) Green else Coral,
                            trackColor = Color.White.copy(alpha = .15f),
                        )
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (complete) mystery.answer.uppercase() else displayAnswer,
                            modifier = Modifier.fillMaxWidth(),
                            color = if (complete) Green else Navy,
                            fontWeight = FontWeight.Black,
                            fontSize = if (answerLength > 14) 20.sp else 25.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            lineHeight = 30.sp,
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            stringResource(
                                R.string.science_answer_shape,
                                pluralStringResource(R.plurals.science_letter_count, answerLength, answerLength),
                                pluralStringResource(R.plurals.science_word_count, mystery.wordCount, mystery.wordCount),
                            ),
                            color = Navy.copy(alpha = .55f),
                            fontSize = 11.sp,
                        )
                    }
                }
            }
            items(visibleClues) { index ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (index == visibleClues - 1) Color(0xFFFFF3C9) else Color.White,
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.Top) {
                        Box(Modifier.size(28.dp).background(Gold, CircleShape), contentAlignment = Alignment.Center) {
                            Text((index + 1).toString(), color = Navy, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.science_clue_number, index + 1), color = Navy.copy(alpha = .5f), fontWeight = FontWeight.Black, fontSize = 10.sp)
                            Text(mystery.clues[index], color = Navy, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
                        }
                    }
                }
            }
            if (!complete) {
                item {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM").forEach { keyboardRow ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)) {
                                keyboardRow.forEach { letter ->
                                    val alreadyGuessed = letter in guessedLetters
                                    Box(
                                        Modifier.size(width = 31.dp, height = 40.dp)
                                            .background(if (alreadyGuessed) Navy.copy(alpha = .08f) else Color.White, RoundedCornerShape(8.dp))
                                            .border(1.dp, Navy.copy(alpha = .12f), RoundedCornerShape(8.dp))
                                            .clickable(enabled = !alreadyGuessed) {
                                                val updatedGuesses = guessedLetters + letter
                                                guessedLetters = updatedGuesses
                                                message = null
                                                if (letter in mystery.normalizedAnswer) {
                                                    if (mystery.normalizedAnswer.toSet().all { it in updatedGuesses }) {
                                                        solved = true
                                                        if (wrongAnswers == 0) points += ScienceMysteries.PERFECT_BONUS
                                                        val updatedScore = totalScore + points
                                                        totalScore = updatedScore
                                                        solvedCount++
                                                        viewModel.recordScienceBest(updatedScore)
                                                        saveRun(storedGuessedLetters = updatedGuesses, storedSolved = true)
                                                    } else {
                                                        saveRun(storedGuessedLetters = updatedGuesses)
                                                    }
                                                } else {
                                                    wrongAnswers++
                                                    points = (points - 10).coerceAtLeast(0)
                                                    val remainingLives = (lives - 1).coerceAtLeast(0)
                                                    lives = remainingLives
                                                    if (remainingLives == 0) {
                                                        revealed = true
                                                        runOver = true
                                                        viewModel.recordScienceBest(totalScore)
                                                        saveRun(
                                                            storedGuessedLetters = updatedGuesses,
                                                            storedPoints = points,
                                                            storedWrongAnswers = wrongAnswers,
                                                            storedRevealed = true,
                                                            storedRunOver = true,
                                                        )
                                                    } else {
                                                        message = context.getString(R.string.science_letter_wrong, letter.toString())
                                                        saveRun(
                                                            storedGuessedLetters = updatedGuesses,
                                                            storedPoints = points,
                                                            storedWrongAnswers = wrongAnswers,
                                                        )
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            letter.toString(),
                                            color = if (alreadyGuessed) Navy.copy(alpha = .28f) else Navy,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            stringResource(R.string.science_keyboard_hint),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            color = Navy.copy(alpha = .58f),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                        )
                    }
                }
                message?.let { feedback ->
                    item { Text(feedback, color = Coral, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                visibleClues++
                                points = (points - 15).coerceAtLeast(0)
                                message = null
                                saveRun(storedVisibleClues = visibleClues, storedPoints = points)
                            },
                            enabled = visibleClues < mystery.clues.size,
                            modifier = Modifier.weight(1.35f),
                        ) { Text(stringResource(if (visibleClues < mystery.clues.size) R.string.science_next_clue else R.string.science_all_clues)) }
                        OutlinedButton(
                            onClick = {
                                points = 0
                                revealed = true
                                val remainingLives = (lives - 1).coerceAtLeast(0)
                                lives = remainingLives
                                if (remainingLives == 0) {
                                    runOver = true
                                    viewModel.recordScienceBest(totalScore)
                                }
                                saveRun(storedPoints = 0, storedRevealed = true, storedRunOver = remainingLives == 0)
                            },
                            modifier = Modifier.weight(1f),
                        ) { Text(stringResource(R.string.science_skip)) }
                    }
                }
            } else {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (solved) Color(0xFFDDF8F2) else Color(0xFFFFE8E8)),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            Text(
                                when {
                                    solved -> pluralStringResource(R.plurals.science_solved, points, points)
                                    runOver -> pluralStringResource(R.plurals.science_run_over, totalScore, totalScore)
                                    else -> stringResource(R.string.science_revealed)
                                },
                                color = if (solved) Green else Coral,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                            )
                            if (wrongAnswers == 0 && solved) Text(pluralStringResource(R.plurals.science_perfect_bonus, ScienceMysteries.PERFECT_BONUS, ScienceMysteries.PERFECT_BONUS), color = Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            if (runOver && lastOfToday && lives > 0) Text(pluralStringResource(R.plurals.science_life_bonus, lives, lives * ScienceMysteries.LIFE_BONUS, lives), color = Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            if (wrongAnswers > 0 && solved) Text(pluralStringResource(R.plurals.science_solved_after, wrongAnswers, wrongAnswers), color = Navy.copy(alpha = .62f), fontSize = 12.sp)
                            Text(mystery.story, color = Navy, lineHeight = 21.sp)
                            if (runOver) {
                                Text(pluralStringResource(R.plurals.science_run_solved_count, solvedCount, solvedCount), color = Navy.copy(alpha = .62f), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            if (runOver) {
                                leave()
                            } else if (lastOfToday) {
                                totalScore += lives * ScienceMysteries.LIFE_BONUS
                                runOver = true
                                viewModel.recordScienceBest(totalScore)
                                saveRun(storedRunOver = true)
                            } else {
                                val nextOffset = mysteryOffset + 1
                                val nextNumber = mysteryNumber + 1
                                mysteryOffset = nextOffset
                                mysteryNumber = nextNumber
                                viewModel.saveScienceRun(
                                    ScienceRunSnapshot(
                                        date = restoredRun.date,
                                        mysteryOffset = nextOffset,
                                        totalScore = totalScore,
                                        lives = lives,
                                        solvedCount = solvedCount,
                                        mysteryNumber = nextNumber,
                                    ),
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            when {
                                runOver -> stringResource(R.string.science_back_home)
                                lastOfToday -> stringResource(R.string.science_see_score)
                                solved -> stringResource(R.string.science_next_mystery)
                                else -> pluralStringResource(R.plurals.science_continue_lives, lives, lives)
                            },
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
                if (runOver) {
                    item {
                        Text(
                            stringResource(R.string.science_done_today),
                            color = Navy.copy(alpha = .62f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(title: String, back: () -> Unit, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.ExtraBold) },
        navigationIcon = { IconButton(back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.common_back)) } },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = Navy),
    )
}

@Composable
private fun FactScreen(nav: NavHostController) {
    val leave = rememberLeaveWithAd(nav)
    val context = LocalContext.current
    val fact = remember { EverydayFacts.today().localized(context) }
    var selectedGuess by remember(fact.id) { mutableStateOf<Int?>(null) }
    var revealed by remember(fact.id) { mutableStateOf(false) }
    Scaffold(topBar = { AppBar(stringResource(R.string.home_everyday_title), leave) }, containerColor = Color(0xFFF6F0E5)) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(if (revealed) R.string.everyday_the_answer else R.string.everyday_the_question),
                        color = Green,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Text(stringResource(R.string.everyday_card_number, fact.number), color = Navy.copy(alpha = .55f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
            item {
                Image(
                    painter = painterResource(everydayArtwork(fact.id)),
                    contentDescription = fact.setup,
                    modifier = Modifier.fillMaxWidth().height(178.dp).clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
            if (!revealed) {
                item {
                    Text(
                        fact.question,
                        modifier = Modifier.fillMaxWidth(),
                        color = Navy,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        lineHeight = 33.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                        Text(fact.setup, Modifier.fillMaxWidth().padding(16.dp), color = Navy.copy(alpha = .75f), lineHeight = 20.sp)
                    }
                }
                item {
                    Text(
                        stringResource(R.string.everyday_quick_guess),
                        Modifier.fillMaxWidth().padding(top = 3.dp),
                        color = Navy.copy(alpha = .58f),
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                    )
                }
                items(fact.guesses.size) { index ->
                    val selected = selectedGuess == index
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedGuess = index },
                        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFDDF8F2) else Color.White),
                        shape = RoundedCornerShape(15.dp),
                    ) {
                        Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(28.dp).border(2.dp, if (selected) Green else Navy.copy(alpha = .25f), CircleShape)
                                    .background(if (selected) Green else Color.Transparent, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (selected) Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(fact.guesses[index], color = Navy, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        }
                    }
                }
                item {
                    Button(
                        onClick = { revealed = true },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green),
                        shape = RoundedCornerShape(15.dp),
                    ) { Text(stringResource(R.string.everyday_show_me), fontWeight = FontWeight.Black) }
                }
            } else {
                selectedGuess?.let { guess ->
                    item {
                        Text(
                            stringResource(if (guess == fact.correctGuess) R.string.everyday_spot_on else R.string.everyday_not_quite),
                            color = if (guess == fact.correctGuess) Green else Coral,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                item {
                    Text(
                        fact.answerHeadline,
                        modifier = Modifier.fillMaxWidth(),
                        color = Navy,
                        fontWeight = FontWeight.Black,
                        fontSize = 25.sp,
                        lineHeight = 31.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(18.dp)) {
                        Column(Modifier.fillMaxWidth().padding(17.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            Text(stringResource(R.string.everyday_chemistry_underneath), color = Gold, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.1.sp)
                            Text(fact.chemistryTags, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(fact.explanation, color = Color.White.copy(alpha = .88f), lineHeight = 21.sp)
                            HorizontalDivider(color = Color.White.copy(alpha = .14f))
                            Text(fact.equation, color = Gold, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                if (fact.tryIt != null) {
                    item {
                        InfoCard(
                            stringResource(R.string.everyday_try_it, fact.tryIt) +
                                (fact.safety?.let { "\n\n" + stringResource(R.string.everyday_safety, it) } ?: ""),
                            Icons.Default.Science, Color(0xFFFFEABB),
                        )
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        OutlinedButton(
                            onClick = { selectedGuess = null; revealed = false },
                            modifier = Modifier.weight(1f),
                        ) { Text(stringResource(R.string.everyday_ask_again)) }
                        Button(onClick = { leave() }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.everyday_got_it)) }
                    }
                }
            }
        }
    }
}

private fun everydayArtwork(id: String): Int = when (id) {
    "antacid" -> R.drawable.everyday_art_antacid
    "apple_brown" -> R.drawable.everyday_art_apple_brown
    "avocado_brown" -> R.drawable.everyday_art_avocado_brown
    "baking_powder", "yeast_bread" -> R.drawable.everyday_art_baking_powder_yeast
    "baking_soda_vinegar" -> R.drawable.everyday_art_baking_soda_vinegar
    "banana_brown" -> R.drawable.everyday_art_banana_brown
    "battery" -> R.drawable.everyday_art_battery
    "bleach" -> R.drawable.everyday_art_bleach
    "bleach_disinfect" -> R.drawable.everyday_art_bleach_disinfect
    "bread_stale" -> R.drawable.everyday_art_bread_stale
    "cake_rise" -> R.drawable.everyday_art_cake_rise
    "chilli_after" -> R.drawable.everyday_art_chilli_after
    "chilli_heat" -> R.drawable.everyday_art_chilli_heat
    "coffee_aroma" -> R.drawable.everyday_art_coffee_aroma
    "cold_can" -> R.drawable.everyday_art_cold_can
    "descale" -> R.drawable.everyday_art_descale
    "dishwasher" -> R.drawable.everyday_art_dishwasher
    "egg_white", "egg_yolk" -> R.drawable.everyday_art_egg_cheese
    "fireworks" -> R.drawable.everyday_art_fireworks
    "fizzy_drink" -> R.drawable.everyday_art_fizzy_drink
    "garlic_smell", "mint_cool", "mustard", "pepper_aroma" -> R.drawable.everyday_art_flavour_aroma
    "fluoride" -> R.drawable.everyday_art_fluoride
    "fluoride_varnish" -> R.drawable.everyday_art_fluoride_varnish
    "glow_stick" -> R.drawable.everyday_art_glow_stick
    "hard_water", "shower_scale" -> R.drawable.everyday_art_hard_water
    "ice_slippery" -> R.drawable.everyday_art_ice_slippery
    "led" -> R.drawable.everyday_art_led
    "lemon_sour" -> R.drawable.everyday_art_lemon_sour
    "caramel", "meat_brown" -> R.drawable.everyday_art_meat_caramel
    "cheese", "milk_curdle" -> R.drawable.everyday_art_milk_curdle
    "onion_storage", "onion_tears" -> R.drawable.everyday_art_onion_tears
    "orange_peel" -> R.drawable.everyday_art_orange_peel
    "nail_polish", "paint_dry" -> R.drawable.everyday_art_paint_nailpolish
    "perfume", "perfume_heat" -> R.drawable.everyday_art_perfume
    "red_cabbage", "tea_lemon" -> R.drawable.everyday_art_red_cabbage
    "redox_apple" -> R.drawable.everyday_art_redox_apple
    "rust", "rust_protection" -> R.drawable.everyday_art_rust
    "salt_ice" -> R.drawable.everyday_art_salt_ice
    "salt_pasta" -> R.drawable.everyday_art_salt_pasta
    "soap_bubbles" -> R.drawable.everyday_art_soap_bubbles
    "detergent_grease", "soap_oil" -> R.drawable.everyday_art_soap_grease
    "soap_ph" -> R.drawable.everyday_art_soap_ph
    "static_shock" -> R.drawable.everyday_art_static_shock
    "steam_burn" -> R.drawable.everyday_art_steam_burn
    "toast_brown" -> R.drawable.everyday_art_toast_brown
    "tooth_decay" -> R.drawable.everyday_art_tooth_decay
    "yogurt" -> R.drawable.everyday_art_yogurt
    else -> R.drawable.everyday_art_everyday_mixed
}

@Composable
private fun InfoCard(text: String, icon: ImageVector, color: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = color), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.padding(16.dp)) { Icon(icon, null, tint = Color(0xFFE48600)); Spacer(Modifier.width(12.dp)); Text(text, lineHeight = 20.sp) }
    }
}

private fun familyColor(family: String): Color = when (family) {
    "Alkali metals" -> Color(0xFFFFB4A2)
    "Alkaline-earth metals" -> Color(0xFFFFD58A)
    "Transition metals" -> Color(0xFFB9D7F7)
    "Post-transition metals" -> Color(0xFFC9E4C5)
    "Metalloids" -> Color(0xFFB7ECE7)
    "Reactive nonmetals" -> Color(0xFFFFE49A)
    "Halogens" -> Color(0xFFBDE9B6)
    "Noble gases" -> Color(0xFFD4C8FF)
    "Lanthanides" -> Color(0xFFFFC4DE)
    "Actinides" -> Color(0xFFF2C6F7)
    else -> Sky
}

@Composable
private fun ElementOfDayScreen(nav: NavHostController, viewModel: MainViewModel) {
    val leave = rememberLeaveWithAd(nav)
    val element = remember { ElementOfDay.today() }
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val guesses by viewModel.elementGuesses.collectAsStateWithLifecycle()
    val todayGuess = guesses[viewModel.todayUtc()]
    val collected = guesses.values.map { it.symbol }.toSet()
    val tint = familyColor(element.family)
    Scaffold(topBar = { AppBar(stringResource(R.string.home_element_title), leave) }, containerColor = Color(0xFFFFF5F1)) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            item {
                Text(stringResource(R.string.element_todays), color = Coral, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.5.sp)
            }
            if (todayGuess == null) {
                item { ElementGuessCard(element, tint) { choice -> viewModel.recordElementGuess(choice.symbol, choice == element) } }
            } else {
                item {
                    val correct = todayGuess.correct
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (correct) Color(0xFFDDF8F2) else Color(0xFFFFE8E8)),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            stringResource(if (correct) R.string.element_correct else R.string.element_wrong, element.name),
                            Modifier.fillMaxWidth().padding(14.dp),
                            color = if (correct) Green else Coral, fontWeight = FontWeight.Bold,
                        )
                    }
                }
                item { ElementTileRow(element, tint, animate = !settings.reducedMotion) }
                item {
                    Text(
                        listOfNotNull(
                            localizedFamily(element.family), localizedState(element.state),
                            stringResource(R.string.element_made_in_lab).takeIf { element.atomicNumber >= 93 },
                            stringResource(R.string.element_period, element.period), localizedGroup(element.group),
                        ).joinToString(" · "),
                        color = Navy.copy(alpha = .7f), fontWeight = FontWeight.Bold, fontSize = 13.sp,
                    )
                }
                item { ElementFactsCard(element) }
                item { ElementStoryCard(stringResource(R.string.element_remember_it), element.story.hook, Icons.Default.Lightbulb, Gold, highlight = true) }
                item { ElementStoryCard(stringResource(R.string.element_how_found), element.story.discovery, Icons.Default.Science, Coral) }
                item { ElementStoryCard(stringResource(R.string.element_where_meet), element.story.uses, Icons.Default.Home, Green) }
                val compounds = ElementOfDay.compounds(element)
                if (compounds.isNotEmpty()) item { ElementCompoundsCard(compounds) }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Row(Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(R.string.element_collection), color = Navy, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                                Text(stringResource(R.string.element_collection_count, collected.size, ElementOfDay.all.size), color = Coral, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                            MiniPeriodicTable(element, collected)
                            Text(
                                stringResource(R.string.element_collection_hint),
                                color = Navy.copy(alpha = .55f), fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }
            }
            item {
                Text(
                    stringResource(R.string.element_new_daily),
                    Modifier.fillMaxWidth(), color = Navy.copy(alpha = .55f), textAlign = TextAlign.Center, fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun ElementGuessCard(element: DailyElement, tint: Color, onGuess: (DailyElement) -> Unit) {
    val options = remember { ElementOfDay.guessOptions() }
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(64.dp).background(tint, RoundedCornerShape(16.dp))
                        .border(2.dp, Navy.copy(alpha = .18f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) { Text("?", color = Navy, fontWeight = FontWeight.Black, fontSize = 34.sp) }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(stringResource(R.string.element_which_am_i), color = Navy, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text(
                        listOf(
                            localizedFamily(element.family),
                            stringResource(R.string.element_period, element.period),
                            localizedGroup(element.group),
                        ).joinToString(" · "),
                        color = Navy.copy(alpha = .6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3C9)), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.padding(14.dp)) {
                    Icon(Icons.Default.Lightbulb, null, tint = Gold, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(ElementOfDay.maskedClue(element), color = Navy, fontWeight = FontWeight.Bold, lineHeight = 21.sp)
                }
            }
            options.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { option ->
                        OutlinedButton(
                            { onGuess(option) },
                            Modifier.weight(1f).heightIn(min = 52.dp),
                            shape = RoundedCornerShape(14.dp),
                        ) { Text("${option.name} (${option.symbol})", color = Navy, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
                    }
                }
            }
            Text(
                stringResource(R.string.element_first_guess_counts),
                color = Navy.copy(alpha = .55f), fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun ElementTileRow(element: DailyElement, tint: Color, animate: Boolean) {
    val tileDescription = stringResource(
        R.string.element_tile_description,
        element.name,
        element.symbol,
        element.atomicNumber,
        element.atomicWeight,
        element.core.orEmpty() + " " + element.configuration.joinToString(" ") { "${it.first} ${it.second}" },
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(
            Modifier.weight(1f).aspectRatio(1f)
                .background(tint, RoundedCornerShape(22.dp))
                .border(3.dp, Navy.copy(alpha = .18f), RoundedCornerShape(22.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .semantics {
                    contentDescription = tileDescription
                },
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(element.atomicNumber.toString(), color = Navy, fontWeight = FontWeight.Black, fontSize = 17.sp, modifier = Modifier.weight(1f))
                Text(element.atomicWeight, color = Navy, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text(element.symbol, color = Navy, fontWeight = FontWeight.Black, fontSize = 52.sp)
            }
            Text(
                buildAnnotatedString {
                    element.core?.let(::append)
                    element.configuration.forEachIndexed { index, (subshell, count) ->
                        if (index > 0) append(" ")
                        append(subshell)
                        withStyle(SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 8.sp)) { append(count.toString()) }
                    }
                },
                color = Navy, fontSize = 11.sp, lineHeight = 14.sp, maxLines = 2,
            )
            Text(element.name.lowercase(), color = Navy, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
        }
        val shells = remember(element) { ElementOfDay.electronShells(element.atomicNumber) }
        val outer = remember(element) { ElementOfDay.outerSubshell(element.atomicNumber) }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            AtomModel(shells, outer.second, tint, animate = animate, modifier = Modifier.fillMaxWidth().aspectRatio(1f))
            Text(
                stringResource(R.string.element_outer_electrons, "${outer.first}${"spdf"[outer.second]}"),
                color = Navy.copy(alpha = .6f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun ElementFactsCard(element: DailyElement) {
    val facts = ElementFactsTable.bySymbol[element.symbol]
    val densityUnit = if (element.state == "Gas") "g/L" else "g/cm³"
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            facts?.picture?.let { picture ->
                Image(
                    painterResource(picture), contentDescription = stringResource(R.string.element_picture_description, element.name),
                    modifier = Modifier.width(76.dp).height(120.dp), contentScale = ContentScale.Fit,
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FactLine(Icons.Default.Thermostat, stringResource(R.string.element_melts), facts?.meltingC?.let { "$it °C" })
                FactLine(Icons.Default.LocalFireDepartment, stringResource(R.string.element_boils), facts?.boilingC?.let { "$it °C" })
                FactLine(Icons.Default.Scale, stringResource(R.string.element_density), facts?.density?.let { "$it $densityUnit" })
                FactLine(Icons.Default.CalendarMonth, stringResource(R.string.element_discovered), facts?.discovered)
            }
        }
    }
}

@Composable
private fun FactLine(icon: ImageVector, label: String, value: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Coral, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, color = Navy.copy(alpha = .6f), fontSize = 13.sp, modifier = Modifier.width(84.dp))
        Text(value ?: stringResource(R.string.element_not_measured), color = Navy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun ElementCompoundsCard(compounds: List<Substance>) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.element_in_things_you_know), color = ScienceBlue, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.1.sp)
            compounds.forEach { substance ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(substance.formula.withSubscripts(), color = Navy, fontWeight = FontWeight.Black, fontSize = 17.sp, modifier = Modifier.width(92.dp))
                    Text(substance.displayName, color = Navy, modifier = Modifier.weight(1f))
                    if (substance.isPlayable) {
                        Text(
                            stringResource(R.string.element_on_the_board),
                            Modifier.clip(CircleShape).background(Color(0xFFDDF8F2)).padding(horizontal = 8.dp, vertical = 3.dp),
                            color = Green, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

private class CloudPoint(val x: Float, val y: Float, val size: Float, val phase: Float, val outer: Boolean)

/**
 * Schrödinger-style electron cloud: each dot is a likely place to find an electron. Inner electrons form
 * a soft spherical haze; the outermost subshell is drawn in its orbital shape (s sphere, p dumbbell,
 * d cloverleaf, f six lobes) as the 2D projection |cos(lθ)|.
 */
private fun electronCloud(shells: List<Int>, outerL: Int, seed: Int): List<CloudPoint> {
    val random = java.util.Random(seed.toLong())
    val points = mutableListOf<CloudPoint>()
    val shellCount = shells.size
    // Inner haze: denser near the nucleus, one soft band per shell.
    shells.forEachIndexed { index, electrons ->
        repeat((25 + electrons * 4).coerceAtMost(140)) {
            val angle = random.nextDouble() * 2 * PI
            val radius = ((index + .7f) / (shellCount + .5f) * .8f + random.nextGaussian().toFloat() * .05f).coerceIn(.1f, .85f)
            points += CloudPoint(
                (radius * cos(angle)).toFloat(), (radius * sin(angle)).toFloat(),
                .5f + random.nextFloat() * .5f, random.nextFloat() * 2f * PI.toFloat(), outer = false,
            )
        }
    }
    // Outer orbital lobes.
    repeat(700) {
        val angle = random.nextDouble() * 2 * PI
        val lobe = if (outerL == 0) 1.0 else kotlin.math.abs(cos(outerL * angle))
        val radius = if (outerL == 0) {
            (.55 + random.nextGaussian() * .14).coerceIn(.15, 1.0)
        } else {
            .18 + .82 * lobe * kotlin.math.sqrt(random.nextDouble())
        }
        if (outerL > 0 && radius < .2 + random.nextDouble() * .1) return@repeat
        points += CloudPoint(
            (radius * cos(angle)).toFloat(), (radius * sin(angle)).toFloat(),
            .6f + random.nextFloat() * .9f, random.nextFloat() * 2f * PI.toFloat(), outer = true,
        )
    }
    return points
}

@Composable
private fun AtomModel(shells: List<Int>, outerL: Int, nucleusColor: Color, animate: Boolean, modifier: Modifier) {
    val cloud = remember(shells, outerL) { electronCloud(shells, outerL, shells.sum()) }
    val time = if (animate) {
        val transition = rememberInfiniteTransition(label = "cloud")
        transition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(30_000, easing = LinearEasing)),
            label = "time",
        ).value
    } else 0f
    val orbitalName = "spdf"[outerL.coerceIn(0, 3)]
    val cloudDescription = stringResource(R.string.element_cloud_description, shells.joinToString(", "), orbitalName.toString())
    Canvas(
        modifier.semantics {
            contentDescription = cloudDescription
        },
    ) {
        val centre = this.center
        val scale = size.minDimension / 2f * .92f
        val dot = size.minDimension / 170f
        drawCircle(
            Brush.radialGradient(listOf(Sky.copy(alpha = .35f), Color.Transparent), centre, scale),
            scale, centre,
        )
        val turn = time * 2f * PI.toFloat()
        val cosT = cos(turn)
        val sinT = sin(turn)
        cloud.forEach { point ->
            val (x, y) = if (point.outer) (point.x * cosT - point.y * sinT) to (point.x * sinT + point.y * cosT) else point.x to point.y
            val shimmer = .3f + .7f * ((sin(point.phase + turn * 8f) + 1f) / 2f)
            drawCircle(
                (if (point.outer) Coral else Color(0xFF2F7DD1)).copy(alpha = shimmer * if (point.outer) .7f else .35f),
                dot * point.size,
                Offset(centre.x + x * scale, centre.y + y * scale),
            )
        }
        drawCircle(
            Brush.radialGradient(listOf(Color.White, nucleusColor, Navy.copy(alpha = .6f)), centre, scale * .1f),
            scale * .09f, centre,
        )
    }
}

@Composable
private fun ElementStoryCard(label: String, text: String, icon: ImageVector, accent: Color, highlight: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (highlight) Color(0xFFFFF3C9) else Color.White),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp)) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(label, color = accent, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.1.sp)
                Text(
                    text, color = Navy,
                    fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (highlight) 17.sp else 15.sp, lineHeight = if (highlight) 23.sp else 21.sp,
                )
            }
        }
    }
}

@Composable
private fun MiniPeriodicTable(selected: DailyElement, collected: Set<String>) {
    val cells = remember { ElementOfDay.all.associateBy(ElementOfDay::position) }
    val collectedSuffix = stringResource(R.string.element_collected)
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        for (row in 0..9) {
            if (row == 7) { Spacer(Modifier.height(5.dp)); continue }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                for (column in 0..17) {
                    val element = cells[TablePosition(row, column)]
                    Box(Modifier.weight(1f).aspectRatio(1f)) {
                        if (element != null) {
                            val isSelected = element == selected
                            Box(
                                Modifier.fillMaxSize()
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        when {
                                            isSelected -> Coral
                                            element.symbol in collected -> familyColor(element.family)
                                            else -> Navy.copy(alpha = .07f)
                                        },
                                    )
                                    .semantics {
                                        contentDescription = element.name + if (element.symbol in collected) collectedSuffix else ""
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardScreen(nav: NavHostController, viewModel: MainViewModel) {
    val leave = rememberLeaveWithAd(nav)
    val entries by viewModel.scienceLeaderboard.collectAsStateWithLifecycle()
    val cloud by viewModel.scienceCloud.collectAsStateWithLifecycle()
    val today = viewModel.todayUtc()
    val weekStart = viewModel.currentWeekStartUtc()
    val weekEnd = weekStart.plusDays(6)
    val run = remember { viewModel.loadScienceRun() }
    val history by viewModel.dailyHistory.collectAsStateWithLifecycle()
    val board = history[today]
    val localDailyTotals = remember(entries, history) {
        val scienceByDate = entries.associateBy { it.date }
        (scienceByDate.keys + history.keys).distinct().map { date ->
            LocalDailyTotal(date, scienceByDate[date], history[date])
        }.sortedWith(
            compareByDescending<LocalDailyTotal> { it.score }
                .thenByDescending { it.science?.mysteriesSolved ?: 0 },
        )
    }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()) }
    var showWeekly by remember { mutableStateOf(false) }
    val visibleRankings = if (showWeekly) cloud.weeklyRankings else cloud.rankings
    val currentWeekTotals = localDailyTotals.filter { !it.date.isBefore(weekStart) && !it.date.isAfter(weekEnd) }
    val displayedBoardScore = if (showWeekly) currentWeekTotals.sumOf { it.molecule?.score ?: 0 } else board?.score ?: 0
    val displayedScienceScore = if (showWeekly) currentWeekTotals.sumOf { it.science?.score ?: 0 } else if (run.runOver) run.totalScore else 0
    var editNickname by remember { mutableStateOf(false) }
    var nicknameDraft by remember(cloud.nickname) { mutableStateOf(cloud.nickname) }
    Scaffold(topBar = { AppBar(stringResource(R.string.champions_title), leave) }, containerColor = Color(0xFFF4FCFF)) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).background(Sky, CircleShape), contentAlignment = Alignment.Center) {
                            if (cloud.connecting) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Navy)
                            else Icon(Icons.Default.Science, null, tint = Navy)
                        }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            val defaultPlayer = stringResource(R.string.champions_default_player)
                            Text(cloud.nickname.ifBlank { defaultPlayer }, color = Navy, fontWeight = FontWeight.Black)
                            Text(cloud.message, color = Navy.copy(alpha = .6f), fontSize = 10.sp, lineHeight = 14.sp)
                        }
                        if (cloud.online) {
                            IconButton({ nicknameDraft = cloud.nickname; editNickname = true }) {
                                Icon(Icons.Default.Edit, stringResource(R.string.champions_edit_nickname), tint = Coral)
                            }
                        } else {
                            IconButton(viewModel::refreshScienceLeaderboard) {
                                Icon(Icons.Default.Refresh, stringResource(R.string.champions_retry), tint = Coral)
                            }
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showWeekly = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showWeekly) Navy else Color.White,
                            contentColor = if (!showWeekly) Color.White else Navy,
                        ),
                    ) { Text(stringResource(R.string.champions_today), fontWeight = FontWeight.Black) }
                    Button(
                        onClick = { showWeekly = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showWeekly) Navy else Color.White,
                            contentColor = if (showWeekly) Color.White else Navy,
                        ),
                    ) { Text(stringResource(R.string.champions_this_week), fontWeight = FontWeight.Black) }
                }
            }
            if (visibleRankings.isEmpty()) {
                item {
                    Text(
                        stringResource(
                            if (!cloud.online) R.string.champions_scores_need_firebase
                            else if (showWeekly) R.string.champions_no_scores_week
                            else R.string.champions_no_scores_today,
                        ),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                        color = Navy.copy(alpha = .62f),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                items(visibleRankings.size) { index ->
                    val entry = visibleRankings[index]
                    val isPlayer = entry.playerId == cloud.playerId
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isPlayer) Color(0xFFFFF3C9) else Color.White),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(42.dp).background(if (entry.rank <= 3) Gold else Sky, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) { Text("#${entry.rank}", color = Navy, fontWeight = FontWeight.Black) }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(if (isPlayer) stringResource(R.string.champions_you, entry.displayName) else entry.displayName, fontWeight = FontWeight.Bold)
                                Text(
                                    stringResource(R.string.champions_entry_scores, entry.moleculeScore, entry.scienceScore),
                                    color = Navy.copy(alpha = .6f), fontSize = 11.sp,
                                )
                            }
                            Text(entry.score.toString(), color = Green, fontWeight = FontWeight.Black, fontSize = 22.sp)
                        }
                    }
                }
            }
            item {
                Text(stringResource(R.string.champions_your_score), modifier = Modifier.padding(top = 8.dp), color = Navy, fontWeight = FontWeight.Black, fontSize = 17.sp)
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                stringResource(
                                    if (showWeekly) R.string.champions_week_total
                                    else if (board != null && run.runOver) R.string.champions_todays_total
                                    else R.string.champions_today_so_far,
                                ),
                                color = Gold,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                            )
                            Text(
                                stringResource(
                                    R.string.champions_today_breakdown,
                                    displayedBoardScore.toString(),
                                    displayedScienceScore.toString(),
                                ),
                                color = Color.White.copy(alpha = .7f),
                                fontSize = 12.sp,
                            )
                        }
                        Text((displayedBoardScore + displayedScienceScore).toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 32.sp)
                    }
                }
            }
            // Today's entry is the "Your score" card above; this is the phone's record of earlier days.
            val pastDays = localDailyTotals.filter { it.date.isBefore(today) }.sortedByDescending { it.date }.take(7)
            if (pastDays.isNotEmpty()) {
                item {
                    Text(stringResource(R.string.champions_past_days), modifier = Modifier.padding(top = 8.dp), color = Navy.copy(alpha = .7f), fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
                items(pastDays.size) { index ->
                    val total = pastDays[index]
                    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(total.date.format(dateFormatter), color = Navy, fontSize = 12.sp, modifier = Modifier.width(96.dp))
                        Text(
                            stringResource(R.string.champions_entry_scores, total.molecule?.score ?: 0, total.science?.score ?: 0),
                            color = Navy.copy(alpha = .55f), fontSize = 11.sp, modifier = Modifier.weight(1f),
                        )
                        Text(total.score.toString(), color = Green, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        }
    }
    if (editNickname) {
        AlertDialog(
            onDismissRequest = { editNickname = false },
            title = { Text(stringResource(R.string.champions_choose_name)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.champions_nickname_note), color = Navy.copy(alpha = .7f))
                    OutlinedTextField(
                        value = nicknameDraft,
                        onValueChange = { if (it.length <= 16) nicknameDraft = it },
                        label = { Text(stringResource(R.string.champions_nickname_label)) },
                        supportingText = { Text(stringResource(R.string.champions_nickname_support)) },
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.updatePlayerNickname(nicknameDraft); editNickname = false }) { Text(stringResource(R.string.common_save)) }
            },
            dismissButton = { TextButton(onClick = { editNickname = false }) { Text(stringResource(R.string.common_cancel)) } },
        )
    }
}

@Composable
private fun DailyHistoryScreen(nav: NavHostController, viewModel: MainViewModel) {
    androidx.compose.runtime.LaunchedEffect(Unit) { viewModel.refreshDailyHistory() }
    val history by viewModel.dailyHistory.collectAsStateWithLifecycle()
    val scienceRuns by viewModel.scienceLeaderboard.collectAsStateWithLifecycle()
    val scienceDates = scienceRuns.map { it.date }.toSet()
    val elementGuesses by viewModel.elementGuesses.collectAsStateWithLifecycle()
    val elementDates = elementGuesses.values.filter { it.correct }.map { it.date }.toSet()
    val today = viewModel.todayUtc()
    // A day counts when the Molecule Board or Science Uncovered is finished, or today's element is guessed.
    val summary = DailyHistory.streaks(history.keys + scienceDates + elementDates, today)
    val currentMonth = YearMonth.from(today)
    val firstMonth = history.keys.minOrNull()?.let(YearMonth::from)?.takeIf { it.isBefore(currentMonth) } ?: currentMonth
    var month by remember { mutableStateOf(currentMonth) }
    val recent = history.values.sortedByDescending { it.date }.take(14)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE d MMM", Locale.getDefault()) }
    Scaffold(topBar = { AppBar(stringResource(R.string.streak_title), { nav.popBackStack() }) }, containerColor = Color(0xFFF4FCFF)) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Coral.copy(alpha = .13f)), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalFireDepartment, null, tint = Coral, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    when (summary.current) {
                                        0 -> stringResource(R.string.streak_none)
                                        else -> pluralStringResource(R.plurals.streak_days, summary.current, summary.current)
                                    },
                                    color = Navy, fontWeight = FontWeight.Black, fontSize = 24.sp,
                                )
                                Text(
                                    when {
                                        summary.playedToday -> stringResource(R.string.streak_played_today)
                                        summary.current > 0 -> stringResource(R.string.streak_keep_going)
                                        else -> stringResource(R.string.streak_start)
                                    },
                                    color = Navy.copy(alpha = .7f), fontSize = 12.sp,
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatPill(stringResource(R.string.streak_best), summary.longest.toString(), Modifier.weight(1f))
                            StatPill(stringResource(R.string.streak_days_played), summary.daysPlayed.toString(), Modifier.weight(1f))
                            StatPill(stringResource(R.string.streak_best_score), history.values.maxOfOrNull { it.score }?.toString() ?: "–", Modifier.weight(1f))
                        }
                        if (!summary.playedToday) {
                            Spacer(Modifier.height(12.dp))
                            Button(
                                { nav.navigate(Routes.Daily) },
                                Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Green),
                            ) { Text(stringResource(R.string.streak_play_today)) }
                        }
                    }
                }
            }
            item {
                StreakCalendar(
                    month = month,
                    days = DailyHistory.monthGrid(month, history, today),
                    scienceDates = scienceDates,
                    elementDates = elementDates,
                    canGoBack = month.isAfter(firstMonth),
                    canGoForward = month.isBefore(currentMonth),
                    onBack = { month = month.minusMonths(1) },
                    onForward = { month = month.plusMonths(1) },
                )
            }
            item { Text(stringResource(R.string.streak_recent), fontWeight = FontWeight.Black, fontSize = 18.sp, color = Navy) }
            if (recent.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.streak_recent_empty),
                        color = Navy.copy(alpha = .62f), fontSize = 13.sp,
                    )
                }
            }
            items(recent, key = { it.date.toString() }) { record ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (record.missionAchieved) Icons.Default.Star else Icons.Default.CheckCircle,
                            null,
                            tint = if (record.missionAchieved) Gold else Green,
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(record.date.format(dateFormatter), fontWeight = FontWeight.Bold)
                            Text(
                                pluralStringResource(
                                    if (record.missionAchieved) R.plurals.streak_unique_mission else R.plurals.streak_unique,
                                    record.uniqueDiscoveries,
                                    record.uniqueDiscoveries,
                                ),
                                color = Navy.copy(alpha = .6f), fontSize = 11.sp,
                            )
                        }
                        Text(record.score.toString(), color = Green, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    }
                }
            }
            item {
                Text(
                    stringResource(R.string.streak_footer),
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    color = Navy.copy(alpha = .55f), textAlign = TextAlign.Center, fontSize = 11.sp,
                )
            }
        }
    }
}

private val ScienceBlue = Color(0xFF2F9BD6)

@Composable
private fun StreakCalendar(
    month: YearMonth,
    days: List<CalendarDay>,
    scienceDates: Set<LocalDate>,
    elementDates: Set<LocalDate>,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
) {
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()) }
    val dayFormatter = remember { DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault()) }
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onBack, enabled = canGoBack) { Icon(Icons.Default.ChevronLeft, stringResource(R.string.streak_previous_month)) }
                Text(
                    month.format(monthFormatter), Modifier.weight(1f),
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Black, color = Navy,
                )
                IconButton(onForward, enabled = canGoForward) { Icon(Icons.Default.ChevronRight, stringResource(R.string.streak_next_month)) }
            }
            Row(Modifier.fillMaxWidth()) {
                DayOfWeek.entries.map { it.getDisplayName(TextStyle.NARROW, Locale.getDefault()) }.forEach { label ->
                    Text(
                        label, Modifier.weight(1f), textAlign = TextAlign.Center,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Navy.copy(alpha = .5f),
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            days.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        Box(Modifier.weight(1f).aspectRatio(1f).padding(3.dp), contentAlignment = Alignment.Center) {
                            val date = day.date ?: return@Box
                            val record = day.record
                            val science = date in scienceDates
                            val element = date in elementDates
                            val played = record != null || science || element
                            val fill = when {
                                record != null && science -> Gold
                                record != null -> Green
                                science -> ScienceBlue
                                element -> Coral
                                else -> Color.Transparent
                            }
                            val description = buildString {
                                append(date.format(dayFormatter))
                                if (!played) append(stringResource(R.string.streak_day_not_played))
                                if (record != null) append(stringResource(R.string.streak_day_board_score, record.score))
                                if (science) append(stringResource(R.string.streak_day_science))
                                if (element) append(stringResource(R.string.streak_day_element))
                                if (day.isToday) append(stringResource(R.string.streak_day_today))
                            }
                            Box(
                                Modifier.fillMaxSize()
                                    .clip(CircleShape)
                                    .background(fill)
                                    .then(if (day.isToday) Modifier.border(2.dp, Coral, CircleShape) else Modifier)
                                    .semantics { contentDescription = description },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    date.dayOfMonth.toString(),
                                    color = if (played) Color.White else Navy.copy(alpha = .75f),
                                    fontWeight = if (played || day.isToday) FontWeight.Black else FontWeight.Normal,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                CalendarLegend(Green, stringResource(R.string.streak_legend_board))
                CalendarLegend(ScienceBlue, stringResource(R.string.streak_legend_science))
                CalendarLegend(Coral, stringResource(R.string.streak_legend_element))
                CalendarLegend(Gold, stringResource(R.string.streak_legend_both))
            }
        }
    }
}

@Composable
private fun CalendarLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Spacer(Modifier.width(5.dp))
        Text(label, fontSize = 11.sp, color = Navy.copy(alpha = .65f))
    }
}

@Composable
private fun ReviewScreen(nav: NavHostController, viewModel: MainViewModel) {
    val records by viewModel.pending.collectAsStateWithLifecycle()
    Scaffold(topBar = { AppBar(stringResource(R.string.review_title), { nav.popBackStack() }) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text(stringResource(R.string.review_intro), color = Navy.copy(alpha = .7f)) }
            if (records.isEmpty()) item {
                Text(stringResource(R.string.review_empty), Modifier.fillMaxWidth().padding(40.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
            items(records, key = { it.canonicalKey }) { record ->
                Card {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LockOpen, null, tint = Gold)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(record.formula, fontWeight = FontWeight.Black, fontSize = 19.sp)
                            Text(pluralStringResource(R.plurals.review_tried, record.count, record.count), fontSize = 12.sp)
                        }
                        IconButton({ viewModel.deletePending(record) }) { Icon(Icons.Default.DeleteOutline, stringResource(R.string.common_delete)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(nav: NavHostController, viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    Scaffold(topBar = { AppBar(stringResource(R.string.settings_title), { nav.popBackStack() }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            SettingRow(stringResource(R.string.settings_sound), stringResource(R.string.settings_sound_description), settings.sound, viewModel::setSound)
            SettingRow(stringResource(R.string.settings_vibration), stringResource(R.string.settings_vibration_description), settings.vibration, viewModel::setVibration)
            SettingRow(stringResource(R.string.settings_reduced_motion), stringResource(R.string.settings_reduced_motion_description), settings.reducedMotion, viewModel::setReducedMotion)
            val context = LocalContext.current
            var showLanguageDialog by remember { mutableStateOf(false) }
            val currentLanguage = AppLanguage.fromTag(settings.languageTag)
            val currentLanguageLabel = if (currentLanguage == AppLanguage.SYSTEM) {
                stringResource(R.string.settings_language_system)
            } else {
                currentLanguage.displayName
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { showLanguageDialog = true }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Language, contentDescription = null, tint = Coral)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.settings_language), fontWeight = FontWeight.Bold)
                    Text(currentLanguageLabel, fontSize = 12.sp, color = Navy.copy(alpha = .65f))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Coral)
            }
            if (showLanguageDialog) {
                LanguagePickerDialog(
                    current = currentLanguage,
                    onDismiss = { showLanguageDialog = false },
                    onSelect = { language ->
                        showLanguageDialog = false
                        if (language.tag != settings.languageTag) {
                            viewModel.setLanguage(language.tag)
                            context.findActivity()?.recreate()
                        }
                    },
                )
            }
            fun open(url: String) = runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.settings_privacy), fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(stringResource(R.string.settings_privacy_text), color = Navy.copy(alpha = .7f), modifier = Modifier.padding(top = 6.dp))
            Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (AdMobInterstitial.privacyOptionsRequired(context)) {
                    OutlinedButton({ context.findActivity()?.let(AdMobInterstitial::showPrivacyOptions) }) { Text(stringResource(R.string.settings_ad_choices)) }
                }
                OutlinedButton({ open("https://www.edbetter.uk/mobile-privacy/") }) { Text(stringResource(R.string.settings_privacy_policy)) }
            }
            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.settings_about), fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(stringResource(R.string.settings_about_text), color = Navy.copy(alpha = .7f), modifier = Modifier.padding(top = 6.dp))
            TextButton({ open("https://www.edbetter.uk") }, contentPadding = PaddingValues(0.dp)) { Text("edbetter.uk", color = Coral, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(10.dp))
            Text(stringResource(R.string.settings_more), fontWeight = FontWeight.Black, fontSize = 18.sp)
            Card(
                Modifier.fillMaxWidth().padding(top = 8.dp).clickable {
                    open("market://details?id=com.siddbetter.mendleev").onFailure {
                        open("https://play.google.com/store/apps/details?id=com.siddbetter.mendleev")
                    }
                },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(R.drawable.kemistri_app_icon), contentDescription = null,
                        modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Kemistri", fontWeight = FontWeight.Black, fontSize = 17.sp, color = Navy)
                        Text(stringResource(R.string.settings_kemistri_description), fontSize = 12.sp, color = Navy.copy(alpha = .65f))
                    }
                    Icon(Icons.Default.ChevronRight, stringResource(R.string.settings_open_kemistri), tint = Coral)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
                Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Navy.copy(alpha = .5f), fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, checked: Boolean, change: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, fontSize = 12.sp, color = Navy.copy(alpha = .65f)) }
        Switch(checked, change)
    }
}

@Composable
private fun LanguagePickerDialog(
    current: AppLanguage,
    onDismiss: () -> Unit,
    onSelect: (AppLanguage) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language), fontWeight = FontWeight.Black) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                AppLanguage.entries.forEach { language ->
                    val label = if (language == AppLanguage.SYSTEM) {
                        stringResource(R.string.settings_language_system)
                    } else {
                        language.displayName
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(language) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = language == current, onClick = { onSelect(language) })
                        Spacer(Modifier.width(8.dp))
                        Text(label, fontSize = 15.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        },
    )
}

