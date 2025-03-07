/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.testutils

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

/**
 * Takes the given test case and prepares it for execution-controlled test via
 * [ComposeTestCaseSetup].
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.forGivenTestCase(
    testCase: ComposeTestCase
): ComposeTestCaseSetup {
    fun getActivity(): A {
        var activity: A? = null
        activityRule.scenario.onActivity { activity = it }
        if (activity == null) {
            throw IllegalStateException("Activity was not set in the ActivityScenarioRule!")
        }
        return activity!!
    }

    return AndroidComposeTestCaseSetup(testCase, getActivity())
}
