package com.sidescroller.objects;

import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.sidescroller.Character.GameCharacter;
import com.sidescroller.game.*;
import com.sidescroller.Character.Player;

import java.util.ArrayList;
import java.util.List;

public class Ladder implements InteractGameObject, Update, CollisionListener
{

	private List<GameCharacter> collidingCharacters;
	private SideScrollGameV2 sideScrollGameV2;
	private GameShape gameShape;

	private final long id;

	public Ladder(final long id, SideScrollGameV2 sideScrollGameV2, final GameShape gameShape) {
		this.id = id;
		this.sideScrollGameV2 = sideScrollGameV2;
		this.gameShape = gameShape;
		gameShape.getBody().setUserData(this);

		collidingCharacters = new ArrayList<GameCharacter>(1);
	}

	public void update(){
		for (GameCharacter character : collidingCharacters){
			if (character.isUpKey()){
				character.getBody().setLinearVelocity(character.getBody().getLinearVelocity().x, character.getMaxVelocity().y/2);
			}
			else if (character.isDownKey()){
				character.getBody().setLinearVelocity(character.getBody().getLinearVelocity().x, -character.getMaxVelocity().y/2);
			}
			else{
				character.getBody().setLinearVelocity(character.getBody().getLinearVelocity().x, 0);
			}
		}
	}

	/**
	 * Function called when a interaction starts. A interaction is started when the player presses the key mapped to interact
	 * with other objects.
	 * In this class this function adds/removes the player from the 'collidingCharacters' list.
	 * @param object: Can only be a GameCharacter or a subclass of it. Use the 'GameObject.getTypeOfGameObject' to check
	 *              for specific types then cast to said type (safe casting).* @param character The character that interacts with the object.
	 */
	public void startInteract(GameObject object){
		GameCharacter character = (GameCharacter) object;
		//Toggles between adding/removing the character
		if (collidingCharacters.contains(character)){
			removeOrAddPlayer(character, true);
			character.getBody().setLinearVelocity(0, 0);
		}
		else{
			removeOrAddPlayer(character, false);
		}
	}

	/**
	 * Checks so that the player is disconnected from the ladder when no longer in contact with it.
	 * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
	 *                such as point of contact and so on.
	 */
	public void beginContact(Contact contact){}

	/**
	 * Checks so that the player is disconnected from the ladder when no longer in contact with it.
	 * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
	 *                such as point of contact and so on.
	 */
	public void endContact(Contact contact){
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		GameObject gameObjectA = (GameObject) fixtureA.getBody().getUserData();
		GameObject gameObjectB = (GameObject) fixtureB.getBody().getUserData();

		if (gameObjectA.getTypeOfGameObject() == TypeOfGameObject.PLAYER){
			Player player = (Player) gameObjectA;
			removeOrAddPlayer(player, true);
		}
		if (gameObjectB.getTypeOfGameObject() == TypeOfGameObject.PLAYER){
			Player player = (Player) gameObjectB;
			removeOrAddPlayer(player, true);
		}
	}

	/**
	 * Either removes or adds the player depending on the input.
	 * @param character The character to add/remove.
	 * @param remove True if removing else false.
	 */
	private void removeOrAddPlayer(GameCharacter character, boolean remove){
		if (remove && collidingCharacters.contains(character)){
			character.setDisableDownKey(false);
			character.setDisableUpKey(false);
			collidingCharacters.remove(character);
		}
		else if (!remove){
			character.setDisableDownKey(true);
			character.setDisableUpKey(true);
			collidingCharacters.add(character);
		}
	}



	/**
	 * Function called when a interaction ends. A interaction ends when the player releases the key mapped to interact with
	 * other objects.
	 * In This class this function does nothing.
	 * @param object: Can only be a GameCharacter or a subclass of it. Use the 'GameObject.getTypeOfGameObject' to check
	 *              for specific types then cast to said type (safe casting).* @param character The character that interacts with the object.
	 */
	public void endInteract(GameObject object){}

	/**
	 * returns the individual id for the specific object.
	 * @return int id
	 */
	public long getId(){return id;}

	/**
	 * Returns wich type of gameobject this specific object is.
	 * @return The type of gameobject
	 */
	public TypeOfGameObject getTypeOfGameObject(){return TypeOfGameObject.INTERACTOBJECT;}
}
