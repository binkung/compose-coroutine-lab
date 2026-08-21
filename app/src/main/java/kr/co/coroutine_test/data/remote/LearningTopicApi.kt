package kr.co.coroutine_test.data.remote

import java.io.IOException
import kotlinx.coroutines.delay

data class LearningTopicResponse(
    val id: Int,
    val title: String,
    val category: String
)

interface LearningTopicApi {
    suspend fun fetchTopics(): List<LearningTopicResponse>
}

class FakeLearningTopicApi : LearningTopicApi {

    private var shouldFailNextRequest = false

    fun failNextRequest() {
        shouldFailNextRequest = true
    }

    override suspend fun fetchTopics(): List<LearningTopicResponse> {
        delay(1000)

        if (shouldFailNextRequest) {
            shouldFailNextRequest = false
            throw IOException("네트워크 연결에 실패했습니다.")
        }

        return listOf(
            LearningTopicResponse(1, "Coroutine 기초", "Coroutine"),
            LearningTopicResponse(2, "StateFlow 화면 상태", "Flow"),
            LearningTopicResponse(3, "Repository와 offline-first", "Architecture")
        )
    }
}
