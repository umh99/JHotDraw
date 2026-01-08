package org.jhotdraw.draw.figure;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class EllipseFigureTest {

    private EllipseFigure ellipse;

    @BeforeEach
    void setUp() {
        ellipse = new EllipseFigure(10, 20, 100, 50);
    }

    @Test
    void testInitialBounds() {
        Rectangle2D.Double bounds = ellipse.getBounds();
        assertEquals(10, bounds.x);
        assertEquals(20, bounds.y);
        assertEquals(100, bounds.width);
        assertEquals(50, bounds.height);
    }

    @Test
    void testSetBounds() {
        Point2D.Double anchor = new Point2D.Double(0, 0);
        Point2D.Double lead = new Point2D.Double(40, 20);

        ellipse.setBounds(anchor, lead);
        Rectangle2D.Double bounds = ellipse.getBounds();

        assertEquals(0, bounds.x);
        assertEquals(0, bounds.y);
        assertEquals(40, bounds.width);
        assertEquals(20, bounds.height);
    }

    @Test
    void testSetBoundsMinimumSize() {
        Point2D.Double anchor = new Point2D.Double(10, 10);
        Point2D.Double lead = new Point2D.Double(10, 10);

        ellipse.setBounds(anchor, lead);
        Rectangle2D.Double bounds = ellipse.getBounds();

        assertTrue(bounds.width >= 0.1);
        assertTrue(bounds.height >= 0.1);
    }

    @Test
    void testContainsPointInsideEllipse() {
        Point2D.Double insidePoint = new Point2D.Double(60, 45);
        assertTrue(ellipse.contains(insidePoint));
    }

    @Test
    void testContainsPointOutsideEllipse() {
        Point2D.Double outsidePoint = new Point2D.Double(200, 200);
        assertFalse(ellipse.contains(outsidePoint));
    }

    @Test
    void testTransformTranslation() {
        AffineTransform tx = AffineTransform.getTranslateInstance(10, 10);
        ellipse.transform(tx);

        Rectangle2D.Double bounds = ellipse.getBounds();
        assertEquals(20, bounds.x);
        assertEquals(30, bounds.y);
    }

    @Test
    void testCloneCreatesDeepCopy() {
        EllipseFigure clone = ellipse.clone();

        assertNotSame(ellipse, clone);
        assertEquals(ellipse.getBounds(), clone.getBounds());

        // Modify original
        ellipse.setBounds(
                new Point2D.Double(0, 0),
                new Point2D.Double(10, 10)
        );

        // Clone should remain unchanged
        assertNotEquals(ellipse.getBounds(), clone.getBounds());
    }

    @Test
    void testTransformRestoreData() {
        Object restoreData = ellipse.getTransformRestoreData();

        ellipse.setBounds(
                new Point2D.Double(0, 0),
                new Point2D.Double(20, 20)
        );

        ellipse.restoreTransformTo(restoreData);
        Rectangle2D.Double bounds = ellipse.getBounds();

        assertEquals(10, bounds.x);
        assertEquals(20, bounds.y);
        assertEquals(100, bounds.width);
        assertEquals(50, bounds.height);
    }
}
