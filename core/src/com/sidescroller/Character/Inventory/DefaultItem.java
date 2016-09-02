package com.sidescroller.Character.Inventory;

/**
 * Default item used for testing
 */
public class  DefaultItem extends InventoryItem {

    private static final String DEFAULT_ITEM_DESCRIPTION = "Default item";

    protected DefaultItem(int itemWeight, String description) {
        super(itemWeight, description, false);

        //itemIcon = new Sprite(new Texture(Gdx.files.internal("placeholder.png")));
    }

    protected DefaultItem(int itemWeight) {
        super(itemWeight, false);

        description = DEFAULT_ITEM_DESCRIPTION;
        //itemIcon = new Sprite(new Texture(Gdx.files.internal("placeholder.png")));
    }
}
