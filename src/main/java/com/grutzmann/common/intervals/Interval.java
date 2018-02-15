package com.grutzmann.common.intervals;

import com.google.common.base.Optional;
import com.grutzmann.common.annotation.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/***
 * @author melli on 16/3/12
 * Interval of a real like type
 * use under LGPL
 ***/
@Value
public class Interval<T extends Comparable<T> > implements Comparable<Interval<T> >  {
    @Nonnull
    private final T begin, end;
    private final boolean withB, withE;

    @Nonnull
    public static<T extends Comparable<T> > Optional<Interval<T> >  open(@Nonnull T begin, @Nonnull T end) {
        if(begin.compareTo(end)<0) return Optional.of(new Interval<T>(begin,end,false,false));
        return Optional.absent();
    }

    @Nonnull
    public static<T extends Comparable<T> > Optional<Interval<T> >  closed(@Nonnull T begin, @Nonnull T end) {
        if(begin.compareTo(end)<=0)  return Optional.of(new Interval<T>(begin,end,true,true));
        return Optional.absent();
    }

    @Nonnull
    public static<T extends Comparable<T> > Interval<T> point(@Nonnull T value) {
        return new Interval<>(value,value,true,true);
    }

    @Nonnull
    public static<T extends Comparable<T> > Optional<Interval<T> >  leftOpenRightClosed(@Nonnull T begin, @Nonnull T end) {
        if(begin.compareTo(end)<0) return Optional.of(new Interval<T>(begin,end,false,true));
        return Optional.absent();
    }

    @Nonnull
    public static<T extends Comparable<T> > Optional<Interval<T> >  leftClosedRightOpen(@Nonnull T begin, @Nonnull T end) {
        if(begin.compareTo(end)<0) return Optional.of(new Interval<T>(begin,end,true,false));
        return Optional.absent();
    }

    private Interval(@Nonnull T begin, @Nonnull T end, boolean withB, boolean withE) {
        this.begin= begin;  this.end= end;
        this.withB= withB;  this.withE= withE;
    }

    public boolean contains(@Nonnull T value) {
        int less= value.compareTo(begin);
        if(less<0) return false;
        if(less==0) return withB;
        int bigger= value.compareTo(end);
        if(bigger>0) return false;
        if(bigger==0) return withE;
        return true;
    }

    @Override
    public int compareTo(@Nonnull Interval<T> interval) {
        int less= interval.end.compareTo(begin);
        if(less<0) return 2;
        if(less==0) {
            if(!interval.withE&&!withB) return 2;
            if(!interval.withE||!withB) return 1;
            return 0;
        }
        int bigger= interval.begin.compareTo(end);
        if(bigger>0) return -2;
        if(bigger==0) {
            if(!interval.withB&&!withE) return -2;
            if(!interval.withB||!withE) return -1;
        }
        return 0;
    }

    public boolean isOpen() {
        return !withB&&!withE;
    }

    public boolean isClosed() {
        return withB&&withE;
    }

    public boolean contains(@Nonnull Interval<T> interval) {
        int left= begin.compareTo(interval.begin);
        if(left>0) return false;
        if(left==0&&interval.withB&&!withB) return false;
        int right= end.compareTo(interval.end);
        if(right<0) return false;
        if(right==0&&interval.withE&&!withE) return false;
        return true;
    }

    @Nonnull
    public static <T extends Comparable<T> > Interval<T> supportInterval(@Nonnull Interval<T> interval, @Nonnull Interval<T> interval2) {
        int cmp= interval.begin.compareTo(interval2.begin);
        T begin,end;
        boolean withB, withE;
        if(cmp<0) {
            begin= interval.begin;  withB= interval.withB;
        }else if(cmp==0) {
            begin= interval.begin;  withB= interval.withB||interval2.withB;
        }else {
            begin= interval2.begin; withB= interval2.withB;
        }
        cmp= interval.end.compareTo(interval2.end);
        if(cmp<0) {
            end= interval2.end;  withE= interval2.withE;
        }else if(cmp==0) {
            end= interval2.end;  withE= interval.withE||interval2.withE;
        }else {
            end= interval.end; withE= interval.withE;
        }
        return new Interval<>(begin,end,withB,withE);
    }

    @Nonnull
    public Result<T> minus(@Nonnull Interval<T> interval) {
        int cmp= begin.compareTo(interval.begin);
        int cmp2= end.compareTo(interval.end);
        if(cmp<0) {
            return minusWithLeft(interval, cmp2);
        }else if(cmp==0) {
            Interval<T> first= null;
            if(withB&&!interval.withB) first= point(begin);
            if(cmp2<0) {
                return new Result<>(first);
            }else if(cmp2==0) {
                Interval<T> second= null;
                if(withE&&!interval.withE)  second= point(end);
                if(first!=null)  return new Result<>(first,second);
                return new Result<>(second);
            }else {
                Interval<T> second= new Interval<>(interval.end,end,!interval.withE,withE);
                if(first!=null) return new Result<>(first,second);
                return new Result<>(second);
            }
        }else {
            if(cmp2<0) {
                return new Result<>();
            }else if(cmp2==0) {
                boolean hasPoint= withE&&!interval.withE;
                if(hasPoint) return new Result<>(point(end));
                return new Result<>();
            }else {
                int cmp3 = begin.compareTo(interval.end);
                if(cmp3<0) {
                    return new Result<>(new Interval<>(interval.end,end,!interval.withB,withE));
                }else if(cmp3==0) {
                    boolean withB= this.withB&&!interval.withE;
                    return new Result<>(new Interval<>(begin,end,withB,withE));
                }else {
                    return new Result<>(this);
                }
            }
        }
    }

    @Nonnull
    private Result<T> minusWithLeft(@Nonnull Interval<T> interval, int cmp2) {
        Interval<T> first = new Interval<>(begin, interval.begin, withB, !interval.withB);
        if(cmp2<0) {
            int cmp3= end.compareTo(interval.begin);
            if(cmp3<0) {
                return new Result<>(this);
            }else if(cmp3==0) {
                boolean withE= this.withE&&!interval.withB;
                return new Result<>(new Interval<>(begin,end,withB,withE));
            }else {
                return new Result<>(first);
            }
        }else if(cmp2==0) {
            boolean withE= this.withE&&!interval.withE;
            if(withE) {
                return new Result<>(first, point(end));
            }
            return new Result<>(first);
        }else {
            return new Result<>(first, new Interval<>(interval.end,end,!interval.withE,withE));
        }
    }

    @Override
    public String toString() {
        if(begin.compareTo(end)==0) return "{"+begin+"}";
        return (withB ? "[" :"(") +begin+","+end+ (withE ?"]" :")");
    }

    public boolean equals(@Nullable Interval interval) {
        if(interval==null) return false;
        return withB==interval.withB&&withE==interval.withE
                &&begin.equals(interval.begin)&&end.equals(interval.end);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if(o==null||!o.getClass().equals(getClass())) return false;
        return equals((Interval)o);
    }

    @Nonnull
    public Optional<Interval<T> >  intersect(Interval<T> interval) {
        int cmp= begin.compareTo(interval.begin);
        T begin;  boolean withB;
        if(cmp<0) {
            begin= interval.begin;  withB= interval.withB;
        }else if(cmp==0) {
            begin= this.begin;  withB= this.withB&&interval.withB;
        }else {
            begin= this.begin;  withB= this.withB;
        }
        int cmp2= end.compareTo(interval.end);
        T end;  boolean withE;
        if(cmp2<0) {
            end= this.end;  withE= this.withE;
        }else if(cmp2==0) {
            end=this.end;  withE= this.withE&&interval.withE;
        }else {
            end= interval.end;  withE= interval.withE;
        }
        int cmp3= begin.compareTo(end);
        if(cmp3<0||(cmp3==0&&withB&&withE))  return Optional.of(new Interval<T>(begin,end,withB,withE));
        return Optional.absent();
    }

    public static class Result<T extends Comparable<T> >  {
        @Nullable
        private final Interval<T> first, second;

        public Result() {
            first= null;  second= null;
        }

        public Result(@Nullable Interval<T> first) {
            this.first= first;  this.second= null;
        }

        public Result(@Nonnull Interval<T> first, @Nullable Interval<T> second) {
            this.first= first;  this.second= second;
        }

        public int getNumComponents() {
            if(second!=null)  return 2;
            if(first!=null) return 1;
            return 0;
        }

        public boolean isEmpty() {
            return first==null;
        }

        @Nonnull
        public Optional<Interval<T> > getFirst() {
            return Optional.fromNullable(first);
        }

        @Nonnull
        public Optional<Interval<T> >  getSecond() {
            return Optional.fromNullable(second);
        }

        @Nonnull
        @Override
        public String toString() {
            if(first==null) return "{}";
            if(second==null) return first.toString();
            return "Result("+first+","+second+")";
        }

    }
}
