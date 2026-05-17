package com.example.myapplication.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.AuthEntry
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

/**
 * 认证条目卡片 - 模仿 Google Authenticator 的条目布局
 * 每个条目显示：图标首字母 | 发行者+账号名 | 验证码 | 倒计时进度条
 */
@Composable
fun AuthEntryCard(
    entry: AuthEntry,
    modifier: Modifier = Modifier
) {
    var remainingProgress by remember { mutableFloatStateOf(entry.remainingSeconds / 30f) }
    val animatedProgress by animateFloatAsState(
        targetValue = remainingProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress_animation"
    )

    // 模拟倒计时
    LaunchedEffect(entry.id) {
        while (remainingProgress > 0f) {
            delay(1000)
            remainingProgress = (remainingProgress - 1f / 30f).coerceAtLeast(0f)
        }
    }

    val progressColor = when {
        remainingProgress > 0.5f -> Color(0xFF4CAF50) // 绿色
        remainingProgress > 0.25f -> Color(0xFFFF9800) // 橙色
        else -> Color(0xFFF44336) // 红色
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 图标首字母（圆形背景）
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = entry.iconInitial,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 发行者和账号名
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = entry.issuer,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = entry.accountName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 验证码
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = entry.code,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${entry.remainingSeconds}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 倒计时进度条
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(MaterialTheme.shapes.small),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthEntryCardPreview() {
    MyApplicationTheme {
        AuthEntryCard(
            entry = AuthEntry(
                id = 1,
                issuer = "Google",
                accountName = "user@gmail.com",
                code = "482 901",
                remainingSeconds = 15,
                iconInitial = "G"
            )
        )
    }
}