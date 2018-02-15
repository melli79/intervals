package com.grutzmann.common.intervals;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author melli on 13.03.16.
 */
public class IntervalMapTest {
    public final double epsilon= 1e-16;
    public final double INF= Double.POSITIVE_INFINITY;

    @Test
    public void testConstantMap() {
        IntervalMap<Double,Double> f= new IntervalMap<>(0d);
        System.out.println("Created map "+f);
        assertEquals(0d,(double)f.evalf(0d),epsilon);
    }

    @Test
    public void testStepFunction() {
        IntervalMap<Double,Double> heaviside= new IntervalMap<>(0d);
        heaviside.set(Interval.open(0d,INF).get(),1d);
        System.out.println("Heaviside function "+heaviside);
        assertEquals(0d,heaviside.evalf(-1d),epsilon);
        assertEquals(1d,heaviside.evalf(1d),epsilon);
    }

    @Test
    public void testSignum() {
        IntervalMap<Double,Double> sgn= new IntervalMap<>(0d);
        sgn.set(Interval.open(-INF,INF).get(),-1d);
        sgn.set(Interval.point(0d),0d);
        sgn.set(Interval.open(0d,INF).get(),1d);
        System.out.println("sgn= "+sgn);
        assertEquals(-1d,sgn.evalf(-2d),epsilon);
        assertEquals(0d, sgn.evalf(0d),epsilon);
        assertEquals(1d, sgn.evalf(2d),epsilon);
    }

}
