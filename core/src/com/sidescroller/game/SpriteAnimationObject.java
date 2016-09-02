package com.sidescroller.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * A SpriteAnimation that can be positioned in the world.
 */
public class SpriteAnimationObject extends SpriteAnimation implements Draw
{
	private final long id;
	private int layer;
	private SideScrollGameV2 sideScrollGameV2;

	/**
	 * Creates a simple 2d animation. Only workes with evenly spaced spriteSheets without gaps. Can be placed in the gameworld.
	 * @param framesPerSek The framerate with wich the sprite will be updated.
	 * @param frameColumns The amount of columns in the spriteSheet.
	 * @param frameRows The amount of rows in the sptireSheet.
	 * @param position The position of the animation.
	 * @param size The size of the animation image.
	 * @param angle The angle of the animation image.
     * @param spriteSheet The spriteSheet.
	 * @param id The id of the object
     */
	public SpriteAnimationObject(final int framesPerSek, final int frameColumns, final int frameRows, final Vector2 position,
								 final Vector2 size, final float angle, final Texture spriteSheet, final long id, int layer, SideScrollGameV2 sideScrollGameV2)
	{
		super(framesPerSek, frameColumns, frameRows, position, size, angle, spriteSheet);
		this.id = id;
		this.sideScrollGameV2 = sideScrollGameV2;
		this.layer = layer;
	}

	/**
	 * Draws the object by calling superclass. If the animation is done and not looping this object is removed from the gameworld.
	 * @param batch The SpriteBatch with wich to draw
	 * @param layer The draw layer that is supposed to be drawn
	 */
	public void draw(SpriteBatch batch, int layer){
		if (this.layer == layer) {
			if (!isDone() || loopAnimation) {
				super.draw(batch, layer);
			} else {
				sideScrollGameV2.getCurrentMap().removeDrawObject(this);
			}
		}
	}

	public TypeOfGameObject getTypeOfGameObject(){
		return TypeOfGameObject.OTHER;
	}

	public long getId(){
		return id;
	}
}
