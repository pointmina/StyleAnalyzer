package com.hanto.styleanalyzer.domain.util

import java.util.UUID

/**
 * 고유 ID 생성을 위한 유틸리티 클래스
 */
object IdGenerator {

    /**
     * 테스트 세션 ID 생성
     * 형식: session_timestamp_random
     */
    fun generateSessionId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "session_${timestamp}_${random}"
    }

    /**
     * 스와이프 액션 ID 생성
     * 형식: action_timestamp_random
     */
    fun generateActionId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "action_${timestamp}_${random}"
    }

    /**
     * UUID 기반 고유 ID 생성
     * 더 안전한 고유성이 필요할 때 사용
     */
    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}