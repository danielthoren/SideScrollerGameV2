package com.sidescroller.Character.Inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sidescroller.game.SideScrollGameV2;

import java.util.Arrays;

/**
 * Inventory class
 */
public class Inventory {

    private int size;
    private int maxWeight;
    private InventoryItem[] items;
    private InventoryItem defaultItem;
	private Sprite inventorySprite;

    public Inventory(SideScrollGameV2 sideScrollGameV2, int size, int maxWeight) {
        this.size = size;
        this.maxWeight = maxWeight;
		//Creates a sprite to draw and adds this object to the drawobjects of the Map
		AssetManager assetManager = sideScrollGameV2.getAssetManager();
		if (!assetManager.isLoaded("textures/inventory.jpg")){
			assetManager.load("textures/inventory.jpg", Texture.class);
			assetManager.finishLoading();
		}
		inventorySprite = new Sprite(assetManager.get("textures/inventory.jpg", Texture.class));
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
        //TODO throws NullPointerException when an item is dropped and you then toggle items.
        int resultId = -1;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i] == item){
                resultId = i;
                break;
            }
        }
        return resultId;
    }

    /**
     * Creates a default item and adds it to the inventory.
     */

    public void createDefaultItem() {
        defaultItem = new DefaultItem(1);
        addToInventory(defaultItem);
    }

    public InventoryItem getDefaultItem() {
        return defaultItem;
    }


    /**
     * Adds the given item to the inventory.
     * @param item The item to be added.
     * so it can be handled properly.
     * Returns true when the item can be added to the inventory, else false.
     */
    public boolean addToInventory(InventoryItem item){
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null && doesItemFit(item)){
                items[i] = item;
                return true;
            }
            else if(i == items.length - 1 || !doesItemFit(item)) {
                System.out.println("inventory full!");
                return false;
            }
        }
        return false;
    }

    //Return true of the item is dropped (droppable), else false
    public boolean removeItemFromInventory(int itemID){
        if (items[itemID].isDroppable()) {
            items[itemID] = null;
            return true;
        }
        System.out.println("Item not droppable!");

        return false;
    }

    public boolean removeItemFromInventory(InventoryItem item){
        int id = getItemID(item);
        return removeItemFromInventory(id);
    }

    public boolean doesItemFit(InventoryItem item){
        return (getWeightUsed() + item.getItemWeight()) < maxWeight;
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
	 * If inventory is supposed to be shown then the player must call on this function to draw the inventory.
	 * @param batch The batch in wich to draw
	 */
	public void draw(SpriteBatch batch, Vector2 position){
		inventorySprite.setPosition(position.x, position.y);
		inventorySprite.setSize(0.5f, 0.5f);
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

    public int getWeightUsed(){
        int totalItemWeight = 0;

        for (InventoryItem invItem :
                items) {
            if (invItem != null){
                totalItemWeight += invItem.getItemWeight();
            }
        }
        return totalItemWeight;
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
}
