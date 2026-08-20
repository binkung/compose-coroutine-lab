package kr.co.coroutine_test.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class LessonTopicCategory(val label: String) {
    ALL("전체"),
    BASICS("기초"),
    FLOW("Flow"),
    ANDROID("Android")
}

data class LessonTopic(
    val title: String,
    val category: LessonTopicCategory
)

data class CombineLessonUiState(
    val query: String = "",
    val selectedCategory: LessonTopicCategory = LessonTopicCategory.ALL,
    val results: List<LessonTopic> = emptyList()
)

class CombineLessonViewModel : ViewModel() {

    private val lessonTopics = listOf(
        LessonTopic("launch와 suspend", LessonTopicCategory.BASICS),
        LessonTopic("Job 시작과 취소", LessonTopicCategory.BASICS),
        LessonTopic("async와 await", LessonTopicCategory.BASICS),
        LessonTopic("Coroutine Dispatcher", LessonTopicCategory.BASICS),
        LessonTopic("예외 처리와 supervisorScope", LessonTopicCategory.BASICS),
        LessonTopic("Flow 기초", LessonTopicCategory.FLOW),
        LessonTopic("Flow 연산자와 debounce", LessonTopicCategory.FLOW),
        LessonTopic("StateFlow와 ViewModel", LessonTopicCategory.ANDROID),
        LessonTopic("SharedFlow와 UI Event", LessonTopicCategory.ANDROID)
    )

    private val query = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow(LessonTopicCategory.ALL)

    val uiState: StateFlow<CombineLessonUiState> = combine(
        query,
        selectedCategory
    ) { currentQuery, currentCategory ->
        createUiState(currentQuery, currentCategory)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = createUiState("", LessonTopicCategory.ALL)
    )

    fun updateQuery(query: String) {
        this.query.value = query
    }

    fun selectCategory(category: LessonTopicCategory) {
        selectedCategory.value = category
    }

    private fun createUiState(
        query: String,
        category: LessonTopicCategory
    ): CombineLessonUiState {
        val normalizedQuery = query.trim()
        val results = lessonTopics.filter { topic ->
            val matchesQuery = normalizedQuery.isEmpty() ||
                topic.title.contains(normalizedQuery, ignoreCase = true)
            val matchesCategory = category == LessonTopicCategory.ALL ||
                topic.category == category

            matchesQuery && matchesCategory
        }

        return CombineLessonUiState(
            query = query,
            selectedCategory = category,
            results = results
        )
    }
}
