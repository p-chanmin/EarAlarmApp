package com.dev.earalarm.feature.setting

import app.cash.turbine.test
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.core.model.TimerAlarmInfo
import com.dev.earalarm.feature.timer.TimerViewModel
import com.dev.testing.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TimerViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val timerRepository: TimerRepository = mockk(relaxed = true)
    private lateinit var timerViewModel: TimerViewModel

    @Test
    fun `저장된 타이머 데이터가 있으면 true를 반환한다`() = runTest {

        // Given
        coEvery { timerRepository.alarmInfo } returns flowOf(fakeTimerAlarmInfo)
        timerViewModel = TimerViewModel(timerRepository)

        // When
        timerViewModel.hasTimer.test {

            // Then
            val actual = awaitItem()
            assertTrue(actual)
        }
    }

    @Test
    fun `저장된 타이머 데이터가 없으면 false를 반환한다`() = runTest {

        // Given
        coEvery { timerRepository.alarmInfo } returns flowOf(null)
        timerViewModel = TimerViewModel(timerRepository)


        // When
        timerViewModel.hasTimer.test {

            // Then
            val actual = awaitItem()
            assertFalse(actual)
        }
    }

    companion object {
        val fakeTimerAlarmInfo = TimerAlarmInfo(
            minute = 10,
            startTime = "2025-05-01T00:00:00.000000Z",
            endTime = "2025-05-02T00:00:00.000000Z"
        )
    }
}