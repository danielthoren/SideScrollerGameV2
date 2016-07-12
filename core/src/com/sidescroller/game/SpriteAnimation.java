package com.sidescroller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by daniel on 2016-07-12.
 */
public class SpriteAnimation  {

	private int frameColumns;
	private int frameRows;

	private Animation walkAnimation;
	private Texture spriteSheet;
	private TextureRegion[] walkFrames;
	private TextureRegion currentFrame;

	private int framesPerSek;
	private float stateTime;

	public SpriteAnimation(int framesPerSek, int frameColumns, int frameRows, Texture spriteSheet) {
		this.frameColumns = frameColumns;
		this.frameRows = frameRows;
		this.spriteSheet = spriteSheet;
		this.framesPerSek = framesPerSek;

		TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/frameColumns, spriteSheet.getHeight()/frameRows);
		walkFrames = new TextureRegion[frameColumns * frameRows];
		int index = 0;
		for (int i = 0; i < frameRows; i++) {
			for (int j = 0; j < frameColumns; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}
		walkAnimation = new Animation((float) (1f/framesPerSek), walkFrames);
		stateTime = 0f;
	}

	/**
	 * The function that draws the object every frame
	 * @param batch The SpriteBatch with wich to draw
	 * @param layer The draw layer that is supposed to be drawn
	 */
	public void draw(SpriteBatch batch, int layer){
		stateTime += Gdx.graphics.getDeltaTime();
		currentFrame = walkAnimation.getKeyFrame(stateTime, true);
		batch.draw(currentFrame, 1, 1);
	}
}
