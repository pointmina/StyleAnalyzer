package com.hanto.styleanalyzer.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hanto.styleanalyzer.data.sample.SampleData
import com.hanto.styleanalyzer.domain.model.FashionItem
import com.hanto.styleanalyzer.domain.model.SwipeAction
import com.hanto.styleanalyzer.domain.model.SwipeType
import com.hanto.styleanalyzer.domain.model.TestSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StyleTestViewModel @Inject constructor() : ViewModel() {

    private val TAG = "StyleTestViewModel"

    var currentSession by mutableStateOf(createNewSession())
        private set

    var displayItems by mutableStateOf(SampleData.getShuffledItems())
        private set

    // 테스트 완료 여부
    val isCompleted: Boolean
        get() = displayItems.isEmpty()

    // 좋아요 개수
    val likeCount: Int
        get() = currentSession.likeCount

    // 싫어요 개수
    val dislikeCount: Int
        get() = currentSession.dislikeCount

    init {
        Log.d(TAG, "ViewModel 초기화 - 총 아이템 수: ${displayItems.size}")
        displayItems.forEachIndexed { index, item ->
            Log.d(TAG, "아이템 [$index]: ${item.description} (ID: ${item.id})")
        }
    }

    /**
     * 새 테스트 세션 생성
     */
    private fun createNewSession(): TestSession {
        return TestSession(
            totalItems = SampleData.sampleFashionItems.size
        )
    }

    /**
     * 좋아요 스와이프 처리 - 로깅 추가
     */
    fun onSwipeRight(item: FashionItem) {
        Log.d(TAG, "좋아요 스와이프 - 아이템: ${item.description} (ID: ${item.id})")
        Log.d(TAG, "스와이프 전 남은 아이템 수: ${displayItems.size}")
        processSwipeAction(item, SwipeType.LIKE)
        Log.d(TAG, "스와이프 후 남은 아이템 수: ${displayItems.size}")
    }

    /**
     * 싫어요 스와이프 처리 - 로깅 추가
     */
    fun onSwipeLeft(item: FashionItem) {
        Log.d(TAG, "싫어요 스와이프 - 아이템: ${item.description} (ID: ${item.id})")
        Log.d(TAG, "스와이프 전 남은 아이템 수: ${displayItems.size}")
        processSwipeAction(item, SwipeType.DISLIKE)
        Log.d(TAG, "스와이프 후 남은 아이템 수: ${displayItems.size}")
    }

    /**
     * 스와이프 액션 처리 - 개선된 로직 및 로깅
     */
    private fun processSwipeAction(item: FashionItem, action: SwipeType) {
        try {
            // 현재 상태 로그
            Log.d(TAG, "processSwipeAction 시작")
            Log.d(TAG, "처리할 아이템 ID: ${item.id}")
            Log.d(TAG, "현재 displayItems: ${displayItems.map { "${it.id}-${it.description}" }}")

            // 스와이프 액션 생성
            val swipeAction = SwipeAction(
                itemId = item.id,
                item = item,
                action = action,
                sessionId = currentSession.sessionId
            )

            // 세션에 액션 추가
            currentSession = currentSession.copy(
                swipeActions = currentSession.swipeActions + swipeAction
            )

            val itemsBeforeRemoval = displayItems.size
            displayItems = displayItems.filterNot { it.id == item.id }
            val itemsAfterRemoval = displayItems.size

            // 완료 상태 체크
            if (displayItems.isEmpty()) {
                currentSession = currentSession.copy(isCompleted = true)
                Log.d(TAG, "테스트 완료!")
            } else {
                Log.d(TAG, "다음 아이템: ${displayItems.firstOrNull()?.description}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "스와이프 처리 중 오류 발생", e)
        }
    }

    /**
     * 테스트 재시작 기능
     */
    fun restartTest() {
        Log.d(TAG, "테스트 재시작")
        displayItems = SampleData.getShuffledItems()
        currentSession = createNewSession()
        Log.d(TAG, "재시작 완료 - 아이템 수: ${displayItems.size}")
    }

    fun getDebugInfo(): String {
        return """
            현재 상태:
            - 남은 아이템: ${displayItems.size}개
            - 처리된 아이템: ${currentSession.completedCount}개
            - 좋아요: ${currentSession.likeCount}개
            - 싫어요: ${currentSession.dislikeCount}개
            - 완료 여부: ${isCompleted}
            - 다음 아이템: ${displayItems.firstOrNull()?.description ?: "없음"}
        """.trimIndent()
    }
}