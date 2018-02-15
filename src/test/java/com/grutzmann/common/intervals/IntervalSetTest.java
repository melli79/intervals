package com.grutzmann.common.intervals;

import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author melli on 08.03.16.
 */
public class IntervalSetTest {
    @Test
    public void testEmpty() {
        IntervalSet<Double> empty= IntervalSet.empty();
        System.out.println("empty set "+empty+".");
        assert empty.isEmpty();
        assert !empty.contains(1d);
    }

    @Test
    public void testContains() {
        IntervalSet<Double> interval= new IntervalSet<Double>(Interval.closed(1d,2d).get());
        assert interval.contains(Interval.closed(1d,2d).get());
        assert !interval.contains(0d);
        assert !interval.contains(3d);
        assert !interval.contains(Interval.closed(0d,2d).get());
    }

    @Test
    public void testIntervalUnion() {
        Interval<Double> interval= Interval.open(1d,2d).get();
        IntervalSet<Double> set= new IntervalSet<>(interval);
        System.out.println("Interval "+set+".");
        IntervalSet<Double> union= set.union(Interval.closed(0d,0.5).get());
        System.out.println("Union of two intervals "+union);
        assertEquals(2,union.getNumberOfIntervals());
        IntervalSet<Double> leftUnion= set.union(Interval.closed(0d,1d).get());
        System.out.println("Union of two overlapping intervals "+leftUnion);
        assert leftUnion.contains(0d);
        Interval<Double> rightInterval = Interval.open(2d, 3d).get();
        IntervalSet<Double> rightUnion= set.union(rightInterval);
        System.out.println("Union of two tipping intervals "+rightUnion);
        assert !rightUnion.contains(2d);
        assert rightUnion.contains(interval);
        assert rightUnion.contains(rightInterval);
    }

    @Test
    public void testIntervalRemoval() {
        Interval<Double> interval= Interval.closed(1d,2d).get();
        IntervalSet<Double> set= new IntervalSet<Double>(interval);
        assert set.minus(interval).isEmpty();
        IntervalSet<Double> twoIntervals= set.minus(Interval.point(1.5));
        System.out.println("interval with removed one point "+twoIntervals);
        assertEquals(2,twoIntervals.getNumberOfIntervals());
        assert !twoIntervals.contains(1.5);

        IntervalSet<Double> result= new IntervalSet<Double>(Interval.leftClosedRightOpen(2d,5d).get());
        result.remove(Interval.leftClosedRightOpen(2d,3d).get());
        assertEquals(1,result.getNumberOfIntervals());
        result.remove(Interval.leftClosedRightOpen(4d,5d).get());
        System.out.println("Interval with two removals "+result);
        assertEquals(1,result.getNumberOfIntervals());
        assert result.equals(new IntervalSet<Double>(Interval.leftClosedRightOpen(3d,4d).get()));
    }

    @Test
    public void testComplexRemoval() {
        Interval<Double>[] intervals= new Interval[]{Interval.point(0d),Interval.open(1d,2d).get(),Interval.closed(3d,4d).get()};
        IntervalSet<Double> set= new IntervalSet<>(toList(intervals));
        Interval<Double> interval = Interval.leftOpenRightClosed(0d, 3.5).get();
        IntervalSet<Double> result= set.minus(interval);
        System.out.println("Removing "+interval+" from "+set+" gives: "+result);
        assertEquals(2,result.getNumberOfIntervals());
        assert result.contains(0d);
        assert result.contains(Interval.leftOpenRightClosed(3.5,4d).get());
    }

    @Nonnull
    private<T> List<T> toList(@Nonnull T[] elements) {
        ArrayList<T> result = new ArrayList<>();
        Collections.addAll(result, elements);
        return result;
    }

}
