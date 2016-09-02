package com.sidescroller.map;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.sidescroller.game.InputListener;
import com.sidescroller.map.Map;

/**
 * This class is instanciated in the main class and added to the 'libGDX' library. Theese functions are then called
 * when inputs are registered.
 */
public class InputHandler extends InputAdapter {

	private Map map;

	public InputHandler(Map map) {
		this.map = map;
	}

	/** Called when a key is pressed
     *
     * @param keyCode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public boolean keyDown (int keyCode){
        for (InputListener listener : map.getInputListenerList()){
            listener.keyDown(keyCode);
        }
        return true;
    }

    /** Called when a key is released
     *
     * @param keyCode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public boolean keyUp (int keyCode){
        for (InputListener listener : map.getInputListenerList()){
            listener.keyUp(keyCode);
        }
        return true;
    }
}
