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
fun SharingLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    lessonViewModel: SharingLessonViewModel = viewModel()
) {
    val uiState by lessonViewModel.uiState.collectAsStateWithLifecycle()

    LessonScreen(
        title = "Cold Flow vs stateIn / shareIn",
        description = "Cold Flow의 중복 실행과 shareIn의 생산자 공유, stateIn의 최신 값 보관을 비교합니다.",
        logText = uiState.logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Text(
            text = "생산자 시작 횟수: ${uiState.producerStartCount}",
            style = MaterialTheme.typography.titleLarge
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isRunning,
            onClick = lessonViewModel::runColdFlowDemo
        ) {
            Text("Cold Flow 두 번 수집")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isRunning,
            onClick = lessonViewModel::runShareInDemo
        ) {
            Text("shareIn으로 생산자 공유")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isRunning,
            onClick = lessonViewModel::runStateInDemo
        ) {
            Text("stateIn 최신 값 확인")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = lessonViewModel::reset
        ) {
            Text("실행 중지 / 초기화")
        }
    }
}
