package com.miro.service.widget.service;

import com.miro.service.widget.exception.WidgetNotFound;
import com.miro.service.widget.model.Paging;
import com.miro.service.widget.model.Widget;
import com.miro.service.widget.repository.WidgetRepository;
import com.miro.service.widget.util.WidgetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class WidgetService {
    @Autowired
    private WidgetRepository widgetRepository;

    private static final PageRequest ALL = PageRequest.of(0, Integer.MAX_VALUE);

    public List<Widget> findAll(final Paging paging) {
        return widgetRepository.findAll(WidgetUtil.pageable(paging));
    }

    public Widget findById(final long id) {
        return widgetRepository.findById(id)
                .orElseThrow(() -> new WidgetNotFound(id));
    }

    public Widget create(final Widget widget) {
        if (widget.getZIndex() == null) {
            // find maximum zIndex among existing widgets
            final Integer maxZIndex = widgetRepository.findAll(ALL).stream()
                    .map(Widget::getZIndex)
                    .max(Comparator.naturalOrder())
                    .orElse(0);
            widget.setZIndex(maxZIndex + 1);
        }
        return save(widget);
    }

    public Widget update(final Widget widgetUpdate) {
        final Widget sourceWidget = widgetRepository.findById(widgetUpdate.getId())
                .orElseThrow(() -> new WidgetNotFound(widgetUpdate.getId()));

        final Widget updatedWidget = WidgetUtil.update(sourceWidget, widgetUpdate);

        return save(updatedWidget);
    }

    public void delete(final long id) {
        try {
            widgetRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new WidgetNotFound(id);
        }
    }

    private Widget save(final Widget widgetToSave) {
        final List<Widget> widgets = widgetRepository.findAll(ALL);
        final List<Widget> widgetsToUpdate = new LinkedList<>();

        Widget widgetToInsert = widgetToSave;
        for (Widget widget : widgets) {
            if (widget.getZIndex().intValue() == widgetToInsert.getZIndex().intValue()) {
                // widget to insert has the same z-index as existing, some following widgets must be shifted by z-index
                widgetToInsert = widget;
                widgetToInsert.setZIndex(widgetToInsert.getZIndex() + 1);

                // put widget with maximum zIndex to the head
                widgetsToUpdate.add(0, widgetToInsert);
            } else if (widget.getZIndex() > widgetToInsert.getZIndex()) {
                // widgetToSave's zIndex is unique, so no need to update any widgetToSave
                break;
            }
        }

        // widgetsToUpdate is already reverse sorted by zIndex, so saving one by one doesn't produce zIndex duplicates
        widgetsToUpdate.forEach(widgetRepository::save);

        widgetToSave.setLastModified(LocalDateTime.now());
        return widgetRepository.save(widgetToSave);
    }
}
