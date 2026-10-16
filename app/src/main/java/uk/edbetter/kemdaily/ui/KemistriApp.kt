package uk.edbetter.kemdaily.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
import uk.edbetter.kemdaily.R
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
import uk.edbetter.kemdaily.core.game.GameMode
import uk.edbetter.kemdaily.core.game.GamePhase
import uk.edbetter.kemdaily.core.game.GameState
import uk.edbetter.kemdaily.core.game.GameEngine
import uk.edbetter.kemdaily.core.game.MISSION_TARGET
import uk.edbetter.kemdaily.data.DailyElement
import uk.edbetter.kemdaily.data.ElementOfDay
import uk.edbetter.kemdaily.data.EverydayFacts
import uk.edbetter.kemdaily.data.TablePosition
import uk.edbetter.kemdaily.ui.theme.Coral
import uk.edbetter.kemdaily.ui.theme.Gold
import uk.edbetter.kemdaily.ui.theme.Green
import uk.edbetter.kemdaily.ui.theme.Navy
import uk.edbetter.kemdaily.ui.theme.Sky
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private object Routes {
    const val Home = "home"
    const val Daily = "daily"
    const val DailyHistory = "daily_history"
    const val Fact = "fact"
    const val Element = "element"
    const val Leaderboard = "leaderboard"
    const val Review = "review"
    const val Settings = "settings"
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
    val streak = DailyHistory.streaks(history.keys, viewModel.todayUtc()).current
    Scaffold(containerColor = Color(0xFFE8FAFF)) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painterResource(R.drawable.kemistri_everyday_brand),
                        contentDescription = "Kemistri Everyday logo",
                        modifier = Modifier.size(184.dp).clip(RoundedCornerShape(38.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.height(9.dp))
                    Text(
                        "A new chemistry wonder every day",
                        color = Navy.copy(alpha = .72f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            item { Spacer(Modifier.height(6.dp)) }
            item {
                val dailySubtitle = if (streak > 0) "$streak-day streak · one shared puzzle every day" else "One shared chemistry puzzle every day"
                FeatureCard("Daily Molecule Board", dailySubtitle, Icons.Default.CalendarMonth, Green) {
                    nav.navigate(Routes.Daily)
                }
            }
            item {
                FeatureCard("Everyday Chemistry", "Guess the chemistry hiding in daily life", Icons.Default.Lightbulb, Gold) {
                    nav.navigate(Routes.Fact)
                }
            }
            item {
                FeatureCard("Element a Day", "Meet a new element every day", Icons.Default.Science, Coral) {
                    nav.navigate(Routes.Element)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SmallNavCard("Streak", Icons.Default.LocalFireDepartment, Modifier.weight(1f)) { nav.navigate(Routes.DailyHistory) }
                    SmallNavCard("Leaders", Icons.Default.EmojiEvents, Modifier.weight(1f)) { nav.navigate(Routes.Leaderboard) }
                    SmallNavCard("Review", Icons.Default.Science, Modifier.weight(1f)) { nav.navigate(Routes.Review) }
                    SmallNavCard("Settings", Icons.Default.Settings, Modifier.weight(1f)) { nav.navigate(Routes.Settings) }
                }
            }
            item {
                Text(
                    "Composition tells us which atoms are present — bonding determines the structure.",
                    modifier = Modifier.fillMaxWidth().padding(10.dp), textAlign = TextAlign.Center,
                    color = Navy.copy(alpha = .65f), fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(title: String, subtitle: String, icon: ImageVector, color: Color, action: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(92.dp).clickable(onClick = action),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = .13f)),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
                    "${discovery.substance.formula.withSubscripts()} already found · +${discovery.score.awarded} points",
                    Toast.LENGTH_SHORT,
                ).show()
                viewModel.dismissDiscovery()
            }
        }
    }
    Scaffold(
        topBar = {
            AppBar(
                title = if (daily) "Daily Molecule Board" else "Molecule Hunt",
                back = { nav.popBackStack() },
                actions = {
                    if (daily) {
                        IconButton({ nav.navigate(Routes.DailyHistory) }) { Icon(Icons.Default.CalendarMonth, "Daily history") }
                    }
                    IconButton({ showDiscoveries = true }) {
                        Box {
                            Icon(Icons.Default.EmojiEvents, "Session discoveries")
                            val found = state?.uniqueDiscoveries?.size ?: 0
                            if (found > 0) {
                                Box(
                                    Modifier.align(Alignment.TopEnd).size(16.dp).background(Coral, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) { Text(found.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                            }
                        }
                    }
                    IconButton({ showRules = true }) { Icon(Icons.Default.Info, "Game rules") }
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
                        "Shared puzzle · ${viewModel.todayUtc().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH))}",
                        color = Coral, fontWeight = FontWeight.Bold, fontSize = 12.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                }
                StatsRow(game, onFoundClick = { showDiscoveries = true })
                Spacer(Modifier.height(5.dp))
                Text("Find 10 different substances", fontSize = 11.sp, color = Navy.copy(alpha = .65f))
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
                    Text(game.message.orEmpty(), color = if (game.message?.contains("not yet") == true) Coral else Green,
                        fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp), textAlign = TextAlign.Center)
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    OutlinedButton(viewModel::hint, Modifier.weight(1f), contentPadding = PaddingValues(8.dp)) {
                        Icon(Icons.Default.Lightbulb, null); Spacer(Modifier.width(5.dp)); Text("Hint")
                    }
                    OutlinedButton(viewModel::shuffle, Modifier.weight(1f), contentPadding = PaddingValues(8.dp)) {
                        Icon(Icons.Default.Shuffle, null); Spacer(Modifier.width(5.dp)); Text("Shuffle")
                    }
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    OutlinedButton(viewModel::clearSelection, Modifier.weight(.8f)) { Text("Clear", color = Coral) }
                    Button(viewModel::submit, Modifier.weight(1.35f), colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                        Icon(Icons.Default.AutoAwesome, null); Spacer(Modifier.width(6.dp)); Text("Discover")
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
        StatPill("Moves", game.movesRemaining.toString(), Modifier.weight(1f))
        StatPill("Score", game.score.toString(), Modifier.weight(1f))
        StatPill("Found", "${game.uniqueDiscoveries.size}/$MISSION_TARGET", Modifier.weight(1f).clickable(onClick = onFoundClick))
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
                    Box(
                        Modifier.weight(1f).fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .then(if (hinted) Modifier.border(3.dp, Gold, RoundedCornerShape(12.dp)) else Modifier)
                            .clickable { onSelect(cell) }
                            .semantics {
                                contentDescription = buildString {
                                    append(ElementCatalog.names[tile.symbol] ?: tile.symbol)
                                    append(", row ${row + 1}, column ${column + 1}")
                                    if (selected) append(", selected number $number")
                                    if (hinted) append(", hinted")
                                }
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
                "Select neighbouring atoms",
                Modifier.fillMaxWidth().padding(10.dp),
                textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Navy,
            )
        } else {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(symbols.joinToString(" + "), fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("→", color = Navy.copy(alpha = .55f), fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp))
                if (recognized == null) {
                    Text("Ready to review", color = Navy.copy(alpha = .65f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    val first = recognized.id !in game.uniqueDiscoveries
                    val points = GameEngine.score(recognized, first, game.consecutiveUnique).awarded
                    Column(horizontalAlignment = Alignment.End) {
                        Text(recognized.formula.withSubscripts(), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text(recognized.displayName, fontSize = 10.sp, color = Navy.copy(alpha = .65f))
                        Text("+$points points", fontSize = 10.sp, color = Green, fontWeight = FontWeight.Bold)
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
            Text("Found", fontSize = 10.sp, color = Navy.copy(alpha = .55f), fontWeight = FontWeight.Bold)
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
        title = { Text("Mission complete!", fontWeight = FontWeight.Black) },
        text = { Text("You found ${game.uniqueDiscoveries.size} substances with ${game.movesRemaining} moves remaining. Score: ${game.score}") },
        confirmButton = { Button(viewModel::continueMission) { Text("Keep discovering") } },
        dismissButton = {
            val activity = LocalContext.current.findActivity()
            OutlinedButton({
                viewModel.finish()
                showAdThen(activity) { nav.popBackStack() }
            }) { Text("Finish") }
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
                            if (discovery.isFirst) "Great Discovery!" else "Discovered Again!",
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
                        "+${score.awarded} points",
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
                    "${score.atoms} atoms · ${score.elements} elements · rarity +${score.rarity}" +
                        if (score.streak > 0) " · streak +${score.streak}" else "",
                    color = Navy.copy(alpha = .55f), fontSize = 10.sp,
                )
                Button(
                    viewModel::dismissDiscovery,
                    modifier = Modifier.fillMaxWidth().padding(18.dp).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                ) { Text("Continue", fontWeight = FontWeight.Black, fontSize = 16.sp) }
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
        "Connect adjacent atoms horizontally, vertically or diagonally.",
        "Use 2–6 atoms for most discoveries; advanced formulas may use 7.",
        "Tap Discover to check the composition. Selection order does not change the formula.",
        "A recognised substance awards points, uses one move and removes the selected tiles.",
        "Atoms fall down and balanced replacements enter from above.",
        "Find 10 different substances to complete the mission.",
        "Repeats are allowed, use a move and score 25%, but do not increase the unique count.",
        "Not-yet-recognised combinations use no move and are saved for Discovery Review.",
        "Hint and Shuffle currently use no move.",
        "After completing the mission, continue discovering with the moves that remain.",
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
                            Text("Molecule Hunt — Rules", fontWeight = FontWeight.Black, fontSize = 21.sp)
                            Text("Build real chemical compositions on the 5 × 5 board", color = Navy.copy(alpha = .62f), fontSize = 12.sp)
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
                            Text("Scoring", fontWeight = FontWeight.Black, fontSize = 17.sp)
                            Text("Score = 5 + 5A + 5E + R + S", color = Green, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text(
                                "A = atoms · E = different elements · R = rarity (0–40) · S = new-discovery streak (0–15)",
                                color = Navy.copy(alpha = .68f), textAlign = TextAlign.Center, fontSize = 11.sp,
                            )
                        }
                    }
                }
                item {
                    Text(
                        "A formula describes composition, not necessarily one unique structure. For example, C₂H₆O can represent more than one substance.",
                        color = Navy.copy(alpha = .65f), textAlign = TextAlign.Center, fontSize = 11.sp,
                    )
                }
                item { Button(onDismiss, Modifier.fillMaxWidth()) { Text("Got it") } }
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
                            Text("This hunt’s discoveries", fontWeight = FontWeight.Black, fontSize = 22.sp)
                            Text(
                                "${missionDiscoveries.size} mission · ${extraDiscoveries.size} extra · ${game.discoveryHistory.size} total finds",
                                color = Navy.copy(alpha = .62f),
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
                if (discoveries.isEmpty()) {
                    item {
                        Text("No discoveries yet — connect some atoms and see what you can make!", Modifier.padding(vertical = 24.dp), textAlign = TextAlign.Center)
                    }
                } else {
                    item { Text("MISSION DISCOVERIES", color = Green, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp) }
                    items(missionDiscoveries, key = { "mission_${it.id}" }) { substance ->
                        DiscoverySummaryRow(substance, counts[substance.id] ?: 1)
                    }
                    if (extraDiscoveries.isNotEmpty()) {
                        item {
                            Text(
                                "EXTRA DISCOVERIES",
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
                    Button(onDismiss, Modifier.fillMaxWidth()) { Text("Back to this hunt") }
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
        title = { Text(if (game.missionAchieved) "Brilliant chemistry!" else "Hunt complete") },
        text = { Text("Score ${game.score} · ${game.uniqueDiscoveries.size} unique substances") },
        confirmButton = {
            val activity = LocalContext.current.findActivity()
            Button({ showAdThen(activity) { viewModel.start(game.mode, fresh = true) } }) { Text("Play again") }
        },
        dismissButton = {
            val activity = LocalContext.current.findActivity()
            OutlinedButton({ showAdThen(activity) { nav.popBackStack() } }) { Text("Home") }
        },
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(title: String, back: () -> Unit, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.ExtraBold) },
        navigationIcon = { IconButton(back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = Navy),
    )
}

@Composable
private fun FactScreen(nav: NavHostController) {
    val fact = EverydayFacts.today()
    var selectedGuess by remember(fact.id) { mutableStateOf<Int?>(null) }
    var revealed by remember(fact.id) { mutableStateOf(false) }
    Scaffold(topBar = { AppBar("Everyday Chemistry", { nav.popBackStack() }) }, containerColor = Color(0xFFF6F0E5)) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (revealed) "THE ANSWER" else "THE QUESTION",
                        color = Green,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Text("CARD ${fact.number}", color = Navy.copy(alpha = .55f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                        "TAKE A QUICK GUESS",
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
                    ) { Text("SHOW ME", fontWeight = FontWeight.Black) }
                }
            } else {
                selectedGuess?.let { guess ->
                    item {
                        Text(
                            if (guess == fact.correctGuess) "Spot on — you chose well." else "Not quite — here’s what is happening.",
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
                            Text("THE CHEMISTRY UNDERNEATH", color = Gold, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.1.sp)
                            Text(fact.chemistryTags, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(fact.explanation, color = Color.White.copy(alpha = .88f), lineHeight = 21.sp)
                            HorizontalDivider(color = Color.White.copy(alpha = .14f))
                            Text(fact.equation, color = Gold, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                item { InfoCard("Try it: ${fact.tryIt}\n\nSafety: ${fact.safety}", Icons.Default.Science, Color(0xFFFFEABB)) }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        OutlinedButton(
                            onClick = { selectedGuess = null; revealed = false },
                            modifier = Modifier.weight(1f),
                        ) { Text("ASK AGAIN") }
                        Button(onClick = { nav.popBackStack() }, modifier = Modifier.weight(1f)) { Text("GOT IT") }
                    }
                }
            }
        }
    }
}

private fun everydayArtwork(id: String): Int = when (id) {
    "soap_oil" -> R.drawable.everyday_art_soap_grease
    "apple_brown" -> R.drawable.everyday_art_apple_brown
    "fizzy_drink" -> R.drawable.everyday_art_fizzy_drink
    "cold_can" -> R.drawable.everyday_art_cold_can
    "salt_ice" -> R.drawable.everyday_art_salt_ice
    "rust" -> R.drawable.everyday_art_rust
    "onion_tears" -> R.drawable.everyday_art_onion_tears
    "milk_curdle" -> R.drawable.everyday_art_milk_curdle
    "descale" -> R.drawable.everyday_art_descale
    "cake_rise" -> R.drawable.everyday_art_cake_rise
    "fluoride" -> R.drawable.everyday_art_fluoride
    "chilli_heat" -> R.drawable.everyday_art_chilli_heat
    else -> R.drawable.kemistri_everyday_brand
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
    val element = remember { ElementOfDay.today() }
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val tint = familyColor(element.family)
    Scaffold(topBar = { AppBar("Element a Day", { nav.popBackStack() }) }, containerColor = Color(0xFFFFF5F1)) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            item {
                Text(
                    "TODAY'S ELEMENT",
                    color = Coral, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.5.sp,
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.weight(1f).aspectRatio(1f)
                            .background(tint, RoundedCornerShape(26.dp))
                            .border(3.dp, Navy.copy(alpha = .18f), RoundedCornerShape(26.dp))
                            .semantics { contentDescription = "${element.name}, symbol ${element.symbol}, atomic number ${element.atomicNumber}" },
                    ) {
                        Text(
                            element.atomicNumber.toString(),
                            Modifier.align(Alignment.TopStart).padding(12.dp),
                            color = Navy, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                        )
                        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(element.symbol, color = Navy, fontWeight = FontWeight.Black, fontSize = 54.sp)
                            Text(element.name, color = Navy.copy(alpha = .8f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    val shells = remember(element) { ElementOfDay.electronShells(element.atomicNumber) }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        AtomModel(shells, tint, animate = !settings.reducedMotion, modifier = Modifier.fillMaxWidth().aspectRatio(1f))
                        Text(
                            "Electrons ${shells.joinToString(" · ")}",
                            color = Navy.copy(alpha = .6f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            item {
                Text(
                    listOf(element.family, "Period ${element.period}", element.group?.let { "Group $it" } ?: "f-block").joinToString(" · "),
                    color = Navy.copy(alpha = .7f), fontWeight = FontWeight.Bold, fontSize = 13.sp,
                )
            }
            item { ElementStoryCard("REMEMBER IT", element.story.hook, Icons.Default.Lightbulb, Gold, highlight = true) }
            item { ElementStoryCard("HOW IT WAS FOUND", element.story.discovery, Icons.Default.Science, Coral) }
            item { ElementStoryCard("WHERE YOU MEET IT", element.story.uses, Icons.Default.Home, Green) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Where it sits", color = Navy, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 8.dp))
                        MiniPeriodicTable(element)
                    }
                }
            }
            item {
                Text(
                    "A new element appears every day at midnight UTC.",
                    Modifier.fillMaxWidth(), color = Navy.copy(alpha = .55f), textAlign = TextAlign.Center, fontSize = 11.sp,
                )
            }
        }
    }
}

/** Bohr-style model: electrons orbit their shells, inner shells faster. Static when reduced motion is on. */
@Composable
private fun AtomModel(shells: List<Int>, nucleusColor: Color, animate: Boolean, modifier: Modifier) {
    val turn = if (animate) {
        val transition = rememberInfiniteTransition(label = "atom")
        transition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(12_000, easing = LinearEasing)),
            label = "orbit",
        ).value
    } else 0f
    Canvas(
        modifier.semantics {
            contentDescription = "Atom model with electron shells holding ${shells.joinToString(", ")} electrons"
        },
    ) {
        val centre = this.center
        val maxRadius = size.minDimension / 2f * .94f
        val nucleusRadius = maxRadius * .16f
        val step = (maxRadius - nucleusRadius * 1.6f) / shells.size
        val dotRadius = (step * .22f).coerceIn(2.2f, 7f)
        shells.forEachIndexed { index, count ->
            val radius = nucleusRadius * 1.6f + step * (index + 1)
            drawCircle(Navy.copy(alpha = .16f), radius, centre, style = Stroke(width = 2f))
            // Inner shells turn faster; each shell starts at a different angle so they do not line up.
            val offset = 2f * PI.toFloat() * (turn * (shells.size - index) + index * .13f)
            repeat(count) { electron ->
                val angle = offset + 2f * PI.toFloat() * electron / count
                drawCircle(
                    Sky, dotRadius,
                    Offset(centre.x + radius * cos(angle), centre.y + radius * sin(angle)),
                )
                drawCircle(
                    Navy, dotRadius, Offset(centre.x + radius * cos(angle), centre.y + radius * sin(angle)),
                    style = Stroke(width = 1.5f),
                )
            }
        }
        drawCircle(nucleusColor, nucleusRadius, centre)
        drawCircle(Navy.copy(alpha = .5f), nucleusRadius, centre, style = Stroke(width = 2f))
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
private fun MiniPeriodicTable(selected: DailyElement) {
    val cells = remember { ElementOfDay.all.associateBy(ElementOfDay::position) }
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
                                    .background(if (isSelected) Coral else familyColor(element.family).copy(alpha = .75f))
                                    .semantics { contentDescription = element.name },
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
    val entries by viewModel.leaderboard.collectAsStateWithLifecycle()
    val visible = entries.filter { it.mode == GameMode.DAILY }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH) }
    Scaffold(topBar = { AppBar("Leaderboard", { nav.popBackStack() }) }, containerColor = Color(0xFFF4FCFF)) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3C9)), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Gold)
                            Spacer(Modifier.width(9.dp))
                        Text("Daily Board leaderboard", color = Navy, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                        Text(
                            "Every player receives the same dated board, so Daily Board scores can be compared fairly.",
                            color = Navy.copy(alpha = .7f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                        )
                    }
                }
            }
            if (visible.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(54.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No completed hunts yet", fontWeight = FontWeight.Black, fontSize = 19.sp)
                        Text("Finish a game to place your first score.", color = Navy.copy(alpha = .62f))
                    }
                }
            } else {
                items(visible.size) { index ->
                    val entry = visible[index]
                    val completed = Instant.ofEpochMilli(entry.completedAt).atZone(ZoneId.systemDefault()).format(dateFormatter)
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(42.dp).background(if (index < 3) Gold else Sky, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) { Text("#${index + 1}", color = Navy, fontWeight = FontWeight.Black) }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Daily Molecule Board", fontWeight = FontWeight.Bold)
                                Text(
                                    "${entry.uniqueDiscoveries} unique · ${entry.movesRemaining} moves left · $completed",
                                    color = Navy.copy(alpha = .6f),
                                    fontSize = 11.sp,
                                )
                            }
                            Text(entry.score.toString(), color = Green, fontWeight = FontWeight.Black, fontSize = 22.sp)
                        }
                    }
                }
            }
            item {
                Text(
                    "This first version is stored on this device. The online version will publish verified Daily Board scores.",
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    color = Navy.copy(alpha = .55f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun DailyHistoryScreen(nav: NavHostController, viewModel: MainViewModel) {
    androidx.compose.runtime.LaunchedEffect(Unit) { viewModel.refreshDailyHistory() }
    val history by viewModel.dailyHistory.collectAsStateWithLifecycle()
    val today = viewModel.todayUtc()
    val summary = DailyHistory.streaks(history.keys, today)
    val currentMonth = YearMonth.from(today)
    val firstMonth = history.keys.minOrNull()?.let(YearMonth::from)?.takeIf { it.isBefore(currentMonth) } ?: currentMonth
    var month by remember { mutableStateOf(currentMonth) }
    val recent = history.values.sortedByDescending { it.date }.take(14)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH) }
    Scaffold(topBar = { AppBar("Daily Streak", { nav.popBackStack() }) }, containerColor = Color(0xFFF4FCFF)) { padding ->
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
                                        0 -> "No streak yet"
                                        1 -> "1-day streak"
                                        else -> "${summary.current}-day streak"
                                    },
                                    color = Navy, fontWeight = FontWeight.Black, fontSize = 24.sp,
                                )
                                Text(
                                    when {
                                        summary.playedToday -> "Today's board is done. Come back tomorrow."
                                        summary.current > 0 -> "Play today's board to keep your streak going."
                                        else -> "Play today's board to start a streak."
                                    },
                                    color = Navy.copy(alpha = .7f), fontSize = 12.sp,
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatPill("Best streak", summary.longest.toString(), Modifier.weight(1f))
                            StatPill("Days played", summary.daysPlayed.toString(), Modifier.weight(1f))
                        }
                        if (!summary.playedToday) {
                            Spacer(Modifier.height(12.dp))
                            Button(
                                { nav.navigate(Routes.Daily) },
                                Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Green),
                            ) { Text("Play today's board") }
                        }
                    }
                }
            }
            item {
                StreakCalendar(
                    month = month,
                    days = DailyHistory.monthGrid(month, history, today),
                    canGoBack = month.isAfter(firstMonth),
                    canGoForward = month.isBefore(currentMonth),
                    onBack = { month = month.minusMonths(1) },
                    onForward = { month = month.plusMonths(1) },
                )
            }
            item { Text("Recent boards", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Navy) }
            if (recent.isEmpty()) {
                item {
                    Text(
                        "No Daily Boards played yet. Reach the mission or finish a hunt to mark the day.",
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
                                "${record.uniqueDiscoveries} unique" + if (record.missionAchieved) " · mission complete" else "",
                                color = Navy.copy(alpha = .6f), fontSize = 11.sp,
                            )
                        }
                        Text(record.score.toString(), color = Green, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    }
                }
            }
            item {
                Text(
                    "Each day's board changes at midnight UTC. History is stored only on this device.",
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    color = Navy.copy(alpha = .55f), textAlign = TextAlign.Center, fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun StreakCalendar(
    month: YearMonth,
    days: List<CalendarDay>,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
) {
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH) }
    val dayFormatter = remember { DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH) }
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onBack, enabled = canGoBack) { Icon(Icons.Default.ChevronLeft, "Previous month") }
                Text(
                    month.format(monthFormatter), Modifier.weight(1f),
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Black, color = Navy,
                )
                IconButton(onForward, enabled = canGoForward) { Icon(Icons.Default.ChevronRight, "Next month") }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { label ->
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
                            val fill = when {
                                record?.missionAchieved == true -> Gold
                                record != null -> Green
                                else -> Color.Transparent
                            }
                            val description = buildString {
                                append(date.format(dayFormatter))
                                append(if (record == null) ", not played" else ", played, score ${record.score}")
                                if (record?.missionAchieved == true) append(", mission complete")
                                if (day.isToday) append(", today")
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
                                    color = if (record != null) Color.White else Navy.copy(alpha = .75f),
                                    fontWeight = if (record != null || day.isToday) FontWeight.Black else FontWeight.Normal,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                CalendarLegend(Green, "Played")
                CalendarLegend(Gold, "Mission complete")
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
    Scaffold(topBar = { AppBar("Discovery Review", { nav.popBackStack() }) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text("Unrecognised combinations are not labelled impossible. They stay on this device for later review.", color = Navy.copy(alpha = .7f)) }
            if (records.isEmpty()) item {
                Text("Nothing waiting for review", Modifier.fillMaxWidth().padding(40.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
            items(records, key = { it.canonicalKey }) { record ->
                Card {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LockOpen, null, tint = Gold)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(record.formula, fontWeight = FontWeight.Black, fontSize = 19.sp)
                            Text("Not yet recognised · tried ${record.count} times", fontSize = 12.sp)
                        }
                        IconButton({ viewModel.deletePending(record) }) { Icon(Icons.Default.DeleteOutline, "Delete") }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(nav: NavHostController, viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    Scaffold(topBar = { AppBar("Settings", { nav.popBackStack() }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            SettingRow("Sound", "Discovery and tile sounds", settings.sound, viewModel::setSound)
            SettingRow("Vibration", "Gentle touch feedback", settings.vibration, viewModel::setVibration)
            SettingRow("Reduced motion", "Use simpler transitions", settings.reducedMotion, viewModel::setReducedMotion)
            Spacer(Modifier.height(20.dp))
            Text("Privacy", fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("Game progress, streaks and pending reviews are stored only on this device. Ads are provided by Google AdMob, which may use your device's advertising ID.", color = Navy.copy(alpha = .7f), modifier = Modifier.padding(top = 8.dp))
            val context = LocalContext.current
            if (AdMobInterstitial.privacyOptionsRequired(context)) {
                OutlinedButton(
                    { context.findActivity()?.let(AdMobInterstitial::showPrivacyOptions) },
                    Modifier.padding(top = 10.dp),
                ) { Text("Ad privacy choices") }
            }
            Spacer(Modifier.height(20.dp))
            Text("About the chemistry", fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("A formula describes composition, not necessarily a unique structure. Discoveries describe known substances with that composition; they do not simulate a laboratory synthesis.", color = Navy.copy(alpha = .7f), modifier = Modifier.padding(top = 8.dp))
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
