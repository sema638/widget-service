package com.miro.service.widget.repository.impl;

import com.miro.service.widget.model.Widget;
import com.miro.service.widget.repository.WidgetRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryWidgetRepository implements WidgetRepository {
    private final Map<Long, Widget> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public List<Widget> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Widget> findById(final long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Widget save(final Widget widget) {
        widget.setId(idGenerator.incrementAndGet());
        storage.put(idGenerator.incrementAndGet(), widget);
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
