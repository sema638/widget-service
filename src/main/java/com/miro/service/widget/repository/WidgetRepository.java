package com.miro.service.widget.repository;

import com.miro.service.widget.model.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WidgetRepository {
    Page<Widget> findAll(Pageable pageable);

    Optional<Widget> findById(long id);

    Optional<Widget> findTopByOrderByZindexDesc();

    List<Widget> findByZindexGreaterThanEqualOrderByZindex(int zIndex);

    Widget save(Widget widget);

    void deleteById(long id);
}
