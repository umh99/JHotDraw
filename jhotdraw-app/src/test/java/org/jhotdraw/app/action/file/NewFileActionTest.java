package org.jhotdraw.app.action.file;

import org.jhotdraw.api.app.Application;
import org.jhotdraw.api.app.View;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.ArgumentCaptor;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NewFileActionTest {

    @Test
    public void actionPerformed_createsAddsExecutesAndShows_newView_withMultiOpenId1_whenNoOtherViews() {
        Application app = mock(Application.class);
        View newView = mock(View.class);

        when(app.createView()).thenReturn(newView);
        when(app.views()).thenReturn(Collections.emptyList());

        NewFileAction action = new NewFileAction(app);

        action.actionPerformed(mock(ActionEvent.class));

        verify(newView).setMultipleOpenId(1);

        verify(app).add(newView);
        verify(app).show(newView);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(newView).execute(runnableCaptor.capture());

        Runnable r = runnableCaptor.getValue();
        assertNotNull("Runnable passed to newView.execute(...) must not be null", r);

        verify(newView, never()).clear();

        r.run();
        verify(newView).clear();

        InOrder inOrder = inOrder(newView, app);
        inOrder.verify(newView).setMultipleOpenId(1);
        inOrder.verify(app).add(newView);
        inOrder.verify(newView).execute(any(Runnable.class));
        inOrder.verify(app).show(newView);
    }

    @Test
    public void actionPerformed_setsMultiOpenId_toMaxNullUriMultipleOpenIdPlus1() throws Exception {
        Application app = mock(Application.class);
        View newView = mock(View.class);

        View v1 = mock(View.class);
        when(v1.getURI()).thenReturn(null);
        when(v1.getMultipleOpenId()).thenReturn(1);

        View v2 = mock(View.class);
        when(v2.getURI()).thenReturn(null);
        when(v2.getMultipleOpenId()).thenReturn(7);

        View v3 = mock(View.class);
        when(v3.getURI()).thenReturn(new URI("file:///tmp/saved.svg"));
        when(v3.getMultipleOpenId()).thenReturn(999);

        when(app.createView()).thenReturn(newView);
        when(app.views()).thenReturn(Arrays.asList(v1, v2, v3));

        NewFileAction action = new NewFileAction(app);

        action.actionPerformed(mock(ActionEvent.class));

        verify(newView).setMultipleOpenId(8);
    }

    @Test
    public void actionPerformed_ignoresViewsWithNonNullUri_whenComputingMultiOpenId() throws Exception {
        Application app = mock(Application.class);
        View newView = mock(View.class);

        View saved = mock(View.class);
        when(saved.getURI()).thenReturn(new URI("file:///tmp/saved.svg"));
        when(saved.getMultipleOpenId()).thenReturn(1234);

        when(app.createView()).thenReturn(newView);
        when(app.views()).thenReturn(Collections.singletonList(saved));

        NewFileAction action = new NewFileAction(app);

        action.actionPerformed(mock(ActionEvent.class));

        verify(newView).setMultipleOpenId(1);
    }
}
