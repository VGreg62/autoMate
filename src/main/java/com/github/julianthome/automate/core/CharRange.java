package com.github.julianthome.automate.core;

import com.github.julianthome.automate.utils.EscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class CharRange implements TransitionLabel{

    final static Logger LOGGER = LoggerFactory.getLogger(CharRange.class);

    public char min;
    public char max;

    public static CharRange ANY = new CharRange(Character.MIN_VALUE,
            Character.MAX_VALUE);


    public CharRange(char c) {
        min = c;
        max = c;
    }

    public CharRange(char min, char max) {
        this.min = (char)Math.min(min,max);
        this.max = (char)Math.max(min,max);
    }

    @Override
    public boolean match(TransitionLabel other) {

        if(other instanceof Epsilon)
            return true;

        if(other instanceof CharRange) {
            CharRange o = (CharRange)other;
            return min >= o.min && max <= o.max;
        }

        return false;
    }

    @Override
    public boolean match(char c) {
        return min <= c && max >= c;
    }

    @Override
    public boolean isEpsilon() {
        return false;
    }

    @Override
    public boolean isMatch() {
        return true;
    }

    @Override
    public boolean isConsecutive(TransitionLabel l) {

        if(l instanceof CharRange) {
            CharRange cr = (CharRange)l;

            if(isect(l) != null)
                return true;

            if(cr.max + 1 == min) {
                return true;
            }

            if(max + 1 == cr.min) {
                return true;
            }

        }
        return false;

    }

    @Override
    public TransitionLabel join(TransitionLabel l) {
        if(!isConsecutive(l))
            return null;

        CharRange cr = (CharRange)l;

        char min = (char)Math.min(this.min, cr.min);
        char max = (char)Math.max(this.max, cr.max);

        assert min <= max;

        return new CharRange(min, max);
    }

    @Override
    public CharRange isect(TransitionLabel other) {
        if(other instanceof Epsilon)
            return this.clone();

        if(other instanceof CharRange) {
            CharRange cr = (CharRange)other;

            if(!hasOverlap(cr))
                return null;

            char mmin = (char)Math.max(min,cr.min);
            char mmax = (char)Math.min(max,cr.max);

            return new CharRange(mmin, mmax);
        }

        return null;
    }

    @Override
    public boolean contains(TransitionLabel l) {

        if(l instanceof Epsilon) {
            return false;
        }

        CharRange cr = (CharRange)l;

        return min <= cr.min && max >= cr.max;

    }

    @Override
    public Collection<TransitionLabel> minus(TransitionLabel l) {

        Set<TransitionLabel> ret = new HashSet<>();

        if(l instanceof Epsilon) {
            ret.add(this.clone());
        } else {
            CharRange other = (CharRange)l;

            CharRange is = null;

            if ((is = isect(other)) != null) {

                LOGGER.debug("is {}", is);
                if(is.min > Character.MIN_VALUE) {
                    CharRange mm = new CharRange(Character.MIN_VALUE, (char)
                            (is.min-1));

                    if(this.isect(mm) != null) {
                        ret.add(this.isect(mm));
                    }

                    if(other.isect(mm) != null) {
                        ret.add(other.isect(mm));
                    }
                }

                if(is.max < Character.MAX_VALUE) {

                    CharRange mm = new CharRange((char)(is.max+1), Character.MAX_VALUE);

                    if(this.isect(mm) != null) {
                        ret.add(this.isect(mm));
                    }

                    if(other.isect(mm) != null) {
                        ret.add(other.isect(mm));
                    }

                }

            }
        }

        return ret;
    }


    public boolean isSingleton() {
        return this.min == this.max;
    }

    @Override
    public CharRange clone() {
        return new CharRange(this.min, this.max);
    }

    @Override
    public int compareTo(TransitionLabel lbl) {

        LOGGER.debug("lbl {} vs {}", this, lbl);
        if(lbl instanceof Epsilon)
            return 1;

        if(lbl instanceof CharRange) {

            CharRange cr = (CharRange) lbl;

            if(equals(cr))
                return 0;

            if(min < cr.min)
                return -1;
            if(min > cr.min)
                return 1;
            if(max > cr.max)
                return -1;
            if(max < cr.min)
                return 1;

        }

        assert false;
        return 0;
    }

    public char getMin() {
        return min;
    }

    public void setMin(char min) {
        this.min = min;
    }

    public char getMax() {
        return max;
    }

    public void setMax(char max) {
        this.max = max;
    }

    public boolean hasOverlap(CharRange r) {

        boolean distict = max < r.min || min > r.max;

        return !distict;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof CharRange))
            return false;

        CharRange c = (CharRange)o;

        return c.min == min && c.max == max;
    }

    @Override
    public int hashCode() {
        int hc = 0;
        hc = 37 * hc + min;
        return 37 * hc + max;
    }

    @Override
    public String toDot() {
        if(isSingleton()) {
            return Character.toString(this.min);
        } else {
            String out = "[" + getCharString(min) + "-" + getCharString(max)
                    +"]";
            return EscapeUtils.escapeSpecialCharacters(out);
        }
    }

    static String getCharString(char c) {
        StringBuilder b = new StringBuilder();
        if (c >= 0x21 && c <= 0x7e && c != '\\' && c != '"')
            b.append(c);
        else {
            b.append("\\u");
            String s = Integer.toHexString(c);
            if (c < 0x10)
                b.append("000").append(s);
            else if (c < 0x100)
                b.append("00").append(s);
            else if (c < 0x1000)
                b.append("0").append(s);
            else
                b.append(s);
        }
        return b.toString();
    }

    @Override
    public String toString(){
        return "[" + min + "," + max + "]";
    }
}
