package com.dev.earalarm.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.dev.earalarm.core.model.TimerAlarmInfo
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.rules.TemporaryFolder
import java.io.File

internal class TimerRepositoryTest : StringSpec() {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var tempFolder: TemporaryFolder

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var timerRepository: TimerRepository

    init {

        beforeSpec {
            testDispatcher = StandardTestDispatcher()
            tempFolder = TemporaryFolder.builder().assureDeletion().build().apply { create() }

            dataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(testDispatcher),
                produceFile = { tempFolder.newFile("test.preferences_pb") }
            )
            timerRepository = TimerRepository(dataStore)
        }

        afterSpec {
            tempFolder.delete()
        }

        "alarmVolume 초기 상태 테스트" {
            runTest(testDispatcher) {
                // Given

                // When
                timerRepository.alarmVolume.test {

                    // Then
                    awaitItem() shouldBe TimerRepository.DEFAULT_VOLUME_SIZE
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "media 초기 상태 테스트" {
            runTest(testDispatcher) {
                // Given

                // When
                timerRepository.media.test {

                    // Then
                    awaitItem() shouldBe null
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "vibrate 초기 상태 테스트" {
            runTest(testDispatcher) {
                // Given

                // When
                timerRepository.vibrate.test {

                    // Then
                    awaitItem() shouldBe true
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarmInfo 초기 상태 테스트" {
            runTest(testDispatcher) {
                // Given

                // When
                timerRepository.alarmInfo.test {

                    // Then
                    awaitItem() shouldBe null
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarmVolume 저장 및 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                val result = 100
                timerRepository.setAlarmVolume(result)

                // When
                timerRepository.alarmVolume.test {

                    // Then
                    awaitItem() shouldBe result
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "media 저장 및 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                val result = "/files/test.m4a"
                timerRepository.setMediaPath(result)

                // When
                timerRepository.media.test {

                    // Then
                    awaitItem() shouldBe File(result)
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "media 삭제 및 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                timerRepository.removeMediaPath()

                // When
                timerRepository.media.test {

                    // Then
                    awaitItem() shouldBe null
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "vibrate 저장 및 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                timerRepository.setVibrate(false)

                // When
                timerRepository.vibrate.test {

                    // Then
                    awaitItem() shouldBe false
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarmInfo 저장 및 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                val result = TimerAlarmInfo(
                    minute = 60,
                    startTime = "2025-05-02T11:09:35.545010Z",
                    endTime = "2025-05-02T12:09:35.544822Z"
                )
                timerRepository.setTimerAlarmInfo(result)

                // When
                timerRepository.alarmInfo.test {

                    // Then
                    awaitItem() shouldBe result
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "alarmInfo 삭제 및 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                timerRepository.removeTimerAlarmInfo()

                // When
                timerRepository.alarmInfo.test {

                    // Then
                    awaitItem() shouldBe null
                    cancelAndConsumeRemainingEvents()
                }
            }
        }
    }
}