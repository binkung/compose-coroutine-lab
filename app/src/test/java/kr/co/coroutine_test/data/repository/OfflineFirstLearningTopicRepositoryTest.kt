package kr.co.coroutine_test.data.repository

import java.io.IOException
import kr.co.coroutine_test.data.local.InMemoryLearningTopicDao
import kr.co.coroutine_test.data.remote.FakeLearningTopicApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineFirstLearningTopicRepositoryTest {

    @Test
    fun 초기에는_DAO에저장된_캐시를전달한다() = runTest {
        val repository = createRepository()

        val topics = repository.observeTopics().first()

        assertEquals(1, topics.size)
        assertEquals("캐시에 저장된 오프라인 학습", topics.first().title)
    }

    @Test
    fun API새로고침에성공하면_DAO_Flow가갱신된다() = runTest {
        val repository = createRepository()

        repository.refresh()
        val topics = repository.observeTopics().first()

        assertEquals(3, topics.size)
        assertTrue(topics.any { topic -> topic.title.contains("Repository") })
    }

    @Test
    fun API새로고침에실패하면_기존캐시를유지한다() = runTest {
        val api = FakeLearningTopicApi()
        val repository = createRepository(api)
        val cachedTopics = repository.observeTopics().first()
        api.failNextRequest()

        var errorThrown = false
        try {
            repository.refresh()
        } catch (_: IOException) {
            errorThrown = true
        }

        assertTrue(errorThrown)
        assertEquals(cachedTopics, repository.observeTopics().first())
    }

    private fun createRepository(
        api: FakeLearningTopicApi = FakeLearningTopicApi()
    ) = OfflineFirstLearningTopicRepository(
        api = api,
        dao = InMemoryLearningTopicDao()
    )
}
