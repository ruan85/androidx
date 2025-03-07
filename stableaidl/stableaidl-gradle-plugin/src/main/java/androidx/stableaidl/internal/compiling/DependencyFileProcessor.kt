/*
 * Copyright (C) 2013 The Android Open Source Project
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
package androidx.stableaidl.internal.compiling

import androidx.stableaidl.internal.incremental.DependencyData
import java.io.File
import java.io.IOException

/**
 * A Class that processes a dependency file after a compilation.
 *
 * During compilation of aidl, it is possible to provide an instance of DependencyFileProcessor to
 * process the dependency files generated by the compilers.
 *
 * It can be useful to store the dependency in a better format than a per-file dependency file.
 *
 * The instance will be called for each dependency file that is created during compilation.
 *
 * Cloned from `com.android.builder.compiling.DependencyFileProcessor`.
 */
interface DependencyFileProcessor {
    /**
     * Processes the dependency file.
     *
     * @param dependencyFile the dependency file.
     * @return the dependency data that was created.
     */
    @Throws(IOException::class) fun processFile(dependencyFile: File): DependencyData?
}
