package com.sidescroller.Character.Inventory;

/**
 * Another item for testing
 */
public class TestItem extends InventoryItem {

    private static final String DEFAULT_ITEM_DESCRIPTION = "Test item";

    public TestItem(int itemWeight, String description) {
        super(itemWeight, description, true);
    }

    public TestItem(int itemWeight) {
        super(itemWeight, true);

        description = DEFAULT_ITEM_DESCRIPTION;
    }
}
