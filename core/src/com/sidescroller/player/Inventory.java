package com.sidescroller.player;

import java.util.Arrays;

/**
 * Inventory class
 */
public class Inventory {

    private int size;
    private int maxWeight;
    private InventoryItem[] items;
    private InventoryItem defaultItem;


    public Inventory(int size, int maxWeight) {
        this.size = size;
        this.maxWeight = maxWeight;
        items = new InventoryItem[size];
        //Creates a default item for the player to use
        createDefaultItem();
    }

    public InventoryItem getItem(int itemId){
        return items[itemId];
    }

    public InventoryItem[] getItems() {
        return items;
    }

    public int getItemID(InventoryItem item){
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)){
                return i;
            }
        }
        //TODO CHANGE
        return 9999;
    }

    public void createDefaultItem(){
        defaultItem = new DefaultItem(1, 1);
        try {
            addToInventory(defaultItem);
        }catch (InventoryFullException e){
            e.printStackTrace();
        }
    }

    public InventoryItem getDefaultItem() {
        return defaultItem;
    }

    public void addToInventory(InventoryItem item) throws InventoryFullException {

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null){
                items[i] = item;
                break;
            }
            else if(i == items.length - 1) {
                throw new InventoryFullException("Inventory is full");
            }
        }
    }

    public void dropItem(int itemID){
        
    }

    public int getSpaceUsed(){
        int total = 0;
        for (InventoryItem item :
                items) {
            if (item != null) {
                total += 1;
            }
        }
        return total;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "items=" + Arrays.toString(items) +
                '}';
    }
}
