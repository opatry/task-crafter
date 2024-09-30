/*
 * Copyright (c) 2024 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.test.runTest
import net.opatry.tasks.app.MainActivity
import org.junit.Rule
import org.junit.Test
import java.io.File

@OptIn(ExperimentalTestApi::class)
class StoreScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val screenshotOnFailureRule = ScreenshotOnFailureRule()

    private fun screenshot(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val outputDir = File(instrumentation.targetContext.cacheDir, "store_screenshots").also(File::mkdirs)
        val outputFile = File(outputDir, "$name.png")
        if (outputFile.exists()) {
            outputFile.delete()
        }
        UiDevice.getInstance(instrumentation)
            .takeScreenshot(outputFile)
    }

    @Test
    fun playstoreScreenshotSequence() = runTest {
        composeTestRule.waitUntilAtLeastOneExists(hasText("Toto", substring = true, ignoreCase = true))
//        composeTestRule.waitUntil {
//            composeTestRule.onNodeWithText("My Tasks").isDisplayed()
//        }
//        composeTestRule.waitForIdle()
        screenshot("start")
//        composeTestRule.onNodeWithText("Skip").performClick()
//        composeTestRule.onNodeWithText("Add task list…").performClick()
//        composeTestRule.onNodeWithText("Create").performClick()
        screenshot("tasks_list_light")

        val boolll = composeTestRule.onNodeWithText("Toto", substring = true, ignoreCase = true).isDisplayed()
        println("boolll=$boolll")
        composeTestRule.onNodeWithText("Toto", substring = true, ignoreCase = true).assertExists()
        composeTestRule.onNodeWithText("Toto", substring = true, ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Toto", substring = true, ignoreCase = true).performClick()
        screenshot("my_tasks_light")

        composeTestRule.onNodeWithTag("ADD_TASK_FAB").performClick()
        composeTestRule.waitForIdle()
        screenshot("add_task_light")
    }
}