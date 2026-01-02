package org.jhotdraw.undo;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class AbstractUndoRedoAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final UndoRedoManager manager;

    public AbstractUndoRedoAction(UndoRedoManager undoRedoManager, String ID) {
        this.manager = undoRedoManager;
        UndoRedoManager.getLabels().configureAction(this, "edit." + ID);
        setEnabled(false);
    }
    @Override
    public void actionPerformed(ActionEvent evt) {
        performAction();
    }

    protected abstract void performAction();

    protected UndoRedoManager getManager() {
        return this.manager;
    }
}