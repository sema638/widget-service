package com.miro.service.widget;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("memory")
@AutoConfigureMockMvc
@SpringBootTest
class WidgetApplicationTests {
    @Autowired
    MockMvc mockMvc;

    private static final String WIDGETS_LINK = "/api/v1/widgets";

    @Test
    void integrationTest() throws Exception {
        // create widget 1 with zIndex = 1
        String widgetBody = "{\"x\":10,\"y\":20,\"width\":25,\"height\":35,\"zindex\":1}";
        mockMvc.perform(MockMvcRequestBuilders.post(WIDGETS_LINK)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(widgetBody)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated());

        // create widget 2 with zIndex = 1
        widgetBody = "{\"x\":10,\"y\":20,\"width\":25,\"height\":35,\"zindex\":1}";
        mockMvc.perform(MockMvcRequestBuilders.post(WIDGETS_LINK)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(widgetBody)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated());

        // widget 1 now has zIndex = 2, widget 2 has zIndex = 1
        mockMvc.perform(MockMvcRequestBuilders.get(WIDGETS_LINK)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(2)))
                .andExpect(jsonPath("$.[0].x", is(10)))
                .andExpect(jsonPath("$.[0].y", is(20)))
                .andExpect(jsonPath("$.[0].zindex", is(1)))
                .andExpect(jsonPath("$.[0].width", is(25)))
                .andExpect(jsonPath("$.[0].height", is(35)))
                .andExpect(jsonPath("$.[1].id", is(1)))
                .andExpect(jsonPath("$.[1].x", is(10)))
                .andExpect(jsonPath("$.[1].y", is(20)))
                .andExpect(jsonPath("$.[1].zindex", is(2)))
                .andExpect(jsonPath("$.[1].width", is(25)))
                .andExpect(jsonPath("$.[1].height", is(35)));

        // update widget 1 with zIndex 1
        widgetBody = "{\"zindex\":1}";
        mockMvc.perform(MockMvcRequestBuilders.put(WIDGETS_LINK + "/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(widgetBody)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        // widget 1 now has zIndex = 1 and widget 2 has zIndex = 2
        mockMvc.perform(MockMvcRequestBuilders.get(WIDGETS_LINK)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].x", is(10)))
                .andExpect(jsonPath("$.[0].y", is(20)))
                .andExpect(jsonPath("$.[0].zindex", is(1)))
                .andExpect(jsonPath("$.[0].width", is(25)))
                .andExpect(jsonPath("$.[0].height", is(35)))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].x", is(10)))
                .andExpect(jsonPath("$.[1].y", is(20)))
                .andExpect(jsonPath("$.[1].zindex", is(2)))
                .andExpect(jsonPath("$.[1].width", is(25)))
                .andExpect(jsonPath("$.[1].height", is(35)));

        // delete widget 1
        mockMvc.perform(MockMvcRequestBuilders.delete(WIDGETS_LINK + "/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(widgetBody)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNoContent());

        // only widget 2
        mockMvc.perform(MockMvcRequestBuilders.get(WIDGETS_LINK)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(2)))
                .andExpect(jsonPath("$.[0].x", is(10)))
                .andExpect(jsonPath("$.[0].y", is(20)))
                .andExpect(jsonPath("$.[0].zindex", is(2)))
                .andExpect(jsonPath("$.[0].width", is(25)))
                .andExpect(jsonPath("$.[0].height", is(35)));
    }
}
