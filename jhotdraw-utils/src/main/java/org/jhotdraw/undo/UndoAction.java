package org.jhotdraw.undo;

import javax.swing.undo.CannotUndoException;

/**
 * Undo Action for use in a menu bar.
 */
class UndoAction extends AbstractUndoRedoAction  {

    public UndoAction(UndoRedoManager undoRedoManger) {
        super(undoRedoManger, "undo");
    }
    /**
     * Invoked when an action occurs.
     */
    @Override
    protected void performAction() {
        try {
            super.getManager().undo();
        } catch (CannotUndoException e) {
            System.err.println("Cannot undo: " + e);
            e.printStackTrace();
        }
    }
}