package com.miro.service.widget.service;


import com.miro.service.widget.exception.WidgetNotFound;
import com.miro.service.widget.model.Widget;
import com.miro.service.widget.repository.WidgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class WidgetServiceTest {
    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
        @Bean
        public WidgetService widgetService() {
            return new WidgetService();
        }
    }

    @Autowired
    private WidgetService widgetService;

    @MockBean
    private WidgetRepository mockWidgetRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findAllReturnsAll() {
        List<Widget> widgets = List.of(
                new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20)),
                new Widget(2L, 5, 5, 2, 5, 5, LocalDateTime.of(2021, 1, 29, 11, 21)));
        when(mockWidgetRepository.findAll()).thenReturn(widgets);

        List<Widget> serviceWidgets = widgetService.findAll();

        assertEquals(widgets, serviceWidgets);
    }

    @Test
    void findAllReturnsEmptyWhenNoWidgets() {
        when(mockWidgetRepository.findAll()).thenReturn(Collections.emptyList());

        List<Widget> serviceWidgets = widgetService.findAll();

        assertEquals(0, serviceWidgets.size());
    }

    @Test
    void findAllReturnsSortedByZIndex() {
        List<Widget> widgets = List.of(
                new Widget(1L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20)),
                new Widget(2L, 5, 5, 1, 5, 5, LocalDateTime.of(2021, 1, 29, 11, 21)));
        when(mockWidgetRepository.findAll()).thenReturn(widgets);

        List<Integer> zIndexes = widgetService.findAll().stream()
                .map(Widget::getZIndex).collect(Collectors.toList());
        assertEquals(List.of(1, 2), zIndexes);
    }

    @Test
    void findByIdReturnsWidget() {
        Widget widget = new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.findById(1)).thenReturn(Optional.of(widget));

        Widget serviceWidget = widgetService.findById(1);

        assertEquals(widget, serviceWidget);
    }

    @Test
    void findByIdThrowsExceptionWhenNotFound() {
        when(mockWidgetRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(WidgetNotFound.class, () -> widgetService.findById(1));
    }

    @Test
    void createReturnsSavedWidget() {
        Widget widget = new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(any())).thenReturn(widget);

        Widget widgetToCreate = new Widget(null, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.create(widgetToCreate);

        assertEquals(widget, serviceWidget);
    }

    @Test
    void updateReturnsSavedWidget() {
        Widget originalWidget = new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.findById(1L)).thenReturn(Optional.of(originalWidget));

        Widget savedWidget = new Widget(1L, 2, 2, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(any())).thenReturn(savedWidget);

        Widget widgetToUpdate = new Widget(1L, 2, 2, null, null, null, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.update(widgetToUpdate);

        assertEquals(savedWidget, serviceWidget);
    }

    @Test
    void updateThrowsExceptionWhenNotFound() {
        when(mockWidgetRepository.findById(1)).thenReturn(Optional.empty());

        Widget widgetToUpdate = new Widget(1L, 2, 2, null, null, null, LocalDateTime.of(2021, 1, 29, 11, 20));
        assertThrows(WidgetNotFound.class, () -> widgetService.update(widgetToUpdate));
    }

    @Test
    void deleteWidget() {
        widgetService.delete(1);
        verify(mockWidgetRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteThrowsExceptionWhenNotFound() {
        doThrow(new EntityNotFoundException()).when(mockWidgetRepository).deleteById(1L);

        assertThrows(WidgetNotFound.class, () -> widgetService.delete(1));
    }
}
