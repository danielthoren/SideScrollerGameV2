package com.sidescroller.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by daniel on 2016-06-06.
 */
public class InputHandler extends InputAdapter {

    public InputHandler() {}

    /** Called when a key was pressed
     *
     * @param keycode one of the constants in {@link Input.Keys}
     * @return whether the input was processed */
    public boolean keyDown (int keycode){
        for (InputListener listener : SideScrollerGameV2.getCurrentMap().getInputListenerList()){
            listener.keyDown(keycode);
        }
        return true;
    }

    /** Called when a key was released
     *
     * @param keycode one of the constants in {@link Input.Keys}
     * @return whether the input was processed */
    public boolean keyUp (int keycode){
        for (InputListener listener : SideScrollerGameV2.getCurrentMap().getInputListenerList()){
            listener.keyUp(keycode);
        }
        return true;
    }

    /** Called when a key was typed
     *
     * @param character The character
     * @return whether the input was processed */
    public boolean keyTyped (char character){
        for (InputListener listener : SideScrollerGameV2.getCurrentMap().getInputListenerList()){
            listener.keyTyped(character);
        }
        return true;
    }
}
