package com.niz;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.Input.*;
/**
 * Created by niz on 29/06/2014.
 */
public class Input {
    public enum Code  {BUTTON_JUMP, WALK_FORWARDS, WALK_BACKWARDS, STRAFE_LEFT, STRAFE_RIGHT, WALK, TOGGLE_JETPACK, CLEAR_MESHES};
    public static IntMap<Code> keys = new IntMap<Code>();
    static IntMap<Code> padButtons = new IntMap<Code>();




    static {

        keys.put(Keys.J, Code.TOGGLE_JETPACK);
        keys.put(Keys.C, Code.CLEAR_MESHES);
        keys.put(Keys.SPACE, Code.BUTTON_JUMP);
        keys.put(Keys.W, Code.WALK_FORWARDS);
        keys.put(Keys.A, Code.STRAFE_LEFT);
        keys.put(Keys.S, Code.WALK_BACKWARDS);
        keys.put(Keys.D, Code.STRAFE_RIGHT);
}
}
