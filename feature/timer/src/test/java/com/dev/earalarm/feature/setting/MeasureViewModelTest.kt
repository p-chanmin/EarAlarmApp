package com.dev.earalarm.feature.setting

import app.cash.turbine.test
import com.dev.earalarm.core.alarm.EarAlarmManager
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.core.model.TimerAlarmInfo
import com.dev.earalarm.feature.timer.measure.MeasureViewModel
import com.dev.earalarm.feature.timer.model.MeasureUiState
import com.dev.testing.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale
import kotlin.test.assertEquals

internal class MeasureViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private val timerRepository: TimerRepository = mockk(relaxed = true)
    private val alarmManager: EarAlarmManager = mockk(relaxed = true)
    private lateinit var measureViewModel: MeasureViewModel

    @Before
    fun setUp() {
        mockkStatic(ZonedDateTime::class)
        mockkStatic(ZoneId::class)

        every { ZoneId.systemDefault() } returns ZoneId.of("Asia/Seoul")
        every { ZonedDateTime.now() } returns ZonedDateTime.parse("2025-05-01T00:00:00.000000Z")
        Locale.setDefault(Locale.US)
    }

    @Test
    fun `타이머의 남은 시간을 실시간으로 확인할 수 있다`() = runTest {

        // Given
        coEvery { timerRepository.alarmInfo } returns flowOf(fakeTimerAlarmInfo)
        measureViewModel = MeasureViewModel(timerRepository, alarmManager)

        // When
        measureViewModel.measureUiState.test {

            // Then
            var uiState = awaitItem()

            assertEquals(
                MeasureUiState(
                    minute = fakeTimerAlarmInfo.minute,
                    startTime = ZonedDateTime.parse(fakeTimerAlarmInfo.startTime),
                    endTime = ZonedDateTime.parse(fakeTimerAlarmInfo.endTime),
                    endTimeString = "AM 10:40",
                    progress = 0f,
                    leftTime = "01:40:00"
                ),
                uiState
            )

            // When
            every { ZonedDateTime.now() } returns ZonedDateTime.parse("2025-05-01T00:50:00.000000Z")

            // Then
            uiState = awaitItem()
            assertEquals(0.5f, uiState.progress)
            assertEquals("00:50:00", uiState.leftTime)

            // When
            every { ZonedDateTime.now() } returns ZonedDateTime.parse("2025-05-01T01:10:00.000000Z")

            // Then
            uiState = awaitItem()
            assertEquals(0.7f, uiState.progress)
            assertEquals("00:30:00", uiState.leftTime)

            // When
            every { ZonedDateTime.now() } returns ZonedDateTime.parse("2025-05-01T01:30:00.000000Z")

            // Then
            uiState = awaitItem()
            assertEquals(0.9f, uiState.progress)
            assertEquals("00:10:00", uiState.leftTime)

            // When
            every { ZonedDateTime.now() } returns ZonedDateTime.parse("2025-05-01T01:40:00.000000Z")

            // Then
            uiState = awaitItem()
            assertEquals(1.0f, uiState.progress)
            assertEquals("00:00:00", uiState.leftTime)
        }
    }

    @Test
    fun `타이머를 해제할 수 있다`() = runTest {

        // Given
        val flow = MutableStateFlow<TimerAlarmInfo?>(fakeTimerAlarmInfo)
        coEvery { timerRepository.alarmInfo } returns flow
        coEvery { alarmManager.cancelTimerAlarm() } answers {
            flow.update { null }
        }

        measureViewModel = MeasureViewModel(timerRepository, alarmManager)

        measureViewModel.measureUiState.test {
            var uiState = awaitItem()
            assertEquals(
                MeasureUiState(
                    minute = fakeTimerAlarmInfo.minute,
                    startTime = ZonedDateTime.parse(fakeTimerAlarmInfo.startTime),
                    endTime = ZonedDateTime.parse(fakeTimerAlarmInfo.endTime),
                    endTimeString = "AM 10:40",
                    progress = 0f,
                    leftTime = "01:40:00"
                ),
                uiState
            )

            // When
            measureViewModel.dismissTimerAlarm()

            // Then
            uiState = awaitItem()
            assertEquals(MeasureUiState(), uiState)
        }
    }


    companion object {
        val fakeTimerAlarmInfo = TimerAlarmInfo(
            minute = 100,
            startTime = "2025-05-01T00:00:00.000000Z",
            endTime = "2025-05-01T01:40:00.000000Z"
        )
    }
}