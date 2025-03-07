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

package androidx.room.solver.binderprovider

import androidx.room.ext.PagingTypeNames
import androidx.room.ext.RoomPagingRx3TypeNames
import androidx.room.processor.Context
import androidx.room.processor.ProcessorErrors.MISSING_ROOM_PAGING_RXJAVA3_ARTIFACT
import androidx.room.solver.QueryResultBinderProvider

fun RxJava3PagingSourceQueryResultBinderProvider(context: Context): QueryResultBinderProvider {
    val rxjava3PagingSource = RoomPagingRx3TypeNames.LIMIT_OFFSET_RX_PAGING_SOURCE

    return MultiTypedPagingSourceQueryResultBinderProvider(
            context = context,
            roomPagingClassName = rxjava3PagingSource,
            pagingSourceTypeName = PagingTypeNames.RX3_PAGING_SOURCE
        )
        .requireArtifact(
            context = context,
            requiredType = rxjava3PagingSource,
            missingArtifactErrorMsg = MISSING_ROOM_PAGING_RXJAVA3_ARTIFACT
        )
}
