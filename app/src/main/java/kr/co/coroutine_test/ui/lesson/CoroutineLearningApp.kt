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
    ),
    STATE_FLOW(
        title = "8. StateFlow + ViewModel",
        description = "화면 상태를 ViewModel에 보관하고 Lifecycle에 맞춰 수집합니다."
    ),
    SHARED_FLOW(
        title = "9. SharedFlow + UI Event",
        description = "Snackbar처럼 한 번만 처리할 UI 이벤트를 전달합니다."
    ),
    FLOW_OPERATORS(
        title = "10. Flow 연산자 + 검색 debounce",
        description = "검색어 흐름을 변환하고 이전 검색을 자동으로 취소합니다."
    ),
    COMBINE(
        title = "11. combine + 여러 화면 상태",
        description = "여러 StateFlow를 하나의 UI 상태로 조합합니다."
    ),
    SHARING(
        title = "12. Cold Flow vs stateIn / shareIn",
        description = "여러 수집자가 하나의 생산자를 공유하는 방법을 비교합니다."
    ),
    API_RETRY(
        title = "13. API 오류 처리 + retry / retryWhen",
        description = "실패한 요청을 자동으로 재시도하고 제한 시간 후 취소합니다."
    ),
    COROUTINE_TESTING(
        title = "14. Coroutine · Flow 테스트",
        description = "가상 시간으로 retry, delay, timeout을 빠르게 검증합니다."
    ),
    REPOSITORY(
        title = "15. Repository + API · DB Flow",
        description = "API 결과를 캐시에 저장하고 DAO Flow로 화면을 갱신합니다."
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
        CoroutineLesson.STATE_FLOW -> StateFlowLessonScreen(modifier, openMenu)
        CoroutineLesson.SHARED_FLOW -> SharedFlowLessonScreen(modifier, openMenu)
        CoroutineLesson.FLOW_OPERATORS -> FlowOperatorsLessonScreen(modifier, openMenu)
        CoroutineLesson.COMBINE -> CombineLessonScreen(modifier, openMenu)
        CoroutineLesson.SHARING -> SharingLessonScreen(modifier, openMenu)
        CoroutineLesson.API_RETRY -> ApiRetryLessonScreen(modifier, openMenu)
        CoroutineLesson.COROUTINE_TESTING -> CoroutineTestingLessonScreen(modifier, openMenu)
        CoroutineLesson.REPOSITORY -> RepositoryLessonScreen(modifier, openMenu)
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
