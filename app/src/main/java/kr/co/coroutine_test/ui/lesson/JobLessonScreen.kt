package kr.co.coroutine_test.ui.lesson

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JobLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var logText by remember {
        mutableStateOf("Job을 시작한 뒤 완료되기 전에 취소해보세요.")
    }

    var job by remember {
        mutableStateOf<Job?>(null)
    }

    LessonScreen(
        title = "Job 시작과 취소",
        description = "launch가 반환한 Job을 보관하면 실행 중인 코루틴을 취소할 수 있습니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                job?.cancel()

                job = scope.launch {
                    logText = "Job 작업 시작"
                    delay(5000)
                    logText = "Job 작업 완료"
                }
            }
        ) {
            Text("Job 시작")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                job?.cancel()
                logText = "Job 취소됨"
            }
        ) {
            Text("Job 취소")
        }
    }
}
