package org.jhotdraw.draw.figure;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.action.GroupingService;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GroupingServiceTest {

    private GroupingService service;

    private DrawingView view;
    private Drawing drawing;
    private CompositeFigure group;

    @Before
    public void setUp() {
        service = new GroupingService();

        view = mock(DrawingView.class);
        drawing = mock(Drawing.class);
        group = mock(CompositeFigure.class);

        when(view.getDrawing()).thenReturn(drawing);
    }

    @Test
    public void group_shouldMoveFiguresIntoCompositeAndSelectGroup() {
        // Arrange
        Figure f1 = mock(Figure.class);
        Figure f2 = mock(Figure.class);
        Collection<Figure> input = Arrays.asList(f1, f2);

        List<Figure> sorted = Arrays.asList(f2, f1);
        when(drawing.sort(input)).thenReturn(sorted);
        when(drawing.indexOf(sorted.iterator().next())).thenReturn(5);

        // Act
        service.group(view, group, input);

        // Assert (core behaviour)
        verify(drawing).basicRemoveAll(input);
        verify(view).clearSelection();
        verify(drawing).add(5, group);

        verify(group).willChange();
        verify(f2).willChange();
        verify(group).basicAdd(f2);
        verify(f1).willChange();
        verify(group).basicAdd(f1);
        verify(group).changed();

        verify(view).addToSelection(group);
    }

    @Test
    public void ungroup_shouldRestoreChildrenAndSelectThem() {
        // Arrange
        Figure c1 = mock(Figure.class);
        Figure c2 = mock(Figure.class);
        List<Figure> children = Arrays.asList(c1, c2);

        when(group.getChildren()).thenReturn(children);
        when(drawing.indexOf(group)).thenReturn(3);

        // Act
        Collection<Figure> result = service.ungroup(view, group);

        // Assert (returned figures)
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(children));

        // Assert (core behaviour)
        verify(view).clearSelection();
        verify(group).basicRemoveAllChildren();

        verify(drawing).basicAddAll(3, children);
        verify(drawing).remove(group);

        verify(view).addToSelection(children);
    }
}
