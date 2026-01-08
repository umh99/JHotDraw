/*
 * @(#)CreationTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.CompositeFigure;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.util.*;

/**
 * A {@link Tool} to create a new figure by drawing its bounds. The figure to be created is
 * specified by a prototype.
 * <p>
 * To create a figure using the {@code CreationTool}, the user does the following mouse gestures on
 * a DrawingView:
 * <ol>
 * <li>Press the mouse button over the DrawingView. This defines the start point of the Figure
 * bounds.</li>
 * <li>Drag the mouse while keeping the mouse button pressed, and then release the mouse button.
 * This defines the end point of the Figure bounds.</li>
 * </ol>
 * The CreationTool works well with most figures that fit into a rectangular shape or that concist
 * of a single straight line. For figures that need additional editing after these mouse gestures,
 * the use of a specialized creation tool is recommended. For example the TextTool allows to enter
 * the text into a TextFigure after the user has performed the mouse gestures.
 * <p>
 * Alltough the mouse gestures might be fitting for the creation of a connection, the CreationTool
 * is not suited for the creation of a ConnectionFigure. Use the ConnectionTool for this type of
 * figures instead.
 * <p>
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p>
 * <em>Prototype</em><br>
 * The creation tool creates new figures by cloning a prototype figure object. That's the reason why
 * {@code Figure} extends the {@code Cloneable} interface.
 * <br>
 * Prototype: {@link Figure}; Client: {@link CreationTool}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CreationTool extends AbstractTool {

    private static final long serialVersionUID = 1L;

    protected Map<AttributeKey<?>, Object> prototypeAttributes;
    protected String presentationName;
    protected Dimension minimalSizeTreshold = new Dimension(2, 2);
    protected Dimension minimalSize = new Dimension(40, 40);
    protected Figure prototype;
    protected Figure createdFigure;
    private boolean isToolDoneAfterCreation = true;

    /* =========================
       Constructors
       ========================= */

    public CreationTool(String prototypeClassName) {
        this(prototypeClassName, null, null);
    }

    public CreationTool(String prototypeClassName, Map<AttributeKey<?>, Object> attributes) {
        this(prototypeClassName, attributes, null);
    }

    public CreationTool(String prototypeClassName,
                        Map<AttributeKey<?>, Object> attributes,
                        String name) {
        try {
            this.prototype = (Figure) Class.forName(prototypeClassName).newInstance();
        } catch (Exception e) {
            InternalError error =
                    new InternalError("Unable to create Figure from " + prototypeClassName);
            error.initCause(e);
            throw error;
        }
        this.prototypeAttributes = attributes;
        if (name == null) {
            ResourceBundleUtil labels =
                    ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
            name = labels.getString("edit.createFigure.text");
        }
        this.presentationName = name;
    }

    public CreationTool(Figure prototype) {
        this(prototype, null, null);
    }

    public CreationTool(Figure prototype, Map<AttributeKey<?>, Object> attributes) {
        this(prototype, attributes, null);
    }

    @Deprecated
    public CreationTool(Figure prototype,
                        Map<AttributeKey<?>, Object> attributes,
                        String name) {
        this.prototype = prototype;
        this.prototypeAttributes = attributes;
        if (name == null) {
            ResourceBundleUtil labels =
                    ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
            name = labels.getString("edit.createFigure.text");
        }
        this.presentationName = name;
    }

    public Figure getPrototype() {
        return prototype;
    }

    protected Figure getCreatedFigure() {
        return createdFigure;
    }

    protected Figure getAddedFigure() {
        return createdFigure;
    }

    public void setToolDoneAfterCreation(boolean newValue) {
        isToolDoneAfterCreation = newValue;
    }

    public boolean isToolDoneAfterCreation() {
        return isToolDoneAfterCreation;
    }



    @Override
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        if (getView() != null) {
            getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    @Override
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        if (getView() != null) {
            getView().setCursor(Cursor.getDefaultCursor());
        }
        if (createdFigure != null) {
            layoutIfComposite(createdFigure);
            createdFigure = null;
        }
    }



    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        if (getView() == null) {
            return;
        }
        getView().clearSelection();
        createdFigure = createFigure();
        Point2D.Double p = constrainPoint(viewToDrawing(anchor), createdFigure);
        anchor.x = evt.getX();
        anchor.y = evt.getY();
        createdFigure.setBounds(p, p);
        getDrawing().add(createdFigure);
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        if (createdFigure != null) {
            Point2D.Double p =
                    constrainPoint(new Point(evt.getX(), evt.getY()), createdFigure);
            createdFigure.willChange();
            createdFigure.setBounds(
                    constrainPoint(new Point(anchor.x, anchor.y), createdFigure),
                    p);
            createdFigure.changed();
        }
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        if (createdFigure == null) {
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
            return;
        }

        if (removeIfZeroSize()) {
            createdFigure = null;
            return;
        }

        ensureMinimalSize(evt);
        layoutIfComposite(createdFigure);
        registerUndo(createdFigure);
        invalidateBounds(evt);
        creationFinished(createdFigure);
        createdFigure = null;
    }


    private boolean removeIfZeroSize() {
        Rectangle2D.Double bounds = createdFigure.getBounds();
        if (bounds.width == 0 && bounds.height == 0) {
            getDrawing().remove(createdFigure);
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
            return true;
        }
        return false;
    }

    private void ensureMinimalSize(MouseEvent evt) {
        Rectangle2D.Double bounds = createdFigure.getBounds();
        if (Math.abs(anchor.x - evt.getX()) < minimalSizeTreshold.width
                && Math.abs(anchor.y - evt.getY()) < minimalSizeTreshold.height) {

            createdFigure.willChange();
            createdFigure.setBounds(
                    constrainPoint(new Point(anchor.x, anchor.y), createdFigure),
                    constrainPoint(
                            new Point(anchor.x
                                    + (int) Math.max(bounds.width, minimalSize.width),
                                    anchor.y
                                            + (int) Math.max(bounds.height, minimalSize.height)),
                            createdFigure));
            createdFigure.changed();
        }
    }

    private void layoutIfComposite(Figure f) {
        if (f instanceof CompositeFigure) {
            ((CompositeFigure) f).layout();
        }
    }

    private void registerUndo(final Figure addedFigure) {
        final Drawing addedDrawing = getDrawing();
        addedDrawing.fireUndoableEditHappened(new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                return presentationName;
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                addedDrawing.remove(addedFigure);
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                addedDrawing.add(addedFigure);
            }
        });
    }

    private void invalidateBounds(MouseEvent evt) {
        Rectangle r = new Rectangle(anchor.x, anchor.y, 0, 0);
        r.add(evt.getX(), evt.getY());
        maybeFireBoundsInvalidated(r);
    }


    @SuppressWarnings("unchecked")
    protected Figure createFigure() {
        Figure f = prototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (prototypeAttributes != null) {
            for (Map.Entry<AttributeKey<?>, Object> entry
                    : prototypeAttributes.entrySet()) {
                f.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
            }
        }
        return f;
    }

    protected void creationFinished(Figure createdFigure) {
        if (createdFigure.isSelectable()) {
            getView().addToSelection(createdFigure);
        }
        if (isToolDoneAfterCreation()) {
            fireToolDone();
        }
    }

    @Override
    public void updateCursor(DrawingView view, Point p) {
        if (view.isEnabled()) {
            view.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }
}