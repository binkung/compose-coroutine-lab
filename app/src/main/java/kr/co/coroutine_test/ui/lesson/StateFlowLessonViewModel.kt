package kr.co.coroutine_test.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StateFlowLessonUiState(
    val count: Int = 0,
    val isRunning: Boolean = false,
    val logText: String = "ViewModel의 StateFlow가 화면 상태를 보관합니다."
)

class StateFlowLessonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StateFlowLessonUiState())
    val uiState: StateFlow<StateFlowLessonUiState> = _uiState.asStateFlow()

    private var countJob: Job? = null

    fun startCounting() {
        countJob?.cancel()

        countJob = viewModelScope.launch {
            _uiState.value = StateFlowLessonUiState(
                isRunning = true,
                logText = "카운트 시작"
            )

            repeat(5) {
                delay(1000)
                _uiState.update { state ->
                    val nextCount = state.count + 1
                    state.copy(
                        count = nextCount,
                        logText = state.logText + "\nStateFlow 값: $nextCount"
                    )
                }
            }

            _uiState.update { state ->
                state.copy(
                    isRunning = false,
                    logText = state.logText + "\n카운트 완료"
                )
            }
        }
    }

    fun reset() {
        countJob?.cancel()
        countJob = null
        _uiState.value = StateFlowLessonUiState(
            logText = "카운트를 초기화했습니다."
        )
    }
}
