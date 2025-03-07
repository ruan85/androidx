/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.transition

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.test.filters.FlakyTest
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.transition.ktx.test.R
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
class TransitionTest {
    @Suppress("DEPRECATION")
    @JvmField
    @Rule
    val rule = androidx.test.rule.ActivityTestRule<TestActivity>(TestActivity::class.java)

    private lateinit var transition: Transition

    @Before
    fun setup() {
        transition = Fade().setDuration(50)
    }

    @FlakyTest(bugId = 190193705)
    @Test
    fun testDoOnStart() {
        val called = AtomicBoolean()
        transition.doOnStart { called.set(true) }

        startTransition(transition)
        assertTrue(called.get())
    }

    @FlakyTest(bugId = 190193705)
    @Test
    fun testDoOnEnd() {
        val called = AtomicBoolean()
        transition.doOnEnd { called.set(true) }

        val latch = CountDownLatch(1)
        transition.addListener(
            object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    latch.countDown()
                }

                override fun onTransitionResume(transition: Transition) = Unit

                override fun onTransitionPause(transition: Transition) = Unit

                override fun onTransitionCancel(transition: Transition) = Unit

                override fun onTransitionStart(transition: Transition) = Unit
            }
        )

        startTransition(transition)

        assertTrue(latch.await(1, TimeUnit.SECONDS))
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertTrue(called.get())
    }

    private fun startTransition(t: Transition) {
        rule.runOnUiThread {
            val sceneRoot = rule.activity.findViewById<ViewGroup>(R.id.root)
            val view = rule.activity.findViewById<ImageView>(R.id.image_view)

            TransitionManager.beginDelayedTransition(sceneRoot, t)

            view.visibility = View.INVISIBLE
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
}
