package com.niz.component.systems;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.niz.actions.AStand;
import com.niz.actions.AWalk;
import com.niz.actions.Action;
import com.niz.actions.ActionList;
import com.niz.component.ButtonInput;
import com.niz.component.Move;
import com.niz.component.Physics;
import com.niz.component.Position;
import com.niz.observer.Subject;

/**
 * Created by niz on 10/07/2014.
 */
public class FPSInputSystem extends InputSystem {


    private static final String TAG = "fps input";
    private ComponentMapper<Physics> physM;
    private ComponentMapper<Position> posM;
    private ComponentMapper<ActionList> actM;
    private ComponentMapper<Move> moveM;
    private CameraSystem cameraSys;
    private Vector2 tmp = new Vector2();

    @Override
    public void onNotify(Entity e, Subject.Event event, Component c) {
        if (c instanceof ButtonInput){
            ButtonInput b = (ButtonInput) c;
            button(b, event, e);

        }

    }

    private void button(ButtonInput b, Subject.Event event, Entity e) {
        if (event == Subject.Event.BUTTON_IS_PRESSED)
            switch (b.code){
                case TOGGLE_JETPACK :
                    Physics phys = physM.get(e);
                    if (phys == null) break;
                    Position pos = posM.get(e);
                    if (pos == null) break;
                    pos.pos.set(phys.oldPosition);
                    pos.pos.y = 60f;

                    break;
                case BURROW : 
                	 Physics physs = physM.get(e);
                     if (physs == null) break;
                     Position poss = posM.get(e);
                     if (poss == null) break;
                     physs.oldPosition.y -= .6f;
                     poss.pos.set(physs.oldPosition);
                     
                	break;
                case CLEAR_MESHES:
                    world.getSystem(VoxelSystem.class).voxelWorld.clearAllMeshes();
                    break;

                case WALK_FORWARDS:
                    ActionList act = actM.get(e);
                    //Gdx.app.log(TAG, "wa;l");
                    if (!act.containsAction(AWalk.class)){

                        AWalk walk = Pools.obtain(AWalk.class);
                        Action stand = act.getAction(AStand.class);
                        if (stand != null)
                            stand.isFinished = true;
                        act.addToStart(walk);
                    }
                    tmp.x = cameraSys.camera.direction.x;
                    tmp.y = cameraSys.camera.direction.z;
                    // Gdx.app.log(TAG, "dir ;l "+tmp);

                    float rot = tmp.angle();
                    moveM.get(e).rotation = rot;
                    break;
                case BUTTON_JUMP:
                    Move move = moveM.get(e);
                    move.startJumping(e);

                    break;
                case STRAFE_LEFT:
                    moveM.get(e).rotation += 90;
                    break;
                case STRAFE_RIGHT:
                    moveM.get(e).rotation -= 90;
                    break;
            } else if (event == Subject.Event.BUTTON_RELEASE)
            switch (b.code){
                case TOGGLE_JETPACK:

                    break;
                case WALK_FORWARDS:
                    ActionList act = actM.get(e);
                    //Gdx.app.log(TAG, "waggggggggggggggggg;l");

                    if (act.containsAction(AWalk.class)){


                        act.addToStart(AStand.class);
                        act.getAction(AWalk.class).isFinished = true;
                    }

                    break;
                case BUTTON_JUMP:
                    Move move = moveM.get(e);
                    move.stopJumping(e);

                    break;
            }
    }

    @Override
    public void initialize() {
        super.initialize();
        cameraSys = world.getSystem(CameraSystem.class);
        physM = world.getMapper(Physics.class);
        posM = world.getMapper(Position.class);
        actM = world.getMapper(ActionList.class);
        moveM = world.getMapper(Move.class);
    }
}
