package org.tessarchy.tae

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun requiredAppsTileOpensSearchableDrawer() {
        composeRule.onRoot().performTouchInput {
            repeat(2) { swipeUp() }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("Browse installed apps").performClick()
        composeRule.onNodeWithText("All apps").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Search installed apps")
            .performTextInput("tae_query_that_matches_nothing")

        composeRule.onNodeWithContentDescription("Clear search").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("All apps").assertIsDisplayed()
    }
}
