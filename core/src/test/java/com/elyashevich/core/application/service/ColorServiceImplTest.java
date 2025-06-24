package com.elyashevich.core.application.service;

import com.elyashevich.core.application.port.out.ColorRepository;
import com.elyashevich.core.domain.exception.ResourceAlreadyExistsException;
import com.elyashevich.core.domain.exception.ResourceNotFoundException;
import com.elyashevich.core.domain.model.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColorServiceImplTest {

    @Mock
    private ColorRepository colorRepository;

    @InjectMocks
    private ColorServiceImpl colorService;

    private static Stream<Arguments> provideColorsForStore() {
        return Stream.of(
                Arguments.of(
                        "Multiple colors",
                        List.of(
                                createTestColor("1", "Red", "#FF0000", "store-1"),
                                createTestColor("2", "Blue", "#0000FF", "store-1")
                        ),
                        "store-1",
                        2
                ),
                Arguments.of(
                        "Empty list",
                        List.of(),
                        "store-2",
                        0
                ),
                Arguments.of(
                        "Single color",
                        List.of(createTestColor("3", "Green", "#00FF00", "store-3")),
                        "store-3",
                        1
                )
        );
    }

    private static Stream<Arguments> provideColorFindByIdCases() {
        return Stream.of(
                Arguments.of(
                        "Existing color",
                        Optional.of(createTestColor("1", "Red", "#FF0000", "store-1")),
                        "1",
                        false
                ),
                Arguments.of(
                        "Non-existing color",
                        Optional.empty(),
                        "999",
                        true
                )
        );
    }

    private static Stream<Arguments> provideColorCreationCases() {
        return Stream.of(
                Arguments.of(
                        "New unique color",
                        createTestColor(null, "Red", "#FF0000", "store-1"),
                        false,
                        false
                ),
                Arguments.of(
                        "Duplicate color name",
                        createTestColor(null, "Red", "#FF0000", "store-1"),
                        true,
                        true
                )
        );
    }

    private static Stream<Arguments> provideColorUpdateCases() {
        return Stream.of(
                Arguments.of(
                        "Update name only",
                        "1",
                        "New Red",
                        null
                ),
                Arguments.of(
                        "Update value only",
                        "2",
                        null,
                        "#FF0001"
                ),
                Arguments.of(
                        "Update both fields",
                        "3",
                        "Dark Blue",
                        "#0000AA"
                )
        );
    }

    private static Color createTestColor(String id, String name, String value, String storeId) {
        return Color.builder()
                .id(id)
                .name(name)
                .value(value)
                .storeId(storeId)
                .build();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideColorsForStore")
    @DisplayName("findByStoreId should return correct colors")
    void findByStoreId_ShouldReturnColors(
            String description,
            List<Color> expectedColors,
            String storeId,
            int expectedSize
    ) {
        when(colorRepository.findByStoreId(storeId)).thenReturn(expectedColors);

        List<Color> result = colorService.findByStoreId(storeId);

        assertEquals(expectedSize, result.size());
        if (!expectedColors.isEmpty()) {
            assertEquals(expectedColors.get(0).getId(), result.get(0).getId());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideColorFindByIdCases")
    @DisplayName("findById should handle cases correctly")
    void findById_ShouldHandleCases(
            String description,
            Optional<Color> mockResponse,
            String inputId,
            boolean shouldThrow
    ) {
        when(colorRepository.findById(inputId)).thenReturn(mockResponse);

        if (shouldThrow) {
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> colorService.findById(inputId)
            );
            assertEquals(
                    String.format(ColorServiceImpl.COLOR_WITH_ID_NOT_FOUND_TEMPLATE, inputId),
                    exception.getMessage()
            );
        } else {
            Color result = colorService.findById(inputId);
            assertEquals(mockResponse.get().getId(), result.getId());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideColorCreationCases")
    @DisplayName("create should handle color creation correctly")
    void create_ShouldHandleCases(
            String description,
            Color inputColor,
            boolean exists,
            boolean shouldThrow
    ) {
        when(colorRepository.existsByNameAndStoreId(
                inputColor.getName(),
                inputColor.getStoreId()
        )).thenReturn(exists);

        if (shouldThrow) {
            assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> colorService.create(inputColor)
            );
            verify(colorRepository, never()).create(any());
        } else {
            when(colorRepository.create(any(Color.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Color result = colorService.create(inputColor);

            assertNotNull(result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());
            verify(colorRepository).create(any(Color.class));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideColorUpdateCases")
    @DisplayName("update should modify existing color")
    void update_ExistingColor_ShouldUpdate(
            String description,
            String colorId,
            String newName,
            String newValue
    ) {
        Color existing = createTestColor(colorId, "OldName", "#000000", "store-1");
        Color updateData = Color.builder()
                .name(newName)
                .value(newValue)
                .build();

        when(colorRepository.findById(colorId)).thenReturn(Optional.of(existing));
        when(colorRepository.create(any(Color.class))).thenReturn(existing);

        Color result = colorService.update(colorId, updateData);

        assertAll(
                () -> assertEquals(newName != null ? newName : existing.getName(), result.getName()),
                () -> assertEquals(newValue != null ? newValue : existing.getValue(), result.getValue()),
                () -> assertNotNull(result.getUpdatedAt())
        );
    }

    @ParameterizedTest
    @CsvSource({
            "color-1",
            "color-2",
            "color-3"
    })
    @DisplayName("delete should invoke repository delete")
    void delete_ShouldCallRepository(String colorId) {
        Color existing = createTestColor(colorId, "ToDelete", "#000000", "store-1");

        when(colorRepository.findById(colorId)).thenReturn(Optional.of(existing));
        doNothing().when(colorRepository).delete(existing);

        assertDoesNotThrow(() -> colorService.delete(colorId));
        verify(colorRepository).delete(existing);
    }

    @Test
    @DisplayName("delete should throw when color not found")
    void delete_NonExistingColor_ShouldThrow() {
        String colorId = "nonexistent";
        when(colorRepository.findById(colorId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> colorService.delete(colorId)
        );

        assertEquals(
                String.format(ColorServiceImpl.COLOR_WITH_ID_NOT_FOUND_TEMPLATE, colorId),
                exception.getMessage()
        );
        verify(colorRepository, never()).delete(any());
    }
}