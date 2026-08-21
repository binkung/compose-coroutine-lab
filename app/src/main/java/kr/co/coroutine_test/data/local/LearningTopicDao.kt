package kr.co.coroutine_test.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LearningTopicEntity(
    val id: Int,
    val title: String,
    val category: String
)

interface LearningTopicDao {
    fun observeTopics(): Flow<List<LearningTopicEntity>>

    suspend fun replaceAll(topics: List<LearningTopicEntity>)

    suspend fun clear()
}

class InMemoryLearningTopicDao(
    initialTopics: List<LearningTopicEntity> = listOf(
        LearningTopicEntity(0, "캐시에 저장된 오프라인 학습", "Cache")
    )
) : LearningTopicDao {

    private val topics = MutableStateFlow(initialTopics)

    override fun observeTopics(): Flow<List<LearningTopicEntity>> =
        topics.asStateFlow()

    override suspend fun replaceAll(topics: List<LearningTopicEntity>) {
        this.topics.value = topics
    }

    override suspend fun clear() {
        topics.value = emptyList()
    }
}
