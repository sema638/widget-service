package com.miro.service.widget.util;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.miro.service.widget.model.Widget;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RTreeWrapper {
    @VisibleForTesting
    volatile RTree<Widget, Geometry> tree = RTree.create();

    @VisibleForTesting
    final Map<Long, Geometry> geometryMap = new ConcurrentHashMap<>();

    public synchronized void add(final Widget widget) {
        final Rectangle geometry = Geometries.rectangle(widget.getX(), widget.getY(),
                widget.getX() + widget.getWidth() - 1, widget.getY() + widget.getHeight() - 1);
        tree = tree.add(widget, geometry);
        geometryMap.put(widget.getId(), geometry);
    }

    public synchronized void update(final Widget widget) {
        delete(widget);
        add(widget);
    }

    public synchronized void delete(final Widget widget) {
        if (geometryMap.containsKey(widget.getId())) {
            tree = tree.delete(widget, geometryMap.get(widget.getId()));
            geometryMap.remove(widget.getId());
        }
    }

    public List<Widget> search(final int left, final int bottom, final int right, final int top) {
        final Rectangle area = Geometries.rectangle(left, bottom, right, top);
        final Observable<Entry<Widget, Geometry>> results = tree.search(area);

        final List<Widget> result = new ArrayList<>();
        results.map(Entry::value)
                .filter(widget -> area.contains(widget.getX(), widget.getY())
                        && area.contains(widget.getX() + widget.getWidth(), widget.getY() + widget.getHeight()))
                .subscribe(result::add);

        return result;
    }
}
