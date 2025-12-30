package org.jhotdraw.undo;

import javax.swing.*;
import javax.swing.undo.CannotUndoException;
import java.awt.event.ActionEvent;

/**
 * Undo Action for use in a menu bar.
 */
class UndoAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final UndoRedoManager manager;

    public UndoAction(UndoRedoManager undoRedoManger) {
        manager = undoRedoManger;
        UndoRedoManager.getLabels().configureAction(this, "edit.undo");
        setEnabled(false);
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            manager.undo();
        } catch (CannotUndoException e) {
            System.err.println("Cannot undo: " + e);
            e.printStackTrace();
        }
    }
}