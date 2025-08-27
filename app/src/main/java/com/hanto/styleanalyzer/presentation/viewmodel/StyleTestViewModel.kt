package com.hanto.styleanalyzer.presentation.viewmodel

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

    var currentSession by mutableStateOf(createNewSession())
        private set

    // 표시할 아이템들
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

    /**
     * 새 테스트 세션 생성
     */
    private fun createNewSession(): TestSession {
        return TestSession(
            totalItems = SampleData.sampleFashionItems.size
        )
    }

    /**
     * 좋아요 스와이프 처리
     */
    fun onSwipeRight(item: FashionItem) {
        processSwipeAction(item, SwipeType.LIKE)
    }

    /**
     * 싫어요 스와이프 처리
     */
    fun onSwipeLeft(item: FashionItem) {
        processSwipeAction(item, SwipeType.DISLIKE)
    }

    /**
     * 스와이프 액션 처리 - 핵심 로직
     */
    private fun processSwipeAction(item: FashionItem, action: SwipeType) {
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

        // 표시 아이템에서 제거 (핵심: 실제 제거)
        displayItems = displayItems.filter { it.id != item.id }

        // 완료 체크
        if (displayItems.isEmpty()) {
            currentSession = currentSession.copy(isCompleted = true)
        }
    }
}