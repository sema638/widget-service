package com.miro.service.widget.repository;

import com.miro.service.widget.model.Widget;

import java.util.List;
import java.util.Optional;

public interface WidgetRepository {
    List<Widget> findAll();

    Optional<Widget> findById(long id);

    Widget save(Widget widget);

    void deleteById(long id);
}
