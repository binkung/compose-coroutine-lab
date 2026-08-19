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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Composable
fun FlowLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var logText by remember {
        mutableStateOf("Flow를 시작하면 1초마다 새로운 값이 도착합니다.")
    }

    var flowJob by remember {
        mutableStateOf<Job?>(null)
    }

    LessonScreen(
        title = "Flow",
        description = "suspend 함수가 결과 하나를 반환한다면, Flow는 시간에 따라 여러 값을 전달할 수 있습니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                flowJob?.cancel()

                flowJob = scope.launch {
                    logText = "Flow 수집 시작"

                    countFlow().collect { value ->
                        logText += "\n현재 값: $value"
                    }

                    logText += "\nFlow 수집 완료"
                }
            }
        ) {
            Text("Flow 시작")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                flowJob?.cancel()
                logText += "\nFlow 수집 취소"
            }
        ) {
            Text("Flow 취소")
        }
    }
}

private fun countFlow(): Flow<Int> = flow {
    for (number in 1..5) {
        delay(1000)
        emit(number)
    }
}
