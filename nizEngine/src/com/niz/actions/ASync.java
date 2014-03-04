package com.niz.actions;

import com.artemis.World;

public class ASync extends Action {

	@Override
	public void update(float dt) {
		if (parentList.getRoot() == this)
			isFinished = true;

	}

	@Override
	public void onStart(World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub

	}

	

}
