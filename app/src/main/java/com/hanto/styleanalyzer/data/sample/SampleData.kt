package com.hanto.styleanalyzer.data.sample

import com.hanto.styleanalyzer.domain.model.ColorType
import com.hanto.styleanalyzer.domain.model.FashionItem
import com.hanto.styleanalyzer.domain.model.FitType
import com.hanto.styleanalyzer.domain.model.ItemCategory
import com.hanto.styleanalyzer.domain.model.PatternType
import com.hanto.styleanalyzer.domain.model.StyleType

/**
 * 테스트용 샘플 패션 아이템 데이터
 */
object SampleData {

    val sampleFashionItems = listOf(
        FashionItem(
            id = "1",
            imageUrl = "https://picsum.photos/300/400?random=1",
            category = ItemCategory.TOP,
            colors = listOf(ColorType.BLACK, ColorType.WHITE),
            styles = listOf(StyleType.MINIMALIST, StyleType.CASUAL),
            patterns = listOf(PatternType.SOLID),
            fit = FitType.REGULAR,
            tags = listOf("basic", "cotton", "comfortable"),
            brand = "Sample Brand",
            description = "심플한 블랙 티셔츠"
        ),
        FashionItem(
            id = "2",
            imageUrl = "https://picsum.photos/300/400?random=2",
            category = ItemCategory.BOTTOM,
            colors = listOf(ColorType.BLUE),
            styles = listOf(StyleType.CASUAL, StyleType.STREET),
            patterns = listOf(PatternType.SOLID),
            fit = FitType.SLIM,
            tags = listOf("denim", "vintage", "comfortable"),
            brand = "Denim Co",
            description = "클래식 슬림 진"
        ),
        FashionItem(
            id = "3",
            imageUrl = "https://picsum.photos/300/400?random=3",
            category = ItemCategory.DRESS,
            colors = listOf(ColorType.PINK, ColorType.WHITE),
            styles = listOf(StyleType.ROMANTIC, StyleType.ELEGANT),
            patterns = listOf(PatternType.FLORAL),
            fit = FitType.REGULAR,
            tags = listOf("flowy", "feminine", "summer"),
            brand = "Floral Dreams",
            description = "플로럴 원피스"
        ),
        FashionItem(
            id = "4",
            imageUrl = "https://picsum.photos/300/400?random=4",
            category = ItemCategory.OUTER,
            colors = listOf(ColorType.BLACK),
            styles = listOf(StyleType.EDGY, StyleType.STREET),
            patterns = listOf(PatternType.SOLID),
            fit = FitType.OVERSIZED,
            tags = listOf("leather", "bold", "statement"),
            brand = "Edge Wear",
            description = "레더 바이커 재킷"
        ),
        FashionItem(
            id = "5",
            imageUrl = "https://picsum.photos/300/400?random=5",
            category = ItemCategory.TOP,
            colors = listOf(ColorType.BLUE, ColorType.WHITE),
            styles = listOf(StyleType.PREPPY, StyleType.FORMAL),
            patterns = listOf(PatternType.STRIPE),
            fit = FitType.SLIM,
            tags = listOf("button-up", "professional", "cotton"),
            brand = "Classic Shirts",
            description = "스트라이프 셔츠"
        ),
        FashionItem(
            id = "6",
            imageUrl = "https://picsum.photos/300/400?random=6",
            category = ItemCategory.SHOES,
            colors = listOf(ColorType.WHITE),
            styles = listOf(StyleType.CASUAL, StyleType.SPORTY),
            patterns = listOf(PatternType.SOLID),
            fit = FitType.REGULAR,
            tags = listOf("sneakers", "comfortable", "daily"),
            brand = "Sport Plus",
            description = "화이트 스니커즈"
        ),
        FashionItem(
            id = "7",
            imageUrl = "https://picsum.photos/300/400?random=7",
            category = ItemCategory.BAG,
            colors = listOf(ColorType.BROWN),
            styles = listOf(StyleType.VINTAGE, StyleType.BOHEMIAN),
            patterns = listOf(PatternType.SOLID),
            fit = FitType.REGULAR,
            tags = listOf("leather", "crossbody", "vintage"),
            brand = "Leather Craft",
            description = "빈티지 크로스백"
        ),
        FashionItem(
            id = "8",
            imageUrl = "https://picsum.photos/300/400?random=8",
            category = ItemCategory.TOP,
            colors = listOf(ColorType.GREEN),
            styles = listOf(StyleType.BOHEMIAN, StyleType.CASUAL),
            patterns = listOf(PatternType.ABSTRACT),
            fit = FitType.LOOSE,
            tags = listOf("boho", "flowy", "artistic"),
            brand = "Boho Chic",
            description = "보헤미안 블라우스"
        ),
        FashionItem(
            id = "9",
            imageUrl = "https://picsum.photos/300/400?random=9",
            category = ItemCategory.BOTTOM,
            colors = listOf(ColorType.BLACK),
            styles = listOf(StyleType.FORMAL, StyleType.ELEGANT),
            patterns = listOf(PatternType.SOLID),
            fit = FitType.SLIM,
            tags = listOf("tailored", "professional", "wool"),
            brand = "Formal Wear",
            description = "테일러드 슬랙스"
        ),
        FashionItem(
            id = "10",
            imageUrl = "https://picsum.photos/300/400?random=10",
            category = ItemCategory.ACCESSORY,
            colors = listOf(ColorType.GOLD),
            styles = listOf(StyleType.ELEGANT, StyleType.MINIMALIST),
            patterns = listOf(PatternType.SOLID),
            fit = FitType.REGULAR,
            tags = listOf("jewelry", "delicate", "gold"),
            brand = "Fine Jewelry",
            description = "골드 미니멀 목걸이"
        )
    )

    /**
     * 랜덤하게 섞인 패션 아이템 리스트 반환
     */
    fun getShuffledItems(): List<FashionItem> = sampleFashionItems.shuffled()

    /**
     * 특정 카테고리의 아이템만 반환
     */
    fun getItemsByCategory(category: ItemCategory): List<FashionItem> =
        sampleFashionItems.filter { it.category == category }

    /**
     * 특정 스타일의 아이템만 반환
     */
    fun getItemsByStyle(style: StyleType): List<FashionItem> =
        sampleFashionItems.filter { it.styles.contains(style) }
}