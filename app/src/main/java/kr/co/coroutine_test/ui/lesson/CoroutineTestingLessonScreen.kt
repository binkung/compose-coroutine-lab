package kr.co.coroutine_test.ui.lesson

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CoroutineTestingLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    LessonScreen(
        title = "Coroutine · Flow 테스트",
        description = "가상 시간을 사용해 delay, retry, timeout이 있는 코루틴을 실제로 기다리지 않고 검증합니다.",
        logText = "./gradlew :app:testDebugUnitTest\n13번의 retry·backoff·timeout·수동 재시도 테스트 4개를 실행합니다.",
        onBack = onBack,
        modifier = modifier
    ) {
        Text(
            text = "runTest",
            style = MaterialTheme.typography.titleMedium
        )
        Text("코루틴 테스트용 Scope와 가상 시간 스케줄러를 제공합니다.")

        HorizontalDivider()

        Text(
            text = "MainDispatcherRule",
            style = MaterialTheme.typography.titleMedium
        )
        Text("ViewModel의 Dispatchers.Main을 StandardTestDispatcher로 교체합니다.")

        HorizontalDivider()

        Text(
            text = "advanceTimeBy / advanceUntilIdle",
            style = MaterialTheme.typography.titleMedium
        )
        Text("delay를 실제로 기다리지 않고 가상 시간을 이동해 결과를 확인합니다.")

        HorizontalDivider()

        Text(
            text = "검증 대상",
            style = MaterialTheme.typography.titleMedium
        )
        Text("StateFlow의 상태, API 요청 횟수, backoff 시간, timeout 결과를 검증합니다.")
    }
}
