package uk.edbetter.kemdaily

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeLaunchesAndDailyBoardIsPlayable() {
        composeRule.onNodeWithContentDescription("Kemistri Everyday logo").assertIsDisplayed()
        // Home's only board entry is the Daily Molecule Board; a saved daily game may have used moves.
        composeRule.onNodeWithText("Daily Molecule Board").assertIsDisplayed().performClick()

        composeRule.onNodeWithText("Moves").assertIsDisplayed()
        composeRule.onNodeWithText("Discover").assertIsEnabled()
        composeRule.onNodeWithText("Hint").assertIsEnabled()
        composeRule.onNodeWithText("Shuffle").assertIsEnabled()
        composeRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun elementOfDayOpens() {
        composeRule.onNodeWithText("Element a Day").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("TODAY'S ELEMENT").assertIsDisplayed()
        composeRule.onNodeWithText("REMEMBER IT").assertIsDisplayed()
    }

    @Test
    fun streakCalendarOpens() {
        composeRule.onNodeWithText("Streak").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Best streak").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Previous month").assertIsDisplayed()
    }
}
