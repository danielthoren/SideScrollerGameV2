package com.sidescroller.Character.Inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * A simple sword class for testing out some things.
 */
public class TestSword extends InventoryItem {
    private static final String DEFAULT_DESC = "Test sword item";
    private int damage = 10;
    private Sprite itemIcon;

    public TestSword(int itemWeight, String description, boolean isDroppable) {
        super(itemWeight, description, isDroppable);

        itemIcon = new Sprite(new Texture(Gdx.files.internal("placeholder.png")));
    }

    public TestSword(int itemWeight, boolean isDroppable) {
        super(itemWeight, isDroppable);

        itemIcon = new Sprite(new Texture(Gdx.files.internal("placeholder.png")));
    }

}
