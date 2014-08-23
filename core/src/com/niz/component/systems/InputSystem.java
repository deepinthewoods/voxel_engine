package com.niz.component.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.niz.Input;
import com.niz.component.ButtonInput;
import com.niz.component.Player;
import com.niz.component.Position;
import com.niz.observer.Observer;
import com.niz.observer.Subject;
import com.niz.observer.Subjects;

import java.util.Iterator;

/**
 * Created by niz on 10/07/2014.
 */
public abstract class InputSystem  extends EntitySystem implements Observer {
    private static final String TAG = "Input System";
    private Subject playerControl;
    private ButtonInput c = new ButtonInput();
    /**
     * Creates an entity system that uses the specified filter
     * as a matcher against entities.
     *
     */
    public InputSystem() {
        super(Aspect.getAspectForAll(Player.class));
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        if (entities.size == 0) return;
        IntMap tmp = pressed;
        pressed = currentlyPressed;
        currentlyPressed = tmp;
        currentlyPressed.clear();
        Iterator<IntMap.Entry<Input.Code>> i = Input.keys.iterator();
        while (i.hasNext()){
            IntMap.Entry<Input.Code> entry = i.next();

            if (Gdx.input.isKeyPressed(entry.key)){
                currentlyPressed.put(entry.key, entry.value);
                c.code = entry.value;

                Entity playerEntity = entities.get(0);
                //Gdx.app.log(TAG, "player pos "+playerEntity.getComponent(Position.class).pos.y);

                playerControl.notify(playerEntity, Subject.Event.BUTTON_IS_PRESSED, c);
            } else {
                if (pressed.containsKey(entry.key)){//unpressed
                    Entity playerEntity = entities.get(0);
                    c.code = entry.value;
                    playerControl.notify(playerEntity, Subject.Event.BUTTON_RELEASE, c);

                }
            }

        }
    }

    private IntMap<Input.Code> pressed = new IntMap<Input.Code>(), currentlyPressed = new IntMap<Input.Code>();

    @Override
    public void initialize() {
        playerControl = Subjects.get("playerControl");
        playerControl.add(this);
    }


}
