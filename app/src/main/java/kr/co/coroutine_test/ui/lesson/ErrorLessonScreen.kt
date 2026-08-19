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
import java.io.IOException

@Composable
fun ErrorLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var logText by remember {
        mutableStateOf("실패하는 suspend 함수를 try-catch로 처리합니다.")
    }

    LessonScreen(
        title = "try-catch",
        description = "예상할 수 있는 실패를 처리하면 앱을 종료하지 않고 오류 상태를 보여줄 수 있습니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    try {
                        logText = "사용자 조회 중"
                        val user = getUserFail()
                        logText = "사용자: $user"
                    } catch (error: IOException) {
                        logText = "에러를 안전하게 처리함: ${error.message}"
                    }
                }
            }
        ) {
            Text("try-catch 에러 처리")
        }
    }
}

private suspend fun getUserFail(): String {
    delay(2000)
    throw IOException("서버 오류 발생")
}
