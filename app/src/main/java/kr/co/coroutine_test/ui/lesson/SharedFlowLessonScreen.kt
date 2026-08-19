package kr.co.coroutine_test.ui.lesson

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SharedFlowLessonScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    lessonViewModel: SharedFlowLessonViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lessonViewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            lessonViewModel.events.collect { event ->
                when (event) {
                    is SharedFlowLessonEvent.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LessonScreen(
            title = "SharedFlow + UI Event",
            description = "SharedFlow로 Snackbar처럼 한 번만 처리할 UI 이벤트를 전달합니다.",
            logText = "SharedFlow는 현재 상태를 보관하지 않습니다. 버튼을 여러 번 누르면 같은 이벤트도 매번 전달됩니다.",
            onBack = onBack,
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = lessonViewModel::sendSuccessEvent
            ) {
                Text("성공 이벤트 보내기")
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = lessonViewModel::sendErrorEvent
            ) {
                Text("오류 이벤트 보내기")
            }
        }
    }
}
