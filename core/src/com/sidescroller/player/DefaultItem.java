package com.sidescroller.player;

/**
 * Default item used for testing
 */
public class  DefaultItem extends InventoryItem {

    private static final String DEFAULT_ITEM_DESCRIPTION = "Default item";

    protected DefaultItem(int itemSize, int itemWeight, String description) {
        super(itemSize, itemWeight, description, false);
    }

    protected DefaultItem(int itemSize, int itemWeight) {
        super(itemSize, itemWeight, false);

        description = DEFAULT_ITEM_DESCRIPTION;
    }
}
