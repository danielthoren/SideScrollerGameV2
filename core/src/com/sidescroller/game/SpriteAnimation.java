package com.sidescroller.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Animates a evenly spaced sprite.
 */
public class SpriteAnimation  {

	private Animation animation;

	private Vector2 position;
	private Vector2 size;

	private int framesPerSek;
	private float angle;
	private float stateTime;

	protected boolean loopAnimation;

	/**
	 * Creates a simple 2d animation. Only workes with evenly spaced spriteSheets without gaps.
	 * @param framesPerSek The framerate with wich the sprite will be updated.
	 * @param frameColumns The amount of columns in the spriteSheet.
	 * @param frameRows The amount of rows in the sptireSheet.
	 * @param position The position of the animation.
	 * @param size The size of the animation image.
	 * @param angle The angle of the animation image.
     * @param spriteSheet The spriteSheet.
     */
	public SpriteAnimation(int framesPerSek, int frameColumns, int frameRows, Vector2 position, Vector2 size, float angle, Texture spriteSheet) {
		this.angle = angle;
		this.size = size;
		this.position = position;
		this.framesPerSek = framesPerSek;
		loopAnimation = true;

		TextureRegion[] walkFrames;

		TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/frameColumns, spriteSheet.getHeight()/frameRows);
		walkFrames = new TextureRegion[frameColumns * frameRows];
		int index = 0;
		for (int i = 0; i < frameRows; i++) {
			for (int j = 0; j < frameColumns; j++) {
				walkFrames[index] = tmp[i][j];
				index++;
			}
		}
		animation = new Animation( (1f / framesPerSek), walkFrames);
		stateTime = 0;
	}

	/**
	 * The function that draws the object every frame
	 * @param batch The SpriteBatch with wich to draw
	 * @param layer The draw layer that is supposed to be drawn
	 */
	public void draw(SpriteBatch batch, int layer){
		stateTime += Gdx.graphics.getDeltaTime();
		TextureRegion currentFrame = animation.getKeyFrame(stateTime, loopAnimation);
		//TODO check if the creation of a new sprite every frame could be circumvented
		Sprite sprite = new Sprite(currentFrame);

		sprite.setSize(size.x, size.y);
		sprite.setPosition(position.x , position.y );
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setRotation(SideScrollGameV2.radToDeg(angle));

		sprite.draw(batch);
	}

	/**
	 * Reverses or makes the animation run as normal depending on the input.
	 * @param reversed If true, then reversing, otherwise running normal.
     */
	public void reverseAnimation(boolean reversed){
		if (reversed) {
			if (loopAnimation) {
				animation.setPlayMode(PlayMode.LOOP_REVERSED);
			} else {
				animation.setPlayMode(PlayMode.REVERSED);
			}
		}
		else{
			if (loopAnimation) {
				animation.setPlayMode(PlayMode.LOOP);
			}
			else {
				animation.setPlayMode(PlayMode.NORMAL);
			}
		}
	}

	/**
	 * Makes the animation move, first forward then backward and so on.
	 */
	public void pingPongAnimation(){
		animation.setPlayMode(PlayMode.LOOP_PINGPONG);
	}

	/**
	 * Sets if the animation sould run continuesly or just one time. Due note that, to make the changes to this parameter
	 * take effect one must call the 'reverseAnimation' function after calling this one.
	 * @param loopAnimation if true, then loop animation.
     */
	public void setLoopAnimation(final boolean loopAnimation) {this.loopAnimation = loopAnimation;}

	public Vector2 getPosition() {return position;}

	public void setPosition(final Vector2 position) {this.position = position;}

	public Vector2 getSize() {return size;}

	public void setSize(final Vector2 size) {this.size = size;}

	public float getAngle() {return angle;}

	public void setAngle(final float angle) {this.angle = angle;}

	public int getFramesPerSek() {return framesPerSek;}

	public void setFramesPerSek(final int framesPerSek) {this.framesPerSek = framesPerSek;}

	public boolean isDone(){return animation.isAnimationFinished(stateTime);}

	public void reset(){stateTime = 0;}
}
