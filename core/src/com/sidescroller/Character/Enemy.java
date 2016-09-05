package com.sidescroller.Character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sidescroller.game.Draw;
import com.sidescroller.game.SideScrollGameV2;

public class Enemy extends GameCharacter implements Draw
{
	private Sprite sprite;
	private Vector2 size;
	private long timeCount;
	private int stepCount;

	public Enemy(final long id, SideScrollGameV2 sideScrollGameV2, float density, float restitution, float friction,
				 Vector2 startPos, float bodyWidth, final Texture texture) {
		super(id, sideScrollGameV2, density, restitution, friction);
		this.sprite = new Sprite(texture);
		float bodyHeight;
		bodyHeight = bodyWidth * ((float) texture.getHeight()/texture.getWidth());
		sprite.setSize(bodyWidth, bodyHeight);
		size = new Vector2(bodyWidth, bodyHeight);

		timeCount = System.currentTimeMillis();
		isLeftKey = true;

		createBody(startPos, size, sideScrollGameV2.getCurrentMap());
	}

	@Override
	public void update(){
		super.update();
		if (System.currentTimeMillis() - timeCount > 1000){
			if (isLeftKey){
				isLeftKey = false;
				isRightKey = true;
				timeCount = System.currentTimeMillis();
				stepCount++;
			}
			else if(isRightKey){
				isRightKey = false;
				isLeftKey = true;
				timeCount = System.currentTimeMillis();
				stepCount++;
			}
		}
		if (stepCount > 3){
			if (isUpKey){
				isUpKey = false;
				stepCount = 0;
			}
			else{
				isUpKey = true;
			}
		}
	}

	/**
  * The function that draws the object every frame
  * @param batch The SpriteBatch with which to draw
  */
 @Override
 public void draw(SpriteBatch batch, int layer){
     if (layer == SideScrollGameV2.PLAYER_DRAW_LAYER) {
         sprite.setPosition(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
         sprite.setRotation(SideScrollGameV2.radToDeg(body.getAngle()));
         sprite.draw(batch);
     }
 }


}
