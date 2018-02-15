package com.grutzmann.common.intervals;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author melli on 08.03.16.
 */
public class IntervalTest {

    @Test
    public void testOpen() {
        assert !Interval.open(2d,1d).isPresent();
        assert !Interval.open(1d,1d).isPresent();
        Interval<Double> openInterval= Interval.open(1d,2d).orNull();
        assert openInterval!=null;
        System.out.println("open interval "+openInterval+".");
        assert openInterval.isOpen();
        assert !openInterval.isClosed();
        assert !openInterval.contains(1d);
        assert !openInterval.contains(2d);
        assert openInterval.contains(1.5);
        assert !openInterval.contains(0.5);
        assert !openInterval.contains(2.5);
    }

    @Test
    public void testClosed() {
        assert !Interval.closed(2d,1d).isPresent();
        Interval<Double> point = Interval.point(1d);
        assert point.contains(1d);
        Interval<Double> closedInterval= Interval.closed(1d,2d).get();
        System.out.println("Closed interval "+closedInterval+".");
        assert !closedInterval.isOpen();
        assert closedInterval.isClosed();
        assert closedInterval.contains(1d);
        assert closedInterval.contains(2d);
    }

    @Test
    public void testLeftOpenRightClosed() {
        assert !Interval.leftOpenRightClosed(1d,1d).isPresent();
        Interval<Double> leftOpen= Interval.leftOpenRightClosed(1d,2d).orNull();
        assert leftOpen!=null;
        System.out.println("left open interval "+leftOpen+".");
        assert !leftOpen.isOpen();
        assert !leftOpen.isClosed();
        assert !leftOpen.contains(1d);
        assert leftOpen.contains(2d);
    }

    @Test
    public void testLeftClosedRightOpen() {
        assert !Interval.leftClosedRightOpen(1d,1d).isPresent();
        Interval<Double> leftClosed= Interval.leftClosedRightOpen(1d,2d).get();
        System.out.println("half open interval "+leftClosed+".");
        assert !leftClosed.isOpen();
        assert !leftClosed.isClosed();
        assert leftClosed.contains(1d);
        assert !leftClosed.contains(2d);
    }

    @Test
    public void testPoint() {
        Interval<Double> point= Interval.point(1d);
        System.out.println("Single point "+point+".");
        assert point.isClosed();
        assert !point.isOpen();
        assert point.contains(1d);
    }

    @Test
    public void testCompareTo() {
        Interval<Double> interval= Interval.open(1d,2d).orNull();
        assert interval!=null;
        Interval<Double> left= Interval.open(0d,0.5).get();
        assertEquals(2,interval.compareTo(left));
        Interval<Double> leftPoint= Interval.point(1d);
        assertEquals(1,interval.compareTo(leftPoint));

        Interval<Double> right= Interval.open(2.5,3d).get();
        assertEquals(-2, interval.compareTo(right));

        Interval<Double> rightPoint= Interval.point(2d);
        assertEquals(-1,interval.compareTo(rightPoint));

        Interval<Double> closed= Interval.closed(1d,2d).get();
        assertEquals(0,closed.compareTo(leftPoint));
        assertEquals(0,closed.compareTo(rightPoint));
    }

    @Test
    public void testContains() {
        Interval<Double> interval= Interval.open(1d,2d).get();
        assert interval.contains(Interval.closed(1.1,1.2).get());
        assert !interval.contains(Interval.leftClosedRightOpen(1d,2d).get());
        assert !interval.contains(Interval.leftOpenRightClosed(1d,2d).get());
        assert interval.contains(interval);
    }

    @Test
    public void testSupport() {
        Interval<Double> interval= Interval.open(1d,2d).get();
        Interval<Double> intervalLeft= Interval.closed(0d,1d).get();
        Interval<Double> leftJoin= Interval.supportInterval(interval,intervalLeft);
        System.out.println("left join "+leftJoin);
        assert leftJoin.contains(interval);
        assert leftJoin.contains(intervalLeft);
        Interval<Double> rightJoin= Interval.supportInterval(intervalLeft,interval);
        System.out.println("right join "+rightJoin);
        assert rightJoin.contains(interval);
        assert rightJoin.contains(intervalLeft);
        Interval<Double> sameInterval= Interval.supportInterval(interval,interval);
        System.out.println("identical join "+sameInterval);
        assert sameInterval.equals(interval);
    }

    @Test
    public void testRemove() {
        Interval<Double> interval= Interval.closed(1d,2d).get();

        Interval.Result<Double> result= interval.minus(Interval.leftOpenRightClosed(1.5,2d).get());
        System.out.println("Removed right end "+result);
        assertEquals(1,result.getNumComponents());
        assertEquals(Interval.closed(1d,1.5).get(),result.getFirst().get());

        result= interval.minus(Interval.open(1d,2d).get());
        System.out.println("Removed interior "+result);
        assertEquals(2,result.getNumComponents());
        assertEquals(Interval.point(1d),result.getFirst().get());
        assertEquals(Interval.point(2d),result.getSecond().get());

        result= interval.minus(Interval.closed(1.2,1.7).get());
        System.out.println("Removed a piece in the middle "+result);
        assertEquals(2,result.getNumComponents());
        assertEquals(Interval.leftClosedRightOpen(1d,1.2).get(),result.getFirst().get());
        assertEquals(Interval.leftOpenRightClosed(1.7,2d).get(),result.getSecond().get());

        result= interval.minus(Interval.open(0d,2d).get());
        System.out.println("Removed all except right point "+result);
        assertEquals(1,result.getNumComponents());
        assertEquals(Interval.point(2d),result.getFirst().get());

        result= interval.minus(Interval.leftOpenRightClosed(0d,1.5).get());
        System.out.println("Removed left half "+result);
        assertEquals(1,result.getNumComponents());
        assertEquals(Interval.closed(1.5,2d).get(),result.getFirst().get());

        result= interval.minus(Interval.closed(0d,1d).get());
        System.out.println("Removed left end "+result);
        assertEquals(1,result.getNumComponents());
        assertEquals(Interval.leftOpenRightClosed(1d,2d).get(),result.getFirst().get());
    }

    @Test
    public void testIntersect() {
        Interval<Double> interval= Interval.closed(1d,2d).get();
        Interval<Double> result= interval.intersect(Interval.open(1d,1.5).get()).orNull();
        System.out.println("Intersection with open interval "+result);
        assertEquals(Interval.open(1d,1.5d).get(),result);
    }

}
