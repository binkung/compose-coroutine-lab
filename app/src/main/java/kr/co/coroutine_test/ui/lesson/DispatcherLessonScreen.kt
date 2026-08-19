package kr.co.coroutine_test.ui.lesson

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DispatcherLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var logText by remember {
        mutableStateOf("IO 작업과 CPU 계산을 각각 실행해보세요.")
    }

    LessonScreen(
        title = "IO / Default Dispatcher",
        description = "파일·네트워크처럼 기다리는 작업은 IO, 무거운 계산은 Default가 적합합니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    logText = "IO 작업 시작"

                    withContext(Dispatchers.IO) {
                        Thread.sleep(3000)
                    }

                    logText += "\nIO 작업 완료"
                }
            }
        ) {
            Text("Dispatchers.IO 테스트")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    logText = "CPU 계산 시작"

                    val result = withContext(Dispatchers.Default) {
                        heavyCalculation()
                    }

                    logText = "계산 완료\n$result"
                }
            }
        ) {
            Text("Dispatchers.Default 계산")
        }
    }
}

private fun heavyCalculation(): Long {
    var result = 0L

    for (number in 0..200_000_000L) {
        result += number
    }

    return result
}
