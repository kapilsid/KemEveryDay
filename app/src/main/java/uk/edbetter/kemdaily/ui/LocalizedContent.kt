package uk.edbetter.kemdaily.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import java.util.Locale
import uk.edbetter.kemdaily.R
import uk.edbetter.kemdaily.data.EverydayFact
import uk.edbetter.kemdaily.data.MysteryCategory

/**
 * Everyday Chemistry text comes from string resources imported from Kemistri (res/values-xx/everyday_cards.xml),
 * so each card follows the phone's language. The "try it" activities exist only in English.
 */
private val kemistriCorrectGuess = mapOf(
        "soap_oil" to 2, "apple_brown" to 1, "toast_brown" to 2, "fizzy_drink" to 0, "cold_can" to 1, "salt_ice" to 2,
        "rust" to 1, "bleach" to 2, "onion_tears" to 1, "milk_curdle" to 1, "descale" to 1, "cake_rise" to 1,
        "fluoride" to 2, "perfume" to 1, "chilli_heat" to 1, "ice_slippery" to 0, "bread_stale" to 1, "static_shock" to 2,
        "banana_brown" to 0, "avocado_brown" to 1, "lemon_sour" to 2, "baking_soda_vinegar" to 0, "baking_powder" to 1, "yeast_bread" to 2,
        "yogurt" to 0, "cheese" to 1, "egg_white" to 2, "egg_yolk" to 0, "meat_brown" to 1, "coffee_aroma" to 2,
        "caramel" to 0, "red_cabbage" to 1, "tea_lemon" to 2, "onion_storage" to 0, "garlic_smell" to 1, "mint_cool" to 2,
        "chilli_after" to 0, "mustard" to 1, "pepper_aroma" to 2, "orange_peel" to 0, "perfume_heat" to 1, "nail_polish" to 2,
        "paint_dry" to 0, "soap_bubbles" to 1, "detergent_grease" to 2, "dishwasher" to 0, "hard_water" to 1, "shower_scale" to 2,
        "antacid" to 0, "tooth_decay" to 1, "fluoride_varnish" to 2, "sunscreen" to 0, "bleach_disinfect" to 1, "rust_protection" to 2,
        "battery" to 0, "led" to 1, "glow_stick" to 2, "fireworks" to 0, "soap_ph" to 1, "redox_apple" to 2,
        "salt_pasta" to 0, "steam_burn" to 1,
)

fun EverydayFact.localized(context: Context): EverydayFact {
    fun text(field: String): String? = context.resources
        .getIdentifier("ev_${id}_$field", "string", context.packageName)
        .takeIf { it != 0 }
        ?.let(context::getString)
    val guessA = text("guess_a") ?: return this
    val english = Locale.getDefault().language == "en"
    return copy(
        question = text("question") ?: question,
        setup = text("setup") ?: setup,
        guesses = listOf(guessA, text("guess_b") ?: guesses[1], text("guess_c") ?: guesses[2]),
        correctGuess = kemistriCorrectGuess[id] ?: correctGuess,
        answerHeadline = text("headline") ?: answerHeadline,
        explanation = text("body") ?: explanation,
        chemistryTags = text("tags") ?: chemistryTags,
        equation = text("equation") ?: equation,
        tryIt = tryIt.takeIf { english },
        safety = safety.takeIf { english },
    )
}

/*
 * The game engine and data classes stay plain English Kotlin (no Context, unit tested), so their
 * player-facing words are translated here at display time. Anything unmapped is shown unchanged.
 */

private val maxAtomsMessage = Regex("A discovery can use up to (\\d+) atoms")
private const val NOT_RECOGNISED_SUFFIX = " is not yet recognised — saved for review"

/** Translates a GameEngine message (GameState.message) into the phone's language. */
@Composable
fun localizedGameMessage(message: String): String {
    maxAtomsMessage.matchEntire(message)?.let { match ->
        val atoms = match.groupValues[1].toInt()
        return pluralStringResource(R.plurals.hunt_msg_max_atoms, atoms, atoms)
    }
    if (message.endsWith(NOT_RECOGNISED_SUFFIX)) {
        return stringResource(R.string.hunt_msg_not_recognised, message.removeSuffix(NOT_RECOGNISED_SUFFIX))
    }
    val id = when (message) {
        "Choose a neighbouring tile" -> R.string.hunt_msg_choose_neighbour
        "Board refreshed — try this new layout" -> R.string.hunt_msg_board_refreshed
        "A possible discovery is glowing" -> R.string.hunt_msg_hint_glowing
        "Board shuffled" -> R.string.hunt_msg_board_shuffled
        "No moves remaining" -> R.string.hunt_msg_no_moves
        "Connect at least two atoms" -> R.string.hunt_msg_too_short
        "New discovery!" -> R.string.hunt_msg_new_discovery
        "Repeat discovery · 25% score" -> R.string.hunt_msg_repeat_discovery
        else -> return message
    }
    return stringResource(id)
}

@Composable
fun MysteryCategory.localizedLabel(): String = stringResource(
    when (this) {
        MysteryCategory.SCIENTIST -> R.string.science_category_scientist
        MysteryCategory.ELEMENT -> R.string.science_category_element
        MysteryCategory.PLACE -> R.string.science_category_place
        MysteryCategory.DISCOVERY -> R.string.science_category_discovery
        MysteryCategory.INSTRUMENT -> R.string.science_category_instrument
    },
)

@Composable
fun MysteryCategory.localizedPrompt(): String = stringResource(
    when (this) {
        MysteryCategory.SCIENTIST -> R.string.science_prompt_scientist
        MysteryCategory.ELEMENT -> R.string.science_prompt_element
        MysteryCategory.PLACE -> R.string.science_prompt_place
        MysteryCategory.DISCOVERY -> R.string.science_prompt_discovery
        MysteryCategory.INSTRUMENT -> R.string.science_prompt_instrument
    },
)

/** Translates DailyElement.family; the English value is still used for colours and guess options. */
@Composable
fun localizedFamily(family: String): String {
    val id = when (family) {
        "Alkali metals" -> R.string.element_family_alkali_metals
        "Alkaline-earth metals" -> R.string.element_family_alkaline_earth_metals
        "Transition metals" -> R.string.element_family_transition_metals
        "Post-transition metals" -> R.string.element_family_post_transition_metals
        "Metalloids" -> R.string.element_family_metalloids
        "Reactive nonmetals" -> R.string.element_family_reactive_nonmetals
        "Halogens" -> R.string.element_family_halogens
        "Noble gases" -> R.string.element_family_noble_gases
        "Lanthanides" -> R.string.element_family_lanthanides
        "Actinides" -> R.string.element_family_actinides
        else -> return family
    }
    return stringResource(id)
}

/** Translates DailyElement.state; the English value is still used to pick the density unit. */
@Composable
fun localizedState(state: String): String {
    val id = when (state) {
        "Solid" -> R.string.element_state_solid
        "Liquid" -> R.string.element_state_liquid
        "Gas" -> R.string.element_state_gas
        "State unknown" -> R.string.element_state_unknown
        else -> return state
    }
    return stringResource(id)
}

/** "Group 14", or "f-block" for the lanthanides and actinides. */
@Composable
fun localizedGroup(group: Int?): String =
    if (group != null) stringResource(R.string.element_group, group) else stringResource(R.string.element_f_block)
