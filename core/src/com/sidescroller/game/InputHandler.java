package com.sidescroller.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * This class is instanciated in the main class and added to the 'libGDX' library. Theese functions are then called
 * when inputs are registered.
 */
public class InputHandler extends InputAdapter {

	private SideScrollGameV2 sideScrollGameV2;

	public InputHandler(SideScrollGameV2 sideScrollGameV2) {
		super();
		this.sideScrollGameV2 = sideScrollGameV2;
	}

	/** Called when a key is pressed
     *
     * @param keyCode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public boolean keyDown (int keyCode){
        for (InputListener listener : sideScrollGameV2.getCurrentMap().getInputListenerList()){
            listener.keyDown(keyCode);
        }
        return true;
    }

    /** Called when a key is released
     *
     * @param keyCode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public boolean keyUp (int keyCode){
        for (InputListener listener : sideScrollGameV2.getCurrentMap().getInputListenerList()){
            listener.keyUp(keyCode);
        }
        return true;
    }
}
