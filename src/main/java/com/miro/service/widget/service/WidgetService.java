package com.miro.service.widget.service;

import com.miro.service.widget.exception.WidgetNotFound;
import com.miro.service.widget.model.Paging;
import com.miro.service.widget.model.Widget;
import com.miro.service.widget.repository.WidgetRepository;
import com.miro.service.widget.util.RTreeWrapper;
import com.miro.service.widget.util.WidgetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class WidgetService {
    @Autowired
    private WidgetRepository widgetRepository;

    private final RTreeWrapper searchTree = new RTreeWrapper();

    public List<Widget> findAll(final Paging paging) {
        return widgetRepository.findAll(WidgetUtil.pageable(paging)).getContent();
    }

    public List<Widget> findAllInArea(final int left, final int bottom, final int right, final int top) {
        return searchTree.search(left, bottom, right, top);
    }

    public Widget findById(final long id) {
        return widgetRepository.findById(id)
                .orElseThrow(() -> new WidgetNotFound(id));
    }

    public Widget create(final Widget widget) {
        if (widget.getZindex() == null) {
            // find maximum zIndex among existing widgets
            final Integer maxZIndex = widgetRepository.findTopByOrderByZindexDesc()
                    .map(Widget::getZindex)
                    .orElse(0);
            widget.setZindex(maxZIndex + 1);
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
            final Widget widget = findById(id);
            widgetRepository.deleteById(id);
            searchTree.delete(widget);
        } catch (EntityNotFoundException e) {
            throw new WidgetNotFound(id);
        }
    }

    private Widget save(final Widget widgetToSave) {
        final List<Widget> widgets = widgetRepository.findByZindexGreaterThanEqualOrderByZindex(widgetToSave.getZindex());
        final List<Widget> widgetsToUpdate = new LinkedList<>();

        Widget widgetToInsert = widgetToSave;
        for (Widget widget : widgets) {
            // no need to update the widget itself
            if (widget.getId().equals(widgetToSave.getId())) {
                continue;
            }

            if (widget.getZindex().intValue() == widgetToInsert.getZindex().intValue()) {
                // widget to insert has the same z-index as existing, some following widgets must be shifted by z-index
                widgetToInsert = widget;
                widgetToInsert.setZindex(widgetToInsert.getZindex() + 1);

                // put widget with maximum zIndex to the head
                widgetsToUpdate.add(0, widgetToInsert);
            } else if (widget.getZindex() > widgetToInsert.getZindex()) {
                // widgetToSave's zIndex is unique, so no need to update any widgetToSave
                break;
            }
        }

        // widgetsToUpdate is already reverse sorted by zIndex, so saving one by one doesn't produce zIndex duplicates
        widgetsToUpdate.forEach(widgetRepository::save);

        widgetToSave.setLastModified(LocalDateTime.now());

        final Widget savedWidget = widgetRepository.save(widgetToSave);
        searchTree.update(savedWidget);

        return savedWidget;
    }
}
