package kr.co.coroutine_test.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

enum class ApiRequestStatus(val label: String) {
    IDLE("대기"),
    LOADING("요청 중"),
    SUCCESS("성공"),
    ERROR("실패")
}

data class ApiRetryLessonUiState(
    val status: ApiRequestStatus = ApiRequestStatus.IDLE,
    val attemptCount: Int = 0,
    val isRunning: Boolean = false,
    val response: String? = null,
    val logText: String = "재시도 방식을 선택해 가짜 API를 호출하세요."
)

class ApiRetryLessonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ApiRetryLessonUiState())
    val uiState: StateFlow<ApiRetryLessonUiState> = _uiState.asStateFlow()

    private var requestJob: Job? = null

    fun runRetryDemo() {
        startAutomaticDemo("retry(2): 최대 두 번 자동 재시도") {
            apiRequestFlow()
                .retry(2) { error ->
                    val shouldRetry = error is IOException
                    appendLog("retry 판단: ${if (shouldRetry) "재시도" else "중단"}")
                    shouldRetry
                }
                .catch { error -> showError(error) }
                .collect { response -> showSuccess(response) }
        }
    }

    fun runRetryWhenDemo() {
        startAutomaticDemo("retryWhen: 재시도 간격을 점점 증가") {
            apiRequestFlow()
                .retryWhen { error, attempt ->
                    val shouldRetry = error is IOException && attempt < 2

                    if (shouldRetry) {
                        val waitMillis = 500L * (1L shl attempt.toInt())
                        appendLog("${waitMillis}ms 후 ${attempt + 1}번째 재시도")
                        delay(waitMillis)
                    }

                    shouldRetry
                }
                .catch { error -> showError(error) }
                .collect { response -> showSuccess(response) }
        }
    }

    fun runTimeoutDemo() {
        startAutomaticDemo("withTimeout: 1초 안에 응답이 없으면 취소") {
            try {
                val response = withTimeout(1000) {
                    apiRequestFlow(responseDelayMillis = 3000).first()
                }
                showSuccess(response)
            } catch (error: TimeoutCancellationException) {
                appendLog("1초 초과: API 요청 취소")
                showError(error, "요청 시간이 초과되었습니다.")
            }
        }
    }

    fun runManualRequest() {
        if (_uiState.value.isRunning) return

        requestJob = viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    status = ApiRequestStatus.LOADING,
                    isRunning = true,
                    response = null,
                    logText = if (state.attemptCount == 0) {
                        "수동 API 요청 시작"
                    } else {
                        state.logText + "\n사용자가 직접 다시 시도"
                    }
                )
            }

            try {
                showSuccess(apiRequestFlow().first())
            } catch (error: IOException) {
                showError(error)
            }

            markFinished()
        }
    }

    fun reset() {
        requestJob?.cancel()
        requestJob = null
        _uiState.value = ApiRetryLessonUiState(
            logText = "요청을 취소하고 실행 기록을 초기화했습니다."
        )
    }

    private fun startAutomaticDemo(
        title: String,
        block: suspend () -> Unit
    ) {
        requestJob?.cancel()
        _uiState.value = ApiRetryLessonUiState(
            status = ApiRequestStatus.LOADING,
            isRunning = true,
            logText = title
        )

        requestJob = viewModelScope.launch {
            block()
            markFinished()
        }
    }

    private fun apiRequestFlow(responseDelayMillis: Long = 600): Flow<String> = flow {
        val attemptNumber = _uiState.value.attemptCount + 1
        _uiState.update { state ->
            state.copy(
                attemptCount = attemptNumber,
                logText = state.logText + "\nAPI 요청 ${attemptNumber}회차"
            )
        }

        delay(responseDelayMillis)

        if (attemptNumber <= 2) {
            appendLog("HTTP 500 오류")
            throw IOException("서버 오류가 발생했습니다.")
        }

        appendLog("API 응답 성공")
        emit("서버 데이터 수신 완료")
    }

    private fun showSuccess(response: String) {
        _uiState.update { state ->
            state.copy(
                status = ApiRequestStatus.SUCCESS,
                response = response,
                logText = state.logText + "\n성공: $response"
            )
        }
    }

    private fun showError(error: Throwable, message: String? = null) {
        val errorMessage = message ?: error.message ?: "알 수 없는 오류"
        _uiState.update { state ->
            state.copy(
                status = ApiRequestStatus.ERROR,
                response = null,
                logText = state.logText + "\n최종 실패: $errorMessage"
            )
        }
    }

    private fun markFinished() {
        _uiState.update { state -> state.copy(isRunning = false) }
    }

    private fun appendLog(message: String) {
        _uiState.update { state ->
            state.copy(logText = state.logText + "\n$message")
        }
    }
}
