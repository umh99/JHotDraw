package org.jhotdraw.undo;

import javax.swing.undo.CannotUndoException;

/**
 * Redo Action for use in a menu bar.
 */
class RedoAction extends AbstractUndoRedoAction  {
    public RedoAction(UndoRedoManager undoRedoManger) {
        super(undoRedoManger, "redo");
    }
    /**
     * Invoked when an action occurs.
     */
    @Override
    protected void performAction() {
        try {
        super.getManager().redo();
    } catch (CannotUndoException e) {
        System.err.println("Cannot undo: " + e);
        e.printStackTrace();
    }
}
}