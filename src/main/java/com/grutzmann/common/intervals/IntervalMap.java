package com.grutzmann.common.intervals;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author melli on 13.03.16.
 * use under LGPL
 */
public class IntervalMap<X extends Comparable<X>, Y> {
    @Nonnull
    private TreeMap<Interval<X>,Y> map= new TreeMap<>();

    @Nonnull
    private Y defaultValue;

    public IntervalMap(@Nonnull Y y) {
        this.defaultValue= y;
    }

    public void set(@Nonnull Interval<X> interval, @Nonnull Y y) {
        Interval<X> tobeInsertedInterval= null;  Y tobeInsertedValue= null;
        for(Iterator<Map.Entry<Interval<X>,Y> > iterator =map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Interval<X>,Y> entry= iterator.next();
            int cmp= interval.compareTo(entry.getKey());
            if(cmp<-1) break;
            if(cmp==0||cmp==-1||cmp==1) {
                if(y.equals(entry.getValue())) {
                    iterator.remove();
                    interval = Interval.supportInterval(interval, entry.getKey());
                    if(cmp<0)  break;
                }else if(cmp==0) {
                    iterator.remove();
                    Interval.Result<X> difference = entry.getKey().minus(interval);
                    if(difference.getFirst().isPresent()) {
                        if (tobeInsertedInterval == null) {
                            tobeInsertedInterval = difference.getFirst().get();  tobeInsertedValue = entry.getValue();
                        } else {
                            map.put(difference.getFirst().get(), entry.getValue());
                            break;
                        }
                        if (difference.getSecond().isPresent()) {
                            map.put(difference.getSecond().get(), entry.getValue());
                            break;
                        }
                    }
                }else if(cmp<0)  break;
            }
        }
        if(tobeInsertedInterval!=null) map.put(tobeInsertedInterval,tobeInsertedValue);
        map.put(interval,y);
    }

    @Nonnull
    public Y evalf(@Nonnull X x) {
        Y result= map.get(Interval.point(x));
        if(result!=null) return result;
        return defaultValue;
    }

    @Nonnull
    @Override
    public String toString() {
        StringBuilder result= new StringBuilder();
        for(final Map.Entry<Interval<X>,Y> pair :map.entrySet()) {
            result.append(pair.getKey().toString());
            result.append(" -> ");
            result.append(pair.getValue().toString());
            result.append("; ");
        }
        result.append("otherwise -> ");
        result.append(defaultValue.toString());
        return result.toString();
    }

}
