package com.elyashevich.core.application.service;

import com.elyashevich.core.application.port.out.CategoryRepository;
import com.elyashevich.core.domain.exception.ResourceAlreadyExistsException;
import com.elyashevich.core.domain.exception.ResourceNotFoundException;
import com.elyashevich.core.domain.model.Category;
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
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private static Stream<Arguments> provideCategoriesForStore() {
        return Stream.of(
                Arguments.of(
                        "Multiple categories",
                        List.of(
                                createTestCategory("1", "Electronics", "store-1"),
                                createTestCategory("2", "Clothing", "store-1")
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
                        "Single category",
                        List.of(createTestCategory("3", "Books", "store-3")),
                        "store-3",
                        1
                )
        );
    }

    private static Stream<Arguments> provideCategoryFindByIdCases() {
        return Stream.of(
                Arguments.of(
                        "Existing category",
                        Optional.of(createTestCategory("1", "Electronics", "store-1")),
                        "1",
                        false
                ),
                Arguments.of(
                        "Non-existing category",
                        Optional.empty(),
                        "999",
                        true
                )
        );
    }

    private static Stream<Arguments> provideCategoryCreationCases() {
        return Stream.of(
                Arguments.of(
                        "New unique category",
                        createTestCategory(null, "Electronics", "store-1"),
                        false,
                        false
                ),
                Arguments.of(
                        "Duplicate category",
                        createTestCategory(null, "Electronics", "store-1"),
                        true,
                        true
                )
        );
    }

    private static Category createTestCategory(String id, String title, String storeId) {
        return Category.builder()
                .id(id)
                .title(title)
                .storeId(storeId)
                .build();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideCategoriesForStore")
    @DisplayName("findByStoreId should return correct categories")
    void findByStoreId_ShouldReturnCategories(
            String description,
            List<Category> expectedCategories,
            String storeId,
            int expectedSize
    ) {
        when(categoryRepository.findByStoreId(storeId)).thenReturn(expectedCategories);

        List<Category> result = categoryService.findByStoreId(storeId);

        assertEquals(expectedSize, result.size());
        if (!expectedCategories.isEmpty()) {
            assertEquals(expectedCategories.get(0).getId(), result.get(0).getId());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideCategoryFindByIdCases")
    @DisplayName("findById should handle cases correctly")
    void findById_ShouldHandleCases(
            String description,
            Optional<Category> mockResponse,
            String inputId,
            boolean shouldThrow
    ) {
        when(categoryRepository.findById(inputId)).thenReturn(mockResponse);

        if (shouldThrow) {
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> categoryService.findById(inputId)
            );
            assertEquals(
                    String.format(CategoryServiceImpl.CATEGORY_WITH_ID_NOT_FOUND_TEMPLATE, inputId),
                    exception.getMessage()
            );
        } else {
            Category result = categoryService.findById(inputId);
            assertEquals(mockResponse.get().getId(), result.getId());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideCategoryCreationCases")
    @DisplayName("create should handle category creation correctly")
    void create_ShouldHandleCases(
            String description,
            Category inputCategory,
            boolean exists,
            boolean shouldThrow
    ) {
        when(categoryRepository.existsByTitleAndStoreId(
                inputCategory.getTitle(),
                inputCategory.getStoreId()
        )).thenReturn(exists);

        if (shouldThrow) {
            assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> categoryService.create(inputCategory)
            );
            verify(categoryRepository, never()).create(any());
        } else {
            when(categoryRepository.create(any(Category.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Category result = categoryService.create(inputCategory);

            assertNotNull(result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());
            verify(categoryRepository).create(any(Category.class));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "category-1, New Title, New Description",
            "category-2, Updated Electronics, Updated devices"
    })
    @DisplayName("update should modify existing category")
    void update_ExistingCategory_ShouldUpdate(
            String categoryId,
            String newTitle,
            String newDescription
    ) {
        Category existing = createTestCategory(categoryId, "Old Title", "store-1");
        Category updateData = Category.builder()
                .title(newTitle)
                .description(newDescription)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        when(categoryRepository.create(any(Category.class))).thenReturn(existing);

        Category result = categoryService.update(categoryId, updateData);

        assertAll(
                () -> assertEquals(newTitle, result.getTitle()),
                () -> assertEquals(newDescription, result.getDescription()),
                () -> assertNotNull(result.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("delete should invoke repository delete")
    void delete_ShouldCallRepository() {
        String categoryId = "cat-123";
        Category existing = createTestCategory(categoryId, "To Delete", "store-1");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        doNothing().when(categoryRepository).delete(existing);

        assertDoesNotThrow(() -> categoryService.delete(categoryId));
        verify(categoryRepository).delete(existing);
    }
}