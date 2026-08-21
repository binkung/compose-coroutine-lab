package kr.co.coroutine_test.ui.lesson

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RepositoryLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    lessonViewModel: RepositoryLessonViewModel = viewModel()
) {
    val uiState by lessonViewModel.uiState.collectAsStateWithLifecycle()

    val logText = "상태: ${uiState.status.label}" +
        "\n캐시 데이터: ${uiState.topics.size}개" +
        "\n${uiState.message}"

    LessonScreen(
        title = "Repository + API · DB Flow",
        description = "Repository가 API 결과를 캐시에 저장하고 화면은 DAO Flow만 관찰하는 offline-first 구조입니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Text(
            text = "Fake API → Repository → In-memory DAO Flow → ViewModel → Compose",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Fake API와 In-memory DAO는 실제 프로젝트에서 Retrofit과 Room 구현으로 교체할 수 있습니다.",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            onClick = lessonViewModel::refresh
        ) {
            Text("API 성공 → 캐시 갱신")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            onClick = lessonViewModel::refreshWithError
        ) {
            Text("API 실패 → 기존 캐시 유지")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            onClick = lessonViewModel::clearCache
        ) {
            Text("DAO 캐시 삭제")
        }

        HorizontalDivider()

        Text(
            text = "DAO Flow 데이터",
            style = MaterialTheme.typography.titleMedium
        )

        if (uiState.topics.isEmpty()) {
            Text("저장된 데이터가 없습니다.")
        } else {
            uiState.topics.forEach { topic ->
                Text("• ${topic.title} (${topic.category})")
            }
        }
    }
}
