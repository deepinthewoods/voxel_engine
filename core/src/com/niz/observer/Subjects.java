package com.niz.observer;

import com.badlogic.gdx.utils.IntMap;

/**
 * Created by niz on 27/05/2014.
 */
public class Subjects {
    private static IntMap<Subject> subjects = new IntMap<Subject>();
    private static IntMap<String> hashes = new IntMap<String>();

    public static Subject get(String string){
        return get(hash(string));
    }

    public static Subject get(int hash){
        if (subjects.containsKey(hash)) return subjects.get(hash);
        Subject sub = new Subject();
        subjects.put(hash, sub);
        return sub;
    }

    public static int hash(String s){
        int hash = s.hashCode();
        if (!hashes.containsKey(hash)){
            hashes.put(hash, s);
        }


        return hash;
    }

    public static String nameOfHash(int hash){
        return hashes.get(hash);
    }
}
