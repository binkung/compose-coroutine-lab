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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BasicsLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var logText by remember {
        mutableStateOf("버튼을 눌러 실행 순서를 확인하세요.")
    }

    LessonScreen(
        title = "launch / suspend / delay",
        description = "launch로 여러 코루틴을 시작하면 delay 중에 다른 작업이 먼저 실행될 수 있습니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                logText = "동시 실행 시작"

                scope.launch {
                    logText += "\nA 시작"
                    delay(3000)
                    logText += "\nA 완료"
                }

                scope.launch {
                    logText += "\nB 시작"
                    delay(1000)
                    logText += "\nB 완료"
                }

                scope.launch {
                    logText += "\n사용자 조회"
                    val user = getUser()
                    logText += "\n사용자: $user"
                }
            }
        ) {
            Text("동시 실행 테스트")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    logText = "delay 시작"
                    delay(3000)
                    logText += "\ndelay 완료"
                }
            }
        ) {
            Text("delay 테스트")
        }
    }
}

private suspend fun getUser(): String {
    delay(2000)
    return "유저1"
}
