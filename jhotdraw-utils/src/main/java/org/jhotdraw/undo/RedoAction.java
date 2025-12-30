package org.jhotdraw.undo;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import java.awt.event.ActionEvent;

import static org.jhotdraw.undo.UndoRedoManager.getLabels;

/**
 * Redo Action for use in a menu bar.
 */
class RedoAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final UndoRedoManager manager;

    public RedoAction(UndoRedoManager undoRedoManger) {
        manager = undoRedoManger;
        getLabels().configureAction(this, "edit.redo");
        setEnabled(false);
//      .......
//      .......
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            manager.redo();
        } catch (CannotRedoException e) {
            System.out.println("Cannot redo: " + e);
        }
    }
}