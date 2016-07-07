package com.sidescroller.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Default item used for testing
 */
public class  DefaultItem extends InventoryItem {

    private static final String DEFAULT_ITEM_DESCRIPTION = "Default item";

    protected DefaultItem(int itemSize, int itemWeight, String description) {
        super(itemSize, itemWeight, description, false);

        //itemIcon = new Sprite(new Texture(Gdx.files.internal("placeholder.png")));
    }

    protected DefaultItem(int itemSize, int itemWeight) {
        super(itemSize, itemWeight, false);

        description = DEFAULT_ITEM_DESCRIPTION;
        //itemIcon = new Sprite(new Texture(Gdx.files.internal("placeholder.png")));
    }
}
