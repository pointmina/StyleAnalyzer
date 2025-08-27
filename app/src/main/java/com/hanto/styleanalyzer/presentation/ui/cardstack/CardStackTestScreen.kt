package com.hanto.styleanalyzer.presentation.ui.cardstack

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hanto.styleanalyzer.R
import com.hanto.styleanalyzer.data.sample.SampleData
import com.hanto.styleanalyzer.domain.model.FashionItem
import com.hanto.styleanalyzer.presentation.ui.common.cardstack.CardAlignment
import com.hanto.styleanalyzer.presentation.ui.common.cardstack.DragAlignment
import com.hanto.styleanalyzer.presentation.ui.common.cardstack.DraggableCardStack

/**
 * CardStack 테스트 화면
 * 패션 아이템들을 스와이프할 수 있는 카드 스택으로 표시합니다
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CardStackTestScreen(
    modifier: Modifier = Modifier
) {
    var likeCount by remember { mutableIntStateOf(0) }
    var dislikeCount by remember { mutableIntStateOf(0) }
    var currentItems by remember { mutableStateOf(SampleData.getShuffledItems()) }
    var lastSwipedItem by remember { mutableStateOf<FashionItem?>(null) }

    // 간단한 반응형 설정
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 360
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val horizontalPadding = if (isSmallScreen) 12.dp else 16.dp
    val cardHeight = when {
        isLandscape -> 280.dp
        isSmallScreen -> 250.dp
        else -> 320.dp
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 메인 컨텐츠
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = horizontalPadding,
                    vertical = 16.dp
                )
                .padding(bottom = 80.dp), // 하단 도움말 공간 확보
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (isSmallScreen) 16.dp else 20.dp)
        ) {
            // 헤더
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "StyleAnalyzer",
                    style = if (isSmallScreen) {
                        MaterialTheme.typography.headlineMedium
                    } else {
                        MaterialTheme.typography.headlineLarge
                    },
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                if (!isSmallScreen) {
                    Text(
                        text = "좌우로 스와이프하여 선호도를 표시하세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 통계 표시
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatCard(
                    icon = Icons.Default.FavoriteBorder,
                    count = dislikeCount,
                    label = "싫어요",
                    color = MaterialTheme.colorScheme.error,
                    isCompact = isSmallScreen
                )
                StatCard(
                    icon = Icons.Default.Favorite,
                    count = likeCount,
                    label = "좋아요",
                    color = MaterialTheme.colorScheme.primary,
                    isCompact = isSmallScreen
                )
            }

            // 마지막 스와이프한 아이템 정보 (작은 화면에서는 숨김)
            if (!isSmallScreen && lastSwipedItem != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "마지막 선택",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = lastSwipedItem!!.description ?: "패션 아이템",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "${lastSwipedItem!!.category.name} • ${
                                lastSwipedItem!!.styles.joinToString(
                                    ", "
                                )
                            }",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // CardStack - 남은 공간을 모두 사용
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 핵심: 남은 공간을 모두 차지
                contentAlignment = Alignment.Center
            ) {
                if (currentItems.isNotEmpty()) {
                    DraggableCardStack(
                        initialItems = currentItems,
                        height = cardHeight,
                        cardSpacingRatio = if (isSmallScreen) 0.08f else 0.1f,
                        cardAlignment = CardAlignment.BOTTOM,
                        dragAlignment = DragAlignment.HORIZONTAL,
                        onSwipeLeft = { item ->
                            dislikeCount++
                            lastSwipedItem = item
                        },
                        onSwipeRight = { item ->
                            likeCount++
                            lastSwipedItem = item
                        }
                    ) { fashionItem ->
                        FashionItemCard(
                            fashionItem = fashionItem,
                            isCompact = isSmallScreen
                        )
                    }
                } else {
                    // 완료 화면
                    CompletionCard(
                        likeCount = likeCount,
                        dislikeCount = dislikeCount,
                        cardHeight = cardHeight,
                        isCompact = isSmallScreen
                    )
                }
            }
        }

        // 하단 고정 도움말 카드
        HelpCard(
            isCompact = isSmallScreen,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = horizontalPadding)
                .padding(bottom = 16.dp)
        )
    }
}

/**
 * 간단한 통계 카드
 */
@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    color: Color,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(if (isCompact) 12.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
            )
            Text(
                text = count.toString(),
                style = if (isCompact) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleLarge
                },
                color = color,
                fontWeight = FontWeight.Bold
            )
            if (!isCompact) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = color
                )
            }
        }
    }
}

/**
 * 간단한 패션 아이템 카드
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun FashionItemCard(
    fashionItem: FashionItem,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    val cornerRadius = if (isCompact) 10.dp else 12.dp

    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GlideImage(
                model = fashionItem.imageUrl,
                contentDescription = fashionItem.description,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius)),
                contentScale = ContentScale.Crop
            )

            // 하단 정보 오버레이
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = cornerRadius,
                    bottomEnd = cornerRadius
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (isCompact) 12.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = fashionItem.description ?: "패션 아이템",
                        style = if (isCompact) {
                            MaterialTheme.typography.titleSmall
                        } else {
                            MaterialTheme.typography.titleMedium
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    if (!isCompact) {
                        Text(
                            text = "${fashionItem.category.name} • ${fashionItem.brand ?: "브랜드"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = fashionItem.styles.joinToString(" • "),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 완료 카드
 */
@Composable
private fun CompletionCard(
    likeCount: Int,
    dislikeCount: Int,
    cardHeight: Dp,
    isCompact: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) 16.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "모든 아이템을 확인했습니다!",
                style = if (isCompact) {
                    MaterialTheme.typography.headlineSmall
                } else {
                    MaterialTheme.typography.headlineMedium
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "좋아요: ${likeCount}개\n싫어요: ${dislikeCount}개",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 하단 고정 도움말 카드
 */
@Composable
private fun HelpCard(
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 12.dp else 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.dislike),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(if (isCompact) 16.dp else 18.dp)
                )
                Text(
                    text = "← 싫어요",
                    style = if (isCompact) {
                        MaterialTheme.typography.labelSmall
                    } else {
                        MaterialTheme.typography.labelMedium
                    },
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }

            if (!isCompact) {
                Text(
                    text = stringResource(R.string.swipe_to_select),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
            ) {
                Text(
                    text = "좋아요 →",
                    style = if (isCompact) {
                        MaterialTheme.typography.labelSmall
                    } else {
                        MaterialTheme.typography.labelMedium
                    },
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.like),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(if (isCompact) 16.dp else 18.dp)
                )
            }
        }
    }
}