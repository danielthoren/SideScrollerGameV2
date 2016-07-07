package com.sidescroller.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sidescroller.game.Draw;
import com.sidescroller.game.GameObject;

/**
 * A parent class for different inventory items.
 */
public class InventoryItem implements Draw, GameObject{
    protected int itemSize;
    protected int itemWeight;
    protected String description;
    protected boolean isDroppable;

    protected InventoryItem(int itemSize, int itemWeight, String description, boolean isDroppable){
        this.itemSize = itemSize;
        this.itemWeight = itemWeight;
        this.description = description;
        this.isDroppable = isDroppable;
    }

    protected InventoryItem(int itemSize, int itemWeight, boolean isDroppable){
        this.itemSize = itemSize;
        this.itemWeight = itemWeight;
        this.isDroppable = isDroppable;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDroppable() {
        return isDroppable;
    }

    @Override
    public String toString() {
        return "InventoryItem{" + description +'}';
    }

    @Override
    public void draw(SpriteBatch batch, int layer) {

    }

    @Override
    public long getId() {
        return 0;
    }
}
