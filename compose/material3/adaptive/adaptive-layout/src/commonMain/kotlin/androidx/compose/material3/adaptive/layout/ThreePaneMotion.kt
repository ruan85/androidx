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

package androidx.compose.material3.adaptive.layout

import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedFiniteAnimationSpec
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastForEachIndexed

@ExperimentalMaterial3AdaptiveApi
@Composable
internal fun calculateThreePaneMotion(
    targetScaffoldValue: ThreePaneScaffoldValue,
    paneOrder: ThreePaneScaffoldHorizontalOrder
): ThreePaneMotion {
    class ThreePaneScaffoldValueHolder(var value: ThreePaneScaffoldValue)

    val layoutDirection = LocalLayoutDirection.current
    val ltrPaneOrder =
        remember(paneOrder, layoutDirection) { paneOrder.toLtrOrder(layoutDirection) }
    val previousScaffoldValue = remember { ThreePaneScaffoldValueHolder(targetScaffoldValue) }
    val threePaneMotion =
        remember(targetScaffoldValue, ltrPaneOrder) {
            val previousValue = previousScaffoldValue.value
            previousScaffoldValue.value = targetScaffoldValue
            val paneMotions = calculatePaneMotion(previousValue, targetScaffoldValue, ltrPaneOrder)
            ThreePaneMotion(
                paneMotions[ltrPaneOrder.indexOf(ThreePaneScaffoldRole.Primary)],
                paneMotions[ltrPaneOrder.indexOf(ThreePaneScaffoldRole.Secondary)],
                paneMotions[ltrPaneOrder.indexOf(ThreePaneScaffoldRole.Tertiary)]
            )
        }
    return threePaneMotion
}

@ExperimentalMaterial3AdaptiveApi
@Immutable
internal class ThreePaneMotion(
    val primaryPaneMotion: PaneMotion,
    val secondaryPaneMotion: PaneMotion,
    val tertiaryPaneMotion: PaneMotion,
    val sizeAnimationSpec: FiniteAnimationSpec<IntSize> =
        ThreePaneMotionDefaults.PaneSizeAnimationSpec,
    val positionAnimationSpec: FiniteAnimationSpec<IntOffset> =
        ThreePaneMotionDefaults.PanePositionAnimationSpec,
    val delayedPositionAnimationSpec: FiniteAnimationSpec<IntOffset> =
        ThreePaneMotionDefaults.PanePositionAnimationSpecDelayed
) {
    fun copy(
        primaryPaneMotion: PaneMotion = this.primaryPaneMotion,
        secondaryPaneMotion: PaneMotion = this.secondaryPaneMotion,
        tertiaryPaneMotion: PaneMotion = this.tertiaryPaneMotion,
        sizeAnimationSpec: FiniteAnimationSpec<IntSize> = this.sizeAnimationSpec,
        positionAnimationSpec: FiniteAnimationSpec<IntOffset> = this.positionAnimationSpec,
        delayedPositionAnimationSpec: FiniteAnimationSpec<IntOffset> =
            this.delayedPositionAnimationSpec
    ): ThreePaneMotion =
        ThreePaneMotion(
            primaryPaneMotion,
            secondaryPaneMotion,
            tertiaryPaneMotion,
            sizeAnimationSpec,
            positionAnimationSpec,
            delayedPositionAnimationSpec
        )

    operator fun get(role: ThreePaneScaffoldRole): PaneMotion =
        when (role) {
            ThreePaneScaffoldRole.Primary -> primaryPaneMotion
            ThreePaneScaffoldRole.Secondary -> secondaryPaneMotion
            ThreePaneScaffoldRole.Tertiary -> tertiaryPaneMotion
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ThreePaneMotion) return false
        if (primaryPaneMotion != other.primaryPaneMotion) return false
        if (secondaryPaneMotion != other.secondaryPaneMotion) return false
        if (tertiaryPaneMotion != other.tertiaryPaneMotion) return false
        if (sizeAnimationSpec != other.sizeAnimationSpec) return false
        if (positionAnimationSpec != other.positionAnimationSpec) return false
        if (delayedPositionAnimationSpec != other.delayedPositionAnimationSpec) return false
        return true
    }

    override fun hashCode(): Int {
        var result = primaryPaneMotion.hashCode()
        result = 31 * result + secondaryPaneMotion.hashCode()
        result = 31 * result + tertiaryPaneMotion.hashCode()
        result = 31 * result + sizeAnimationSpec.hashCode()
        result = 31 * result + positionAnimationSpec.hashCode()
        result = 31 * result + delayedPositionAnimationSpec.hashCode()
        return result
    }

    override fun toString(): String {
        return "ThreePaneMotion(" +
            "primaryPaneMotion=$primaryPaneMotion, " +
            "secondaryPaneMotion=$secondaryPaneMotion, " +
            "tertiaryPaneMotion=$tertiaryPaneMotion, " +
            "sizeAnimationSpec=$sizeAnimationSpec, " +
            "positionAnimationSpec=$positionAnimationSpec, " +
            "delayedPositionAnimationSpec=$delayedPositionAnimationSpec)"
    }

    internal fun toPaneMotionList(ltrOrder: ThreePaneScaffoldHorizontalOrder): List<PaneMotion> =
        listOf(this[ltrOrder.firstPane], this[ltrOrder.secondPane], this[ltrOrder.thirdPane])
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Suppress("PrimitiveInCollection") // No way to get underlying Long of IntSize or IntOffset
internal class ThreePaneScaffoldMotionScopeImpl : PaneScaffoldMotionScope {
    internal lateinit var threePaneMotion: ThreePaneMotion
        private set

    override val sizeAnimationSpec: FiniteAnimationSpec<IntSize>
        get() = threePaneMotion.sizeAnimationSpec

    override val positionAnimationSpec: FiniteAnimationSpec<IntOffset>
        get() = threePaneMotion.positionAnimationSpec

    override val delayedPositionAnimationSpec: FiniteAnimationSpec<IntOffset>
        get() = threePaneMotion.delayedPositionAnimationSpec

    override var scaffoldSize: IntSize = IntSize.Zero
    override val paneMotionDataList: List<PaneMotionData> =
        listOf(PaneMotionData(), PaneMotionData(), PaneMotionData())

    internal fun updateThreePaneMotion(
        threePaneMotion: ThreePaneMotion,
        ltrOrder: ThreePaneScaffoldHorizontalOrder
    ) {
        val paneMotions = threePaneMotion.toPaneMotionList(ltrOrder)
        this.paneMotionDataList.fastForEachIndexed { index, it -> it.motion = paneMotions[index] }
        this.threePaneMotion = threePaneMotion
    }
}

internal class DelayedSpringSpec<T>(
    dampingRatio: Float = Spring.DampingRatioNoBouncy,
    stiffness: Float = Spring.StiffnessMedium,
    private val delayedRatio: Float,
    visibilityThreshold: T? = null
) : FiniteAnimationSpec<T> {
    private val originalSpringSpec = spring(dampingRatio, stiffness, visibilityThreshold)

    override fun <V : AnimationVector> vectorize(
        converter: TwoWayConverter<T, V>
    ): VectorizedFiniteAnimationSpec<V> =
        DelayedVectorizedSpringSpec(originalSpringSpec.vectorize(converter), delayedRatio)
}

private class DelayedVectorizedSpringSpec<V : AnimationVector>(
    val originalVectorizedSpringSpec: VectorizedFiniteAnimationSpec<V>,
    val delayedRatio: Float,
) : VectorizedFiniteAnimationSpec<V> {
    var delayedTimeNanos: Long = 0
    var cachedInitialValue: V? = null
    var cachedTargetValue: V? = null
    var cachedInitialVelocity: V? = null
    var cachedOriginalDurationNanos: Long = 0

    override fun getValueFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        updateDelayedTimeNanosIfNeeded(initialValue, targetValue, initialVelocity)
        return if (playTimeNanos <= delayedTimeNanos) {
            initialValue
        } else {
            originalVectorizedSpringSpec.getValueFromNanos(
                playTimeNanos - delayedTimeNanos,
                initialValue,
                targetValue,
                initialVelocity
            )
        }
    }

    override fun getVelocityFromNanos(
        playTimeNanos: Long,
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ): V {
        updateDelayedTimeNanosIfNeeded(initialValue, targetValue, initialVelocity)
        return if (playTimeNanos <= delayedTimeNanos) {
            initialVelocity
        } else {
            originalVectorizedSpringSpec.getVelocityFromNanos(
                playTimeNanos - delayedTimeNanos,
                initialValue,
                targetValue,
                initialVelocity
            )
        }
    }

    override fun getDurationNanos(initialValue: V, targetValue: V, initialVelocity: V): Long {
        updateDelayedTimeNanosIfNeeded(initialValue, targetValue, initialVelocity)
        return cachedOriginalDurationNanos + delayedTimeNanos
    }

    private fun updateDelayedTimeNanosIfNeeded(
        initialValue: V,
        targetValue: V,
        initialVelocity: V
    ) {
        if (
            initialValue != cachedInitialValue ||
                targetValue != cachedTargetValue ||
                initialVelocity != cachedInitialVelocity
        ) {
            cachedOriginalDurationNanos =
                originalVectorizedSpringSpec.getDurationNanos(
                    initialValue,
                    targetValue,
                    initialVelocity
                )
            delayedTimeNanos = (cachedOriginalDurationNanos * delayedRatio).toLong()
        }
    }
}

@ExperimentalMaterial3AdaptiveApi
internal object ThreePaneMotionDefaults {
    // TODO(conradchen): open this to public when we support motion customization
    val PanePositionAnimationSpec: SpringSpec<IntOffset> =
        spring(
            dampingRatio = 0.8f,
            stiffness = 600f,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )

    // TODO(conradchen): open this to public when we support motion customization
    val PanePositionAnimationSpecDelayed: DelayedSpringSpec<IntOffset> =
        DelayedSpringSpec(
            dampingRatio = 0.8f,
            stiffness = 600f,
            delayedRatio = 0.1f,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )

    // TODO(conradchen): open this to public when we support motion customization
    val PaneSizeAnimationSpec: SpringSpec<IntSize> =
        spring(
            dampingRatio = 0.8f,
            stiffness = 600f,
            visibilityThreshold = IntSize.VisibilityThreshold
        )
}
