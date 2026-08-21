package kr.co.coroutine_test.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kr.co.coroutine_test.data.local.InMemoryLearningTopicDao
import kr.co.coroutine_test.data.model.LearningTopic
import kr.co.coroutine_test.data.remote.FakeLearningTopicApi
import kr.co.coroutine_test.data.repository.LearningTopicRepository
import kr.co.coroutine_test.data.repository.OfflineFirstLearningTopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class RepositoryRefreshStatus(val label: String) {
    IDLE("캐시 관찰 중"),
    LOADING("API 요청 중"),
    SUCCESS("캐시 갱신 완료"),
    ERROR("API 실패 · 캐시 유지")
}

data class RepositoryLessonUiState(
    val topics: List<LearningTopic> = emptyList(),
    val status: RepositoryRefreshStatus = RepositoryRefreshStatus.IDLE,
    val message: String = "화면은 API가 아니라 DAO의 Flow를 관찰합니다."
) {
    val isLoading: Boolean
        get() = status == RepositoryRefreshStatus.LOADING
}

private data class RefreshState(
    val status: RepositoryRefreshStatus = RepositoryRefreshStatus.IDLE,
    val message: String = "화면은 API가 아니라 DAO의 Flow를 관찰합니다."
)

class RepositoryLessonViewModel : ViewModel() {

    private val api = FakeLearningTopicApi()
    private val repository: LearningTopicRepository =
        OfflineFirstLearningTopicRepository(
            api = api,
            dao = InMemoryLearningTopicDao()
        )

    private val refreshState = MutableStateFlow(RefreshState())

    val uiState: StateFlow<RepositoryLessonUiState> = combine(
        repository.observeTopics(),
        refreshState
    ) { topics, refresh ->
        RepositoryLessonUiState(
            topics = topics,
            status = refresh.status,
            message = refresh.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RepositoryLessonUiState()
    )

    fun refresh() {
        refreshFromApi(simulateFailure = false)
    }

    fun refreshWithError() {
        refreshFromApi(simulateFailure = true)
    }

    fun clearCache() {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            repository.clearCache()
            refreshState.value = RefreshState(
                status = RepositoryRefreshStatus.IDLE,
                message = "DAO 캐시를 삭제했습니다."
            )
        }
    }

    private fun refreshFromApi(simulateFailure: Boolean) {
        if (uiState.value.isLoading) return

        if (simulateFailure) {
            api.failNextRequest()
        }

        viewModelScope.launch {
            refreshState.value = RefreshState(
                status = RepositoryRefreshStatus.LOADING,
                message = "Repository가 API 데이터를 요청합니다."
            )

            try {
                repository.refresh()
                refreshState.value = RefreshState(
                    status = RepositoryRefreshStatus.SUCCESS,
                    message = "API 결과를 DAO에 저장했습니다. DAO Flow가 화면을 자동 갱신합니다."
                )
            } catch (error: IOException) {
                refreshState.value = RefreshState(
                    status = RepositoryRefreshStatus.ERROR,
                    message = "${error.message}\nDAO 데이터는 변경하지 않아 기존 캐시가 유지됩니다."
                )
            }
        }
    }
}
