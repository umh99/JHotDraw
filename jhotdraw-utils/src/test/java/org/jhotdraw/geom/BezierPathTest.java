package org.jhotdraw.geom;

import org.testng.annotations.Test;

import java.awt.*;
import java.awt.geom.Point2D;

import static org.testng.Assert.*;

public class BezierPathTest {
    @Test
    public void toPolygonArray_shouldReturnC0PointsInSameOrder(){
        BezierPath p = new BezierPath();
        p.add(new Point2D.Double(0, 0));
        p.add(new Point2D.Double(10, 0));
        p.add(new Point2D.Double(10, 10));
        p.add(new Point2D.Double(0, 10));
        p.setClosed(true);

        Point2D.Double[] pts = p.toPolygonArray();

        assertEquals(4, pts.length);
        assertEquals(0.0, pts[0].x, 0.0001);
        assertEquals(0.0, pts[0].y, 0.0001);
        assertEquals(10.0, pts[1].x, 0.0001);
        assertEquals(0.0, pts[1].y, 0.0001);
        assertEquals(10.0, pts[2].x, 0.0001);
        assertEquals(10.0, pts[2].y, 0.0001);
        assertEquals(0.0, pts[3].x, 0.0001);
        assertEquals(10.0, pts[3].y, 0.0001);

    }

    @Test
    public void toPolygonArray_emptyPath_shouldReturnEmptyArray() {
        BezierPath p = new BezierPath();

        Point2D.Double[] pts = p.toPolygonArray();

        assertNotNull(pts);
        assertEquals(0, pts.length);
    }
}