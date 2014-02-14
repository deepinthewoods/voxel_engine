package com.niz.actions;

public class ASync extends Action {

	@Override
	public void update(float dt) {
		if (parent.getRoot() == this)
			isFinished = true;

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnd() {
		// TODO Auto-generated method stub

	}

}
