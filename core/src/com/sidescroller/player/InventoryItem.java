package com.sidescroller.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sidescroller.game.Draw;

/**
 * A parent class for different inventory items.
 */
public class InventoryItem implements Draw{
    protected int itemSize;
    protected int itemWeight;
    protected String description;
    protected boolean isMovable;

    protected InventoryItem(int itemSize, int itemWeight, String description, boolean isMovable){
        this.itemSize = itemSize;
        this.itemWeight = itemWeight;
        this.description = description;
        this.isMovable = isMovable;
    }

    protected InventoryItem(int itemSize, int itemWeight, boolean isMovable){
        this.itemSize = itemSize;
        this.itemWeight = itemWeight;
        this.isMovable = isMovable;
    }

    public String getDescription() {
        return description;
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
