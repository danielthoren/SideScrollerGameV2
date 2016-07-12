package com.sidescroller.objects.Actions;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by daniel on 2016-07-12.
 */
public class HorizontalLeaver extends ButtonTrigger {
	private Sprite sprite;
	private int framesBetweenAnimation;

	public HorizontalLeaver(Sprite sprite) {
		this.sprite = sprite;

	}
}
