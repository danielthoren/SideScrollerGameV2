package com.sidescroller.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sidescroller.game.Draw;
import com.sidescroller.game.GameObject;
import com.sidescroller.game.TypeOfGameObject;

/**
 * A parent class for different inventory items.
 */
public class InventoryItem implements Draw, GameObject{
    protected int itemWeight;
    protected String description;
    protected boolean isDroppable;
    //protected Sprite itemIcon;

    protected InventoryItem(int itemWeight, String description, boolean isDroppable){
        this.itemWeight = itemWeight;
        this.description = description;
        this.isDroppable = isDroppable;

        //SideScrollerGameV2.getCurrentMap().addDrawObject(this);
    }

    protected InventoryItem(int itemWeight, boolean isDroppable){
        this.itemWeight = itemWeight;
        this.isDroppable = isDroppable;

        //SideScrollerGameV2.getCurrentMap().addDrawObject(this);
    }

    public String getDescription() {
        return description;
    }

    public boolean isDroppable() {
        return isDroppable;
    }

    public int getItemWeight() {
        return itemWeight;
    }

    @Override
    public String toString() {
        return "InventoryItem{" + description +'}';
    }

    @Override
    public void draw(SpriteBatch batch, int layer) {
    //    itemIcon.setPosition(0, 0);
    //    itemIcon.setSize(0.5f, 0.5f);
    //    itemIcon.draw(batch);
    }


    @Override
    public long getId() {
        return 0;
    }

    @Override
    public TypeOfGameObject getTypeOfGameObject() {
        return null;
    }
}
