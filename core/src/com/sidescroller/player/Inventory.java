package com.sidescroller.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sidescroller.game.Draw;
import com.sidescroller.game.SideScrollerGameV2;

import java.util.Arrays;
import java.util.Hashtable;

/**
 * Inventory class
 */
public class Inventory implements Draw {

    private int size;
    private int maxWeight;
    private InventoryItem[] items;
    private InventoryItem defaultItem;
	private Sprite inventorySprite;
	private final long iD;

    public Inventory(long iD, int size, int maxWeight) {
		this.iD = iD;
        this.size = size;
        this.maxWeight = maxWeight;
		//Creates a sprite to draw and adds this object to the drawobjects of the map
		inventorySprite = new Sprite(new Texture(Gdx.files.internal("inventory.jpg")));
        items = new InventoryItem[size];
        //Creates a default item for the player to use
        createDefaultItem();
    }

    public InventoryItem getItem(int itemId){
        return items[itemId];
    }

    public InventoryItem[] getItems() {
        return items;
    }

    /**
     * Returns the ID of the given item. Should probably be rewritten as using
     * an array given some limitations.
     * @param item The item to return the ID of.
     * @return "slot" in the items array.
     */

    public int getItemID(InventoryItem item){
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)){
                return i;
            }
        }
        //TODO CHANGE
        return 9999;
    }

    /**
     * Creates a default item and adds it to the inventory.
     */

    public void createDefaultItem(){
        defaultItem = new DefaultItem(1, 1);
        try {
            addToInventory(defaultItem);
        }catch (InventoryFullException e){
            e.printStackTrace();
        }
    }

    public InventoryItem getDefaultItem() {
        return defaultItem;
    }


    /**
     * Adds the given item to the inventory.
     * @param item The item to be added.
     * @throws InventoryFullException should be thrown when the inventory is full
     * so it can be handled properly.
     */
    public void addToInventory(InventoryItem item) throws InventoryFullException {

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null){
                items[i] = item;
                break;
            }
            else if(i == items.length - 1) {
                throw new InventoryFullException("Inventory is full");
            }
        }
    }

    public void removeItemFromInventory(int itemID) throws ItemNotDroppableException {
        if (items[itemID].isDroppable()) {
            items[itemID] = null;
        }
        else{
            throw new ItemNotDroppableException("This item is not droppable");
        }
    }

    public InventoryItem getNextItem(int itemID){
        for (int i = itemID; i < items.length; i++) {
            if (items[i] != null && !items[itemID].equals(items[i])){
                return items[i];
            }
        }

        for (int i = 0; i < itemID; i++) {
            if (items[i] != null && !items[itemID].equals(items[i])){
                return items[i];
            }
        }

        //TODO Change
        return items[0];
    }

	/**
	 * The function that draws the object every frame
	 * @param batch The SpriteBatch with wich to draw
	 * @param layer The draw layer that is supposed to be drawn
	 */
	public void draw(SpriteBatch batch, int layer){
		inventorySprite.setPosition(0,0);
		inventorySprite.setSize(2,2);
		inventorySprite.draw(batch);
	}

    /**
     * Returns how many slots are used in the inventory. This needs to be rewritten since
     * it doesn't take the size or weight of the items into consideration.
     * @return total "slots" used.
     */
    public int getSpaceUsed(){
        int total = 0;
        for (InventoryItem item :
                items) {
            if (item != null) {
                total += 1;
            }
        }
        return total;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "items=" + Arrays.toString(items) +
                '}';
    }

	/**
	 * returns the individual iD for the specific object.
	 * @return int iD
	 */
	public long getId(){
		return iD;
	}
}
