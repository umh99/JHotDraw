/*
 * @(#)GroupAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.GroupFigure;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * GroupAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GroupAction extends AbstractSelectedAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.groupSelection";
    protected CompositeFigure prototype;
    private  GroupingService groupingService = new GroupingService();

    /**
     * If this variable is true, this action groups figures.
     * If this variable is false, this action ungroups figures.
     */

    /**
     * Creates a new instance.
     */
    public GroupAction(DrawingEditor editor) {
        this(editor, new GroupFigure());
    }

    public GroupAction(DrawingEditor editor, CompositeFigure prototype) {
        this(editor, prototype, true);
    }

    public GroupAction(DrawingEditor editor, CompositeFigure prototype, boolean isGroupingAction) {
        super(editor);
        this.prototype = prototype;
        ResourceBundleUtil labels
                = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        labels.configureAction(this, ID);
        updateEnabledState();
    }

    @Override
    protected void updateEnabledState() {
        setEnabled(getView() != null && canGroup());
    }

    protected boolean canGroup() {
        return getView() != null && getView().getSelectionCount() > 1;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (!canGroup()) return;
        performGroup();
    }

    private void performGroup() {
        final DrawingView view = getView();
        final LinkedList<Figure> ungroupedFigures = new LinkedList<>(view.getSelectedFigures());
        final CompositeFigure group = (CompositeFigure) prototype.clone();

        UndoableEdit edit = createGroupEdit(view, group, ungroupedFigures);

        groupingService.group(view, group, ungroupedFigures);
        fireUndoableEditHappened(edit);
    }

    private UndoableEdit createGroupEdit(final DrawingView view, final CompositeFigure group, final LinkedList<Figure> ungroupedFigures) {
        return new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
                return labels.getString("edit.groupSelection.text");
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                groupingService.group(view, group, ungroupedFigures);
            }

            @Override
            public void undo() throws CannotUndoException {
                groupingService.ungroup(view, group);
                super.undo();
            }

            @Override
            public boolean addEdit(UndoableEdit anEdit) {
                return super.addEdit(anEdit);
            }
        };
    }

    // Keep these methods for subclasses like SplitAction
    public Collection<Figure> ungroupFigures(DrawingView view, CompositeFigure group) {
        return groupingService.ungroup(view, group);
    }

    public void groupFigures(DrawingView view, CompositeFigure group, Collection<Figure> figures) {
        groupingService.group(view, group, figures);
    }


}
