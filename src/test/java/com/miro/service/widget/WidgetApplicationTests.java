package com.miro.service.widget;

import com.miro.service.widget.controller.WidgetController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("memory")
@SpringBootTest
class WidgetApplicationTests {
	@Autowired
	private WidgetController widgetController;

	@Test
	void contextLoads() {
		assertNotNull(widgetController);
	}

}
