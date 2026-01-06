package org.jhotdraw.undo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

public abstract class AbstractUndoRedoAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final UndoRedoManager manager;

    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    AbstractUndoRedoAction(UndoRedoManager undoRedoManager, String id) {
        this.manager = undoRedoManager;
        UndoRedoManager.getLabels().configureAction(this, "edit." + id);
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