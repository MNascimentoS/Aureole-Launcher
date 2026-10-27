package dev.mnascimentos.aureole.feature.home.components.clock

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.closeEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.openEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.resetClockSettings
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.toggleClockBackground
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockAlignment
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockBackgroundColor
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockCustomGreeting
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockDateFormat
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockFontFamily
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockStyle
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockTextColor
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockTimeFormat
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class HomeViewModelClockExtTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context)
            )
        )
    }

    @Test
    fun `when updating clock style, should update state and settings repository`() {
        // Arrange
        val newStyle = "DATE_ON_TOP"

        // Act
        viewModel.updateClockStyle(newStyle)

        // Assert
        assertEquals(newStyle, viewModel.uiState.value.clockStyle)
        assertEquals(newStyle, settingsRepository.clockStyle)
    }

    @Test
    fun `when updating clock custom greeting, should update state and settings repository`() {
        // Arrange
        val greeting = "Welcome back!"

        // Act
        viewModel.updateClockCustomGreeting(greeting)

        // Assert
        assertEquals(greeting, viewModel.uiState.value.clockCustomGreeting)
        assertEquals(greeting, settingsRepository.clockCustomGreeting)
    }

    @Test
    fun `when updating clock alignment, should update state and settings repository`() {
        // Arrange
        val alignment = "CENTER"

        // Act
        viewModel.updateClockAlignment(alignment)

        // Assert
        assertEquals(alignment, viewModel.uiState.value.clockAlignment)
        assertEquals(alignment, settingsRepository.clockAlignment)
    }

    @Test
    fun `when updating clock font family, should update state and settings repository`() {
        // Arrange
        val font = "SERIF"

        // Act
        viewModel.updateClockFontFamily(font)

        // Assert
        assertEquals(font, viewModel.uiState.value.clockFontFamily)
        assertEquals(font, settingsRepository.clockFontFamily)
    }

    @Test
    fun `when updating clock time format, should update state and settings repository`() {
        // Arrange
        val format = "24H"

        // Act
        viewModel.updateClockTimeFormat(format)

        // Assert
        assertEquals(format, viewModel.uiState.value.clockTimeFormat)
        assertEquals(format, settingsRepository.clockTimeFormat)
    }

    @Test
    fun `when updating clock date format, should update state and settings repository`() {
        // Arrange
        val format = "SHORT"

        // Act
        viewModel.updateClockDateFormat(format)

        // Assert
        assertEquals(format, viewModel.uiState.value.clockDateFormat)
        assertEquals(format, settingsRepository.clockDateFormat)
    }

    @Test
    fun `when updating clock text color, should update state and settings repository`() {
        // Arrange
        val color = 0xFF123456.toInt()

        // Act
        viewModel.updateClockTextColor(color)

        // Assert
        assertEquals(color, viewModel.uiState.value.clockTextColor)
        assertEquals(color, settingsRepository.clockTextColor)
    }

    @Test
    fun `when updating clock background color, should update state and settings repository`() {
        // Arrange
        val color = 0xFF654321.toInt()

        // Act
        viewModel.updateClockBackgroundColor(color)

        // Assert
        assertEquals(color, viewModel.uiState.value.clockBackgroundColor)
        assertEquals(color, settingsRepository.clockBackgroundColor)
    }

    @Test
    fun `when toggling clock background, should invert isClockBackgroundEnabled state and repository`() {
        // Arrange
        val initial = viewModel.uiState.value.isClockBackgroundEnabled

        // Act
        viewModel.toggleClockBackground()

        // Assert
        assertEquals(!initial, viewModel.uiState.value.isClockBackgroundEnabled)
        assertEquals(!initial, settingsRepository.isClockBackgroundEnabled)
    }

    @Test
    fun `when opening and closing edit clock bottom sheet, should update state visibility`() {
        // Act & Assert Open
        viewModel.openEditClockBottomSheet()
        assertTrue(viewModel.uiState.value.showEditClockBottomSheet)

        // Act & Assert Close
        viewModel.closeEditClockBottomSheet()
        assertFalse(viewModel.uiState.value.showEditClockBottomSheet)
    }

    @Test
    fun `when resetting clock settings, should restore default values in repository and state`() {
        // Given modified settings
        viewModel.updateClockStyle("TIME_ONLY")
        viewModel.updateClockCustomGreeting("Hello Test")
        viewModel.updateClockAlignment("END")
        viewModel.updateClockTextColor(0xFF112233.toInt())

        // When resetting
        viewModel.resetClockSettings()

        // Then defaults should be restored
        assertEquals(settingsRepository.clockStyle, viewModel.uiState.value.clockStyle)
        assertEquals(settingsRepository.clockCustomGreeting, viewModel.uiState.value.clockCustomGreeting)
        assertEquals(settingsRepository.clockAlignment, viewModel.uiState.value.clockAlignment)
        assertEquals(settingsRepository.clockTextColor, viewModel.uiState.value.clockTextColor)
    }
}
