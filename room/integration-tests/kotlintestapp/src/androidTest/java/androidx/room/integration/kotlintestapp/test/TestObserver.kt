/*
 * Copyright 2018 The Android Open Source Project
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
package androidx.room.integration.kotlintestapp.test

import androidx.kruth.assertThat
import androidx.lifecycle.Observer
import java.util.concurrent.TimeoutException

abstract class TestObserver<T> : Observer<T> {
    private var mLastData: T? = null
    private var mHasValue = false

    fun reset() {
        mHasValue = false
        mLastData = null
    }

    override fun onChanged(value: T) {
        mLastData = value
        mHasValue = true
    }

    @Throws(TimeoutException::class, InterruptedException::class)
    fun hasValue(): Boolean {
        drain()
        return mHasValue
    }

    @Throws(InterruptedException::class, TimeoutException::class)
    fun get(): T? {
        drain()
        assertThat(hasValue()).isTrue()
        return mLastData
    }

    @Throws(TimeoutException::class, InterruptedException::class) protected abstract fun drain()
}
