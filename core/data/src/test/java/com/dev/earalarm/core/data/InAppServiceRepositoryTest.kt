package com.dev.earalarm.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.rules.TemporaryFolder

internal class InAppServiceRepositoryTest : StringSpec() {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var tempFolder: TemporaryFolder

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var inAppServiceRepository: InAppServiceRepository

    init {

        beforeSpec {
            testDispatcher = StandardTestDispatcher()
            tempFolder = TemporaryFolder.builder().assureDeletion().build().apply { create() }

            dataStore = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(testDispatcher),
                produceFile = { tempFolder.newFile("inAppTest.preferences_pb") }
            )
            inAppServiceRepository = InAppServiceRepository(dataStore)
        }

        afterSpec {
            tempFolder.delete()
        }

        "lastReviewDate 초기 상태 테스트" {
            runTest(testDispatcher) {
                // Given

                // When
                inAppServiceRepository.lastReviewDate.test {

                    // Then
                    awaitItem() shouldBe null
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "rejectFlexibleUpdateDate 초기 상태 테스트" {
            runTest(testDispatcher) {
                // Given

                // When
                inAppServiceRepository.rejectFlexibleUpdateDate.test {

                    // Then
                    awaitItem() shouldBe null
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "lastReviewDate 저장 및 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                val result = "2025-05-02T11:09:35.545010Z"
                inAppServiceRepository.setLastReviewDate(result)

                // When
                inAppServiceRepository.lastReviewDate.test {

                    // Then
                    awaitItem() shouldBe result
                    cancelAndConsumeRemainingEvents()
                }
            }
        }

        "rejectFlexibleUpdateDate 저장 및 조회 테스트" {
            runTest(testDispatcher) {
                // Given
                val result = "2025-05-02T11:09:35.545010Z"
                inAppServiceRepository.setRejectFlexibleUpdateDate(result)

                // When
                inAppServiceRepository.rejectFlexibleUpdateDate.test {

                    // Then
                    awaitItem() shouldBe result
                    cancelAndConsumeRemainingEvents()
                }
            }
        }
    }
}