package com.sidescroller.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sidescroller.game.SideScrollerGameV2;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280; //test
		config.height = 720; //hej
		new LwjglApplication(new SideScrollerGameV2(), config); //test igen
	}
}
