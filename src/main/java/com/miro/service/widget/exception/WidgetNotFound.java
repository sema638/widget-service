package com.miro.service.widget.exception;

public class WidgetNotFound extends RuntimeException {
    public WidgetNotFound(final long id) {
        super("Cannot find widget with ID = " + id);
    }
}
