package com.niz;

import com.badlogic.gdx.utils.IntMap;

/**
 * Created by niz on 29/06/2014.
 */
public class Input {
    public enum Code  {BUTTON_JUMP, WALK_FORWARDS, WALK_BACKWARDS, STRAFE_LEFT, STRAFE_RIGHT, WALK};
    IntMap<Code> keys = new IntMap<Code>(), padButtons = new IntMap<Code>();

}
