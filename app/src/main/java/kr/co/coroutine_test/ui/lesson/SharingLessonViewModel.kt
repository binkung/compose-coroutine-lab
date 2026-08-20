package kr.co.coroutine_test.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SharingLessonUiState(
    val producerStartCount: Int = 0,
    val isRunning: Boolean = false,
    val logText: String = "버튼을 눌러 Cold Flow와 공유 Flow의 생산자 실행 횟수를 비교하세요."
)

class SharingLessonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SharingLessonUiState())
    val uiState: StateFlow<SharingLessonUiState> = _uiState.asStateFlow()

    private var demoJob: Job? = null

    fun runColdFlowDemo() {
        startDemo("Cold Flow를 수집자 두 명이 각각 수집") {
            val coldFlow = countingFlow()

            coroutineScope {
                launch {
                    coldFlow.collect { value ->
                        appendLog("수집자 A ← $value")
                    }
                }

                launch {
                    coldFlow.collect { value ->
                        appendLog("수집자 B ← $value")
                    }
                }
            }
        }
    }

    fun runShareInDemo() {
        startDemo("shareIn Flow를 수집자 두 명이 공유") {
            val sharingScope = CoroutineScope(coroutineContext + SupervisorJob())

            try {
                val sharedFlow = countingFlow().shareIn(
                    scope = sharingScope,
                    started = SharingStarted.Eagerly,
                    replay = 0
                )

                coroutineScope {
                    launch {
                        sharedFlow.take(3).collect { value ->
                            appendLog("수집자 A ← $value")
                        }
                    }

                    launch {
                        sharedFlow.take(3).collect { value ->
                            appendLog("수집자 B ← $value")
                        }
                    }
                }
            } finally {
                sharingScope.cancel()
            }
        }
    }

    fun runStateInDemo() {
        startDemo("stateIn이 최신 값을 보관") {
            val stateScope = CoroutineScope(coroutineContext + SupervisorJob())

            try {
                val stateFlow = countingFlow().stateIn(
                    scope = stateScope,
                    started = SharingStarted.Eagerly,
                    initialValue = 0
                )

                appendLog("stateIn 초기값: ${stateFlow.value}")
                delay(1100)

                val latestValue = stateFlow.first()
                appendLog("늦게 연결한 수집자가 즉시 받은 최신 값: $latestValue")
            } finally {
                stateScope.cancel()
            }
        }
    }

    fun reset() {
        demoJob?.cancel()
        demoJob = null
        _uiState.value = SharingLessonUiState(
            logText = "실행을 중지하고 로그를 초기화했습니다."
        )
    }

    private fun startDemo(
        title: String,
        block: suspend CoroutineScope.() -> Unit
    ) {
        demoJob?.cancel()
        _uiState.value = SharingLessonUiState(
            isRunning = true,
            logText = title
        )

        demoJob = viewModelScope.launch {
            block()
            _uiState.update { state ->
                state.copy(
                    isRunning = false,
                    logText = state.logText + "\n비교 완료"
                )
            }
        }
    }

    private fun countingFlow(): Flow<Int> = flow {
        val producerNumber = _uiState.value.producerStartCount + 1
        _uiState.update { state ->
            state.copy(
                producerStartCount = producerNumber,
                logText = state.logText + "\n생산자 #$producerNumber 시작"
            )
        }

        for (value in 1..3) {
            delay(500)
            appendLog("생산자 #$producerNumber → $value 방출")
            emit(value)
        }
    }

    private fun appendLog(message: String) {
        _uiState.update { state ->
            state.copy(logText = state.logText + "\n$message")
        }
    }
}
