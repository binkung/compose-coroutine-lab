package kr.co.coroutine_test.ui.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class CoroutineLesson(
    val title: String,
    val description: String
) {
    BASICS(
        title = "1. launch / suspend / delay",
        description = "여러 코루틴이 동시에 실행되는 순서를 확인합니다."
    ),
    JOB(
        title = "2. Job 시작과 취소",
        description = "실행 중인 코루틴을 Job으로 취소합니다."
    ),
    ASYNC(
        title = "3. async / await",
        description = "두 작업을 동시에 실행하고 결과를 합칩니다."
    ),
    DISPATCHERS(
        title = "4. IO / Default Dispatcher",
        description = "작업 종류에 맞는 스레드에서 실행합니다."
    ),
    ERROR(
        title = "5. try-catch",
        description = "suspend 함수의 실패를 안전하게 처리합니다."
    ),
    FAILURE(
        title = "6. 실패 전파 / supervisorScope",
        description = "한 작업의 실패가 형제 작업에 미치는 영향을 비교합니다."
    ),
    FLOW(
        title = "7. Flow",
        description = "시간에 따라 여러 값이 전달되는 흐름을 확인합니다."
    )
}

@Composable
fun CoroutineLearningApp(
    modifier: Modifier = Modifier
) {
    var selectedLesson by remember {
        mutableStateOf<CoroutineLesson?>(null)
    }

    BackHandler(enabled = selectedLesson != null) {
        selectedLesson = null
    }

    val openMenu = { selectedLesson = null }

    when (selectedLesson) {
        null -> LessonMenuScreen(
            modifier = modifier,
            onLessonClick = { selectedLesson = it }
        )

        CoroutineLesson.BASICS -> BasicsLessonScreen(modifier, openMenu)
        CoroutineLesson.JOB -> JobLessonScreen(modifier, openMenu)
        CoroutineLesson.ASYNC -> AsyncLessonScreen(modifier, openMenu)
        CoroutineLesson.DISPATCHERS -> DispatcherLessonScreen(modifier, openMenu)
        CoroutineLesson.ERROR -> ErrorLessonScreen(modifier, openMenu)
        CoroutineLesson.FAILURE -> FailureLessonScreen(modifier, openMenu)
        CoroutineLesson.FLOW -> FlowLessonScreen(modifier, openMenu)
    }
}

@Composable
private fun LessonMenuScreen(
    modifier: Modifier = Modifier,
    onLessonClick: (CoroutineLesson) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Coroutine Test",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "하나씩 눌러서 주제별로 연습해보세요.",
            style = MaterialTheme.typography.bodyLarge
        )

        CoroutineLesson.entries.forEach { lesson ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = lesson.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onLessonClick(lesson) }
                    ) {
                        Text("열기")
                    }
                }
            }
        }
    }
}
