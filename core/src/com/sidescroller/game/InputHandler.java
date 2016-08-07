package com.sidescroller.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * This class is instanciated in the main class and added to the 'libGDX' library. Theese functions are then called
 * when inputs are registered.
 */
public class InputHandler extends InputAdapter {

    /** Called when a key was pressed
     *
     * @param keycode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public boolean keyDown (int keycode){
        for (InputListener listener : SideScrollerGameV2.getCurrentMap().getInputListenerList()){
            listener.keyDown(keycode);
        }
        return true;
    }

    /** Called when a key was released
     *
     * @param keycode one of the constants in {@link Keys}
     * @return whether the input was processed */
    public boolean keyUp (int keycode){
        for (InputListener listener : SideScrollerGameV2.getCurrentMap().getInputListenerList()){
            listener.keyUp(keycode);
        }
        return true;
    }
}
