/*
 * Copyright 2024 The Android Open Source Project
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

package androidx.wear.compose.material3

import android.os.Build
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.testutils.assertDoesNotContainColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.filters.SdkSuppress
import org.junit.Rule
import org.junit.Test

class SegmentedCircularProgressIndicatorTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun supports_testtag() {
        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                segmentCount = 5,
                progress = { 0.5f },
                modifier = Modifier.testTag(TEST_TAG)
            )
        }

        rule.onNodeWithTag(TEST_TAG).assertExists()
    }

    @Test
    fun allows_semantics_to_be_added_correctly() {
        val progress = mutableStateOf(0f)

        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                progress = { progress.value },
                segmentCount = 5,
                modifier =
                    Modifier.testTag(TEST_TAG).semantics {
                        progressBarRangeInfo = ProgressBarRangeInfo(progress.value, 0f..1f)
                    },
            )
        }

        rule.onNodeWithTag(TEST_TAG).assertRangeInfoEquals(ProgressBarRangeInfo(0f, 0f..1f))

        rule.runOnIdle { progress.value = 0.5f }

        rule.onNodeWithTag(TEST_TAG).assertRangeInfoEquals(ProgressBarRangeInfo(0.5f, 0f..1f))
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun progress_full_contains_progress_color() {
        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                progress = { 1f },
                segmentCount = 5,
                modifier = Modifier.testTag(TEST_TAG),
                colors =
                    ProgressIndicatorDefaults.colors(
                        indicatorColor = Color.Yellow,
                        trackColor = Color.Red
                    ),
            )
        }
        rule.waitForIdle()
        // by default fully filled progress approximately takes 25% of the control.
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Yellow, 20f..25f)
        rule.onNodeWithTag(TEST_TAG).captureToImage().assertDoesNotContainColor(Color.Red)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun progress_zero_contains_track_color() {
        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                progress = { 0f },
                segmentCount = 5,
                modifier = Modifier.testTag(TEST_TAG),
                colors =
                    ProgressIndicatorDefaults.colors(
                        indicatorColor = Color.Yellow,
                        trackColor = Color.Red
                    ),
            )
        }
        rule.waitForIdle()
        rule.onNodeWithTag(TEST_TAG).captureToImage().assertDoesNotContainColor(Color.Yellow)
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Red, 20f..25f)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun change_start_end_angle() {
        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                progress = { 0.5f },
                segmentCount = 5,
                modifier = Modifier.testTag(TEST_TAG),
                startAngle = 0f,
                endAngle = 180f,
                colors =
                    ProgressIndicatorDefaults.colors(
                        indicatorColor = Color.Yellow,
                        trackColor = Color.Red
                    ),
            )
        }
        rule.waitForIdle()
        // Color should take approximately a quarter of the full screen color percentages,
        // eg 25% / 4 ≈ 6%.
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Yellow, 4f..8f)
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Red, 4f..8f)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun set_small_stroke_width() {
        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                progress = { 0.5f },
                segmentCount = 5,
                modifier = Modifier.testTag(TEST_TAG),
                strokeWidth = 4.dp,
                colors =
                    ProgressIndicatorDefaults.colors(
                        indicatorColor = Color.Yellow,
                        trackColor = Color.Red
                    ),
            )
        }
        rule.waitForIdle()
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Yellow, 2f..6f)
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Red, 2f..6f)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun set_large_stroke_width() {
        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                progress = { 0.5f },
                segmentCount = 5,
                modifier = Modifier.testTag(TEST_TAG),
                strokeWidth = 36.dp,
                colors =
                    ProgressIndicatorDefaults.colors(
                        indicatorColor = Color.Yellow,
                        trackColor = Color.Red
                    ),
            )
        }
        rule.waitForIdle()
        // Because of the stroke cap, progress color takes same amount as track color.
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Yellow, 15f..20f)
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Red, 15f..20f)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun set_segments_on_off() {
        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                segmentCount = 6,
                completed = { it % 2 != 0 },
                modifier = Modifier.testTag(TEST_TAG),
                colors =
                    ProgressIndicatorDefaults.colors(
                        indicatorColor = Color.Yellow,
                        trackColor = Color.Red
                    ),
                strokeWidth = 36.dp,
            )
        }

        rule.waitForIdle()
        // Because of the stroke cap, progress color takes same amount as track color.
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Yellow, 15f..20f)
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Red, 15f..20f)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun set_segments_all_on() {
        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                segmentCount = 6,
                completed = { true },
                modifier = Modifier.testTag(TEST_TAG),
                colors =
                    ProgressIndicatorDefaults.colors(
                        indicatorColor = Color.Yellow,
                        trackColor = Color.Red
                    ),
            )
        }

        rule.waitForIdle()
        rule.onNodeWithTag(TEST_TAG).captureToImage().assertDoesNotContainColor(Color.Red)
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Yellow, 20f..25f)
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    @Test
    fun set_segments_all_off() {
        setContentWithTheme {
            SegmentedCircularProgressIndicator(
                segmentCount = 6,
                completed = { false },
                modifier = Modifier.testTag(TEST_TAG),
                colors =
                    ProgressIndicatorDefaults.colors(
                        indicatorColor = Color.Yellow,
                        trackColor = Color.Red
                    ),
            )
        }

        rule.waitForIdle()
        rule.onNodeWithTag(TEST_TAG).captureToImage().assertDoesNotContainColor(Color.Yellow)
        rule
            .onNodeWithTag(TEST_TAG)
            .captureToImage()
            .assertColorInPercentageRange(Color.Red, 20f..25f)
    }

    private fun setContentWithTheme(composable: @Composable BoxScope.() -> Unit) {
        // Use constant size modifier to limit relative color percentage ranges.
        rule.setContentWithTheme(modifier = Modifier.size(204.dp), composable = composable)
    }
}
