package com.niz;

/**
 * Created by niz on 28/05/2014.
 */
public class Strings {
    public static CharSequence getFloat(float f) {
        return floats[(int)(f*10f)];
    }
    static String[] floats = new String[1000];
    static String regex = "%.1f";
    static {
        for (int i = 0; i < 1000; i++){
            floats[i] = String.format(regex, i*.1f);
        }

    }
}
