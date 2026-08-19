package kr.co.coroutine_test.ui.lesson

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.IOException

@Composable
fun FailureLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var logText by remember {
        mutableStateOf("두 버튼을 차례로 실행해 결과를 비교하세요.")
    }

    LessonScreen(
        title = "실패 전파 비교",
        description = "일반 coroutineScope와 supervisorScope에서 형제 작업 B의 결과가 어떻게 달라지는지 확인합니다.",
        logText = logText,
        onBack = onBack,
        modifier = modifier
    ) {
        Text(
            text = "일반 구조: A 실패 → B도 취소",
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    logText = "일반 async 실패 테스트 시작"

                    try {
                        coroutineScope {
                            val failingTask = async<Int> {
                                logText += "\nA 시작"
                                delay(1000)
                                logText += "\nA 실패"
                                throw IOException("A 작업 실패")
                            }

                            val siblingTask = async {
                                logText += "\nB 시작"

                                try {
                                    delay(3000)
                                    logText += "\nB 완료"
                                    20
                                } finally {
                                    logText += "\nB 종료 (A 실패로 취소됨)"
                                }
                            }

                            failingTask.await()
                            siblingTask.await()
                        }
                    } catch (error: IOException) {
                        logText += "\n부모에서 에러 처리: ${error.message}"
                    }
                }
            }
        ) {
            Text("일반 async 실패 테스트")
        }

        HorizontalDivider()

        Text(
            text = "감독 구조: A 실패 → B는 계속 실행",
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    logText = "supervisorScope 테스트 시작"

                    supervisorScope {
                        val failingTask = async<Int> {
                            logText += "\nA 시작"
                            delay(1000)
                            logText += "\nA 실패"
                            throw IOException("A 작업 실패")
                        }

                        val independentTask = async {
                            logText += "\nB 시작"
                            delay(3000)
                            logText += "\nB 완료"
                            20
                        }

                        try {
                            failingTask.await()
                        } catch (error: IOException) {
                            logText += "\nA 에러 처리: ${error.message}"
                        }

                        val resultB = independentTask.await()
                        logText += "\nB 결과: $resultB"
                    }
                }
            }
        ) {
            Text("supervisorScope 테스트")
        }
    }
}
