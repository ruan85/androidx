/*
 * Copyright 2022 The Android Open Source Project
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
package androidx.wear.compose.material.samples

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.wear.compose.material.MaterialTheme
import org.junit.Rule
import org.junit.Test

// Simple tests to ensure samples build and show the expected elements.
class PickerSampleTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun option_change_picker_builds() {
        rule.setContent { MaterialTheme { OptionChangePicker() } }
    }

    @Test
    fun dual_picker_builds() {
        rule.setContent { MaterialTheme { DualPicker() } }
    }
}
