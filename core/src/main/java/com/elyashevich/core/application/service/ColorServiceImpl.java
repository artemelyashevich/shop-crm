package com.elyashevich.core.application.service;

import com.elyashevich.core.application.port.in.ColorService;
import com.elyashevich.core.application.port.out.ColorRepository;
import com.elyashevich.core.domain.exception.ResourceAlreadyExistsException;
import com.elyashevich.core.domain.exception.ResourceNotFoundException;
import com.elyashevich.core.domain.model.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    public static final String COLOR_WITH_ID_NOT_FOUND_TEMPLATE = "Color with id: '%s' not found";
    public static final String COLOR_WITH_NAME_ALREADY_EXISTS_IN_STORE_WITH_ID_TEMPLATE = "Color with name '%s' already exists in store with id '%s'";

    private final ColorRepository colorRepository;

    @Override
    public List<Color> findByStoreId(String storeId) {
        log.debug("Attempting to find colors with store id {}", storeId);

        List<Color> colors = colorRepository.findByStoreId(storeId);

        log.info("Colors found: {}", colors);
        return colors;
    }

    @Override
    public Color findById(String id) {
        log.debug("Attempting to find color with id {}", id);

        Color color = colorRepository.findById(id).orElseThrow(() -> {
            String message = COLOR_WITH_ID_NOT_FOUND_TEMPLATE.formatted(id);
            log.info(message);
            return new ResourceNotFoundException(message);
        });

        log.info("Color found: {}", color);
        return color;
    }

    @Override
    @Transactional
    public Color create(Color color) {
        log.debug("Attempting to create color {}", color);

        String storeId = color.getStoreId();
        String name = color.getName();

        if (colorRepository.existsByNameAndStoreId(name, storeId)) {
            String message = COLOR_WITH_NAME_ALREADY_EXISTS_IN_STORE_WITH_ID_TEMPLATE.formatted(name, storeId);
            log.info(message);
            throw new ResourceAlreadyExistsException(message);
        }

        color.updateTimestamps();
        Color newColor = colorRepository.create(color);

        log.info("Color created: {}", newColor);
        return newColor;
    }

    @Override
    @Transactional
    public Color update(String id, Color color) {
        log.debug("Attempting to update color with id {}", id);

        Color oldColor = findById(id);

        updateColorFields(oldColor, color);

        Color updatedColor = colorRepository.create(oldColor);

        log.info("Color updated: {}", updatedColor);
        return updatedColor;
    }

    @Override
    @Transactional
    public void delete(String id) {
        log.debug("Attempting to delete color with id {}", id);

        Color color = findById(id);

        colorRepository.delete(color);

        log.info("Color deleted: {}", color);
    }

    private void updateColorFields(Color existing, Color updateData) {
        existing.setName(updateData.getName());
        existing.setValue(updateData.getValue());
        existing.updateTimestamps();
    }
}