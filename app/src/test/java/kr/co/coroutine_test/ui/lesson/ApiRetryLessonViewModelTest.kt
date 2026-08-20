package kr.co.coroutine_test.ui.lesson

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ApiRetryLessonViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun retry2_두번실패후_세번째요청에성공한다() = runTest {
        val viewModel = ApiRetryLessonViewModel()

        viewModel.runRetryDemo()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ApiRequestStatus.SUCCESS, state.status)
        assertEquals(3, state.attemptCount)
        assertFalse(state.isRunning)
        assertEquals("서버 데이터 수신 완료", state.response)
    }

    @Test
    fun retryWhen_backoff를_가상시간으로즉시검증한다() = runTest {
        val viewModel = ApiRetryLessonViewModel()

        viewModel.runRetryWhenDemo()
        advanceUntilIdle()

        assertEquals(3300, currentTime)
        assertEquals(ApiRequestStatus.SUCCESS, viewModel.uiState.value.status)
        assertTrue(viewModel.uiState.value.logText.contains("500ms 후"))
        assertTrue(viewModel.uiState.value.logText.contains("1000ms 후"))
    }

    @Test
    fun withTimeout_1초가지나면_요청을실패처리한다() = runTest {
        val viewModel = ApiRetryLessonViewModel()

        viewModel.runTimeoutDemo()
        advanceTimeBy(999)
        runCurrent()
        assertEquals(ApiRequestStatus.LOADING, viewModel.uiState.value.status)

        advanceTimeBy(1)
        runCurrent()

        assertEquals(ApiRequestStatus.ERROR, viewModel.uiState.value.status)
        assertFalse(viewModel.uiState.value.isRunning)
        assertTrue(viewModel.uiState.value.logText.contains("요청 시간이 초과되었습니다."))
    }

    @Test
    fun 수동재시도_세번째요청에성공한다() = runTest {
        val viewModel = ApiRetryLessonViewModel()

        repeat(2) {
            viewModel.runManualRequest()
            advanceUntilIdle()
            assertEquals(ApiRequestStatus.ERROR, viewModel.uiState.value.status)
        }

        viewModel.runManualRequest()
        advanceUntilIdle()

        assertEquals(ApiRequestStatus.SUCCESS, viewModel.uiState.value.status)
        assertEquals(3, viewModel.uiState.value.attemptCount)
    }
}
