package com.sidescroller.game;

import com.badlogic.gdx.Input.Keys;

/**
 * This interface listens to key events.
 */
public interface InputListener extends GameObject
{
    /** Called when a key was pressed
     *
     * @param keycode one of the constants in {@link Keys}
     * */
    void keyDown(int keycode);

    /** Called when a key was released
     *
     * @param keycode one of the constants in {@link Keys}
     * */
    void keyUp (int keycode);
}
