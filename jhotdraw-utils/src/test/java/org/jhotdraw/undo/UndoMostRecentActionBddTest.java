package org.jhotdraw.undo;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

import javax.swing.undo.AbstractUndoableEdit;

import static org.junit.Assert.assertTrue;

public class UndoMostRecentActionBddTest extends ScenarioTest<
        UndoMostRecentActionBddTest.Given,
        UndoMostRecentActionBddTest.When,
        UndoMostRecentActionBddTest.Then> {

    @Test
    public void undo_the_most_recent_action() {
        given().an_undo_manager_with_one_action();
        when().the_user_clicks_undo();
        then().the_action_is_undone_and_redo_is_available();
    }

    public static class Given extends Stage<Given> {
        @ProvidedScenarioState UndoRedoManager manager;
        @ProvidedScenarioState TestEdit edit;

        public Given an_undo_manager_with_one_action() {
            manager = new UndoRedoManager();
            edit = new TestEdit();
            manager.addEdit(edit);
            return self();
        }
    }

    public static class When extends Stage<When> {
        @ProvidedScenarioState UndoRedoManager manager;

        public When the_user_clicks_undo() {
            manager.undo();
            return self();
        }
    }

    public static class Then extends Stage<Then> {
        @ProvidedScenarioState UndoRedoManager manager;
        @ProvidedScenarioState TestEdit edit;

        public Then the_action_is_undone_and_redo_is_available() {
            assertTrue("Undo should be called on the edit", edit.undoCalled);
            assertTrue("Redo should be available after undo", manager.canRedo());
            return self();
        }
    }

    static class TestEdit extends AbstractUndoableEdit {
        boolean undoCalled = false;

        @Override
        public void undo() {
            super.undo();
            undoCalled = true;
        }

        @Override
        public boolean isSignificant() {
            return true;
        }
    }
}
