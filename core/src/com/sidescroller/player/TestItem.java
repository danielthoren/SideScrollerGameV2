package com.sidescroller.player;

/**
 * Another item for testing
 */
public class TestItem extends InventoryItem {

    private static final String DEFAULT_ITEM_DESCRIPTION = "Test item";

    protected TestItem(int itemSize, int itemWeight, String description) {
        super(itemSize, itemWeight, description, true);
    }

    protected TestItem(int itemSize, int itemWeight) {
        super(itemSize, itemWeight, true);

        description = DEFAULT_ITEM_DESCRIPTION;
    }
}
