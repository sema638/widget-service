package com.miro.service.widget.repository;

import com.miro.service.widget.model.Widget;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WidgetRepository {
    List<Widget> findAll(Pageable pageable);

    Optional<Widget> findById(long id);

    Widget save(Widget widget);

    void deleteById(long id);
}
