package com.sidescroller.objects;

import com.badlogic.gdx.physics.box2d.Contact;
import com.sidescroller.game.CollisionListener;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.SideScrollGameV2;
import com.sidescroller.game.TypeOfGameObject;
import com.sidescroller.game.Update;
import com.sidescroller.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Ladder implements CollisionListener, Update
{

	private List<Player> collidingCharacters;
	private SideScrollGameV2 sideScrollGameV2;
	private GameShape gameShape;

	private final long id;

	public Ladder(final long id, SideScrollGameV2 sideScrollGameV2, final GameShape gameShape) {
		this.id = id;
		this.sideScrollGameV2 = sideScrollGameV2;
		this.gameShape = gameShape;
		gameShape.getBody().setUserData(this);

		collidingCharacters = new ArrayList<Player>(1);
	}

	public void update(){
		for (Player player : collidingCharacters){
			if (player.isUpKey()){
				player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, player.getMaxVelocity().y/2);
			}
			else{
				player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 0);
			}
		}
	}

	public void beginContact(Contact contact){
		characterCheck(contact, true);
	}

	public void endContact(Contact contact){
		characterCheck(contact, false);
	}

	/**
	 * Checking if colliding with character, if so then sending information to 'handleCharacter' wich adds
	 * or removes the character based on the boolean 'add'.
	 * @param contact The contact.
	 * @param add true if adding else false.
	 */
	private void characterCheck(Contact contact, boolean add){
		try{
			GameObject gameObjectA = (GameObject) contact.getFixtureA().getBody().getUserData();
			GameObject gameObjectB = (GameObject) contact.getFixtureB().getBody().getUserData();

			if (gameObjectA.getTypeOfGameObject() == TypeOfGameObject.PLAYER){
				Player player = (Player) gameObjectA;
				//Adding/removing player from the lists and giving player back gracity and control over the up button.
				handleCharacter(player, add);
			}
			else if (gameObjectB.getTypeOfGameObject() == TypeOfGameObject.PLAYER){
				Player player = (Player) gameObjectB;
				//Adding/removing player from the lists and giving player back gracity and control over the up button.
				handleCharacter(player, add);
			}
		}
		catch (ClassCastException e){
			System.out.println("Error! Body does not hav a 'GameObject' as its userdata! (in ladder)");
		}
	}

	/**
	 * Adding/removing player from the lists and giving player back gracity and control over the up button.
	 * @param player The player to add/remove.
	 * @param add true if adding else false.
	 */
	private void handleCharacter(Player player, boolean add){
		if (add) {
			collidingCharacters.add(player);

			player.getBody().setGravityScale(0);
		}
		else {
			if (collidingCharacters.contains(player)) {
				collidingCharacters.remove(player);
				player.getBody().setGravityScale(1);
			}
		}
		player.setDisableUpKey(add);
	}

	/**
	 * returns the individual id for the specific object.
	 * @return int id
	 */
	public long getId(){return id;}

	/**
	 * Returns wich type of gameobject this specific object is.
	 * @return The type of gameobject
	 */
	public TypeOfGameObject getTypeOfGameObject(){return TypeOfGameObject.OTHER;}
}
