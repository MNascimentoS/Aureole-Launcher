package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class FavoriteContainerRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: FavoriteContainerRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = FavoriteContainerRepository(context)
    }

    @Test
    fun testIndependentFavoriteContainers() = runBlocking {
        val containerA = "container_a"
        val containerB = "container_b"

        val appsA = listOf("com.example.app1", "com.example.app2")
        val appsB = listOf("com.example.app2", "com.example.app3")

        repository.updateContainerFavorites(containerA, appsA)
        repository.updateContainerFavorites(containerB, appsB)

        val resultA = repository.getFavoritesForContainer(containerA)
        val resultB = repository.getFavoritesForContainer(containerB)

        assertEquals(appsA, resultA)
        assertEquals(appsB, resultB)
    }

    @Test
    fun testReorderContainerFavoritesIsIsolated() = runBlocking {
        val containerA = "container_a"
        val containerB = "container_b"

        val initialApps = listOf("com.example.app1", "com.example.app2")
        repository.updateContainerFavorites(containerA, initialApps)
        repository.updateContainerFavorites(containerB, initialApps)

        val reorderedAppsA = listOf("com.example.app2", "com.example.app1")
        repository.updateContainerFavorites(containerA, reorderedAppsA)

        val resultA = repository.getFavoritesForContainer(containerA)
        val resultB = repository.getFavoritesForContainer(containerB)

        assertEquals(reorderedAppsA, resultA)
        assertEquals(initialApps, resultB)
    }

    @Test
    fun testDeleteContainer() = runBlocking {
        val containerId = "container_to_delete"
        val apps = listOf("com.example.app1")

        repository.updateContainerFavorites(containerId, apps)
        assertEquals(apps, repository.getFavoritesForContainer(containerId))

        repository.deleteContainer(containerId)
        val result = repository.getFavoritesForContainer(containerId)
        assertTrue(result.isEmpty())
    }
}
