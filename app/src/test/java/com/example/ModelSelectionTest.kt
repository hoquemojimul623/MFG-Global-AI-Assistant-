package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.model.AiMode
import com.example.ui.components.ModeSelectorBar
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ModelSelectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAllModesRenderedAndSelectable() {
        var selectedMode by mutableStateOf(AiMode.FAST)

        composeTestRule.setContent {
            ModeSelectorBar(
                selectedMode = selectedMode,
                onModeSelected = { selectedMode = it }
            )
        }

        // Verify mode bar is displayed
        composeTestRule.onNodeWithTag("mode_selector_bar").assertIsDisplayed()

        // Verify chips exist for modes
        composeTestRule.onNodeWithTag("mode_chip_${AiMode.FAST.id}").assertIsDisplayed()
        composeTestRule.onNodeWithTag("mode_chip_${AiMode.VERY_FAST.id}").assertExists()
        composeTestRule.onNodeWithTag("mode_chip_${AiMode.AI_MODEL.id}").assertExists()

        // Click Very Fast chip and verify selection updates
        composeTestRule.onNodeWithTag("mode_chip_${AiMode.VERY_FAST.id}").performClick()
        assertEquals(AiMode.VERY_FAST, selectedMode)

        // Click Fast chip and verify selection updates
        composeTestRule.onNodeWithTag("mode_chip_${AiMode.FAST.id}").performClick()
        assertEquals(AiMode.FAST, selectedMode)
    }
}
