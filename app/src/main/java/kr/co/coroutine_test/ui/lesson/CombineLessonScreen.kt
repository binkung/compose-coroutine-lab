package kr.co.coroutine_test.ui.lesson

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CombineLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    lessonViewModel: CombineLessonViewModel = viewModel()
) {
    val uiState by lessonViewModel.uiState.collectAsStateWithLifecycle()

    val queryText = uiState.query.ifBlank { "없음" }
    val logText = "검색어: $queryText" +
        "\n카테고리: ${uiState.selectedCategory.label}" +
        "\ncombine 결과: ${uiState.results.size}개"

    LessonScreen(
        title = "combine + 여러 화면 상태",
        description = "검색어와 카테고리 StateFlow를 combine하고 결과를 하나의 UI 상태로 만듭니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Text(
            text = "query + category → combine → stateIn(WhileSubscribed)",
            style = MaterialTheme.typography.bodySmall
        )

        OutlinedTextField(
            value = uiState.query,
            onValueChange = lessonViewModel::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("학습 주제 검색") },
            placeholder = { Text("예: Flow") },
            singleLine = true
        )

        Text(
            text = "카테고리",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LessonTopicCategory.entries.forEach { category ->
                FilterChip(
                    selected = uiState.selectedCategory == category,
                    onClick = { lessonViewModel.selectCategory(category) },
                    label = { Text(category.label) }
                )
            }
        }

        HorizontalDivider()

        Text(
            text = "조합 결과",
            style = MaterialTheme.typography.titleMedium
        )

        if (uiState.results.isEmpty()) {
            Text("조건에 맞는 학습 주제가 없습니다.")
        } else {
            uiState.results.forEach { topic ->
                Text("• ${topic.title} (${topic.category.label})")
            }
        }
    }
}
