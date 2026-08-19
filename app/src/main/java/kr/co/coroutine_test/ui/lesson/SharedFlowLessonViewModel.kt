package kr.co.coroutine_test.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface SharedFlowLessonEvent {
    data class ShowSnackbar(val message: String) : SharedFlowLessonEvent
}

class SharedFlowLessonViewModel : ViewModel() {

    private val _events = MutableSharedFlow<SharedFlowLessonEvent>(replay = 0)
    val events: SharedFlow<SharedFlowLessonEvent> = _events.asSharedFlow()

    fun sendSuccessEvent() {
        sendSnackbarEvent("저장이 완료되었습니다.")
    }

    fun sendErrorEvent() {
        sendSnackbarEvent("오류가 발생했습니다.")
    }

    private fun sendSnackbarEvent(message: String) {
        viewModelScope.launch {
            _events.emit(SharedFlowLessonEvent.ShowSnackbar(message))
        }
    }
}
