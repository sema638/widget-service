package com.miro.service.widget.util;

import com.miro.service.widget.dto.WidgetDTO;
import com.miro.service.widget.model.Widget;

import java.time.LocalDateTime;

public class WidgetUtil {
    public static WidgetDTO convert(final Widget widget) {
        return new WidgetDTO(widget.getId(),
                widget.getX(),
                widget.getY(),
                widget.getZIndex(),
                widget.getWidth(),
                widget.getHeight(),
                widget.getLastModified());
    }

    public static Widget update(final Widget sourceWidget, final Widget updateData) {
        return new Widget(sourceWidget.getId(),
                getNonNullValue(sourceWidget.getX(), updateData.getX()),
                getNonNullValue(sourceWidget.getY(), updateData.getY()),
                getNonNullValue(sourceWidget.getZIndex(), updateData.getZIndex()),
                getNonNullValue(sourceWidget.getWidth(), updateData.getWidth()),
                getNonNullValue(sourceWidget.getHeight(), updateData.getHeight()),
                LocalDateTime.now());
    }

    private static <T> T getNonNullValue(final T source, final T update) {
        return update != null ? update : source;
    }
}
