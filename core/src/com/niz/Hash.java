package com.niz;

import com.badlogic.gdx.utils.IntMap;

/**
 * Created by niz on 28/05/2014.
 */
public class Hash {
    public static int hash(String s){
        int hash = s.hashCode();
        if (!hashes.containsKey(hash)){
            hashes.put(hash, s);
        }


        return hash;
    }

    private static IntMap<String> hashes = new IntMap<String>();
}
