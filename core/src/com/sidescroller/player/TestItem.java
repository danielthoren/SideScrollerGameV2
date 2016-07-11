package com.sidescroller.player;

/**
 * Another item for testing
 */
public class TestItem extends InventoryItem {

    private static final String DEFAULT_ITEM_DESCRIPTION = "Test item";

    protected TestItem(int itemWeight, String description) {
        super(itemWeight, description, true);
    }

    protected TestItem(int itemWeight) {
        super(itemWeight, true);

        description = DEFAULT_ITEM_DESCRIPTION;
    }
}
