package org.jhotdraw.draw.figure;

import org.jhotdraw.geom.BezierPath;
import org.junit.Test;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BezierFigureTest {
    @Test
    public void setClosed_shouldUpdateFigureAndUnderlyingPath(){
        BezierFigure f = new BezierFigure(false);

        assertFalse(f.isClosed());
        assertFalse(f.getBezierPath().isClosed());

        f.setClosed(true);

        assertTrue(f.isClosed());
        assertTrue(f.getBezierPath().isClosed());

        f.setClosed(false);

        assertFalse(f.isClosed());
        assertFalse(f.getBezierPath().isClosed());
    }

    @Test
    public void contains_closedTriangle_pointInsideShouldReturnTrue(){
        BezierFigure f = new BezierFigure(true);
        f.addNode(new BezierPath.Node(0, 0));
        f.addNode(new BezierPath.Node(10, 0));
        f.addNode(new BezierPath.Node(0, 10));

        assertTrue("Point should be inside closed triangle",
                f.contains(new Point2D.Double(2, 2)));
    }

    @Test
    public void contains_closedTriangle_pointFarOutsideShouldReturnFalse(){
        BezierFigure f = new BezierFigure(true);
        f.addNode(new BezierPath.Node(0, 0));
        f.addNode(new BezierPath.Node(10, 0));
        f.addNode(new BezierPath.Node(0, 10));

        assertFalse("Point far outside should not be contained",
                f.contains(new Point2D.Double(100, 100)));
    }

    @Test
    public void contains_openLine_pointNearOutlineShouldReturnTrue(){
        BezierFigure f = new BezierFigure(false);
        f.addNode(new BezierPath.Node(0, 0));
        f.addNode(new BezierPath.Node(10, 0));

        assertTrue("Point near open line should be considered contained (outline)",
                f.contains(new Point2D.Double(5, 1)));
    }
}