package com.github.julianthome.automate.core;

import com.github.julianthome.automate.utils.EscapeUtils;

/**
 * Created by julian on 28/04/2017.
 */
public class CharRange implements TransitionLabel{

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
    public TransitionLabel isect(TransitionLabel other) {
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

    public boolean isSingleton() {
        return this.min == this.max;
    }

    @Override
    public CharRange clone() {
        return new CharRange(this.min, this.max);
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
            String out = "[" + min + "-" + max +"]";
            return EscapeUtils.escapeSpecialCharacters(out);
        }
    }

    @Override
    public String toString(){
        return "[" + min + "," + max + "]";
    }
}
