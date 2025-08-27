package com.hanto.styleanalyzer.presentation.ui.cardstack

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hanto.styleanalyzer.domain.model.FashionItem
import com.hanto.styleanalyzer.presentation.ui.common.cardstack.CardAlignment
import com.hanto.styleanalyzer.presentation.ui.common.cardstack.DragAlignment
import com.hanto.styleanalyzer.presentation.ui.common.cardstack.DraggableCardStack
import com.hanto.styleanalyzer.presentation.ui.theme.MinimalColors
import com.hanto.styleanalyzer.presentation.viewmodel.StyleTestViewModel


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CardStackTestScreen(
    modifier: Modifier = Modifier,
    viewModel: StyleTestViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val currentItems = viewModel.displayItems
    val currentSession = viewModel.currentSession
    var lastSwipedItem by remember { mutableStateOf<FashionItem?>(null) }

    // 반응형 설정
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 360
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val horizontalPadding = if (isSmallScreen) 16.dp else 20.dp
    val cardHeight = when {
        isLandscape -> 320.dp
        isSmallScreen -> 280.dp
        else -> 360.dp
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = horizontalPadding,
                    vertical = 20.dp
                )
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (isSmallScreen) 20.dp else 24.dp)
        ) {
            // 헤더
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "StyleAnalyzer",
                    style = if (isSmallScreen) {
                        MaterialTheme.typography.headlineLarge
                    } else {
                        MaterialTheme.typography.displaySmall
                    },
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                if (!isSmallScreen) {
                    Text(
                        text = "당신의 패션 취향을 분석합니다",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 통계 표시
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatCard(
                    icon = Icons.Default.Close,
                    count = currentSession.dislikeCount,
                    label = "PASS",
                    color = MinimalColors.AccentRed,
                    isCompact = isSmallScreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Favorite,
                    count = currentSession.likeCount,
                    label = "LIKE",
                    color = MinimalColors.AccentGreen,
                    isCompact = isSmallScreen,
                    modifier = Modifier.weight(1f)
                )
            }

            // 진행률 표시
            if (!isSmallScreen) {
                val totalItems = currentSession.totalItems
                val processedItems = currentSession.completedCount
                val progress = if (totalItems > 0) processedItems.toFloat() / totalItems else 0f

                ProgressIndicator(
                    progress = progress,
                    processedItems = processedItems,
                    totalItems = totalItems
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (currentItems.isNotEmpty()) {
                    DraggableCardStack(
                        items = currentItems,
                        height = cardHeight,
                        cardSpacingRatio = if (isSmallScreen) 0.08f else 0.1f,
                        cardAlignment = CardAlignment.BOTTOM,
                        dragAlignment = DragAlignment.HORIZONTAL,
                        onSwipeLeft = { item ->
                            lastSwipedItem = item
                            viewModel.onSwipeLeft(item)
                        },
                        onSwipeRight = { item ->
                            lastSwipedItem = item
                            viewModel.onSwipeRight(item)
                        }
                    ) { fashionItem ->
                        FashionCard(
                            fashionItem = fashionItem,
                            isCompact = isSmallScreen
                        )
                    }
                } else {
                    // 완료 화면
                    CompletionCard(
                        likeCount = currentSession.likeCount,
                        dislikeCount = currentSession.dislikeCount,
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
                .padding(bottom = 20.dp)
        )
    }
}

/**
 * 통계 카드
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (isCompact) 20.dp else 24.dp,
                    vertical = if (isCompact) 12.dp else 16.dp
                ),
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
                    MaterialTheme.typography.titleLarge
                } else {
                    MaterialTheme.typography.headlineSmall
                },
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = if (isCompact) {
                    MaterialTheme.typography.labelMedium
                } else {
                    MaterialTheme.typography.labelLarge
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 진행률 표시 컴포넌트
 */
@Composable
private fun ProgressIndicator(
    progress: Float,
    processedItems: Int,
    totalItems: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 진행률 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(2.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        Text(
            text = "${processedItems}/${totalItems}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 패션 아이템 카드
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun FashionCard(
    fashionItem: FashionItem,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    val cornerRadius = if (isCompact) 12.dp else 16.dp

    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 이미지
            GlideImage(
                model = fashionItem.imageUrl,
                contentDescription = fashionItem.description,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius)),
                contentScale = ContentScale.Crop
            )

            // 하단 정보 오버레이
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        color = Color.Black.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = cornerRadius,
                            bottomEnd = cornerRadius
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (isCompact) 16.dp else 20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = fashionItem.description ?: "Fashion Item",
                        style = if (isCompact) {
                            MaterialTheme.typography.titleMedium
                        } else {
                            MaterialTheme.typography.titleLarge
                        },
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (!isCompact) {
                        Text(
                            text = fashionItem.brand ?: "BRAND",
                            style = MaterialTheme.typography.labelLarge,
                            color = MinimalColors.SoftGray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fashionItem.category.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier
                                .background(
                                    color = MinimalColors.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        if (!isCompact && fashionItem.styles.isNotEmpty()) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = fashionItem.styles.first().name,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
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
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isCompact) 20.dp else 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "STYLE TEST",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "COMPLETED",
                    style = if (isCompact) {
                        MaterialTheme.typography.headlineMedium
                    } else {
                        MaterialTheme.typography.headlineLarge
                    },
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dislikeCount.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MinimalColors.AccentRed,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "PASS",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "/",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = likeCount.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MinimalColors.AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "LIKES",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (!isCompact) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "분석 결과를 확인해보세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * 하단 도움말 카드
 */
@Composable
private fun HelpCard(
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isCompact) 16.dp else 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 왼쪽 스와이프 안내
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Pass",
                        tint = MinimalColors.AccentRed,
                        modifier = Modifier
                            .size(if (isCompact) 18.dp else 20.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(2.dp)
                    )
                    Text(
                        text = if (isCompact) "PASS" else "← PASS",
                        style = if (isCompact) {
                            MaterialTheme.typography.labelMedium
                        } else {
                            MaterialTheme.typography.labelLarge
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 중앙 구분선
                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = if (isCompact) 20.dp else 24.dp)
                        .background(Color.White.copy(alpha = 0.3f))
                )

                // 오른쪽 스와이프 안내
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
                ) {
                    Text(
                        text = if (isCompact) "LIKE" else "LIKE →",
                        style = if (isCompact) {
                            MaterialTheme.typography.labelMedium
                        } else {
                            MaterialTheme.typography.labelLarge
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = MinimalColors.AccentGreen,
                        modifier = Modifier
                            .size(if (isCompact) 18.dp else 20.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}