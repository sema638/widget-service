package com.miro.service.widget.service;


import com.miro.service.widget.exception.WidgetNotFound;
import com.miro.service.widget.model.Paging;
import com.miro.service.widget.model.Widget;
import com.miro.service.widget.repository.WidgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    WidgetService widgetService;

    @MockBean
    WidgetRepository mockWidgetRepository;

    @Captor
    ArgumentCaptor<Widget> widgetArgumentCaptor;

    @Captor
    ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findAllReturnsAll() {
        List<Widget> widgets = List.of(
                new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20)),
                new Widget(2L, 5, 5, 2, 5, 5, LocalDateTime.of(2021, 1, 29, 11, 21)));
        when(mockWidgetRepository.findAll(any())).thenReturn(getPage(widgets));

        List<Widget> serviceWidgets = widgetService.findAll(new Paging(0, 10));

        assertEquals(widgets, serviceWidgets);
    }

    private PageImpl<Widget> getPage(final List<Widget> widgets) {
        return new PageImpl<>(widgets, Pageable.unpaged(), 0);
    }

    @Test
    void findAllReturnsEmptyWhenNoWidgets() {
        when(mockWidgetRepository.findAll(any())).thenReturn(getPage(Collections.emptyList()));

        List<Widget> serviceWidgets = widgetService.findAll(new Paging(0, 10));

        assertEquals(0, serviceWidgets.size());
    }

    @Test
    void findAllPassesPagingCorrectly() {
        when(mockWidgetRepository.findAll(any())).thenReturn(getPage(Collections.emptyList()));

        widgetService.findAll(new Paging(2, 5));

        verify(mockWidgetRepository).findAll(pageableArgumentCaptor.capture());
        assertEquals(2, pageableArgumentCaptor.getValue().getPageNumber());
        assertEquals(5, pageableArgumentCaptor.getValue().getPageSize());
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
    void createReturnsSavedWidgetWithStartingZIndex() {
        when(mockWidgetRepository.findAll(any())).thenReturn(getPage(Collections.emptyList()));

        Widget savedWidget = new Widget(3L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(widgetArgumentCaptor.capture())).thenReturn(savedWidget);

        Widget widgetToCreate = new Widget(null, 0, 0, null, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.create(widgetToCreate);

        assertEquals(savedWidget, serviceWidget);
        assertEquals(1, widgetArgumentCaptor.getValue().getZindex());
    }

    @Test
    void createReturnsSavedWidgetWithMaximumZIndex() {
        Widget existingWidget1 = new Widget(2L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.findTopByOrderByZindexDesc()).thenReturn(Optional.of(existingWidget1));

        Widget savedWidget = new Widget(3L, 0, 0, 3, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(widgetArgumentCaptor.capture())).thenReturn(savedWidget);

        Widget widgetToCreate = new Widget(null, 0, 0, null, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.create(widgetToCreate);

        assertEquals(savedWidget, serviceWidget);
        assertEquals(3, widgetArgumentCaptor.getValue().getZindex());
    }

    @Test
    void createUpdatesZIndexOfAllExistingWidgets() {
        Widget existingWidget1 = new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget2 = new Widget(2L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget3 = new Widget(3L, 0, 0, 3, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.findByZindexGreaterThanEqualOrderByZindex(1)).thenReturn(List.of(existingWidget1, existingWidget2, existingWidget3));

        Widget savedWidget = new Widget(4L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(widgetArgumentCaptor.capture())).thenReturn(savedWidget);

        Widget widgetToCreate = new Widget(null, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.create(widgetToCreate);

        assertEquals(savedWidget, serviceWidget);
        assertEquals(4, widgetArgumentCaptor.getAllValues().size());

        assertEquals(3L, widgetArgumentCaptor.getAllValues().get(0).getId());
        assertEquals(4, widgetArgumentCaptor.getAllValues().get(0).getZindex());

        assertEquals(2L, widgetArgumentCaptor.getAllValues().get(1).getId());
        assertEquals(3, widgetArgumentCaptor.getAllValues().get(1).getZindex());

        assertEquals(1L, widgetArgumentCaptor.getAllValues().get(2).getId());
        assertEquals(2, widgetArgumentCaptor.getAllValues().get(2).getZindex());

        assertNull(widgetArgumentCaptor.getAllValues().get(3).getId());
        assertEquals(1, widgetArgumentCaptor.getAllValues().get(3).getZindex());
    }

    @Test
    void createUpdatesZIndexOfOneExistingWidgets() {
        Widget existingWidget2 = new Widget(2L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget3 = new Widget(3L, 0, 0, 4, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.findByZindexGreaterThanEqualOrderByZindex(2)).thenReturn(List.of(existingWidget2, existingWidget3));

        Widget savedWidget = new Widget(4L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(widgetArgumentCaptor.capture())).thenReturn(savedWidget);

        Widget widgetToCreate = new Widget(null, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.create(widgetToCreate);

        assertEquals(savedWidget, serviceWidget);
        assertEquals(2, widgetArgumentCaptor.getAllValues().size());

        assertEquals(2L, widgetArgumentCaptor.getAllValues().get(0).getId());
        assertEquals(3, widgetArgumentCaptor.getAllValues().get(0).getZindex());

        assertNull(widgetArgumentCaptor.getAllValues().get(1).getId());
        assertEquals(2, widgetArgumentCaptor.getAllValues().get(1).getZindex());
    }

    @Test
    void createUpdatesZIndexOfNoneExistingWidgets() {
        Widget existingWidget1 = new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget2 = new Widget(2L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget3 = new Widget(3L, 0, 0, 4, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.findAll(any())).thenReturn(getPage(List.of(existingWidget1, existingWidget2, existingWidget3)));

        Widget savedWidget = new Widget(4L, 0, 0, 3, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(widgetArgumentCaptor.capture())).thenReturn(savedWidget);

        Widget widgetToCreate = new Widget(null, 0, 0, 3, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.create(widgetToCreate);

        assertEquals(savedWidget, serviceWidget);
        assertEquals(1, widgetArgumentCaptor.getAllValues().size());

        assertNull(widgetArgumentCaptor.getAllValues().get(0).getId());
        assertEquals(3, widgetArgumentCaptor.getAllValues().get(0).getZindex());
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
    void updateUpdatesZIndexOfAllExistingWidgets() {
        Widget existingWidget1 = new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget2 = new Widget(2L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget3 = new Widget(3L, 0, 0, 3, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.findByZindexGreaterThanEqualOrderByZindex(2)).thenReturn(List.of(existingWidget2, existingWidget3));
        when(mockWidgetRepository.findById(1L)).thenReturn(Optional.of(existingWidget1));

        Widget savedWidget = new Widget(1L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(widgetArgumentCaptor.capture())).thenReturn(savedWidget);

        Widget widgetToUpdate = new Widget(1L, 2, 2, 2, null, null, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.update(widgetToUpdate);

        assertEquals(savedWidget, serviceWidget);
        assertEquals(3, widgetArgumentCaptor.getAllValues().size());

        assertEquals(3L, widgetArgumentCaptor.getAllValues().get(0).getId());
        assertEquals(4, widgetArgumentCaptor.getAllValues().get(0).getZindex());

        assertEquals(2L, widgetArgumentCaptor.getAllValues().get(1).getId());
        assertEquals(3, widgetArgumentCaptor.getAllValues().get(1).getZindex());

        assertEquals(1L, widgetArgumentCaptor.getAllValues().get(2).getId());
        assertEquals(2, widgetArgumentCaptor.getAllValues().get(2).getZindex());
    }

    @Test
    void updateUpdatesZIndexOfOneExistingWidgets() {
        Widget existingWidget1 = new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget2 = new Widget(2L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget3 = new Widget(3L, 0, 0, 4, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.findByZindexGreaterThanEqualOrderByZindex(2)).thenReturn(List.of(existingWidget2, existingWidget3));
        when(mockWidgetRepository.findById(1L)).thenReturn(Optional.of(existingWidget1));

        Widget savedWidget = new Widget(1L, 0, 0, 2, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(widgetArgumentCaptor.capture())).thenReturn(savedWidget);

        Widget widgetToUpdate = new Widget(1L, 2, 2, 2, null, null, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.update(widgetToUpdate);

        assertEquals(savedWidget, serviceWidget);
        assertEquals(2, widgetArgumentCaptor.getAllValues().size());

        assertEquals(2L, widgetArgumentCaptor.getAllValues().get(0).getId());
        assertEquals(3, widgetArgumentCaptor.getAllValues().get(0).getZindex());

        assertEquals(1L, widgetArgumentCaptor.getAllValues().get(1).getId());
        assertEquals(2, widgetArgumentCaptor.getAllValues().get(1).getZindex());
    }

    @Test
    void updateUpdatesZIndexOfNoneExistingWidgets() {
        Widget existingWidget1 = new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget existingWidget3 = new Widget(3L, 0, 0, 4, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.findByZindexGreaterThanEqualOrderByZindex(2)).thenReturn(List.of(existingWidget3));
        when(mockWidgetRepository.findById(1L)).thenReturn(Optional.of(existingWidget1));

        Widget savedWidget = new Widget(1L, 0, 0, 3, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));
        when(mockWidgetRepository.save(widgetArgumentCaptor.capture())).thenReturn(savedWidget);

        Widget widgetToUpdate = new Widget(1L, 2, 2, 3, null, null, LocalDateTime.of(2021, 1, 29, 11, 20));
        Widget serviceWidget = widgetService.update(widgetToUpdate);

        assertEquals(savedWidget, serviceWidget);
        assertEquals(1, widgetArgumentCaptor.getAllValues().size());

        assertEquals(1L, widgetArgumentCaptor.getAllValues().get(0).getId());
        assertEquals(3, widgetArgumentCaptor.getAllValues().get(0).getZindex());
    }

    @Test
    void updateThrowsExceptionWhenNotFound() {
        when(mockWidgetRepository.findById(1)).thenReturn(Optional.empty());

        Widget widgetToUpdate = new Widget(1L, 2, 2, null, null, null, LocalDateTime.of(2021, 1, 29, 11, 20));
        assertThrows(WidgetNotFound.class, () -> widgetService.update(widgetToUpdate));
    }

    @Test
    void deleteWidget() {
        Widget existingWidget1 = new Widget(1L, 0, 0, 1, 7, 7, LocalDateTime.of(2021, 1, 29, 11, 20));

        when(mockWidgetRepository.findById(1)).thenReturn(Optional.of(existingWidget1));

        widgetService.delete(1);
        verify(mockWidgetRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteThrowsExceptionWhenNotFound() {
        doThrow(new EntityNotFoundException()).when(mockWidgetRepository).deleteById(1L);

        assertThrows(WidgetNotFound.class, () -> widgetService.delete(1));
    }
}
