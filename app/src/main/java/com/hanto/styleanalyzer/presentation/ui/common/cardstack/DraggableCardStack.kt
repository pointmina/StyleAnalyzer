package com.hanto.styleanalyzer.presentation.ui.common.cardstack

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
 * @param items 현재 아이템 리스트 (외부 상태와 동기화)
 * @param height 각 카드의 높이
 * @param cardSpacingRatio 카드 간격 비율 (화면 높이 기준)
 * @param cardAlignment 카드 정렬 방식
 * @param dragAlignment 드래그 가능한 방향
 * @param onSwipeLeft 왼쪽 스와이프 콜백 (싫어요)
 * @param onSwipeRight 오른쪽 스와이프 콜백 (좋아요)
 * @param cardContent 각 카드의 컨텐츠
 */
@Composable
fun <T> DraggableCardStack(
    items: List<T>,
    height: Dp,
    cardSpacingRatio: Float = 0.1f,
    cardAlignment: CardAlignment = CardAlignment.BOTTOM,
    dragAlignment: DragAlignment = DragAlignment.HORIZONTAL,
    onSwipeLeft: ((T) -> Unit)? = null,
    onSwipeRight: ((T) -> Unit)? = null,
    cardContent: @Composable BoxScope.(T) -> Unit
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val cardSpacing = remember(screenHeight) { screenHeight * (cardSpacingRatio * 0.15f) }
    val velocityThreshold = remember(screenWidth, screenHeight) {
        minOf(screenWidth, screenHeight) * 0.3f
    }

    val stackedHeight by remember(items.size, cardSpacing, cardAlignment) {
        derivedStateOf {
            when (cardAlignment) {
                CardAlignment.BOTTOM, CardAlignment.BOTTOM_START, CardAlignment.BOTTOM_END,
                CardAlignment.TOP, CardAlignment.TOP_START, CardAlignment.TOP_END ->
                    height + (cardSpacing * (items.size - 1))

                CardAlignment.START, CardAlignment.END -> height
            }
        }
    }

    val contentAlignment = remember(cardAlignment) {
        when (cardAlignment) {
            CardAlignment.BOTTOM -> Alignment.BottomCenter
            CardAlignment.BOTTOM_START -> Alignment.BottomStart
            CardAlignment.BOTTOM_END -> Alignment.BottomEnd
            CardAlignment.TOP -> Alignment.TopCenter
            CardAlignment.TOP_START -> Alignment.TopStart
            CardAlignment.TOP_END -> Alignment.TopEnd
            CardAlignment.START, CardAlignment.END -> Alignment.Center
        }
    }

    val (stackDragProgress, onDrag) = remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = stackedHeight),
        contentAlignment = contentAlignment
    ) {
        items
            .forEachIndexed { index, item ->
                key("card_${item.hashCode()}") {
                    DraggableCard(
                        density = density,
                        height = height,
                        cardSpacing = cardSpacing,
                        velocityThreshold = velocityThreshold,
                        itemCount = items.size,
                        index = index,
                        cardAlignment = cardAlignment,
                        dragAlignment = dragAlignment,
                        stackDragProgress = stackDragProgress,
                        onDrag = onDrag,
                        onSwipe = { direction ->
                            val currentItem = items.firstOrNull()
                            currentItem?.let {
                                when (direction) {
                                    DragDirection.LEFT -> onSwipeLeft?.invoke(it)
                                    DragDirection.RIGHT -> onSwipeRight?.invoke(it)
                                    else -> { /* 상하 스와이프는 처리하지 않음 */ }
                                }
                            }
                        },
                        content = { cardContent(item) }
                    )
                }
            }
    }
}

/**
 * 개별 드래그 가능한 카드 컴포넌트
 */
@Composable
private fun DraggableCard(
    density: Density,
    height: Dp,
    cardSpacing: Dp,
    velocityThreshold: Dp,
    itemCount: Int,
    index: Int,
    cardAlignment: CardAlignment,
    dragAlignment: DragAlignment,
    stackDragProgress: Float,
    onDrag: (Float) -> Unit,
    onSwipe: (DragDirection) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isAnimating by remember { mutableStateOf(false) }

    val velocityThresholdPx = with(density) { velocityThreshold.toPx() }
    val spacingPx = with(density) { cardSpacing.toPx() }

    val swipeProgress = calculateSwipeProgress(offset, velocityThresholdPx)

    val updatedOnDrag by rememberUpdatedState(onDrag)
    val updatedOnSwipe by rememberUpdatedState(onSwipe)

    val transition = updateTransition(targetState = offset, label = "cardTransition")

    val animatedOffset by transition.animateOffset(
        label = "offsetAnimation",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        targetValueByState = { it }
    )

    val animatedElevation by animateFloatAsState(
        targetValue = if (isAnimating) 8f else 4f,
        animationSpec = tween(durationMillis = 300),
        label = "animatedElevation"
    )

    val targetScale = calculateScales(index, itemCount, stackDragProgress)
    val animatedScale by transition.animateFloat(
        label = "scaleAnimation",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        targetValueByState = { targetScale }
    )

    val lastOffset = calculateLastOffset(cardAlignment, index, spacingPx)
    val dragBasedOffset = calculateDragBasedOffset(
        stackDragProgress, index, spacingPx, itemCount, cardAlignment
    )

    LaunchedEffect(swipeProgress) { updatedOnDrag(swipeProgress) }

    LaunchedEffect(itemCount) {
        offset = Offset.Zero
        isAnimating = false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = 12.dp)
            .zIndex((itemCount - index).toFloat())
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                translationX = animatedOffset.x + lastOffset.x + dragBasedOffset.x
                translationY = animatedOffset.y + lastOffset.y + dragBasedOffset.y
                applyAlignmentAdjustment(cardAlignment, animatedScale, size)
            }
            .shadow(elevation = animatedElevation.dp, shape = RoundedCornerShape(12.dp))
            .then(
                if (!isAnimating && index == 0 && itemCount > 0) {
                    Modifier.pointerInput(itemCount) {
                        val velocityTracker = VelocityTracker()
                        detectDragGestures(
                            onDragStart = {
                                velocityTracker.resetTracking()
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offset += when (dragAlignment) {
                                    DragAlignment.VERTICAL -> Offset(x = 0f, y = dragAmount.y)
                                    DragAlignment.HORIZONTAL -> Offset(x = dragAmount.x, 0f)
                                    DragAlignment.NONE -> Offset(x = dragAmount.x, y = dragAmount.y)
                                }
                                velocityTracker.addPosition(change.uptimeMillis, change.position)
                            },
                            onDragEnd = {
                                val velocity = velocityTracker.calculateVelocity()
                                val direction =
                                    determineSwipeDirection(velocity = velocity, offset = offset)


                                val shouldSwipe = direction != null && (
                                        isSwipeVelocityExceeded(
                                            velocity = velocity,
                                            velocityThresholdPx = velocityThresholdPx
                                        ) ||
                                                (direction == DragDirection.LEFT && offset.x < -size.width * 0.15f) ||
                                                (direction == DragDirection.RIGHT && offset.x > size.width * 0.15f) ||
                                                kotlin.math.abs(offset.y) > size.height * 0.25f
                                        )

                                coroutineScope.launch {
                                    isAnimating = true
                                    if (shouldSwipe) {
                                        val targetOffset = calculateTargetOffset(
                                            direction = direction,
                                            size = size,
                                            offset = offset,
                                            cardAlignment = cardAlignment
                                        )
                                        offset = targetOffset
                                        delay(150)
                                        updatedOnSwipe(direction)
                                        delay(100)
                                    }
                                    offset = Offset.Zero
                                    delay(50)
                                    isAnimating = false
                                }
                            }
                        )
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}