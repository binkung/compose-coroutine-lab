package kr.co.coroutine_test.ui.lesson

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FlowOperatorsLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    lessonViewModel: FlowOperatorsLessonViewModel = viewModel()
) {
    val query by lessonViewModel.query.collectAsStateWithLifecycle()
    val searchState by lessonViewModel.searchState.collectAsStateWithLifecycle(
        initialValue = FlowOperatorsLessonUiState()
    )

    val logText = when {
        searchState.isLoading -> {
            "\"${searchState.searchedQuery}\" 검색 중...\n새 검색어를 입력하면 이전 검색은 취소됩니다."
        }

        searchState.searchedQuery.isEmpty() -> {
            "검색어 입력이 멈추면 0.5초 후 검색을 시작합니다."
        }

        else -> {
            "\"${searchState.searchedQuery}\" 검색 완료\n결과 ${searchState.results.size}개"
        }
    }

    LessonScreen(
        title = "Flow 연산자 + 검색 debounce",
        description = "검색어 Flow를 변환하고 중복을 제거하며, 새 검색이 시작되면 이전 검색을 자동으로 취소합니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Text(
            text = "입력 → debounce → map → distinctUntilChanged → flatMapLatest",
            style = MaterialTheme.typography.bodySmall
        )

        OutlinedTextField(
            value = query,
            onValueChange = lessonViewModel::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("학습 주제 검색") },
            placeholder = { Text("예: Flow") },
            singleLine = true
        )

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = query.isNotEmpty(),
            onClick = lessonViewModel::clearQuery
        ) {
            Text("검색어 지우기")
        }

        HorizontalDivider()

        Text(
            text = "검색 결과",
            style = MaterialTheme.typography.titleMedium
        )

        if (searchState.isLoading) {
            CircularProgressIndicator()
        } else if (searchState.results.isEmpty()) {
            Text("검색 결과가 없습니다.")
        } else {
            searchState.results.forEach { topic ->
                Text("• $topic")
            }
        }
    }
}
