package com.sidescroller.Character.Inventory;

import com.sidescroller.Character.Player;

/**
 * Created by alexander on 9/20/16.
 */
public class HealthPotion extends InventoryItem {

    private static final int DEFAULT_ITEM_WEIGHT = 1;
    private static final String DEFAULT_ITEM_DESC = "Health potion";
    private static final boolean DEFAULT_DROPPABLE = true;
    private int healPower;
    private int charges;



    public HealthPotion(int itemWeight, String description, boolean isDroppable, int healPower) {
        super(itemWeight, description, isDroppable);
        this.healPower = healPower;
    }

    public HealthPotion(int itemWeight, boolean isDroppable, int healPower) {
        super(itemWeight, isDroppable);
        this.description = DEFAULT_ITEM_DESC;
        this.healPower = healPower;
    }

    public HealthPotion(boolean isDroppable, int healPower) {
        super(isDroppable);
        this.description = DEFAULT_ITEM_DESC;
        this.itemWeight = DEFAULT_ITEM_WEIGHT;
        this.healPower = healPower;
    }

    public HealthPotion(int healPower) {
        this.description = DEFAULT_ITEM_DESC;
        this.itemWeight = DEFAULT_ITEM_WEIGHT;
        this.isDroppable = DEFAULT_DROPPABLE;
        this.healPower = healPower;
    }

    public void pickedUp(Player usedPlayer){
        this.player = usedPlayer;
    }

    public void use(){
        player.giveHealth(healPower);
        player.getInventory().removeItemFromInventory(this);
    }
}
