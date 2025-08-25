package com.hanto.styleanalyzer.presentation.ui.common.cardstack

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import kotlin.math.absoluteValue

/**
 * 카드 스와이프 방향을 나타내는 열거형
 */
enum class DragDirection { LEFT, RIGHT, UP, DOWN }

/**
 * 카드 스택 내 카드들의 정렬 방식을 정의하는 열거형
 */
enum class CardAlignment { BOTTOM, BOTTOM_START, BOTTOM_END, TOP, TOP_START, TOP_END, START, END }

/**
 * 드래그 가능한 방향을 정의하는 열거형
 */
enum class DragAlignment { VERTICAL, HORIZONTAL, NONE }

/**
 * 속도와 오프셋을 기반으로 스와이프 방향을 결정합니다
 */
internal fun determineSwipeDirection(velocity: Velocity, offset: Offset): DragDirection? {
    val isHorizontal = velocity.x.absoluteValue > velocity.y.absoluteValue
    return when {
        isHorizontal && velocity.x > 0 && offset.x > 0 -> DragDirection.RIGHT
        isHorizontal && velocity.x < 0 && offset.x < 0 -> DragDirection.LEFT
        !isHorizontal && velocity.y > 0 && offset.y > 0 -> DragDirection.DOWN
        !isHorizontal && velocity.y < 0 && offset.y < 0 -> DragDirection.UP
        else -> null
    }
}

/**
 * 스와이프 속도가 임계값을 초과하는지 확인합니다
 */
internal fun isSwipeVelocityExceeded(velocity: Velocity, velocityThresholdPx: Float): Boolean =
    velocity.x.absoluteValue > velocityThresholdPx || velocity.y.absoluteValue > velocityThresholdPx

/**
 * 스와이프 방향에 따른 목표 오프셋을 계산합니다
 */
internal fun calculateTargetOffset(
    direction: DragDirection,
    size: IntSize,
    offset: Offset,
    cardAlignment: CardAlignment
): Offset = when (direction) {
    DragDirection.LEFT -> Offset(-size.width.toFloat() * 1.5f, offset.y)
    DragDirection.RIGHT -> Offset(size.width.toFloat() * 1.5f, offset.y)
    DragDirection.UP -> Offset(offset.x, calculateVerticalOffset(cardAlignment, size, offset))
    DragDirection.DOWN -> Offset(offset.x, size.height.toFloat() * 1.5f)
}

/**
 * 카드 정렬에 따른 세로 오프셋을 계산합니다
 */
internal fun calculateVerticalOffset(
    cardAlignment: CardAlignment,
    size: IntSize,
    offset: Offset
): Float = when (cardAlignment) {
    CardAlignment.BOTTOM, CardAlignment.BOTTOM_START, CardAlignment.BOTTOM_END -> -size.height.toFloat() * 1.5f
    else -> offset.y
}

/**
 * 스와이프 진행도를 -1과 1 사이의 정규화된 값으로 계산합니다
 */
internal fun calculateSwipeProgress(offset: Offset, velocityThresholdPx: Float): Float {
    val xProgress = offset.x / velocityThresholdPx
    val yProgress = offset.y / velocityThresholdPx
    return when {
        xProgress.absoluteValue > yProgress.absoluteValue -> xProgress.coerceIn(-1f, 1f)
        else -> -yProgress.coerceIn(-1f, 1f)
    }
}

/**
 * 스택 내 각 카드의 스케일 팩터를 계산합니다
 */
internal fun calculateScales(index: Int, itemCount: Int, stackDragProgress: Float): Float {
    val baseScale = 1f - (index * 0.05f)
    val scaleIncrement = 0.05f * stackDragProgress
    return when {
        index > 0 -> baseScale + (scaleIncrement * (itemCount - index - 1))
            .coerceAtMost(.05f)

        else -> baseScale
    }
}

/**
 * 스택 내 위치에 따른 카드의 초기 오프셋을 계산합니다
 */
internal fun calculateLastOffset(
    cardAlignment: CardAlignment,
    index: Int,
    spacingPx: Float
): Offset = when (cardAlignment) {
    CardAlignment.TOP -> Offset(0f, spacingPx * index)
    CardAlignment.TOP_START -> Offset(-spacingPx * index, spacingPx * index)
    CardAlignment.TOP_END -> Offset(spacingPx * index, spacingPx * index)
    CardAlignment.BOTTOM -> Offset(0f, -spacingPx * index)
    CardAlignment.BOTTOM_START -> Offset(-spacingPx * index, -spacingPx * index)
    CardAlignment.BOTTOM_END -> Offset(spacingPx * index, -spacingPx * index)
    CardAlignment.START -> Offset(-spacingPx * index, 0f)
    CardAlignment.END -> Offset(spacingPx * index, 0f)
}

/**
 * 드래그 중 스택 구성에 따른 오프셋을 계산합니다
 */
internal fun calculateDragBasedOffset(
    stackDragProgress: Float,
    index: Int,
    spacingPx: Float,
    itemCount: Int,
    cardAlignment: CardAlignment
): Offset {
    val progress = if (index == itemCount - 1 && stackDragProgress < 0f) {
        (-stackDragProgress).coerceIn(0f, 1f)
    } else {
        (stackDragProgress * (1f - (index.toFloat() / itemCount))).coerceIn(-1f, 1f)
    }

    val easedProgress = progress * progress * (3 - 2 * progress)

    return when (cardAlignment) {
        CardAlignment.TOP -> Offset(
            0f,
            spacingPx * easedProgress * if (index == itemCount - 1 && stackDragProgress < 0f) 1f else -1f
        )

        CardAlignment.BOTTOM -> Offset(0f, spacingPx * easedProgress)
        CardAlignment.TOP_START -> Offset(spacingPx * easedProgress, -spacingPx * easedProgress)
        CardAlignment.BOTTOM_START -> Offset(spacingPx * easedProgress, spacingPx * easedProgress)
        CardAlignment.TOP_END -> Offset(-spacingPx * easedProgress, -spacingPx * easedProgress)
        CardAlignment.BOTTOM_END -> Offset(-spacingPx * easedProgress, spacingPx * easedProgress)
        CardAlignment.START -> Offset(spacingPx * easedProgress, 0f)
        CardAlignment.END -> Offset(-spacingPx * easedProgress, 0f)
    }
}

/**
 * 카드 정렬에 따른 변형 조정을 적용합니다
 */
internal fun GraphicsLayerScope.applyAlignmentAdjustment(
    cardAlignment: CardAlignment,
    animatedScale: Float,
    size: Size
) {
    val widthAdjustment = (size.width * (1f - animatedScale)) / 2f
    val heightAdjustment = (size.height * (1f - animatedScale)) / 2f

    when (cardAlignment) {
        CardAlignment.TOP -> translationY += heightAdjustment
        CardAlignment.TOP_START -> {
            translationY += heightAdjustment
            translationX -= widthAdjustment
        }

        CardAlignment.TOP_END -> {
            translationY += heightAdjustment
            translationX += widthAdjustment
        }

        CardAlignment.BOTTOM -> translationY -= heightAdjustment
        CardAlignment.BOTTOM_START -> {
            translationY -= heightAdjustment
            translationX -= widthAdjustment
        }

        CardAlignment.BOTTOM_END -> {
            translationY -= heightAdjustment
            translationX += widthAdjustment
        }

        CardAlignment.START -> translationX -= widthAdjustment
        CardAlignment.END -> translationX += widthAdjustment
    }
}