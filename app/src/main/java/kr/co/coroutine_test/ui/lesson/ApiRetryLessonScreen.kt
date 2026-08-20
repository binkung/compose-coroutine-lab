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
fun ApiRetryLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    lessonViewModel: ApiRetryLessonViewModel = viewModel()
) {
    val uiState by lessonViewModel.uiState.collectAsStateWithLifecycle()

    LessonScreen(
        title = "API 오류 처리 + retry / retryWhen",
        description = "실패하는 API를 자동 또는 수동으로 재시도하고, 오래 걸리는 요청을 제한 시간 후 취소합니다.",
        logText = uiState.logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Text(
            text = "상태: ${uiState.status.label} · 요청 횟수: ${uiState.attemptCount}",
            style = MaterialTheme.typography.titleMedium
        )

        uiState.response?.let { response ->
            Text(
                text = response,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isRunning,
            onClick = lessonViewModel::runRetryDemo
        ) {
            Text("retry(2) 자동 재시도")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isRunning,
            onClick = lessonViewModel::runRetryWhenDemo
        ) {
            Text("retryWhen + backoff")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isRunning,
            onClick = lessonViewModel::runTimeoutDemo
        ) {
            Text("withTimeout 요청")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isRunning,
            onClick = lessonViewModel::runManualRequest
        ) {
            Text("수동 요청 / 다시 시도")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = lessonViewModel::reset
        ) {
            Text("요청 취소 / 초기화")
        }
    }
}
