package com.github.julianthome.automate.core;

/**
 * Created by julian on 09/05/2017.
 */
public interface AutomatonInterface <T> {
    T union(T other);
    T star();
    T plus();
    T optional();
    T repeat(int min, int max);
    T repeatMax(int max);
    T repeatMin(int min);
    T append(char c);
    T append(char min, char max);
    T concat(T other);
    T concat(T other, boolean accept);
    T intersect(T other);
    T determinize();
    T expand();
    T complement();
    T minus(T other);

}
