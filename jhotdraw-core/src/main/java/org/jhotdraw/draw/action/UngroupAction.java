/*
 * @(#)UngroupAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

/**
 * UngroupAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class UngroupAction extends GroupAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.ungroupSelection";
    protected final GroupingService groupingService = new GroupingService();

    public UngroupAction(DrawingEditor editor) {
        this(editor, new GroupFigure());
    }

    public UngroupAction(DrawingEditor editor, CompositeFigure prototype) {
        super(editor, prototype);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        updateEnabledState();
    }

    @Override
    protected void updateEnabledState() {
        setEnabled(getView() != null && canUngroup());
    }

    protected boolean canUngroup() {
        return getView() != null
                && getView().getSelectionCount() == 1
                && prototype != null
                && getView().getSelectedFigures().iterator().next().getClass().equals(prototype.getClass());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!canUngroup()) return;

        final DrawingView view = getView();
        final CompositeFigure group =
                (CompositeFigure) view.getSelectedFigures().iterator().next();
        final LinkedList<Figure> ungroupedFigures = new LinkedList<>();

        UndoableEdit edit = createUngroupEdit(view, group, ungroupedFigures);

        ungroupedFigures.addAll(groupingService.ungroup(view, group));
        fireUndoableEditHappened(edit);
    }

    private UndoableEdit createUngroupEdit(final DrawingView view, final CompositeFigure group, final LinkedList<Figure> ungroupedFigures) {
        return new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
                return labels.getString("edit.ungroupSelection.text");
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                groupingService.ungroup(view, group);
            }

            @Override
            public void undo() throws CannotUndoException {
                groupingService.group(view, group, ungroupedFigures);
                super.undo();
            }
        };
    }
}
