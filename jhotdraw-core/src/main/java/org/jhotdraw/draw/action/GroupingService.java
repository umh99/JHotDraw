package org.jhotdraw.draw.action;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;

import java.util.Collection;
import java.util.LinkedList;

public class GroupingService {

    public Collection<Figure> ungroup(DrawingView view, CompositeFigure group) {
        Drawing drawing = view.getDrawing();

        LinkedList<Figure> figures = snapshotChildren(group);

        beginSelectionUpdate(view);
        detachChildrenFromGroup(group);
        replaceGroupWithFigures(drawing, group, figures);
        selectFigures(view, figures);

        return figures;
    }

    public void group(DrawingView view, CompositeFigure group, Collection<Figure> figures) {
        Drawing drawing = view.getDrawing();

        Collection<Figure> sorted = sortForStableOrder(drawing, figures);
        int insertionIndex = firstFigureIndex(drawing, sorted);

        removeFiguresFromDrawing(drawing, figures);
        beginSelectionUpdate(view);

        insertGroupIntoDrawing(drawing, insertionIndex, group);
        moveFiguresIntoGroup(group, sorted);

        selectGroup(view, group);
    }

    private LinkedList<Figure> snapshotChildren(CompositeFigure group) {
        return new LinkedList<>(group.getChildren());
    }

    private void beginSelectionUpdate(DrawingView view) {
        view.clearSelection();
    }

    private void detachChildrenFromGroup(CompositeFigure group) {
        group.basicRemoveAllChildren();
    }

    private void replaceGroupWithFigures(Drawing drawing, CompositeFigure group, Collection<Figure> figures) {
        int index = drawing.indexOf(group);
        drawing.basicAddAll(index, figures);
        drawing.remove(group);
    }

    private void selectFigures(DrawingView view, Collection<Figure> figures) {
        view.addToSelection(figures);
    }

    private Collection<Figure> sortForStableOrder(Drawing drawing, Collection<Figure> figures) {
        return drawing.sort(figures);
    }

    private int firstFigureIndex(Drawing drawing, Collection<Figure> sorted) {
        return drawing.indexOf(sorted.iterator().next());
    }

    private void removeFiguresFromDrawing(Drawing drawing, Collection<Figure> figures) {
        drawing.basicRemoveAll(figures);
    }

    private void insertGroupIntoDrawing(Drawing drawing, int index, CompositeFigure group) {
        drawing.add(index, group);
    }

    private void moveFiguresIntoGroup(CompositeFigure group, Collection<Figure> sorted) {
        group.willChange();
        for (Figure f : sorted) {
            f.willChange();
            group.basicAdd(f);
        }
        group.changed();
    }

    private void selectGroup(DrawingView view, CompositeFigure group) {
        view.addToSelection(group);
    }
}
