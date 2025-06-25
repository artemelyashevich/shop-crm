package com.elyashevich.user.infrastructure.persistance.adapter;

import com.elyashevich.user.domain.model.User;
import com.elyashevich.user.infrastructure.persistance.entity.UserMongoEntity;
import com.elyashevich.user.infrastructure.persistance.mapper.EntityMapper;
import com.elyashevich.user.infrastructure.persistance.repository.UserMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserMongoRepository userMongoRepository;

    @Mock
    private EntityMapper<User, UserMongoEntity> userEntityMapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String userId = "user123";
        UserMongoEntity mockEntity = new UserMongoEntity();
        User expectedUser = new User();

        when(userMongoRepository.findById(userId)).thenReturn(Optional.of(mockEntity));
        when(userEntityMapper.toDomain(mockEntity)).thenReturn(expectedUser);

        // Act
        Optional<User> result = userRepositoryAdapter.findById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userMongoRepository).findById(userId);
        verify(userEntityMapper).toDomain(mockEntity);
    }

    @Test
    void findById_WhenUserNotExists_ShouldReturnEmpty() {
        // Arrange
        String userId = "non-existent";
        when(userMongoRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userRepositoryAdapter.findById(userId);

        // Assert
        assertFalse(result.isPresent());
        verify(userMongoRepository).findById(userId);
        verify(userEntityMapper, never()).toDomain((UserMongoEntity) any());
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String email = "test@example.com";
        UserMongoEntity mockEntity = new UserMongoEntity();
        User expectedUser = new User();

        when(userMongoRepository.findByEmail(email)).thenReturn(Optional.of(mockEntity));
        when(userEntityMapper.toDomain(mockEntity)).thenReturn(expectedUser);

        // Act
        Optional<User> result = userRepositoryAdapter.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userMongoRepository).findByEmail(email);
        verify(userEntityMapper).toDomain(mockEntity);
    }

    @Test
    void findByEmail_WhenUserNotExists_ShouldReturnEmpty() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userMongoRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userRepositoryAdapter.findByEmail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(userMongoRepository).findByEmail(email);
        verify(userEntityMapper, never()).toDomain((UserMongoEntity) any());
    }

    @Test
    void create_ShouldSaveAndReturnUser() {
        // Arrange
        User userToCreate = User.builder()
                .email("new@example.com")
                .password("password")
                .build();

        UserMongoEntity entityToSave = new UserMongoEntity();
        UserMongoEntity savedEntity = new UserMongoEntity();
        User expectedUser = User.builder()
                .id("new123")
                .email("new@example.com")
                .build();

        when(userEntityMapper.toEntity(userToCreate)).thenReturn(entityToSave);
        when(userMongoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(userEntityMapper.toDomain(savedEntity)).thenReturn(expectedUser);

        // Act
        User result = userRepositoryAdapter.create(userToCreate);

        // Assert
        assertEquals(expectedUser, result);
        verify(userEntityMapper).toEntity(userToCreate);
        verify(userMongoRepository).save(entityToSave);
        verify(userEntityMapper).toDomain(savedEntity);
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        // Arrange
        String email = "exists@example.com";
        when(userMongoRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean result = userRepositoryAdapter.existsByEmail(email);

        // Assert
        assertTrue(result);
        verify(userMongoRepository).existsByEmail(email);
    }

    @Test
    void existsByEmail_WhenEmailNotExists_ShouldReturnFalse() {
        // Arrange
        String email = "new@example.com";
        when(userMongoRepository.existsByEmail(email)).thenReturn(false);

        // Act
        boolean result = userRepositoryAdapter.existsByEmail(email);

        // Assert
        assertFalse(result);
        verify(userMongoRepository).existsByEmail(email);
    }
}