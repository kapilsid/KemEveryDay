package uk.edbetter.kemdaily.ui

import android.content.Context
import java.util.Locale
import uk.edbetter.kemdaily.data.EverydayFact

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
