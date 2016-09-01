package com.sidescroller.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Every objects that needs updating must implement this interface.
 */
public interface Draw extends GameObject
{
    /**
     * The function that draws the object every frame
     * @param batch The SpriteBatch with wich to draw
     * @param layer The draw layer that is supposed to be drawn
     */
    void draw(SpriteBatch batch, int layer);
}
