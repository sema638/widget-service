package com.miro.service.widget.repository.impl;

import com.miro.service.widget.model.Widget;
import com.miro.service.widget.repository.WidgetRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("database")
public interface DbWidgetRepository extends JpaRepository<Widget, Long>, WidgetRepository {
}
