package kr.co.coroutine_test.ui.lesson

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StateFlowLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    lessonViewModel: StateFlowLessonViewModel = viewModel()
) {
    val uiState by lessonViewModel.uiState.collectAsStateWithLifecycle()

    LessonScreen(
        title = "StateFlow + ViewModel",
        description = "ViewModel의 StateFlow로 화면 상태를 관리하고 Lifecycle에 맞춰 안전하게 수집합니다.",
        logText = uiState.logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Text(
            text = "현재 카운트: ${uiState.count}",
            style = MaterialTheme.typography.titleLarge
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isRunning,
            onClick = lessonViewModel::startCounting
        ) {
            Text(if (uiState.isRunning) "카운트 진행 중" else "카운트 시작")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = lessonViewModel::reset
        ) {
            Text("초기화")
        }
    }
}
