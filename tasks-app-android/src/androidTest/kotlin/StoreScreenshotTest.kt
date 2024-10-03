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


import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.test.runTest
import net.opatry.tasks.app.MainActivity
import net.opatry.tasks.app.R
import org.junit.Rule
import org.junit.Test
import java.io.File

@OptIn(ExperimentalTestApi::class)
class StoreScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
//    val composeTestRule = createComposeRule()

    @get:Rule
    val screenshotOnFailureRule = ScreenshotOnFailureRule()

    private val targetContext: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

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
        // How to switch dark/light theme programmatically
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // FIXME not enough for Activity theme (status bar & navigation bar)
//        composeTestRule.setContent {
//            var darkMode by remember { mutableStateOf(false) }
//            TaskfolioTheme(darkMode) {
//                Surface {
//                    val userViewModel = koinViewModel<UserViewModel>()
//                    val tasksViewModel = koinViewModel<TaskListsViewModel>()
//                    TasksApp(AboutApp(
//                        "Tasksfolio",
//                        "1.0.0.1",
//                        { "{\"libraries\": [], \"licenses\": []" }
//                    ), userViewModel, tasksViewModel)
//                }
//            }
//        }

//        if (composeTestRule.onNodeWithText("Skip").isDisplayed()) {
//            composeTestRule.onNodeWithText("Skip").performClick()
//        }
//        composeTestRule.onNodeWithText("Add task list…").performClick()
//        composeTestRule.onNodeWithText("Create").performClick()

        screenshot("start")
        val taskTitle = targetContext.getString(R.string.demo_task_list_default)
        composeTestRule.waitUntilAtLeastOneExists(hasText(taskTitle))
        screenshot("tasks_list_light")
        composeTestRule.onNodeWithText(taskTitle, substring = true, ignoreCase = true)
            .assertIsDisplayed()
            .performClick()
        screenshot("my_tasks_light")

        composeTestRule.waitUntilExactlyOneExists(hasTestTag("ADD_TASK_FAB"))
        composeTestRule.onNodeWithTag("ADD_TASK_FAB")
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitUntilExactlyOneExists(isDialog())
        screenshot("add_task_light")
    }
}