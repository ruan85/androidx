/*
 * Copyright (C) 2024 The Android Open Source Project
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

package androidx.ink.geometry

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PartitionedMeshTest {

    @Test
    fun bounds_shouldBeEmpty() {
        val partitionedMesh = PartitionedMesh()

        assertThat(partitionedMesh.bounds).isNull()
    }

    @Test
    fun renderGroupCount_whenEmptyShape_shouldBeZero() {
        val partitionedMesh = PartitionedMesh()

        assertThat(partitionedMesh.renderGroupCount).isEqualTo(0)
    }

    @Test
    fun outlineCount_whenEmptyShape_shouldThrow() {
        val partitionedMesh = PartitionedMesh()

        assertFailsWith<IllegalArgumentException> { partitionedMesh.outlineCount(-1) }
        assertFailsWith<IllegalArgumentException> { partitionedMesh.outlineCount(0) }
        assertFailsWith<IllegalArgumentException> { partitionedMesh.outlineCount(1) }
    }

    @Test
    fun outlineVertexCount_whenEmptyShape_shouldThrow() {
        val partitionedMesh = PartitionedMesh()

        assertFailsWith<IllegalArgumentException> { partitionedMesh.outlineVertexCount(-1, 0) }
        assertFailsWith<IllegalArgumentException> { partitionedMesh.outlineVertexCount(0, 0) }
        assertFailsWith<IllegalArgumentException> { partitionedMesh.outlineVertexCount(1, 0) }
    }

    @Test
    fun populateOutlinePosition_whenEmptyShape_shouldThrow() {
        val partitionedMesh = PartitionedMesh()

        assertFailsWith<IllegalArgumentException> {
            partitionedMesh.populateOutlinePosition(-1, 0, 0, MutableVec())
        }
        assertFailsWith<IllegalArgumentException> {
            partitionedMesh.populateOutlinePosition(0, 0, 0, MutableVec())
        }
        assertFailsWith<IllegalArgumentException> {
            partitionedMesh.populateOutlinePosition(1, 0, 0, MutableVec())
        }
    }

    @Test
    fun toString_returnsAString() {
        val string = PartitionedMesh().toString()

        // Not elaborate checks - this test mainly exists to ensure that toString doesn't crash.
        assertThat(string).contains("PartitionedMesh")
        assertThat(string).contains("bounds")
        assertThat(string).contains("meshes")
        assertThat(string).contains("nativeAddress")
    }
}
