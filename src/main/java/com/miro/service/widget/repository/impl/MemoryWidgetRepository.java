package com.miro.service.widget.repository.impl;

import com.miro.service.widget.model.Widget;
import com.miro.service.widget.repository.WidgetRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MemoryWidgetRepository implements WidgetRepository {
    private final Map<Long, Widget> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public List<Widget> findAll(final Pageable pageable) {
        return storage.values().stream()
                .sorted(Comparator.comparingLong(Widget::getZIndex))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Widget> findById(final long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Widget save(final Widget widget) {
        if (widget.getId() == null) {
            widget.setId(idGenerator.incrementAndGet());
        }
        storage.put(widget.getId(), widget);
        return widget;
    }

    @Override
    public void deleteById(final long id) {
        if (!storage.containsKey(id)) {
            throw new EntityNotFoundException();
        }

        storage.remove(id);
    }
}
