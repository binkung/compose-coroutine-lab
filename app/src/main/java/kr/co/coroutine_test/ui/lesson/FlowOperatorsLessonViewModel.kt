package kr.co.coroutine_test.ui.lesson

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList

data class FlowOperatorsLessonUiState(
    val searchedQuery: String = "",
    val isLoading: Boolean = false,
    val results: List<String> = emptyList()
)

class FlowOperatorsLessonViewModel : ViewModel() {

    private val lessonTopics = listOf(
        "launch와 suspend",
        "Job 시작과 취소",
        "async와 await",
        "Coroutine Dispatcher",
        "예외 처리",
        "supervisorScope",
        "Flow",
        "StateFlow와 ViewModel",
        "SharedFlow와 UI Event"
        )

        private val _query = MutableStateFlow("")
        val query = _query.asStateFlow()

        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        val searchState: Flow<FlowOperatorsLessonUiState> = _query
            .debounce(500)
            .map { query -> query.trim() }
            .distinctUntilChanged()
            .flatMapLatest(::searchTopics)
            .onStart {
                emit(FlowOperatorsLessonUiState(results = lessonTopics))
            }

        fun updateQuery(query: String) {
        _query.value = query
    }

    fun clearQuery() {
        _query.value = ""
    }

    private fun searchTopics(query: String): Flow<FlowOperatorsLessonUiState> {
        if (query.isEmpty()) {
            return flowOf(FlowOperatorsLessonUiState(results = lessonTopics))
        }

        return flow {
            emit(
                FlowOperatorsLessonUiState(
                    searchedQuery = query,
                    isLoading = true
                )
            )

            delay(1000)

            val results = lessonTopics
                .asFlow()
                .filter { topic -> topic.contains(query, ignoreCase = true) }
                .toList()

            emit(
                FlowOperatorsLessonUiState(
                    searchedQuery = query,
                    results = results
                )
            )
        }
    }
}
