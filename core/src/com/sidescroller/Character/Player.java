package com.sidescroller.Character;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sidescroller.Character.Inventory.Inventory;
import com.sidescroller.Character.Inventory.InventoryItem;
import com.sidescroller.Character.Inventory.TestItem;
import com.sidescroller.game.*;
import com.sidescroller.Map.Map;

import java.util.Arrays;
import java.util.Properties;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameCharacter implements Draw, Update, InputListener, CollisionListener {

    private Sprite sprite;
	private Vector2 size;

    private int interactKey, upKey, rightKey, leftKey, downKey, showInventoryKey, toggleItemKey, dropItemKey;

    //Debug keys
    private int addHPKey, reduceHPKey;

    //Inventory
    private InventoryItem currentItem;
    private Inventory inventory;

    //Loading properties
    Properties prop = new Properties();

    //Player health
    private int maxHealth;
    private int currentHealth;
    private boolean isPlayerAlive;

    public Player(long id, SideScrollGameV2 sideScrollGameV2, float bodyWidth, float density, float restitution, float friction,
				  Vector2 position, Texture texture) {
		super(id, sideScrollGameV2, density, restitution, friction);
        sprite = new Sprite(texture);
		float bodyHeight;
		bodyHeight = bodyWidth * ((float) texture.getHeight()/texture.getWidth());
        sprite.setSize(bodyWidth, bodyHeight);
        sprite.setOrigin(0,0);
		size = new Vector2(bodyWidth, bodyHeight);

        initDefaultKeys();

        sideScrollGameV2.getCurrentMap().updateLayerDepth(SideScrollGameV2.PLAYER_DRAW_LAYER);

        inventory = new Inventory(sideScrollGameV2, 10, 100);

        currentItem = inventory.getDefaultItem();
        maxHealth = 100;
        currentHealth = maxHealth;

		createBody(position, size, sideScrollGameV2.getCurrentMap());
		body.setUserData(this);
    }

    private void initDefaultKeys(){

        //setting default key bindings
        interactKey = Keys.E;
        leftKey = Keys.LEFT;
        rightKey = Keys.RIGHT;
        upKey = Keys.UP;
        downKey = Keys.DOWN;
        showInventoryKey = Keys.I;
        toggleItemKey = Keys.T;
        dropItemKey = Keys.Q;
        addHPKey = Keys.L;
        reduceHPKey = Keys.K;
    }

	/**
	 * Reloads the body in to a specified Map. This is used when loading a new Map so that the player stays the same.
     * @param map The Map to load the player in to.
     * @param position The position to load the player in to.
     */
    public void recreateBodoy(Map map, Vector2 position){
        createBody(position, size, map);
        clearCollidingBodies = true;
        sideScrollGameV2.getCurrentMap().updateLayerDepth(SideScrollGameV2.PLAYER_DRAW_LAYER);
    }

    /**
     * The function that updates the object every frame
     */
	@Override
    public void update() {
		super.update();

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

    /** Called when a key was pressed
     *
     * @param keycode one of the constants in {@link Keys}
     */
    public void keyDown(int keycode){
        if (keycode == leftKey){
            isLeftKey = true;
            //If key is not disabled then do stuff
            if (!disableLeftKey) {
                isRunning = true;
                direction = Direction.LEFT;
            }
        }
        else if (keycode == rightKey){
            isRightKey = true;
            //If key is not disabled then do stuff
            if (!disableRightKey) {
                isRunning = true;
                direction = Direction.RIGHT;
            }
        }
        else if (keycode == upKey) {
			isUpKey = true;
			//If key is not disabled then do stuff
			if (!disableUpKey) {
                jump();
			}
		}
		else if (keycode == downKey){
			isDownKey = true;
		}
		else if(keycode == showInventoryKey) {
			System.out.println(Arrays.toString(inventory.getItems()));
			System.out.println(inventory.getSpaceUsed());
		}
		else if(keycode == toggleItemKey){
			createDummyItem();
		}
		else if(keycode == dropItemKey){
			dropItem(inventory.getItemID(currentItem));
		}
		else if(keycode == toggleItemKey){
			toggleItem();
		}
        else if(keycode == addHPKey){
            giveHealth(20);
        }
        else if(keycode == reduceHPKey){
            takeDamage(15);
        }

        //If keycode is interactkey then check if any of the colliding bodies belongs to a interactobject. If so then
        //notify that object that the interaction has started. Can be used for levers, moving rocks osv.
        else if (keycode == interactKey){
            isInteractKey = true;
            //If key is not disabled then do stuff
            if (!disableInteractKey) {
                notifyInteractObjects(true);
            }
        }

    }

    /** Called when a key was released
     *
     * @param keycode one of the constants in {@link Keys}
     */
    public void keyUp (int keycode){
        if (keycode == leftKey){
            isLeftKey = false;
            //If key is not disabled then do stuff
            if (!disableLeftKey) {
                isRunning = false;
            }
        }
        else if (keycode == rightKey){
            isRightKey = false;
            //If key is not disabled then do stuff
            if (!disableRightKey) {
                isRunning = false;
            }
        }
        else if (keycode == upKey){
            isUpKey = false;
        }
		else if (keycode == downKey){
			isDownKey = false;
		}
        //If keycode is interactkey then check if any of the colliding bodies belongs to a interactobject. If so then
        //notify that object that the interaction has started. Can be used for levers, moving rocks osv.
        else if (keycode == interactKey){
            isInteractKey = false;
            //If key is not disabled then do stuff
            if (!disableInteractKey) {
                notifyInteractObjects(false);
            }
        }
    }



	@Override
    public TypeOfGameObject getTypeOfGameObject(){return TypeOfGameObject.PLAYER;}

    public int getInteractKey() {return interactKey;}

    public void setInteractKey(final int interactKey) {this.interactKey = interactKey;}

    public int getLeftKey() {return leftKey;}

    public void setLeftKey(final int leftKey) {this.leftKey = leftKey;}

    public int getRightKey() {return rightKey;}

	public void setRightKey(final int rightKey) {this.rightKey = rightKey;}

    public int getUpKey() {return upKey;}

	public void setUpKey(final int upKey) {this.upKey = upKey;}

}
