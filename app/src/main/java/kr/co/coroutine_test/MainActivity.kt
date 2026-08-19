package kr.co.coroutine_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import kr.co.coroutine_test.ui.lesson.CoroutineLearningApp
import kr.co.coroutine_test.ui.theme.Coroutine_testTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Coroutine_testTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CoroutineLearningApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
