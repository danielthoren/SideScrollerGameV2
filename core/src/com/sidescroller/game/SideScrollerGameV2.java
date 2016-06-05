package com.sidescroller.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SideScrollerGameV2 extends ApplicationAdapter {
	 private static Map currentMap;
	
	@Override
	public void create () {
	}

	@Override
	public void render () {
	}

	/**
	 * Converts the input meters (world coordinates) to pixels (java Fx coordinates) using the scale factor loaded in
	 * the 'MapLoader' singgelton.
	 * @param meters The amount of meters to be converted
	 * @return The amount of pixels the meters corresponds to
	 */
	public static float metersToPix(float meters){
	}


	/**
	 * Converts the input pixels (java Fx coordinates) to meters (world coordinates) using the scale factor loaded in
	 * the 'MapLoader' singgelton.
	 * @param pix The amount of pixels to be converted
	 * @return The amount of meters the pixels corresponds to
	 */
	public static float pixToMeters(float pix){
		return pix;
	}

	public static Map getCurrentMap() {return currentMap;}
}
