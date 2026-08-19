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
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AsyncLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var logText by remember {
        mutableStateOf("두 작업은 각각 2초가 걸리지만 동시에 실행됩니다.")
    }

    LessonScreen(
        title = "async / await",
        description = "async는 결과가 필요한 작업을 시작하고, await는 그 결과를 기다립니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    logText = "두 계산 시작"

                    val firstResult = async {
                        delay(2000)
                        10
                    }

                    val secondResult = async {
                        delay(2000)
                        20
                    }

                    val result = firstResult.await() + secondResult.await()
                    logText += "\n결과: $result"
                }
            }
        ) {
            Text("async 동시 계산")
        }
    }
}
