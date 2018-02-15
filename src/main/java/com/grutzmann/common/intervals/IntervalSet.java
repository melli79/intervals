package com.grutzmann.common.intervals;

/***
 * @author melli 16/3/8
 * interval sets, i.e. sets of real number like objects that can contain single points 
 * and intervals.
 * use under LGPL
 ***/

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class IntervalSet<T extends Comparable<T> > implements Comparable<IntervalSet<T> >  {
    @Nonnull
    private final SortedSet<Interval<T> > intervals= new TreeSet<>();

    public IntervalSet(@Nonnull Collection<Interval<T> > intervals) {
        addAll(intervals);
    }

    public IntervalSet(@Nonnull Interval<T> interval) {
        intervals.add(interval);
    }

    @Nonnull
    public static<T extends Comparable<T> >  IntervalSet<T> empty() {
        return new IntervalSet<>(Collections.EMPTY_LIST);
    }

    public void add(@Nonnull Interval<T> interval) {
        for(Iterator<Interval<T> > iterator= intervals.iterator(); iterator.hasNext();) {
            Interval<T> myInterval= iterator.next();
            int cmp= myInterval.compareTo(interval);
            if(cmp>0) break;
            if(cmp==-1||cmp==0||cmp==1) {
                iterator.remove();
                interval= Interval.supportInterval(interval,myInterval);
            }
        }
        intervals.add(interval);
    }

    public void remove(@Nonnull Interval<T> interval) {
        Interval<T> tobeReinserted= null;
        for(Iterator<Interval<T> > iterator= intervals.iterator(); iterator.hasNext();) {
            Interval<T> myInterval= iterator.next();
            int cmp= interval.compareTo(myInterval);
            if(cmp<0) return;
            if(cmp==0) {
                iterator.remove();
                Interval.Result<T> result= myInterval.minus(interval);
                if(result.getFirst().isPresent()) {
                    tobeReinserted= result.getFirst().get();
                    if(result.getSecond().isPresent()) {
                        intervals.add(result.getSecond().get());
                        break;
                    }
                }
            }
        }
        if(tobeReinserted!=null) intervals.add(tobeReinserted);
    }

    public void addAll(@Nonnull Collection<Interval<T> > intervals) {
        for(final Interval<T> interval :intervals) {
            add(interval);
        }
    }

    public void add(@Nonnull IntervalSet<T> set) {
        if(set==this) return;
        addAll(set.intervals);
    }

    public void removeAll(@Nonnull Collection<Interval<T> > intervals) {
        for(final Interval<T> interval :intervals) {
            remove(interval);
        }
    }

    public void remove(@Nonnull IntervalSet<T> set) {
        if(set==this) intervals.clear();
        else removeAll(set.intervals);
    }

    public boolean contains(@Nonnull Interval<T> interval) {
        for(final Interval<T> myInterval :intervals) {
            int cmp= interval.compareTo(myInterval);
            if(cmp<0) return false;
            if(cmp==0) return myInterval.contains(interval);
        }
        return false;
    }

    public boolean contains(@Nonnull T value) {
        return contains(Interval.point(value));
    }

    @Override
    public int compareTo(@Nonnull IntervalSet<T> set) {
        Interval<T> first= intervals.first(), setFirst= set.intervals.first();
        Interval<T> last= intervals.last(),  setLast= set.intervals.last();
        int cmp= last.compareTo(setFirst);
        if(cmp<0) return cmp;
        int cmp2= first.compareTo(setLast);
        if(cmp2>0) return cmp2;
        return 0;
    }

    @Nonnull
    @Override
    public String toString() {
        if(intervals.isEmpty()) return "{}";
        StringBuilder result= new StringBuilder();
        boolean first= true;
        for(final Interval<T> interval :intervals) {
            if(first) first= false;
            else result.append(" u");
            result.append(interval);
        }
        return result.toString();
    }

    @Nonnull
    public IntervalSet<T> union(@Nonnull Interval<T> interval) {
        IntervalSet<T> result= new IntervalSet<T>(intervals);
        result.add(interval);
        return result;
    }

    public int getNumberOfIntervals() {
        return intervals.size();
    }

    public boolean isEmpty() {
        return intervals.isEmpty();
    }

    @Nonnull
    public IntervalSet<T> minus(@Nonnull Interval<T> interval) {
        IntervalSet<T> result= new IntervalSet<T>(intervals);
        result.remove(interval);
        return result;
    }

    public boolean equals(@Nullable IntervalSet<T> set) {
        return set!=null && intervals.equals(set.intervals);
    }

}
