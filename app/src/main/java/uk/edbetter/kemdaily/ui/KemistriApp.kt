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
import uk.edbetter.kemdaily.data.ScienceMysteries
import uk.edbetter.kemdaily.data.ScienceRunSnapshot
import uk.edbetter.kemdaily.data.TablePosition
import uk.edbetter.kemdaily.ui.theme.Coral
import uk.edbetter.kemdaily.ui.theme.Gold
import uk.edbetter.kemdaily.ui.theme.Green
import uk.edbetter.kemdaily.ui.theme.Navy
import uk.edbetter.kemdaily.ui.theme.Sky
import java.time.LocalDate
import java.time.YearMonth
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
    const val Science = "science"
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
    val streak = DailyHistory.streaks(history.keys, viewModel.todayUtc()).current
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
                        contentDescription = "Kemistri Everyday logo",
                        modifier = Modifier.size(76.dp).clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Kemistri Everyday", color = Navy, fontWeight = FontWeight.Black, fontSize = 24.sp)
                        Text(
                            "A new chemistry wonder every day",
                            color = Navy.copy(alpha = .72f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (cloud.nickname.isNotBlank()) {
                            // The public leaderboard name; tapping opens Daily Champions, where it can be changed.
                            Row(
                                Modifier.padding(top = 6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable(onClickLabel = "Open Daily Champions") { nav.navigate(Routes.Leaderboard) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(5.dp))
                                Text("Playing as ${cloud.nickname}", color = Navy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            item {
                val dailySubtitle = if (streak > 0) "$streak-day streak · one shared puzzle every day" else "One shared chemistry puzzle every day"
                FeatureCard("Daily Molecule Board", dailySubtitle, Icons.Default.CalendarMonth, Green) {
                    nav.navigate(Routes.Daily)
                }
            }
            item {
                FeatureCard("Science Uncovered", "One daily run. Build a high score.", Icons.Default.AutoAwesome, Sky) {
                    nav.navigate(Routes.Science)
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
                    "© 2026 EdBetter",
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

@Composable
private fun ScienceUncoveredScreen(nav: NavHostController, viewModel: MainViewModel) {
    val savedBest by viewModel.scienceBestScore.collectAsStateWithLifecycle()
    val restoredRun = remember { viewModel.loadScienceRun() }
    val firstMysteryIndex = remember { ScienceMysteries.all.indexOf(ScienceMysteries.today()).coerceAtLeast(0) }
    var mysteryOffset by remember { mutableStateOf(restoredRun.mysteryOffset) }
    var totalScore by remember { mutableStateOf(restoredRun.totalScore) }
    var lives by remember { mutableStateOf(restoredRun.lives) }
    var solvedCount by remember { mutableStateOf(restoredRun.solvedCount) }
    var mysteryNumber by remember { mutableStateOf(restoredRun.mysteryNumber) }
    var runOver by remember { mutableStateOf(restoredRun.runOver) }
    val mystery = ScienceMysteries.all[(firstMysteryIndex + mysteryOffset) % ScienceMysteries.all.size]
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

    Scaffold(topBar = { AppBar("Science Uncovered", { nav.popBackStack() }) }, containerColor = Color(0xFFF4F7FB)) { padding ->
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
                                "RUN SCORE  $totalScore",
                                modifier = Modifier.weight(1f),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                            )
                            Text("${"♥".repeat(lives)}${"♡".repeat(3 - lives)}", color = Coral, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        }
                        Text(
                            "Today's run · Mystery $mysteryNumber · Best $savedBest",
                            color = Color.White.copy(alpha = .62f),
                            fontSize = 10.sp,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(mystery.category.label.uppercase(), color = Gold, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.3.sp)
                                Text(mystery.category.prompt, color = Color.White, fontWeight = FontWeight.Black, fontSize = 25.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(points.toString(), color = Gold, fontWeight = FontWeight.Black, fontSize = 28.sp)
                                Text("possible points", color = Color.White.copy(alpha = .62f), fontSize = 10.sp)
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
                            "$answerLength letters · ${mystery.wordCount} ${if (mystery.wordCount == 1) "word" else "words"}",
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
                            Text("CLUE ${index + 1}", color = Navy.copy(alpha = .5f), fontWeight = FontWeight.Black, fontSize = 10.sp)
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
                                                        message = "$letter is not in the answer — one life lost"
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
                            "Tap a letter to reveal every matching position",
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
                        ) { Text(if (visibleClues < mystery.clues.size) "Next clue (−15)" else "All clues shown") }
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
                        ) { Text("Skip (−1 life)") }
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
                                    solved -> "Solved — +$points points!"
                                    runOver -> "Run over — $totalScore points"
                                    else -> "Mystery revealed — one life lost"
                                },
                                color = if (solved) Green else Coral,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                            )
                            if (wrongAnswers > 0 && solved) Text("Solved after $wrongAnswers incorrect letter ${if (wrongAnswers == 1) "guess" else "guesses"}.", color = Navy.copy(alpha = .62f), fontSize = 12.sp)
                            Text(mystery.story, color = Navy, lineHeight = 21.sp)
                            if (runOver) {
                                Text("$solvedCount mysteries solved in this run", color = Navy.copy(alpha = .62f), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            if (runOver) {
                                nav.popBackStack()
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
                                runOver -> "BACK HOME"
                                solved -> "NEXT MYSTERY"
                                else -> "CONTINUE — $lives ${if (lives == 1) "LIFE" else "LIVES"} LEFT"
                            },
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
                if (runOver) {
                    item {
                        Text(
                            "Today's attempt is complete. A new run unlocks at 00:00 UTC.",
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
                    Column(
                        Modifier.weight(1f).aspectRatio(1f)
                            .background(tint, RoundedCornerShape(22.dp))
                            .border(3.dp, Navy.copy(alpha = .18f), RoundedCornerShape(22.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .semantics {
                                contentDescription = "${element.name}, symbol ${element.symbol}, atomic number ${element.atomicNumber}, " +
                                    "atomic weight ${element.atomicWeight}, electron configuration " +
                                    (element.core.orEmpty() + " " + element.configuration.joinToString(" ") { "${it.first} ${it.second}" })
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
                        AtomModel(shells, outer.second, tint, animate = !settings.reducedMotion, modifier = Modifier.fillMaxWidth().aspectRatio(1f))
                        Text(
                            "Outer electrons: ${outer.first}${"spdf"[outer.second]} orbital",
                            color = Navy.copy(alpha = .6f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            item {
                Text(
                    listOfNotNull(
                        element.family, element.state, "Made in a lab".takeIf { element.atomicNumber >= 93 },
                        "Period ${element.period}", element.group?.let { "Group $it" } ?: "f-block",
                    ).joinToString(" · "),
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
    Canvas(
        modifier.semantics {
            contentDescription = "Electron cloud model. Shells hold ${shells.joinToString(", ")} electrons; " +
                "the outer electrons fill a $orbitalName orbital."
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
    val entries by viewModel.scienceLeaderboard.collectAsStateWithLifecycle()
    val cloud by viewModel.scienceCloud.collectAsStateWithLifecycle()
    val today = viewModel.todayUtc()
    val run = remember { viewModel.loadScienceRun() }
    val history by viewModel.dailyHistory.collectAsStateWithLifecycle()
    val board = history[today]
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH) }
    var editNickname by remember { mutableStateOf(false) }
    var nicknameDraft by remember(cloud.nickname) { mutableStateOf(cloud.nickname) }
    Scaffold(topBar = { AppBar("Daily Champions", { nav.popBackStack() }) }, containerColor = Color(0xFFF4FCFF)) { padding ->
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
                            Text("Daily Champions", color = Navy, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                        Text(
                            "Finish today's Molecule Board and today's Science Uncovered run. Your two scores are added together, and your first completed total for each UTC day goes on the worldwide ranking.",
                            color = Navy.copy(alpha = .7f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                        )
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).background(Sky, CircleShape), contentAlignment = Alignment.Center) {
                            if (cloud.connecting) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Navy)
                            else Icon(Icons.Default.Science, null, tint = Navy)
                        }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(cloud.nickname.ifBlank { "Science player" }, color = Navy, fontWeight = FontWeight.Black)
                            Text(cloud.message, color = Navy.copy(alpha = .6f), fontSize = 10.sp, lineHeight = 14.sp)
                        }
                        if (cloud.online) {
                            IconButton({ nicknameDraft = cloud.nickname; editNickname = true }) {
                                Icon(Icons.Default.Edit, "Edit nickname", tint = Coral)
                            }
                        } else {
                            IconButton(viewModel::refreshScienceLeaderboard) {
                                Icon(Icons.Default.Refresh, "Retry Firebase connection", tint = Coral)
                            }
                        }
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(if (board != null && run.runOver) "TODAY'S TOTAL" else "TODAY SO FAR", color = Gold, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            Text(
                                "Molecule Board " + (board?.score?.toString() ?: "not finished") +
                                    " · Science " + (if (run.runOver) run.totalScore.toString() else "in progress"),
                                color = Color.White.copy(alpha = .7f),
                                fontSize = 12.sp,
                            )
                        }
                        Text(((board?.score ?: 0) + if (run.runOver) run.totalScore else 0).toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 32.sp)
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Worldwide rankings", modifier = Modifier.weight(1f), color = Navy, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    Text(if (cloud.online) "TODAY · UTC" else "OFFLINE", color = Coral, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
            if (cloud.rankings.isEmpty()) {
                item {
                    Text(
                        if (cloud.online) "No worldwide scores have been posted for today yet."
                        else "Worldwide scores appear here when Firebase is connected.",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                        color = Navy.copy(alpha = .62f),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                items(cloud.rankings.size) { index ->
                    val entry = cloud.rankings[index]
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
                                Text(if (isPlayer) "${entry.displayName} · You" else entry.displayName, fontWeight = FontWeight.Bold)
                                Text(
                                    "Board ${entry.moleculeScore} · Science ${entry.scienceScore}",
                                    color = Navy.copy(alpha = .6f), fontSize = 11.sp,
                                )
                            }
                            Text(entry.score.toString(), color = Green, fontWeight = FontWeight.Black, fontSize = 22.sp)
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Your scores on this device", modifier = Modifier.weight(1f), color = Navy, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    Text("LOCAL", color = Coral, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
            if (entries.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Gold, modifier = Modifier.size(54.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No completed runs yet", fontWeight = FontWeight.Black, fontSize = 19.sp)
                        Text("Finish today's Science Uncovered run to place your first score.", color = Navy.copy(alpha = .62f), textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(entries.size) { index ->
                    val entry = entries[index]
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(42.dp).background(if (index < 3) Gold else Sky, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) { Text("#${index + 1}", color = Navy, fontWeight = FontWeight.Black) }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(if (entry.date == today) "Today" else entry.date.format(dateFormatter), fontWeight = FontWeight.Bold)
                                Text(
                                    "${entry.mysteriesSolved} ${if (entry.mysteriesSolved == 1) "mystery" else "mysteries"} solved",
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
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F6FC)), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(if (cloud.online) "Firebase connected" else "Playing offline", color = Navy, fontWeight = FontWeight.Black)
                        Text(
                            if (cloud.online) "Your anonymous player identity keeps the app simple: no Google login screen and no email is shown. A finished daily run is submitted once."
                            else "Your run and score remain on this device. After Anonymous Authentication, Firestore and the score function are enabled, tap Retry to connect.",
                            color = Navy.copy(alpha = .65f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                        )
                        TextButton(onClick = viewModel::refreshScienceLeaderboard) {
                            Icon(Icons.Default.Refresh, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Refresh")
                        }
                    }
                }
            }
        }
    }
    if (editNickname) {
        AlertDialog(
            onDismissRequest = { editNickname = false },
            title = { Text("Choose your player name") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This public nickname appears on Daily Champions. Do not use your real name.", color = Navy.copy(alpha = .7f))
                    OutlinedTextField(
                        value = nicknameDraft,
                        onValueChange = { if (it.length <= 16) nicknameDraft = it },
                        label = { Text("Nickname") },
                        supportingText = { Text("3–16 letters, numbers or spaces") },
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.updatePlayerNickname(nicknameDraft); editNickname = false }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { editNickname = false }) { Text("Cancel") } },
        )
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
                            StatPill("Best score", history.values.maxOfOrNull { it.score }?.toString() ?: "–", Modifier.weight(1f))
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
