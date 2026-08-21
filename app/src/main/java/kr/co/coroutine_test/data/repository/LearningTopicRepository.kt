package kr.co.coroutine_test.data.repository

import kr.co.coroutine_test.data.local.LearningTopicDao
import kr.co.coroutine_test.data.local.LearningTopicEntity
import kr.co.coroutine_test.data.model.LearningTopic
import kr.co.coroutine_test.data.remote.LearningTopicApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LearningTopicRepository {
    fun observeTopics(): Flow<List<LearningTopic>>

    suspend fun refresh()

    suspend fun clearCache()
}

class OfflineFirstLearningTopicRepository(
    private val api: LearningTopicApi,
    private val dao: LearningTopicDao
) : LearningTopicRepository {

    override fun observeTopics(): Flow<List<LearningTopic>> =
        dao.observeTopics().map { entities ->
            entities.map { entity -> entity.toModel() }
        }

    override suspend fun refresh() {
        val responses = api.fetchTopics()
        val entities = responses.map { response ->
            LearningTopicEntity(
                id = response.id,
                title = response.title,
                category = response.category
            )
        }
        dao.replaceAll(entities)
    }

    override suspend fun clearCache() {
        dao.clear()
    }

    private fun LearningTopicEntity.toModel() = LearningTopic(
        id = id,
        title = title,
        category = category
    )
}
