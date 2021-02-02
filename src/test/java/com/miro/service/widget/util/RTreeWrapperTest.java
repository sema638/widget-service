package com.miro.service.widget.util;

import com.miro.service.widget.model.Widget;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RTreeWrapperTest {
    @Test
    void add() {
        RTreeWrapper tree = new RTreeWrapper();

        Widget widget = new Widget(1L, 0, 0, 1, 100, 100, LocalDateTime.now());
        tree.add(widget);

        assertEquals(1, tree.tree.size());
        assertEquals(1, tree.geometryMap.size());
    }

    @Test
    void delete() {
        RTreeWrapper tree = new RTreeWrapper();

        Widget widget = new Widget(1L, 0, 0, 1, 100, 100, LocalDateTime.now());
        tree.add(widget);

        assertEquals(1, tree.tree.size());
        assertEquals(1, tree.geometryMap.size());

        tree.delete(widget);
        assertEquals(0, tree.tree.size());
        assertEquals(0, tree.geometryMap.size());
    }

    @Test
    void search() {
        RTreeWrapper tree = new RTreeWrapper();

        List<Widget> widgets = List.of(
                new Widget(1L, 0, 0, 1, 100, 100, LocalDateTime.now()),
                new Widget(2L, 0, 50, 2, 100, 100, LocalDateTime.now()),
                new Widget(3L, 50, 0, 3, 100, 100, LocalDateTime.now()));

        for (Widget w : widgets) {
            tree.add(w);
        }

        List<Widget> result = tree.search(0, 0, 100, 150);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void searchFoundsNothing() {
        RTreeWrapper tree = new RTreeWrapper();

        List<Widget> widgets = List.of(
                new Widget(1L, 0, 0, 1, 100, 100, LocalDateTime.now()),
                new Widget(2L, 0, 50, 2, 100, 100, LocalDateTime.now()),
                new Widget(3L, 50, 0, 3, 100, 100, LocalDateTime.now()));

        for (Widget w : widgets) {
            tree.add(w);
        }

        List<Widget> result = tree.search(0, 150, 100, 250);

        assertEquals(0, result.size());
    }
}
