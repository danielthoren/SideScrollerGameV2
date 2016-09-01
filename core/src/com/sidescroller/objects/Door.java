package com.sidescroller.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.InteractGameObject;
import com.sidescroller.game.SideScrollGameV2;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.player.Player;

public class Door implements InteractGameObject
{
	private String worldFile;
	private GameShape shape;
	private SideScrollGameV2 sideScrollGameV2;
	private final long id;
	private final int doorId;

	public Door(final long id, final SideScrollGameV2 sideScrollGameV2, final int doorId, final String worldFile, final GameShape shape) {
		this.worldFile = worldFile;
		this.shape = shape;
		this.id = id;
		this.doorId = doorId;
		this.sideScrollGameV2 = sideScrollGameV2;

		shape.getBody().setUserData(this);
	}

	/**
	 * Function called when a interaction starts. A interaction is started when the player presses the key mapped to interact
	 * with other objects.
	 * @param player The player that interacts with the object.
	 */
	public void startInteract(Player player){
		/*
		sideScrollGameV2.getCurrentMap().removeDrawObject(player);
		sideScrollGameV2.getCurrentMap().removeCollisionListener(player);
		sideScrollGameV2.getCurrentMap().removeInputListener(player);
		sideScrollGameV2.getCurrentMap().removeUpdateObject(player);
		*/
		sideScrollGameV2.getCurrentMap().removeBody(player.getBody());

		boolean preLoaded = sideScrollGameV2.loadMap(worldFile);

		if(!preLoaded) {
			sideScrollGameV2.getCurrentMap().addUpdateObject(player);
			sideScrollGameV2.getCurrentMap().addCollisionListener(player);
			sideScrollGameV2.getCurrentMap().addInputListener(player);
			sideScrollGameV2.getCurrentMap().addDrawObject(player);
		}

		Vector2 loadPosition = null;
		for (Body body : sideScrollGameV2.getCurrentMap().getBodies()) {
			GameObject gameObject = (GameObject) body.getUserData();
			if (gameObject.getTypeOfGameObject() == TypeOfGameObject.DOOR && !gameObject.equals(this)) {
				Door otherDoor = (Door) gameObject;
				if (otherDoor.getDoorId() == doorId) {
					loadPosition = otherDoor.getShape().getBody().getPosition();
				}
			}
		}
		if (loadPosition != null) {
			player.recreateBodoy(sideScrollGameV2.getCurrentMap(), loadPosition);
		} else {
			player.recreateBodoy(sideScrollGameV2.getCurrentMap(), new Vector2(0, 0));
			System.out.println("No door with the same doorId found, setting spawnpoint at origin!");
		}
	}

	/**
	 * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
	 * other objects.
	 * @param player the player that interacts with the object.
	 */
	public void endInteract(Player player){
	}

	public int getDoorId(){return doorId;}

	public GameShape getShape(){return shape;}

	/**
	 * returns the individual id for the specific object.
	 * @return int id
	 */
	public long getId(){return id;}

	/**
	 * Returns wich type of gameobject this specific object is.
	 * @return The type of gameobject
	 */
	public TypeOfGameObject getTypeOfGameObject(){
		return TypeOfGameObject.DOOR;
	}
}
