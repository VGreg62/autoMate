package com.github.julianthome.automate.utils;

/**
 * Created by julian on 01/05/2017.
 */
public class Tuple<K, V> {
    public K key;
    public V val;

    public Tuple(K key, V val) {
        this.key = key;
        this.val = val;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getVal() {
        return val;
    }

    public void setVal(V val) {
        this.val = val;
    }


    @Override
    public String toString() {
        return key.toString() + " " + val.toString();
    }

    @Override
    public int hashCode() {
        int hc = 0;
        hc = 37 * hc + key.hashCode();
        return 37 * hc + val.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Tuple))
            return false;

        Tuple<K,V> t = (Tuple<K,V>)o;

        return key.equals(t.key) && val.equals(t.val);
    }
}

