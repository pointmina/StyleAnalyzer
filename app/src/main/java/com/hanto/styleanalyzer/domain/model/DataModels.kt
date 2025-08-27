package com.hanto.styleanalyzer.domain.model

import android.os.Parcelable
import com.hanto.styleanalyzer.domain.util.IdGenerator.generateActionId
import com.hanto.styleanalyzer.domain.util.IdGenerator.generateSessionId
import kotlinx.parcelize.Parcelize

/**
 * 패션 아이템 데이터 모델
 */
@Parcelize
data class FashionItem(
    val id: String,
    val imageUrl: String,
    val category: ItemCategory,
    val colors: List<ColorType>,
    val styles: List<StyleType>,
    val patterns: List<PatternType>,
    val fit: FitType,
    val tags: List<String> = emptyList(),
    val brand: String? = null,
    val description: String? = null
) : Parcelable

/**
 * 사용자의 스타일 프로필
 */
@Parcelize
data class StyleProfile(
    val userId: String,
    val colorPreferences: Map<ColorType, Float>,
    val styleScores: Map<StyleType, Float>,
    val itemPreferences: Map<ItemCategory, Float>,
    val patternScores: Map<PatternType, Float>,
    val fitPreferences: Map<FitType, Float>,
    val uniquenessScore: Float,
    val consistencyScore: Float,
    val totalItemsAnalyzed: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

/**
 * 스타일 테스트 결과
 */
@Parcelize
data class StyleTestResult(
    val testId: String,
    val profile: StyleProfile,
    val styleDNA: String,
    val personalityDescription: String,
    val topColorPreferences: List<ColorType>,
    val topStylePreferences: List<StyleType>,
    val recommendedCategories: List<ItemCategory>,
    val completedAt: Long = System.currentTimeMillis()
) : Parcelable

/**
 * 아이템 카테고리
 */
enum class ItemCategory {
    TOP, BOTTOM, OUTER, DRESS, SHOES, BAG, ACCESSORY, HAT
}

/**
 * 색상 타입
 */
enum class ColorType {
    BLACK, WHITE, GRAY, RED, BLUE, GREEN, YELLOW, ORANGE,
    PURPLE, PINK, BROWN, BEIGE, NAVY, KHAKI, MINT, CORAL, GOLD, SILVER
}

/**
 * 스타일 타입
 */
enum class StyleType {
    CASUAL, FORMAL, STREET, VINTAGE, MINIMALIST, BOHEMIAN,
    SPORTY, ELEGANT, EDGY, ROMANTIC, PREPPY, GRUNGE
}

/**
 * 패턴 타입
 */
enum class PatternType {
    SOLID, STRIPE, CHECK, FLORAL, GEOMETRIC, ANIMAL_PRINT,
    POLKA_DOT, PLAID, ABSTRACT, TRIBAL, PAISLEY
}

/**
 * 핏 타입
 */
enum class FitType {
    SLIM, REGULAR, LOOSE, OVERSIZED, CROPPED, WIDE
}

/**
 * 스와이프 타입
 */
enum class SwipeType {
    LIKE, DISLIKE
}

/**
 * 스와이프 액션 결과
 */
@Parcelize
data class SwipeAction(
    val id: String = generateActionId(),
    val itemId: String,
    val item: FashionItem,
    val action: SwipeType,
    val sessionId: String,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable

/**
 * 스타일 테스트 세션
 */
@Parcelize
data class TestSession(
    val sessionId: String = generateSessionId(),
    val totalItems: Int,
    val swipeActions: List<SwipeAction> = emptyList(),
    val startTime: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
) : Parcelable {

    /**
     * 완료된 아이템 수
     */
    val completedCount: Int get() = swipeActions.size

    /**
     * 좋아요 수
     */
    val likeCount: Int get() = swipeActions.count { it.action == SwipeType.LIKE }

    /**
     * 싫어요 수
     */
    val dislikeCount: Int get() = swipeActions.count { it.action == SwipeType.DISLIKE }
}