package com.miro.service.widget.controller;

import com.miro.service.widget.model.Widget;
import com.miro.service.widget.service.WidgetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WidgetController.class)
public class WidgetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WidgetService widgetService;

    private static final String WIDGET_LINK = "/api/v1/widgets";

    @Test
    void all() throws Exception {
        LocalDateTime lastModified = LocalDateTime.of(2020, 2, 1, 16, 55, 12, 4343542);
        Widget widget = new Widget(1L, 10, 20, 5, 25, 35, lastModified);

        List<Widget> widgets = List.of(widget);
        when(widgetService.findAll()).thenReturn(widgets);

        mockMvc.perform(MockMvcRequestBuilders.get(WIDGET_LINK)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(widgets.size())))
                .andExpect(jsonPath("$.[0].id", is(widget.getId().intValue())))
                .andExpect(jsonPath("$.[0].x", is(widget.getX())))
                .andExpect(jsonPath("$.[0].y", is(widget.getY())))
                .andExpect(jsonPath("$.[0].zindex", is(widget.getZIndex())))
                .andExpect(jsonPath("$.[0].width", is(widget.getWidth())))
                .andExpect(jsonPath("$.[0].height", is(widget.getHeight())))
                .andExpect(jsonPath("$.[0].lastModified", is(lastModified.toString())));
    }

    @Test
    void one() throws Exception {
        LocalDateTime lastModified = LocalDateTime.of(2020, 2, 1, 16, 55, 12, 4343542);
        Widget widget = new Widget(1L, 10, 20, 5, 25, 35, lastModified);

        when(widgetService.findById(1)).thenReturn(widget);

        mockMvc.perform(MockMvcRequestBuilders.get(WIDGET_LINK + "/" + widget.getId())
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(widget.getId().intValue())))
                .andExpect(jsonPath("$.x", is(widget.getX())))
                .andExpect(jsonPath("$.y", is(widget.getY())))
                .andExpect(jsonPath("$.zindex", is(widget.getZIndex())))
                .andExpect(jsonPath("$.width", is(widget.getWidth())))
                .andExpect(jsonPath("$.height", is(widget.getHeight())))
                .andExpect(jsonPath("$.lastModified", is(lastModified.toString())));
    }

    @Test
    void create() throws Exception {
        LocalDateTime lastModified = LocalDateTime.of(2020, 2, 1, 16, 55, 12, 4343542);
        String widgetBody = "{\"x\":10,\"y\":20,\"width\":25,\"height\":35,\"zindex\":5}";
        Widget widget = new Widget(1L, 10, 20, 5, 25, 35, lastModified);

        when(widgetService.create(any())).thenReturn(widget);

        mockMvc.perform(MockMvcRequestBuilders.post(WIDGET_LINK)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(widgetBody)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.x", is(10)))
                .andExpect(jsonPath("$.y", is(20)))
                .andExpect(jsonPath("$.zindex", is(5)))
                .andExpect(jsonPath("$.width", is(25)))
                .andExpect(jsonPath("$.height", is(35)))
                .andExpect(jsonPath("$.lastModified", is(lastModified.toString())));
    }

    @Test
    void update() throws Exception {
        LocalDateTime lastModified = LocalDateTime.of(2020, 2, 1, 16, 55, 12, 4343542);
        String widgetBody = "{\"x\":10,\"y\":20,\"zindex\":5,\"width\":25,\"height\":35}";
        Widget widget = new Widget(1L, 10, 20, 5, 25, 35, lastModified);

        when(widgetService.update(any())).thenReturn(widget);

        mockMvc.perform(MockMvcRequestBuilders.put(WIDGET_LINK + "/" + widget.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(widgetBody)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.x", is(10)))
                .andExpect(jsonPath("$.y", is(20)))
                .andExpect(jsonPath("$.zindex", is(5)))
                .andExpect(jsonPath("$.width", is(25)))
                .andExpect(jsonPath("$.height", is(35)))
                .andExpect(jsonPath("$.lastModified", is(lastModified.toString())));
    }
}
